package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.*;
import cat.rezelyn.watheextended.game.ItemBoundsChecker;
import cat.rezelyn.watheextended.game.PlayerItemManager;
import cat.rezelyn.watheextended.game.PronounsManager;
import cat.rezelyn.watheextended.index.*;
import cat.rezelyn.watheextended.modifiers.adaptive.AdaptiveModifier;
import cat.rezelyn.watheextended.modifiers.noellesroles.binglus.AwesomeBinglusNote;
import cat.rezelyn.watheextended.modifiers.noellesroles.feather.FeatherModifierFix;
import cat.rezelyn.watheextended.modifiers.introverted.IntrovertedModifier;
import cat.rezelyn.watheextended.game.TeleportationHandler;
import cat.rezelyn.watheextended.api.ConfigSync;
import cat.rezelyn.watheextended.modifiers.stupidexpress.lovers.ForbiddenLovers;
import cat.rezelyn.watheextended.modifiers.taxed.TaxedModifier;
import cat.rezelyn.watheextended.api.wathe.GameStatus;
import cat.rezelyn.watheextended.game.LastStandManager;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WatheExtended implements ModInitializer {
    public static final String MOD_ID = "watheextended";
    private static final Logger LOGGER = LoggerFactory.getLogger(WatheExtended.class);

    public static @NotNull Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }

    public static void clearEffects(World world, ServerPlayerEntity player) {
        if (world == null || player == null) return;
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            if (gwc == null) return;
            if (gwc.getGameStatus() == GameWorldComponent.GameStatus.STOPPING) {
                List<StatusEffectInstance> effects = new java.util.ArrayList<>(player.getStatusEffects());
                for (StatusEffectInstance effect : effects) {
                    player.removeStatusEffect(effect.getEffectType());
                }
            }
        } catch (Throwable t) {
        }
    }

    @Override
    public void onInitialize() {
        // registry
        WatheExtendedSounds.initialize();
        WatheExtendedItems.initialize();
        WatheExtendedBlocks.initialize();
        WatheExtendedBlockEntities.initialize();
        WatheExtendedGroup.initialize();

        WatheExtendedServerConfig.load();

        // integrations
        PronounsManager.load();
        cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers.initialize();
        cat.rezelyn.watheextended.api.hml.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.starexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.registerEntries();

        // core
        registerServerConfigEntries();
        registerNetworking();
        registerConnectionEvents();
        registerGameEvents();
        registerTickEvents();
        registerLastStand();

        // commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WatheExtendedMapVariablesCommand.register(dispatcher);
            TeleportationSlotsCommand.register(dispatcher);
            GamemodeRulesCommand.register(dispatcher);
            AddonsConfigCommand.register(dispatcher);
            PronounsCommand.register(dispatcher);
        });

        LOGGER.info("Mod initialized!");
    }

    private static void registerServerConfigEntries() {
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.playerCollisions", true, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isPlayerCollisionsEnabled(); }
            catch (Throwable t) { return true; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setPlayerCollisionsEnabled(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.rtpEnabled", true, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isRtpEnabled(); }
            catch (Throwable t) { return true; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setRtpEnabled(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.blockProtection", true, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isBlockInteractionsProtected(); }
            catch (Throwable t) { return true; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setBlockInteractionsProtected(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.itemBoundsCheck", true, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isItemBoundsCheckEnabled(); }
            catch (Throwable t) { return true; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setItemBoundsCheckEnabled(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.forbiddenLovers", false, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isForbiddenLoversEnabled(); }
            catch (Throwable t) { return false; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setForbiddenLoversEnabled(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.forbiddenLovers.chance", 0.25f,
                WatheExtendedServerConfig::getForbiddenLoversChance,
                WatheExtendedServerConfig::setForbiddenLoversChance));
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
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.taxed.coinReduction", 0.50f,
                WatheExtendedServerConfig::getTaxedCoinReduction,
                WatheExtendedServerConfig::setTaxedCoinReduction));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.taxed.killThreshold", 1,
                WatheExtendedServerConfig::getTaxedKillThreshold,
                WatheExtendedServerConfig::setTaxedKillThreshold));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.taxed.killWindowSeconds", 60,
                WatheExtendedServerConfig::getTaxedKillWindowSeconds,
                WatheExtendedServerConfig::setTaxedKillWindowSeconds));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.adaptive.penaltyReduction", 0.50f,
                WatheExtendedServerConfig::getAdaptivePenaltyReduction,
                WatheExtendedServerConfig::setAdaptivePenaltyReduction));
        ServerConfig.register(ServerConfig.Entry.globalFloat("watheextended.adaptive.bonusMultiplier", 0.50f,
                WatheExtendedServerConfig::getAdaptiveBonusMultiplier,
                WatheExtendedServerConfig::setAdaptiveBonusMultiplier));
        ServerConfig.register(ServerConfig.Entry.globalBool("watheextended.suppressAbilityVfxSfx", false,
                WatheExtendedServerConfig::isSuppressAbilityVfxSfx,
                WatheExtendedServerConfig::setSuppressAbilityVfxSfx));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.cleaner.playerLimit", 10,
                WatheExtendedServerConfig::getCleanerPlayerLimit,
                WatheExtendedServerConfig::setCleanerPlayerLimit));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.killIncreaseTime", 60,
                WatheExtendedServerConfig::getKillIncreaseTime,
                WatheExtendedServerConfig::setKillIncreaseTime));
        ServerConfig.register(ServerConfig.Entry.globalBool("watheextended.lastStand.enabled", false,
                WatheExtendedServerConfig::isLastStandEnabled,
                WatheExtendedServerConfig::setLastStandEnabled));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.lastStand.cooldown", 30,
                WatheExtendedServerConfig::getLastStandCooldown,
                WatheExtendedServerConfig::setLastStandCooldown));
    }

    private static void registerNetworking() {
        PayloadTypeRegistry.playS2C().register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerConfig.ChangePayload.ID, ServerConfig.ChangePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PronounsManager.UpdatePayload.ID, PronounsManager.UpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PronounsManager.SyncPayload.ID, PronounsManager.SyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LastStandManager.LastStandPayload.ID, LastStandManager.LastStandPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PronounsManager.UpdatePayload.ID, (payload, context) -> {
            UUID uuid = context.player().getUuid();
            String pronouns = payload.pronouns().trim();
            context.server().execute(() -> {
                PronounsManager.set(uuid, pronouns);
                String stored = PronounsManager.get(uuid);
                PronounsManager.SyncPayload sync = new PronounsManager.SyncPayload(uuid, stored);
                for (ServerPlayerEntity p : context.server().getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(p, sync);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerConfig.ChangePayload.ID, (payload, context) -> {
            if (!context.player().hasPermissionLevel(2)) return;
            context.server().execute(() -> {
                ServerWorld overworld = context.server().getOverworld();
                Map<String, String> registryChanges = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, String> entry : payload.changes().entrySet()) {
                    if (entry.getKey().startsWith("cmd:")) {
                        String cmd = entry.getKey().substring(4);
                        try {
                            context.server().getCommandManager().getDispatcher().execute(cmd, context.player().getCommandSource().withLevel(4).withSilent());
                        } catch (Throwable ignored) {
                        }
                    } else {
                        registryChanges.put(entry.getKey(), entry.getValue());
                    }
                }
                if (!registryChanges.isEmpty()) ServerConfig.applyChanges(registryChanges, overworld);
                ServerConfig.broadcastToAll(context.server());
            });
        });
    }

    private static void registerConnectionEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity joining = handler.player;
            server.execute(() -> {
                ServerConfig.sendToPlayer(joining);
                PronounsManager.getAll().forEach((uuid, pronouns) ->
                        ServerPlayNetworking.send(joining, new PronounsManager.SyncPayload(uuid, pronouns)));
                try {
                    ServerWorld overworld = server.getOverworld();
                    GameWorldComponent gwc = GameWorldComponent.KEY.get(overworld);
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(overworld);

                    if (gwc.isRunning() && wec.isPlayerKilled(joining.getUuid())) {
                        joining.changeGameMode(GameMode.SPECTATOR);
                    }

                    PlayerItemManager.applyItemState(joining, overworld, gwc.getGameStatus());
                } catch (Throwable ignored) {}
            });
        });
    }

    private static void registerGameEvents() {
        GameEvents.ON_FINISH_INITIALIZE.register((world, gameWorldComponent) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayers(); }
            catch (Throwable ignored) {}
            AdaptiveModifier.clearAll();
            TaxedModifier.clearAll();
            if (world instanceof ServerWorld sw) FeatherModifierFix.applyOnGameStart(sw);
            if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
                AwesomeBinglusNote.applyOnGameStart(world, gameWorldComponent);
            }
            if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
                try {
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                    if (wec.isForbiddenLoversEnabled()) {
                        ForbiddenLovers.apply(world, gameWorldComponent);
                    }
                } catch (Throwable ignored) {}
            }
        });

        GameEvents.ON_FINISH_FINALIZE.register((world, gameWorldComponent) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayers(); }
            catch (Throwable ignored) {}
            AdaptiveModifier.clearAll();
            TaxedModifier.clearAll();
            LastStandManager.clearAll();
        });
    }

    private static void registerTickEvents() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            long worldTime = world.getTime();

            GameWorldComponent gwc;
            try {
                gwc = GameWorldComponent.KEY.get(world);
            } catch (Throwable t) {
                return;
            }
            if (gwc == null) return;

            GameWorldComponent.GameStatus status = gwc.getGameStatus();

            TeleportationHandler.tick(serverWorld, status, worldTime);

            if (worldTime % 20 == 0) {
                PlayerItemManager.tickAll(serverWorld, status);
            }

            if (status == GameWorldComponent.GameStatus.ACTIVE && worldTime % 5 == 0) {
                try {
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                    if (wec != null && wec.isItemBoundsCheckEnabled()) {
                        ItemBoundsChecker.tick(serverWorld);
                    }
                } catch (Throwable ignored) {
                }
            }

            IntrovertedModifier.tick(serverWorld);

            if (serverWorld.getRegistryKey() == World.OVERWORLD) {
                ConfigSync.tick(serverWorld);
                LastStandManager.tick(serverWorld);
            }
        });
    }

    private static void registerLastStand() {
        AllowPlayerDeath.EVENT.register((victim, killer, deathReason) -> {
            if (!WatheExtendedServerConfig.lastStandEnabled) return true;
            if (deathReason == null) return true;
            if (!GameStatus.isActive(victim.getWorld())) return true;

            String id = deathReason.toString();
            String message = null;

            if ("stupid_express:broken_heart".equals(id)) {
                message = "You feel your heart begin to ache...";
            } else if ("noellesroles:voodoo".equals(id)) {
                if (killer != null) {
                    message = "Your identity has been compromised...";
                } else if (!isGuesser(victim)) {
                    message = "Strange visions curse your mind...";
                }
            }

            if (message == null) return true;

            return !LastStandManager.tryActivate(victim, killer, deathReason, message);
        });
    }

    private static boolean isGuesser(net.minecraft.entity.player.PlayerEntity player) {
        try {
            org.agmas.harpymodloader.component.WorldModifierComponent wmc =
                    org.agmas.harpymodloader.component.WorldModifierComponent.KEY.get(player.getWorld());
            return wmc.isRole(player, org.agmas.noellesroles.Noellesroles.GUESSER);
        } catch (Throwable ignored) {}
        return false;
    }
}
