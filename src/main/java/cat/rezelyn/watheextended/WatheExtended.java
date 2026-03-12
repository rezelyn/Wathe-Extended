package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.cca.GameStatus;
import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.AddonsConfigCommand;
import cat.rezelyn.watheextended.command.GamemodeRulesCommand;
import cat.rezelyn.watheextended.command.TeleportationSlotsCommand;
import cat.rezelyn.watheextended.command.WatheExtendedMapVariablesCommand;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
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
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WatheExtended implements ModInitializer {
    public static final String MOD_ID = "watheextended";
    private static final Logger LOGGER = LoggerFactory.getLogger(WatheExtended.class);

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

    // rtp scheduling and tracking
    private static final Map<RegistryKey<World>, Long> rtpSchedule = new ConcurrentHashMap<>();
    private static final Map<RegistryKey<World>, Boolean> prevStarting = new ConcurrentHashMap<>();
    // ticks to wait after the fading starts before teleporting players 40 seems to be the best value
    // the teleportation will happen when screen is completely faded out making it seamless
    private static final int RTP_FADE_TICK = 40;

    @Override
    public void onInitialize() {
        WatheExtendedItems.initialize();
        WatheExtendedBlocks.initialize();
        WatheExtendedSounds.initialize();

        // register all ConfigEntry into ConfigRegistry
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

        // register sync packets
        PayloadTypeRegistry.playS2C().register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerConfig.ChangePayload.ID, ServerConfig.ChangePayload.CODEC);

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
                        } catch (Throwable ignored) {}
                    } else {
                        registryChanges.put(entry.getKey(), entry.getValue());
                    }
                }

                if (!registryChanges.isEmpty()) {
                    ServerConfig.applyChanges(registryChanges, overworld);
                }

                ServerConfig.broadcastToOps(context.server());
            });
        });

        // push current server configs to op client when they join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity joining = handler.player;
            server.execute(() -> {
                if (joining.hasPermissionLevel(2)) {
                    ServerConfig.sendToPlayer(joining);
                }
            });
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WatheExtendedMapVariablesCommand.register(dispatcher);
            TeleportationSlotsCommand.register(dispatcher);
            GamemodeRulesCommand.register(dispatcher);
            AddonsConfigCommand.register(dispatcher);
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
        });

        LOGGER.info("Mod initialized!");
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
}
