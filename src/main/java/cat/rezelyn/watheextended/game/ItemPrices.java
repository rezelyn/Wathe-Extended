package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.mixin.shop.ShopEntryAccessor;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ItemPrices {

    private static final List<Runnable> appliers = new ArrayList<>();
    private static final List<Consumer<World>> worldAppliers = new ArrayList<>();

    private ItemPrices() {}

    public static void registerAll() {
        register(() -> {
            setWatheShopPrice(WatheItems.KNIFE, WatheExtendedServerConfig.knifePrice);
            setWatheShopPrice(WatheItems.REVOLVER, WatheExtendedServerConfig.revolverPrice);
            setWatheShopPrice(WatheItems.GRENADE, WatheExtendedServerConfig.grenadePrice);
            setWatheShopPrice(WatheItems.PSYCHO_MODE, WatheExtendedServerConfig.psychoModePrice);
            setWatheShopPrice(WatheItems.POISON_VIAL, WatheExtendedServerConfig.poisonVialPrice);
            setWatheShopPrice(WatheItems.SCORPION, WatheExtendedServerConfig.scorpionPrice);
            setWatheShopPrice(WatheItems.FIRECRACKER, WatheExtendedServerConfig.firecrackerPrice);
            setWatheShopPrice(WatheItems.LOCKPICK, WatheExtendedServerConfig.lockpickPrice);
            setWatheShopPrice(WatheItems.CROWBAR, WatheExtendedServerConfig.crowbarPrice);
            setWatheShopPrice(WatheItems.BODY_BAG, WatheExtendedServerConfig.bodyBagPrice);
            setWatheShopPrice(WatheItems.BLACKOUT, WatheExtendedServerConfig.blackoutPrice);
            setWatheShopPrice(WatheItems.NOTE, WatheExtendedServerConfig.notePrice);
        });

        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knife.price", 100,
                WatheExtendedServerConfig::getKnifePrice,
                value -> { WatheExtendedServerConfig.setKnifePrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.revolver.price", 300,
                WatheExtendedServerConfig::getRevolverPrice,
                value -> { WatheExtendedServerConfig.setRevolverPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.grenade.price", 350,
                WatheExtendedServerConfig::getGrenadePrice,
                value -> { WatheExtendedServerConfig.setGrenadePrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.psychoMode.price", 300,
                WatheExtendedServerConfig::getPsychoModePrice,
                value -> { WatheExtendedServerConfig.setPsychoModePrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonVial.price", 100,
                WatheExtendedServerConfig::getPoisonVialPrice,
                value -> { WatheExtendedServerConfig.setPoisonVialPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.scorpion.price", 50,
                WatheExtendedServerConfig::getScorpionPrice,
                value -> { WatheExtendedServerConfig.setScorpionPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.firecracker.price", 10,
                WatheExtendedServerConfig::getFirecrackerPrice,
                value -> { WatheExtendedServerConfig.setFirecrackerPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.lockpick.price", 50,
                WatheExtendedServerConfig::getLockpickPrice,
                value -> { WatheExtendedServerConfig.setLockpickPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.crowbar.price", 25,
                WatheExtendedServerConfig::getCrowbarPrice,
                value -> { WatheExtendedServerConfig.setCrowbarPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.bodyBag.price", 200,
                WatheExtendedServerConfig::getBodyBagPrice,
                value -> { WatheExtendedServerConfig.setBodyBagPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blackout.price", 200,
                WatheExtendedServerConfig::getBlackoutPrice,
                value -> { WatheExtendedServerConfig.setBlackoutPrice(value); applyAll(); }));
        ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.note.price", 10,
                WatheExtendedServerConfig::getNotePrice,
                value -> { WatheExtendedServerConfig.setNotePrice(value); applyAll(); }));

        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            register(() -> {
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "CookPanPrice", WatheExtendedServerConfig.panPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "PhysicianPillPrice", WatheExtendedServerConfig.pillPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "LicensedVillainRevolverPrice", WatheExtendedServerConfig.revolverPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "DrugmakerPoisonInjectorPrice", WatheExtendedServerConfig.poisonInjectorPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "DrugmakerBlowgunPrice", WatheExtendedServerConfig.blowgunPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "KidnapperKnockoutDrugPrice", WatheExtendedServerConfig.knockoutDrugPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "TechnicianCaptureDevicePrice", WatheExtendedServerConfig.captureDevicePrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "TechnicianWrenchPrice", WatheExtendedServerConfig.wrenchPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "TechnicianPowerRestorationPrice", WatheExtendedServerConfig.powerRestorationPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "HackerRefreshWeaponCooldownPrice", WatheExtendedServerConfig.refreshWeaponCooldownPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "HackerRefreshAbilityCooldownPrice", WatheExtendedServerConfig.refreshAbilityCooldownPrice);
                setConfigPrice("org.BsXinQin.kinswathe.KinsWatheConfig", "HackerRefreshPotionEffectPrice", WatheExtendedServerConfig.refreshPotionEffectPrice);
            });

            registerWorldApplier(world -> {
                String cls = "org.BsXinQin.kinswathe.component.ConfigWorldComponent";
                setWorldComponentField(world, cls, "CookPanPrice", WatheExtendedServerConfig.panPrice);
                setWorldComponentField(world, cls, "PhysicianPillPrice", WatheExtendedServerConfig.pillPrice);
                setWorldComponentField(world, cls, "LicensedVillainRevolverPrice", WatheExtendedServerConfig.revolverPrice);
                setWorldComponentField(world, cls, "DrugmakerPoisonInjectorPrice", WatheExtendedServerConfig.poisonInjectorPrice);
                setWorldComponentField(world, cls, "DrugmakerBlowgunPrice", WatheExtendedServerConfig.blowgunPrice);
                setWorldComponentField(world, cls, "KidnapperKnockoutDrugPrice", WatheExtendedServerConfig.knockoutDrugPrice);
                setWorldComponentField(world, cls, "TechnicianCaptureDevicePrice", WatheExtendedServerConfig.captureDevicePrice);
                setWorldComponentField(world, cls, "TechnicianWrenchPrice", WatheExtendedServerConfig.wrenchPrice);
                setWorldComponentField(world, cls, "TechnicianPowerRestorationPrice", WatheExtendedServerConfig.powerRestorationPrice);
                setWorldComponentField(world, cls, "HackerRefreshWeaponCooldownPrice", WatheExtendedServerConfig.refreshWeaponCooldownPrice);
                setWorldComponentField(world, cls, "HackerRefreshAbilityCooldownPrice", WatheExtendedServerConfig.refreshAbilityCooldownPrice);
                setWorldComponentField(world, cls, "HackerRefreshPotionEffectPrice", WatheExtendedServerConfig.refreshPotionEffectPrice);
            });

            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.huntingKnife.price", 100,
                    WatheExtendedServerConfig::getHuntingKnifePrice,
                    value -> { WatheExtendedServerConfig.setHuntingKnifePrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.poisonInjector.price", 125,
                    WatheExtendedServerConfig::getPoisonInjectorPrice,
                    value -> { WatheExtendedServerConfig.setPoisonInjectorPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.blowgun.price", 175,
                    WatheExtendedServerConfig::getBlowgunPrice,
                    value -> { WatheExtendedServerConfig.setBlowgunPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.knockoutDrug.price", 75,
                    WatheExtendedServerConfig::getKnockoutDrugPrice,
                    value -> { WatheExtendedServerConfig.setKnockoutDrugPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pan.price", 250,
                    WatheExtendedServerConfig::getPanPrice,
                    value -> { WatheExtendedServerConfig.setPanPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.pill.price", 300,
                    WatheExtendedServerConfig::getPillPrice,
                    value -> { WatheExtendedServerConfig.setPillPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.captureDevice.price", 100,
                    WatheExtendedServerConfig::getCaptureDevicePrice,
                    value -> { WatheExtendedServerConfig.setCaptureDevicePrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.wrench.price", 100,
                    WatheExtendedServerConfig::getWrenchPrice,
                    value -> { WatheExtendedServerConfig.setWrenchPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.powerRestoration.price", 300,
                    WatheExtendedServerConfig::getPowerRestorationPrice,
                    value -> { WatheExtendedServerConfig.setPowerRestorationPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshWeaponCooldown.price", 300,
                    WatheExtendedServerConfig::getRefreshWeaponCooldownPrice,
                    value -> { WatheExtendedServerConfig.setRefreshWeaponCooldownPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshAbilityCooldown.price", 400,
                    WatheExtendedServerConfig::getRefreshAbilityCooldownPrice,
                    value -> { WatheExtendedServerConfig.setRefreshAbilityCooldownPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("watheextended.refreshPotionEffect.price", 200,
                    WatheExtendedServerConfig::getRefreshPotionEffectPrice,
                    value -> { WatheExtendedServerConfig.setRefreshPotionEffectPrice(value); applyAll(); }));
        }

        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            register(() -> {
                setConfigPrice("org.agmas.noellesroles.config.NoellesRolesConfig", "defenseVialPrice", WatheExtendedServerConfig.defenseVialPrice);
                setConfigPrice("org.agmas.noellesroles.config.NoellesRolesConfig", "roleMinePrice", WatheExtendedServerConfig.roleMinePrice);
                setShopListPrice(org.agmas.noellesroles.Noellesroles.FRAMING_ROLES_SHOP, org.agmas.noellesroles.ModItems.DELUSION_VIAL, WatheExtendedServerConfig.delusionVialPrice);
            });

            ServerConfig.register(ServerConfig.Entry.globalInt("noellesroles.defenseVialPrice", 200,
                    WatheExtendedServerConfig::getDefenseVialPrice,
                    value -> { WatheExtendedServerConfig.setDefenseVialPrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("noellesroles.roleMinePrice", 100,
                    WatheExtendedServerConfig::getRoleMinePrice,
                    value -> { WatheExtendedServerConfig.setRoleMinePrice(value); applyAll(); }));
            ServerConfig.register(ServerConfig.Entry.globalInt("noellesroles.delusionVialPrice", 30,
                    WatheExtendedServerConfig::getDelusionVialPrice,
                    value -> { WatheExtendedServerConfig.setDelusionVialPrice(value); applyAll(); }));
        }

        if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
            register(() -> setShopListPrice(org.aussiebox.starexpress.StarryExpressConstants.MUZZLER_SHOP, org.aussiebox.starexpress.item.StarryExpressItems.TAPE, WatheExtendedServerConfig.tapePrice));

            ServerConfig.register(ServerConfig.Entry.globalInt("starexpress.tape.price", 75,
                    WatheExtendedServerConfig::getTapePrice,
                    value -> { WatheExtendedServerConfig.setTapePrice(value); applyAll(); }));
        }
    }

    public static void register(Runnable applier) {
        appliers.add(applier);
    }

    public static void registerWorldApplier(Consumer<World> applier) {
        worldAppliers.add(applier);
    }

    public static void applyAll() {
        for (Runnable applier : appliers) {
            try { applier.run(); } catch (Throwable ignored) {}
        }
    }

    public static void applyWorldAll(World world) {
        applyAll();
        for (Consumer<World> applier : worldAppliers) {
            try { applier.accept(world); } catch (Throwable ignored) {}
        }
    }

    public static void setWatheShopPrice(Item item, int price) {
        for (ShopEntry entry : GameConstants.SHOP_ENTRIES) {
            if (entry.stack().getItem() == item) {
                ((ShopEntryAccessor) entry).watheextended$setPrice(price);
                return;
            }
        }
    }

    public static void setShopListPrice(List<? extends ShopEntry> shop, Item item, int price) {
        for (ShopEntry entry : shop) {
            if (entry.stack().getItem() == item) {
                ((ShopEntryAccessor) entry).watheextended$setPrice(price);
                return;
            }
        }
    }

    public static void setConfigPrice(String configClass, String field, int price) {
        try {
            Class<?> cls = Class.forName(configClass);
            Object handler = cls.getField("HANDLER").get(null);
            Object cfg = handler.getClass().getMethod("instance").invoke(handler);
            cfg.getClass().getField(field).set(cfg, price);
        } catch (Throwable ignored) {}
    }

    public static void setWorldComponentField(World world, String componentClass, String field, int value) {
        try {
            Class<?> cls = Class.forName(componentClass);
            Object key = cls.getField("KEY").get(null);
            Object comp = key.getClass().getMethod("get", Object.class).invoke(key, world);
            comp.getClass().getField(field).set(comp, value);
        } catch (Throwable ignored) {}
    }
}
