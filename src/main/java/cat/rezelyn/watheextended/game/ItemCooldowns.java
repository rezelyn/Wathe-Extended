package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ItemCooldowns {

    private static final List<Runnable> appliers = new ArrayList<>();

    private ItemCooldowns() {
    }

    public static void registerAll() {
        register(() -> {
            setCooldown(WatheItems.KNIFE, WatheExtendedServerConfig.knifeCooldown);
            setCooldown(WatheItems.REVOLVER, WatheExtendedServerConfig.revolverCooldown);
            setCooldown(WatheItems.PSYCHO_MODE, WatheExtendedServerConfig.psychoModeCooldown);
            setCooldown(WatheItems.LOCKPICK, WatheExtendedServerConfig.lockpickCooldown);
            setCooldown(WatheItems.CROWBAR, WatheExtendedServerConfig.crowbarCooldown);
            setCooldown(WatheItems.BODY_BAG, WatheExtendedServerConfig.bodyBagCooldown);
            setCooldown(WatheItems.BLACKOUT, WatheExtendedServerConfig.blackoutCooldown);
        });

        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.grenade.cooldown", 90,
                WatheExtendedServerConfig::getGrenadeCooldown,
                WatheExtendedServerConfig::setGrenadeCooldown));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knife.cooldown", 60,
                WatheExtendedServerConfig::getKnifeCooldown,
                value -> {
                    WatheExtendedServerConfig.setKnifeCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.revolver.cooldown", 10,
                WatheExtendedServerConfig::getRevolverCooldown,
                value -> {
                    WatheExtendedServerConfig.setRevolverCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.psychoMode.cooldown", 300,
                WatheExtendedServerConfig::getPsychoModeCooldown,
                value -> {
                    WatheExtendedServerConfig.setPsychoModeCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.lockpick.cooldown", 180,
                WatheExtendedServerConfig::getLockpickCooldown,
                value -> {
                    WatheExtendedServerConfig.setLockpickCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.crowbar.cooldown", 10,
                WatheExtendedServerConfig::getCrowbarCooldown,
                value -> {
                    WatheExtendedServerConfig.setCrowbarCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.bodyBag.cooldown", 300,
                WatheExtendedServerConfig::getBodyBagCooldown,
                value -> {
                    WatheExtendedServerConfig.setBodyBagCooldown(value);
                    applyAll();
                }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blackout.cooldown", 300,
                WatheExtendedServerConfig::getBlackoutCooldown,
                value -> {
                    WatheExtendedServerConfig.setBlackoutCooldown(value);
                    applyAll();
                }));

        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            register(() -> {
                setCooldown("kinswathe", "sulfuric_acid_barrel", WatheExtendedServerConfig.sulfuricAcidBarrelCooldown);
                setCooldown("kinswathe", "hunting_knife", WatheExtendedServerConfig.huntingKnifeCooldown);
                setCooldown("kinswathe", "medical_kit", WatheExtendedServerConfig.medicalKitCooldown);
                setCooldown("kinswathe", "pan", WatheExtendedServerConfig.panCooldown);
                setCooldown("kinswathe", "poison_injector", WatheExtendedServerConfig.poisonInjectorCooldown);
                setCooldown("kinswathe", "pill", WatheExtendedServerConfig.pillCooldown);
                setCooldown("kinswathe", "blowgun", WatheExtendedServerConfig.blowgunCooldown);
                setCooldown("kinswathe", "knockout_drug", WatheExtendedServerConfig.knockoutDrugCooldown);
                setCooldown("kinswathe", "capture_device", WatheExtendedServerConfig.captureDeviceCooldown);
                setCooldown("kinswathe", "wrench", WatheExtendedServerConfig.wrenchCooldown);
                setCooldown("kinswathe", "icon_power_restoration", WatheExtendedServerConfig.powerRestorationCooldown);
                setCooldown("kinswathe", "icon_weapon_cooldown_refresh", WatheExtendedServerConfig.refreshWeaponCooldownCooldown);
                setCooldown("kinswathe", "icon_ability_cooldown_refresh", WatheExtendedServerConfig.refreshAbilityCooldownCooldown);
                setCooldown("kinswathe", "icon_potion_effect_refresh", WatheExtendedServerConfig.refreshPotionEffectCooldown);
            });

            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.sulfuricAcidBarrel.cooldown", 60,
                    WatheExtendedServerConfig::getSulfuricAcidBarrelCooldown,
                    value -> {
                        WatheExtendedServerConfig.setSulfuricAcidBarrelCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.huntingKnife.cooldown", 45,
                    WatheExtendedServerConfig::getHuntingKnifeCooldown,
                    value -> {
                        WatheExtendedServerConfig.setHuntingKnifeCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.medicalKit.cooldown", 60,
                    WatheExtendedServerConfig::getMedicalKitCooldown,
                    value -> {
                        WatheExtendedServerConfig.setMedicalKitCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pan.cooldown", 45,
                    WatheExtendedServerConfig::getPanCooldown,
                    value -> {
                        WatheExtendedServerConfig.setPanCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonInjector.cooldown", 60,
                    WatheExtendedServerConfig::getPoisonInjectorCooldown,
                    value -> {
                        WatheExtendedServerConfig.setPoisonInjectorCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pill.cooldown", 180,
                    WatheExtendedServerConfig::getPillCooldown,
                    value -> {
                        WatheExtendedServerConfig.setPillCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blowgun.cooldown", 60,
                    WatheExtendedServerConfig::getBlowgunCooldown,
                    value -> {
                        WatheExtendedServerConfig.setBlowgunCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knockoutDrug.cooldown", 60,
                    WatheExtendedServerConfig::getKnockoutDrugCooldown,
                    value -> {
                        WatheExtendedServerConfig.setKnockoutDrugCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.captureDevice.cooldown", 60,
                    WatheExtendedServerConfig::getCaptureDeviceCooldown,
                    value -> {
                        WatheExtendedServerConfig.setCaptureDeviceCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.wrench.cooldown", 120,
                    WatheExtendedServerConfig::getWrenchCooldown,
                    value -> {
                        WatheExtendedServerConfig.setWrenchCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.powerRestoration.cooldown", 180,
                    WatheExtendedServerConfig::getPowerRestorationCooldown,
                    value -> {
                        WatheExtendedServerConfig.setPowerRestorationCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshWeaponCooldown.cooldown", 180,
                    WatheExtendedServerConfig::getRefreshWeaponCooldownCooldown,
                    value -> {
                        WatheExtendedServerConfig.setRefreshWeaponCooldownCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshAbilityCooldown.cooldown", 300,
                    WatheExtendedServerConfig::getRefreshAbilityCooldownCooldown,
                    value -> {
                        WatheExtendedServerConfig.setRefreshAbilityCooldownCooldown(value);
                        applyAll();
                    }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshPotionEffect.cooldown", 180,
                    WatheExtendedServerConfig::getRefreshPotionEffectCooldown,
                    value -> {
                        WatheExtendedServerConfig.setRefreshPotionEffectCooldown(value);
                        applyAll();
                    }));
        }

        if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
            register(() -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.applyMuzzlerTapeCooldown(WatheExtendedServerConfig.tapeCooldown));

            ServerConfig.register(ServerConfig.Entry.globalInt("starexpress.tapeCooldown", 20,
                    WatheExtendedServerConfig::getTapeCooldown,
                    value -> {
                        WatheExtendedServerConfig.setTapeCooldown(value);
                        applyAll();
                    }));
        }

        if (cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) {
            ServerConfig.register(ServerConfig.Entry.globalInt("stupidexpress.jerryCan.cooldown", 0,
                    WatheExtendedServerConfig::getJerryCanCooldown,
                    WatheExtendedServerConfig::setJerryCanCooldown));
            ServerConfig.register(ServerConfig.Entry.globalInt("stupidexpress.lighter.cooldown", 0,
                    WatheExtendedServerConfig::getLighterCooldown,
                    WatheExtendedServerConfig::setLighterCooldown));
        }
    }

    public static void register(Runnable applier) {
        appliers.add(applier);
    }

    public static void applyAll() {
        for (Runnable applier : appliers) {
            try {
                applier.run();
            } catch (Throwable ignored) {
            }
        }
    }

    public static void setCooldown(Item item, int seconds) {
        GameConstants.ITEM_COOLDOWNS.put(item, seconds * 20);
    }

    public static void setCooldown(String namespace, String path, int seconds) {
        Item item = Registries.ITEM.get(Identifier.of(namespace, path));
        if (item != Items.AIR) GameConstants.ITEM_COOLDOWNS.put(item, seconds * 20);
    }
}
