package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.cca.GameStatus;
import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.AddonsConfigCommand;
import cat.rezelyn.watheextended.command.GamemodeRulesCommand;
import cat.rezelyn.watheextended.command.TeleportationSlotsCommand;
import cat.rezelyn.watheextended.command.WatheExtendedMapVariablesCommand;
import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import cat.rezelyn.watheextended.rtp.TeleportationSlot;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.index.WatheEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WatheExtended implements ModInitializer {
    public static final String MOD_ID = "watheextended";
    private static final Logger LOGGER = LoggerFactory.getLogger(WatheExtended.class);
    // rtp scheduling and tracking
    private static final Map<RegistryKey<World>, Long> rtpSchedule = new ConcurrentHashMap<>();
    private static final Map<RegistryKey<World>, Boolean> prevStarting = new ConcurrentHashMap<>();
    // ticks to wait after the fading starts before teleporting players 40 seems to be the best value
    // the teleportation will happen when screen is completely faded out making it seamless
    private static final int RTP_FADE_TICK = 40;
    private static final int HML_SYNC_INTERVAL = 20; // check every sec for changes
    // sync: fix config sync issues for non-op players
    private static List<String> lastKnownDisabledRoles = List.of();
    private static List<String> lastKnownDisabledModifiers = List.of();
    private static int hmlSyncTimer = 0;

    public static @NotNull Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }

    // give teleport item to player if they are not in the ready area and the game is not running
    public static void giveTeleportItem(ServerPlayerEntity player) {
        boolean haveItem = false;
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                haveItem = true;
                break;
            }
        }
        if (!haveItem) {
            player.getInventory().insertStack(new ItemStack(WatheExtendedItems.TELEPORT_TO_READY_AREA));
        }
    }

    // remove teleport item from player inventory if they are in the ready area
    public static void removeTeleportItem(ServerPlayerEntity player) {
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                player.getInventory().setStack(item, ItemStack.EMPTY);
            }
        }
    }

    // give the guidebook to players
    public static void giveGuidebook(ServerPlayerEntity player) {
        boolean haveItem = false;
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.GUIDEBOOK)) {
                haveItem = true;
                break;
            }
        }
        if (!haveItem) {
            player.getInventory().insertStack(new ItemStack(WatheExtendedItems.GUIDEBOOK));
        }
    }

    private static boolean isStarting(World world) {
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            return gwc != null && gwc.getGameStatus() == GameWorldComponent.GameStatus.STARTING;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void performRtp(ServerWorld world) {
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            if (!wec.isRtpEnabled()) return;

            List<TeleportationSlot> slots = new ArrayList<>(wec.getTeleportationSlots().values());
            if (slots.isEmpty()) return;


            List<ServerPlayerEntity> eligiblePlayers = new ArrayList<>();
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                eligiblePlayers.add(sp);
            }

            if (eligiblePlayers.isEmpty()) return;

            Collections.shuffle(eligiblePlayers);
            Collections.shuffle(slots);

            int count = Math.min(eligiblePlayers.size(), slots.size());
            for (int i = 0; i < count; i++) {
                ServerPlayerEntity player = eligiblePlayers.get(i);
                TeleportationSlot slot = slots.get(i);
                TeleportTarget target = new TeleportTarget(world, new Vec3d(slot.x, slot.y, slot.z), Vec3d.ZERO, slot.yaw, slot.pitch, TeleportTarget.NO_OP);
                player.teleportTo(target);
            }
        } catch (Throwable ignored) {
        }
    }

    // oob item checker: teleport items that are outside the play area to the closest player body or player
    private static void tickItemBoundsCheck(ServerWorld world) {
        try {
            Box playArea = MapVariables.getPlayArea(world);
            if (playArea == null) return;

            List<Entity> targets = new ArrayList<>();
            for (net.minecraft.entity.player.PlayerEntity p : world.getPlayers()) {
                if (p instanceof ServerPlayerEntity player
                        && player.isAlive()
                        && !player.isSpectator()
                        && !player.isCreative()) {
                    targets.add(player);
                }
            }
            for (PlayerBodyEntity body : world.getEntitiesByType(WatheEntities.PLAYER_BODY, body -> true)) {
                targets.add(body);
            }

            if (targets.isEmpty()) return;

            for (ItemEntity item : world.getEntitiesByType(net.minecraft.entity.EntityType.ITEM, item -> !playArea.contains(item.getPos()))) {
                Entity closest = findClosestEntity(item.getPos(), targets);
                if (closest == null) continue;
                Vec3d dest = closest.getPos();
                item.requestTeleport(dest.x, dest.y, dest.z);
            }
        } catch (Throwable ignored) {
        }
    }

    // oob item checker: find the closest entity (player or body)
    private static Entity findClosestEntity(Vec3d from, List<Entity> candidates) {
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            double dist = from.squaredDistanceTo(candidate.getPos());
            if (dist < bestDist) {
                bestDist = dist;
                best = candidate;
            }
        }
        return best;
    }

    public static void clearEffects(World world, ServerPlayerEntity player) {
        if (world == null || player == null) return;
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            if (gwc == null) return;
            if (gwc.getGameStatus() == GameWorldComponent.GameStatus.STOPPING) {
                List<StatusEffectInstance> effects = new ArrayList<>(player.getStatusEffects());
                for (StatusEffectInstance effect : effects) {
                    player.removeStatusEffect(effect.getEffectType());
                }
            }
        } catch (Throwable t) {
        }
    }

    private static void tickHmlConfigSync(ServerWorld world) {
        if (++hmlSyncTimer < HML_SYNC_INTERVAL) return;
        hmlSyncTimer = 0;

        List<String> currentRoles = cat.rezelyn.watheextended.api.hml.ConfigHelper.getDisabledRoles();
        List<String> currentModifiers = cat.rezelyn.watheextended.api.hml.ConfigHelper.getDisabledModifiers();

        if (!currentRoles.equals(lastKnownDisabledRoles) || !currentModifiers.equals(lastKnownDisabledModifiers)) {
            lastKnownDisabledRoles = List.copyOf(currentRoles);
            lastKnownDisabledModifiers = List.copyOf(currentModifiers);
            ServerConfig.broadcastToAll(world.getServer());
        }
    }

    // feather modifier fix:
    // continuously reapplies slow falling each tick.
    private static void tickFeatherModifier(ServerWorld world) {
        if (!cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) return;
        try {
            org.agmas.harpymodloader.component.WorldModifierComponent wmc =
                    org.agmas.harpymodloader.component.WorldModifierComponent.KEY.get(world);
            if (wmc == null) return;
            org.agmas.harpymodloader.modifiers.Modifier feather = org.agmas.noellesroles.Noellesroles.FEATHER;
            if (feather == null) return;
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity serverPlayer)) continue;
                try {
                    if (wmc.isModifier(serverPlayer, feather)) {
                        StatusEffectInstance existing = serverPlayer.getStatusEffect(StatusEffects.SLOW_FALLING);
                        // reapply when duration is about to expire (~20 ticks)
                        if (existing == null || existing.getDuration() < 20) {
                            serverPlayer.addStatusEffect(new StatusEffectInstance(
                                    StatusEffects.SLOW_FALLING, 80, 0, false, false));
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onInitialize() {
        WatheExtendedItems.initialize();
        WatheExtendedBlocks.initialize();
        WatheExtendedBlockEntities.initialize();
        WatheExtendedSounds.initialize();

        WatheExtendedServerConfig.load();

        // register all ConfigEntry into ConfigRegistry
        cat.rezelyn.watheextended.pronouns.PronounsManager.load();
        // register custom modifiers with HarpyModLoader
        cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers.initialize();
        cat.rezelyn.watheextended.api.hml.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.starexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.registerEntries();

        // read/write directly to overworld component
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.playerCollisions", true,
                w -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(w).isPlayerCollisionsEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                },
                (w, v) -> {
                    try {
                        WatheExtendedWorldComponent.KEY.get(w).setPlayerCollisionsEnabled(v);
                    } catch (Throwable ignored) {
                    }
                }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.rtpEnabled", true,
                w -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(w).isRtpEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                },
                (w, v) -> {
                    try {
                        WatheExtendedWorldComponent.KEY.get(w).setRtpEnabled(v);
                    } catch (Throwable ignored) {
                    }
                }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.blockProtection", true,
                w -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(w).isBlockInteractionsProtected();
                    } catch (Throwable t) {
                        return true;
                    }
                },
                (w, v) -> {
                    try {
                        WatheExtendedWorldComponent.KEY.get(w).setBlockInteractionsProtected(v);
                    } catch (Throwable ignored) {
                    }
                }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.itemBoundsCheck", true,
                w -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(w).isItemBoundsCheckEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                },
                (w, v) -> {
                    try {
                        WatheExtendedWorldComponent.KEY.get(w).setItemBoundsCheckEnabled(v);
                    } catch (Throwable ignored) {
                    }
                }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.forbiddenLovers", false,
                w -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(w).isForbiddenLoversEnabled();
                    } catch (Throwable t) {
                        return false;
                    }
                },
                (w, v) -> {
                    try {
                        WatheExtendedWorldComponent.KEY.get(w).setForbiddenLoversEnabled(v);
                    } catch (Throwable ignored) {
                    }
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.introverted.crowdCount", 3,
                WatheExtendedServerConfig::getIntrovertedCrowdCount,
                WatheExtendedServerConfig::setIntrovertedCrowdCount));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.introverted.crowdRange", 5.0f,
                WatheExtendedServerConfig::getIntrovertedCrowdRange,
                WatheExtendedServerConfig::setIntrovertedCrowdRange));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.introverted.crowdDrainMultiplier", 2.0f,
                WatheExtendedServerConfig::getIntrovertedCrowdDrainMultiplier,
                WatheExtendedServerConfig::setIntrovertedCrowdDrainMultiplier));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.introverted.aloneDrainMultiplier", 0.5f,
                WatheExtendedServerConfig::getIntrovertedAloneDrainMultiplier,
                WatheExtendedServerConfig::setIntrovertedAloneDrainMultiplier));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.taxed.coinReduction", 0.25f,
                WatheExtendedServerConfig::getTaxedCoinReduction,
                WatheExtendedServerConfig::setTaxedCoinReduction));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.adaptive.penaltyReduction", 0.50f,
                WatheExtendedServerConfig::getAdaptivePenaltyReduction,
                WatheExtendedServerConfig::setAdaptivePenaltyReduction));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.adaptive.bonusMultiplier", 0.50f,
                WatheExtendedServerConfig::getAdaptiveBonusMultiplier,
                WatheExtendedServerConfig::setAdaptiveBonusMultiplier));

        // register sync packets
        PayloadTypeRegistry.playS2C().register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerConfig.ChangePayload.ID, ServerConfig.ChangePayload.CODEC);

        // register pronouns packets
        PayloadTypeRegistry.playC2S().register(cat.rezelyn.watheextended.pronouns.PronounsManager.UpdatePayload.ID, cat.rezelyn.watheextended.pronouns.PronounsManager.UpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(cat.rezelyn.watheextended.pronouns.PronounsManager.SyncPayload.ID, cat.rezelyn.watheextended.pronouns.PronounsManager.SyncPayload.CODEC);

        // handle incoming pronouns updates from any client
        ServerPlayNetworking.registerGlobalReceiver(cat.rezelyn.watheextended.pronouns.PronounsManager.UpdatePayload.ID, (payload, context) -> {
            UUID uuid = context.player().getUuid();
            String pronouns = payload.pronouns().trim();
            context.server().execute(() -> {
                cat.rezelyn.watheextended.pronouns.PronounsManager.set(uuid, pronouns);
                String stored = cat.rezelyn.watheextended.pronouns.PronounsManager.get(uuid);
                cat.rezelyn.watheextended.pronouns.PronounsManager.SyncPayload sync =
                        new cat.rezelyn.watheextended.pronouns.PronounsManager.SyncPayload(uuid, stored);
                for (ServerPlayerEntity p : context.server().getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(p, sync);
                }
            });
        });

        // handle incoming config changes from op clients
        ServerPlayNetworking.registerGlobalReceiver(ServerConfig.ChangePayload.ID, (payload, context) -> {
            if (!context.player().hasPermissionLevel(2)) return;
            context.server().execute(() -> {
                ServerWorld overworld = context.server().getOverworld();

                Map<String, String> registryChanges = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, String> entry : payload.changes().entrySet()) {
                    if (entry.getKey().startsWith("cmd:")) {
                        String cmd = entry.getKey().substring(4); // strip prefix
                        try {
                            context.server().getCommandManager().getDispatcher()
                                    .execute(cmd, context.player().getCommandSource().withLevel(4).withSilent());
                        } catch (Throwable ignored) {
                        }
                    } else {
                        registryChanges.put(entry.getKey(), entry.getValue());
                    }
                }

                if (!registryChanges.isEmpty()) {
                    ServerConfig.applyChanges(registryChanges, overworld);
                }

                ServerConfig.broadcastToAll(context.server());
            });
        });

        // sync: push current server configs to all clients when they join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity joining = handler.player;
            server.execute(() -> {
                ServerConfig.sendToPlayer(joining);
                // send all stored pronouns to the joining player
                cat.rezelyn.watheextended.pronouns.PronounsManager.getAll().forEach((uuid, pronouns) ->
                        ServerPlayNetworking.send(joining,
                                new cat.rezelyn.watheextended.pronouns.PronounsManager.SyncPayload(uuid, pronouns)));
                try {
                    ServerWorld overworld = server.getOverworld();
                    GameWorldComponent gwc = GameWorldComponent.KEY.get(overworld);
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(overworld);
                    if (gwc.isRunning() && wec.isPlayerKilled(joining.getUuid())) {
                        joining.changeGameMode(GameMode.SPECTATOR);
                    }
                } catch (Throwable ignored) {
                }
            });
        });

        GameEvents.ON_FINISH_INITIALIZE.register((world, gameWorldComponent) -> {
            try {
                WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayers();
            } catch (Throwable ignored) {
            }
            cat.rezelyn.watheextended.modifiers.AdaptiveModifier.clearAll();
            if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
                try {
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                    if (wec.isForbiddenLoversEnabled()) {
                        cat.rezelyn.watheextended.modifiers.stupidexpress.ForbiddenLovers.apply(world, gameWorldComponent);
                    }
                } catch (Throwable ignored) {
                }
            }
        });

        GameEvents.ON_FINISH_FINALIZE.register((world, gameWorldComponent) -> {
            try {
                WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayers();
            } catch (Throwable ignored) {
            }
            cat.rezelyn.watheextended.modifiers.AdaptiveModifier.clearAll();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WatheExtendedMapVariablesCommand.register(dispatcher);
            TeleportationSlotsCommand.register(dispatcher);
            GamemodeRulesCommand.register(dispatcher);
            AddonsConfigCommand.register(dispatcher);
            cat.rezelyn.watheextended.pronouns.PronounsCommand.register(dispatcher);
        });

        // world tick handler
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            boolean gameRunning = GameStatus.State(world);
            boolean isStarting = isStarting(world);
            boolean wasStarting = prevStarting.getOrDefault(world.getRegistryKey(), false);

            if (isStarting && !wasStarting) {
                rtpSchedule.put(world.getRegistryKey(), world.getTime() + RTP_FADE_TICK);
            }
            prevStarting.put(world.getRegistryKey(), isStarting);

            Long fireAt = rtpSchedule.get(world.getRegistryKey());
            if (fireAt != null && world.getTime() >= fireAt) {
                rtpSchedule.remove(world.getRegistryKey());
                performRtp(serverWorld);
            }

            Box readyArea = MapVariables.getReadyArea(world);
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity serverPlayer)) continue;

                if (gameRunning) {
                    removeTeleportItem(serverPlayer);
                } else if (readyArea != null && readyArea.contains(serverPlayer.getPos())) {
                    removeTeleportItem(serverPlayer);
                    giveGuidebook(serverPlayer);
                } else {
                    giveTeleportItem(serverPlayer);
                    giveGuidebook(serverPlayer);
                }
            }

            if (GameStatus.isActive(world)) {
                try {
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                    if (wec != null && wec.isItemBoundsCheckEnabled()) {
                        tickItemBoundsCheck(serverWorld);
                    }
                } catch (Throwable ignored) {
                }
            }

            // feather modifier fix
            tickFeatherModifier(serverWorld);

            // introverted modifier tick
            cat.rezelyn.watheextended.modifiers.IntrovertedModifier.tick(serverWorld);

            // sync: detect HML config changes from direct commands
            if (serverWorld.getRegistryKey() == World.OVERWORLD) {
                tickHmlConfigSync(serverWorld);
            }
        });

        LOGGER.info("Mod initialized!");
    }
}
