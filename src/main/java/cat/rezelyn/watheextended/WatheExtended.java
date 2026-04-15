package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.config.ConfigSync;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.*;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.index.*;
import cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers;
import cat.rezelyn.watheextended.modifiers.IntrovertedModifier;
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
        } catch (Throwable ignored) {
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
        WatheExtendedModifiers.initialize();

        WatheExtendedServerConfig.load();
        PronounsManager.load();

        ItemCooldowns.registerAll();
        ItemPrices.registerAll();
        ItemCooldowns.applyAll();
        ItemPrices.applyAll();

        // integrations
        cat.rezelyn.watheextended.api.config.hml.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.registerEntries();
        cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.registerEntries();

        // core
        registerServerConfigEntries();
        registerNetworking();
        registerConnectionEvents();
        registerTick();
        GameEvents.register();
        LastStand.register();

        // commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MapVariablesCommand.register(dispatcher);
            TeleportationSlotsCommand.register(dispatcher);
            GamemodeRulesCommand.register(dispatcher);
            AddonsConfigCommand.register(dispatcher);
            PronounsCommand.register(dispatcher);
        });

        LOGGER.info("Mod initialized!");
    }

    private static void registerServerConfigEntries() {
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.playerCollisions", true, world -> {
            try { return WatheExtendedWorldComponent.KEY.get(world).isPlayerCollisionsEnabled(); }
            catch (Throwable throwable) { return true; }
        }, (world, value) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).setPlayerCollisionsEnabled(value); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.rtpEnabled", false, world -> {
            try { return WatheExtendedWorldComponent.KEY.get(world).isRtpEnabled(); }
            catch (Throwable throwable) { return false; }
        }, (world, value) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).setRtpEnabled(value); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.blockProtection", false, world -> {
            try { return WatheExtendedWorldComponent.KEY.get(world).isBlockInteractionsProtected(); }
            catch (Throwable throwable) { return false; }
        }, (world, value) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).setBlockInteractionsProtected(value); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.itemBoundsCheck", true, world -> {
            try { return WatheExtendedWorldComponent.KEY.get(world).isItemBoundsCheckEnabled(); }
            catch (Throwable throwable) { return true; }
        }, (world, value) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).setItemBoundsCheckEnabled(value); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.forbiddenLovers", false, world -> {
            try { return WatheExtendedWorldComponent.KEY.get(world).isForbiddenLoversEnabled(); }
            catch (Throwable throwable) { return false; }
        }, (world, value) -> {
            try { WatheExtendedWorldComponent.KEY.get(world).setForbiddenLoversEnabled(value); }
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
        ServerConfig.register(ServerConfig.Entry.globalString("watheextended.jumpMode", "LOBBY",
                WatheExtendedServerConfig::getJumpMode,
                WatheExtendedServerConfig::setJumpMode));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.cleaner.playerLimit", 10,
                WatheExtendedServerConfig::getCleanerPlayerLimit,
                WatheExtendedServerConfig::setCleanerPlayerLimit));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.cleaner.acidBarrelCoins", 50,
                WatheExtendedServerConfig::getCleanerAcidBarrelCoins,
                value -> { WatheExtendedServerConfig.setCleanerAcidBarrelCoins(value); ItemPrices.applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalBool("watheextended.cleaner.acidBarrelCoinBonusEnabled", false,
                WatheExtendedServerConfig::isCleanerAcidBarrelCoinBonusEnabled,
                value -> { WatheExtendedServerConfig.setCleanerAcidBarrelCoinBonusEnabled(value); ItemPrices.applyAll(); }));
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
        PayloadTypeRegistry.playS2C().register(LastStand.LastStandPayload.ID, LastStand.LastStandPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerConfig.ChangePayload.ID, (payload, context) -> {
            if (!context.player().hasPermissionLevel(2)) return;
            context.server().execute(() -> {
                ServerWorld overworld = context.server().getOverworld();
                Map<String, String> registryChanges = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, String> entry : payload.changes().entrySet()) {
                    if (entry.getKey().startsWith("cmd:")) {
                        String command = entry.getKey().substring(4);
                        try {
                            context.server().getCommandManager().getDispatcher().execute(command, context.player().getCommandSource().withLevel(4).withSilent());
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

                    PlayerItem.applyItemState(joining, overworld);
                } catch (Throwable ignored) {}
            });
        });
    }

    private static void registerTick() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            long worldTime = world.getTime();

            GameWorldComponent game;
            try {
                game = GameWorldComponent.KEY.get(world);
            } catch (Throwable throwable) {
                return;
            }
            if (game == null) return;

            GameWorldComponent.GameStatus status = game.getGameStatus();

            TeleportationHandler.tick(serverWorld, status, worldTime);

            if (worldTime % 20 == 0) {
                PlayerItem.tickAll(serverWorld);
            }

            if (status == GameWorldComponent.GameStatus.ACTIVE && worldTime % 5 == 0) {
                try {
                    WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
                    if (component != null && component.isItemBoundsCheckEnabled()) {
                        ItemBoundsChecker.tick(serverWorld);
                    }
                } catch (Throwable ignored) {
                }
            }

            IntrovertedModifier.tick(serverWorld);

            if (serverWorld.getRegistryKey() == World.OVERWORLD) {
                ConfigSync.tick(serverWorld);
                LastStand.tick(serverWorld);
            }
        });
    }
}
