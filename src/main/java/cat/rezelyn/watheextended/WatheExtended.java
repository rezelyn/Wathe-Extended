package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.ConfigSync;
import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.api.wathe.GameStatus;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.*;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.index.*;
import cat.rezelyn.watheextended.mixin.wathe.ShopEntryAccessor;
import cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers;
import cat.rezelyn.watheextended.modifiers.adaptive.AdaptiveModifier;
import cat.rezelyn.watheextended.modifiers.introverted.IntrovertedModifier;
import cat.rezelyn.watheextended.modifiers.noellesroles.binglus.AwesomeBinglusNote;
import cat.rezelyn.watheextended.modifiers.noellesroles.feather.FeatherModifierFix;
import cat.rezelyn.watheextended.modifiers.stupidexpress.lovers.ForbiddenLovers;
import cat.rezelyn.watheextended.modifiers.taxed.TaxedModifier;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
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
        applyItemCooldowns();
        applyShopPrices();

        // integrations
        PronounsManager.load();
        WatheExtendedModifiers.initialize();
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
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.rtpEnabled", false, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isRtpEnabled(); }
            catch (Throwable t) { return false; }
        }, (w, v) -> {
            try { WatheExtendedWorldComponent.KEY.get(w).setRtpEnabled(v); }
            catch (Throwable ignored) {}
        }));
        ServerConfig.register(ServerConfig.Entry.worldBool("watheextended.blockProtection", false, w -> {
            try { return WatheExtendedWorldComponent.KEY.get(w).isBlockInteractionsProtected(); }
            catch (Throwable t) { return false; }
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
        ServerConfig.register(ServerConfig.Entry.globalString("watheextended.jumpMode", "LOBBY",
                WatheExtendedServerConfig::getJumpMode,
                WatheExtendedServerConfig::setJumpMode));
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
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.grenade.cooldown", 90,
                WatheExtendedServerConfig::getGrenadeCooldown,
                WatheExtendedServerConfig::setGrenadeCooldown));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knife.cooldown", 60,
                WatheExtendedServerConfig::getKnifeCooldown,
                v -> { WatheExtendedServerConfig.setKnifeCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.KNIFE, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.revolver.cooldown", 10,
                WatheExtendedServerConfig::getRevolverCooldown,
                v -> { WatheExtendedServerConfig.setRevolverCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.REVOLVER, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.psychoMode.cooldown", 300,
                WatheExtendedServerConfig::getPsychoModeCooldown,
                v -> { WatheExtendedServerConfig.setPsychoModeCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.PSYCHO_MODE, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.lockpick.cooldown", 180,
                WatheExtendedServerConfig::getLockpickCooldown,
                v -> { WatheExtendedServerConfig.setLockpickCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.LOCKPICK, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.crowbar.cooldown", 10,
                WatheExtendedServerConfig::getCrowbarCooldown,
                v -> { WatheExtendedServerConfig.setCrowbarCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.CROWBAR, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.bodyBag.cooldown", 300,
                WatheExtendedServerConfig::getBodyBagCooldown,
                v -> { WatheExtendedServerConfig.setBodyBagCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.BODY_BAG, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blackout.cooldown", 300,
                WatheExtendedServerConfig::getBlackoutCooldown,
                v -> { WatheExtendedServerConfig.setBlackoutCooldown(v); GameConstants.ITEM_COOLDOWNS.put(WatheItems.BLACKOUT, v * 20); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knife.price", 100,
                WatheExtendedServerConfig::getKnifePrice,
                v -> { WatheExtendedServerConfig.setKnifePrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.revolver.price", 300,
                WatheExtendedServerConfig::getRevolverPrice,
                v -> { WatheExtendedServerConfig.setRevolverPrice(v); applyShopPrices(); applyKinsWatheShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.grenade.price", 350,
                WatheExtendedServerConfig::getGrenadePrice,
                v -> { WatheExtendedServerConfig.setGrenadePrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.psychoMode.price", 300,
                WatheExtendedServerConfig::getPsychoModePrice,
                v -> { WatheExtendedServerConfig.setPsychoModePrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonVial.price", 100,
                WatheExtendedServerConfig::getPoisonVialPrice,
                v -> { WatheExtendedServerConfig.setPoisonVialPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.scorpion.price", 50,
                WatheExtendedServerConfig::getScorpionPrice,
                v -> { WatheExtendedServerConfig.setScorpionPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.firecracker.price", 10,
                WatheExtendedServerConfig::getFirecrackerPrice,
                v -> { WatheExtendedServerConfig.setFirecrackerPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.lockpick.price", 50,
                WatheExtendedServerConfig::getLockpickPrice,
                v -> { WatheExtendedServerConfig.setLockpickPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.crowbar.price", 25,
                WatheExtendedServerConfig::getCrowbarPrice,
                v -> { WatheExtendedServerConfig.setCrowbarPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.bodyBag.price", 200,
                WatheExtendedServerConfig::getBodyBagPrice,
                v -> { WatheExtendedServerConfig.setBodyBagPrice(v); applyShopPrices(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blackout.price", 200,
                WatheExtendedServerConfig::getBlackoutPrice,
                v -> { WatheExtendedServerConfig.setBlackoutPrice(v); applyShopPrices(); }));
        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.sulfuricAcidBarrel.cooldown", 60,
                    WatheExtendedServerConfig::getSulfuricAcidBarrelCooldown,
                    v -> { WatheExtendedServerConfig.setSulfuricAcidBarrelCooldown(v); putKinsWatheCooldown("sulfuric_acid_barrel", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.huntingKnife.cooldown", 45,
                    WatheExtendedServerConfig::getHuntingKnifeCooldown,
                    v -> { WatheExtendedServerConfig.setHuntingKnifeCooldown(v); putKinsWatheCooldown("hunting_knife", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.medicalKit.cooldown", 60,
                    WatheExtendedServerConfig::getMedicalKitCooldown,
                    v -> { WatheExtendedServerConfig.setMedicalKitCooldown(v); putKinsWatheCooldown("medical_kit", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pan.cooldown", 45,
                    WatheExtendedServerConfig::getPanCooldown,
                    v -> { WatheExtendedServerConfig.setPanCooldown(v); putKinsWatheCooldown("pan", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonInjector.cooldown", 60,
                    WatheExtendedServerConfig::getPoisonInjectorCooldown,
                    v -> { WatheExtendedServerConfig.setPoisonInjectorCooldown(v); putKinsWatheCooldown("poison_injector", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pill.cooldown", 180,
                    WatheExtendedServerConfig::getPillCooldown,
                    v -> { WatheExtendedServerConfig.setPillCooldown(v); putKinsWatheCooldown("pill", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blowgun.cooldown", 60,
                    WatheExtendedServerConfig::getBlowgunCooldown,
                    v -> { WatheExtendedServerConfig.setBlowgunCooldown(v); putKinsWatheCooldown("blowgun", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knockoutDrug.cooldown", 60,
                    WatheExtendedServerConfig::getKnockoutDrugCooldown,
                    v -> { WatheExtendedServerConfig.setKnockoutDrugCooldown(v); putKinsWatheCooldown("knockout_drug", v); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.huntingKnife.price", 100,
                    WatheExtendedServerConfig::getHuntingKnifePrice,
                    v -> { WatheExtendedServerConfig.setHuntingKnifePrice(v); applyKinsWatheShopPrices(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonInjector.price", 125,
                    WatheExtendedServerConfig::getPoisonInjectorPrice,
                    v -> { WatheExtendedServerConfig.setPoisonInjectorPrice(v); applyKinsWatheShopPrices(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blowgun.price", 175,
                    WatheExtendedServerConfig::getBlowgunPrice,
                    v -> { WatheExtendedServerConfig.setBlowgunPrice(v); applyKinsWatheShopPrices(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knockoutDrug.price", 75,
                    WatheExtendedServerConfig::getKnockoutDrugPrice,
                    v -> { WatheExtendedServerConfig.setKnockoutDrugPrice(v); applyKinsWatheShopPrices(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pan.price", 250,
                    WatheExtendedServerConfig::getPanPrice,
                    v -> { WatheExtendedServerConfig.setPanPrice(v); applyKinsWatheShopPrices(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pill.price", 300,
                    WatheExtendedServerConfig::getPillPrice,
                    v -> { WatheExtendedServerConfig.setPillPrice(v); applyKinsWatheShopPrices(); }));
        }
    }

    private static void applyItemCooldowns() {
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.KNIFE, WatheExtendedServerConfig.knifeCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.REVOLVER, WatheExtendedServerConfig.revolverCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.PSYCHO_MODE, WatheExtendedServerConfig.psychoModeCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.LOCKPICK, WatheExtendedServerConfig.lockpickCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.CROWBAR, WatheExtendedServerConfig.crowbarCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.BODY_BAG, WatheExtendedServerConfig.bodyBagCooldown * 20);
        GameConstants.ITEM_COOLDOWNS.put(WatheItems.BLACKOUT, WatheExtendedServerConfig.blackoutCooldown * 20);
        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            applyKinsWatheItemCooldowns();
            applyKinsWatheShopPrices();
        }
    }

    private static void applyKinsWatheItemCooldowns() {
        putKinsWatheCooldown("sulfuric_acid_barrel", WatheExtendedServerConfig.sulfuricAcidBarrelCooldown);
        putKinsWatheCooldown("hunting_knife", WatheExtendedServerConfig.huntingKnifeCooldown);
        putKinsWatheCooldown("medical_kit", WatheExtendedServerConfig.medicalKitCooldown);
        putKinsWatheCooldown("pan", WatheExtendedServerConfig.panCooldown);
        putKinsWatheCooldown("poison_injector", WatheExtendedServerConfig.poisonInjectorCooldown);
        putKinsWatheCooldown("pill", WatheExtendedServerConfig.pillCooldown);
        putKinsWatheCooldown("blowgun", WatheExtendedServerConfig.blowgunCooldown);
        putKinsWatheCooldown("knockout_drug", WatheExtendedServerConfig.knockoutDrugCooldown);
    }

    private static void applyKinsWatheShopPrices() {
        if (!cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) return;
        try {
            Class<?> cls = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
            Object handler = cls.getField("HANDLER").get(null);
            Object cfg = handler.getClass().getMethod("instance").invoke(handler);
            cfg.getClass().getField("CookPanPrice").set(cfg, WatheExtendedServerConfig.panPrice);
            cfg.getClass().getField("PhysicianPillPrice").set(cfg, WatheExtendedServerConfig.pillPrice);
            cfg.getClass().getField("LicensedVillainRevolverPrice").set(cfg, WatheExtendedServerConfig.revolverPrice);
            cfg.getClass().getField("DrugmakerPoisonInjectorPrice").set(cfg, WatheExtendedServerConfig.poisonInjectorPrice);
            cfg.getClass().getField("DrugmakerBlowgunPrice").set(cfg, WatheExtendedServerConfig.blowgunPrice);
            cfg.getClass().getField("KidnapperKnockoutDrugPrice").set(cfg, WatheExtendedServerConfig.knockoutDrugPrice);
        } catch (Throwable ignored) {
        }
    }

    private static void applyKinsWatheWorldPrices(World world) {
        if (!cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) return;
        try {
            Class<?> cwcCls = Class.forName("org.BsXinQin.kinswathe.component.ConfigWorldComponent");
            Object key = cwcCls.getField("KEY").get(null);
            Object comp = key.getClass().getMethod("get", Object.class).invoke(key, world);
            comp.getClass().getField("CookPanPrice").set(comp, WatheExtendedServerConfig.panPrice);
            comp.getClass().getField("PhysicianPillPrice").set(comp, WatheExtendedServerConfig.pillPrice);
            comp.getClass().getField("LicensedVillainRevolverPrice").set(comp, WatheExtendedServerConfig.revolverPrice);
            comp.getClass().getField("DrugmakerPoisonInjectorPrice").set(comp, WatheExtendedServerConfig.poisonInjectorPrice);
            comp.getClass().getField("DrugmakerBlowgunPrice").set(comp, WatheExtendedServerConfig.blowgunPrice);
            comp.getClass().getField("KidnapperKnockoutDrugPrice").set(comp, WatheExtendedServerConfig.knockoutDrugPrice);
        } catch (Throwable ignored) {
        }
    }

    private static void putKinsWatheCooldown(String itemId, int seconds) {
        net.minecraft.item.Item item = net.minecraft.registry.Registries.ITEM.get(Identifier.of("kinswathe", itemId));
        if (item != net.minecraft.item.Items.AIR) {
            GameConstants.ITEM_COOLDOWNS.put(item, seconds * 20);
        }
    }

    private static void applyShopPrices() {
        for (dev.doctor4t.wathe.util.ShopEntry entry : GameConstants.SHOP_ENTRIES) {
            net.minecraft.item.Item item = entry.stack().getItem();
            int price = -1;
            if (item == WatheItems.KNIFE) price = WatheExtendedServerConfig.knifePrice;
            else if (item == WatheItems.REVOLVER) price = WatheExtendedServerConfig.revolverPrice;
            else if (item == WatheItems.GRENADE) price = WatheExtendedServerConfig.grenadePrice;
            else if (item == WatheItems.PSYCHO_MODE) price = WatheExtendedServerConfig.psychoModePrice;
            else if (item == WatheItems.POISON_VIAL) price = WatheExtendedServerConfig.poisonVialPrice;
            else if (item == WatheItems.SCORPION) price = WatheExtendedServerConfig.scorpionPrice;
            else if (item == WatheItems.FIRECRACKER) price = WatheExtendedServerConfig.firecrackerPrice;
            else if (item == WatheItems.LOCKPICK) price = WatheExtendedServerConfig.lockpickPrice;
            else if (item == WatheItems.CROWBAR) price = WatheExtendedServerConfig.crowbarPrice;
            else if (item == WatheItems.BODY_BAG) price = WatheExtendedServerConfig.bodyBagPrice;
            else if (item == WatheItems.BLACKOUT) price = WatheExtendedServerConfig.blackoutPrice;
            if (price >= 0) ((ShopEntryAccessor) entry).watheextended$setPrice(price);
        }
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
            try { 
                WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                wec.clearKilledPlayers();
                wec.setGameStartWorldTime(world.getTime());
            }
            catch (Throwable ignored) {}
            AdaptiveModifier.clearAll();
            TaxedModifier.clearAll();
            if (world instanceof ServerWorld sw) FeatherModifierFix.applyOnGameStart(sw);
            if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
                applyKinsWatheShopPrices();
                applyKinsWatheWorldPrices(world);
            }
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
