package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import cat.rezelyn.watheextended.mixin.game.shop.ShopEntryAccessor;
import cat.rezelyn.watheextended.network.ServerConfig;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public final class ItemPrices {

  private static final List<Runnable> appliers = new ArrayList<>();
  private static final List<Consumer<World>> worldAppliers = new ArrayList<>();

  private ItemPrices() {}

  public static void registerAll() {
    register(
        () -> {
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

    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.knife.price",
            100,
            () -> WatheExtendedServerConfig.knifePrice,
            value -> {
              WatheExtendedServerConfig.set("knifePrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.revolver.price",
            300,
            () -> WatheExtendedServerConfig.revolverPrice,
            value -> {
              WatheExtendedServerConfig.set("revolverPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.grenade.price",
            350,
            () -> WatheExtendedServerConfig.grenadePrice,
            value -> {
              WatheExtendedServerConfig.set("grenadePrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.psychoMode.price",
            300,
            () -> WatheExtendedServerConfig.psychoModePrice,
            value -> {
              WatheExtendedServerConfig.set("psychoModePrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.poisonVial.price",
            100,
            () -> WatheExtendedServerConfig.poisonVialPrice,
            value -> {
              WatheExtendedServerConfig.set("poisonVialPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.scorpion.price",
            50,
            () -> WatheExtendedServerConfig.scorpionPrice,
            value -> {
              WatheExtendedServerConfig.set("scorpionPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.firecracker.price",
            10,
            () -> WatheExtendedServerConfig.firecrackerPrice,
            value -> {
              WatheExtendedServerConfig.set("firecrackerPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.lockpick.price",
            50,
            () -> WatheExtendedServerConfig.lockpickPrice,
            value -> {
              WatheExtendedServerConfig.set("lockpickPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.crowbar.price",
            25,
            () -> WatheExtendedServerConfig.crowbarPrice,
            value -> {
              WatheExtendedServerConfig.set("crowbarPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.bodyBag.price",
            200,
            () -> WatheExtendedServerConfig.bodyBagPrice,
            value -> {
              WatheExtendedServerConfig.set("bodyBagPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.blackout.price",
            200,
            () -> WatheExtendedServerConfig.blackoutPrice,
            value -> {
              WatheExtendedServerConfig.set("blackoutPrice", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.note.price",
            10,
            () -> WatheExtendedServerConfig.notePrice,
            value -> {
              WatheExtendedServerConfig.set("notePrice", value);
              applyAll();
            }));

    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      register(
          () -> {
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "CleanerGetCoins",
                WatheExtendedServerConfig.cleanerAcidBarrelCoinBonusEnabled
                    ? WatheExtendedServerConfig.cleanerAcidBarrelCoins
                    : 0);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "CookPanPrice",
                WatheExtendedServerConfig.panPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "PhysicianPillPrice",
                WatheExtendedServerConfig.pillPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "LicensedVillainRevolverPrice",
                WatheExtendedServerConfig.revolverPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "DrugmakerPoisonInjectorPrice",
                WatheExtendedServerConfig.poisonInjectorPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "DrugmakerBlowgunPrice",
                WatheExtendedServerConfig.blowgunPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "KidnapperKnockoutDrugPrice",
                WatheExtendedServerConfig.knockoutDrugPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "TechnicianCaptureDevicePrice",
                WatheExtendedServerConfig.captureDevicePrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "TechnicianWrenchPrice",
                WatheExtendedServerConfig.wrenchPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "TechnicianPowerRestorationPrice",
                WatheExtendedServerConfig.powerRestorationPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "HackerRefreshWeaponCooldownPrice",
                WatheExtendedServerConfig.refreshWeaponCooldownPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "HackerRefreshAbilityCooldownPrice",
                WatheExtendedServerConfig.refreshAbilityCooldownPrice);
            setConfigPrice(
                "org.BsXinQin.kinswathe.KinsWatheConfig",
                "HackerRefreshPotionEffectPrice",
                WatheExtendedServerConfig.refreshPotionEffectPrice);
          });

      registerWorldApplier(
          world -> {
            String cls = "org.BsXinQin.kinswathe.component.ConfigWorldComponent";
            setWorldComponentField(world, cls, "CookPanPrice", WatheExtendedServerConfig.panPrice);
            setWorldComponentField(
                world, cls, "PhysicianPillPrice", WatheExtendedServerConfig.pillPrice);
            setWorldComponentField(
                world,
                cls,
                "LicensedVillainRevolverPrice",
                WatheExtendedServerConfig.revolverPrice);
            setWorldComponentField(
                world,
                cls,
                "DrugmakerPoisonInjectorPrice",
                WatheExtendedServerConfig.poisonInjectorPrice);
            setWorldComponentField(
                world, cls, "DrugmakerBlowgunPrice", WatheExtendedServerConfig.blowgunPrice);
            setWorldComponentField(
                world,
                cls,
                "KidnapperKnockoutDrugPrice",
                WatheExtendedServerConfig.knockoutDrugPrice);
            setWorldComponentField(
                world,
                cls,
                "TechnicianCaptureDevicePrice",
                WatheExtendedServerConfig.captureDevicePrice);
            setWorldComponentField(
                world, cls, "TechnicianWrenchPrice", WatheExtendedServerConfig.wrenchPrice);
            setWorldComponentField(
                world,
                cls,
                "TechnicianPowerRestorationPrice",
                WatheExtendedServerConfig.powerRestorationPrice);
            setWorldComponentField(
                world,
                cls,
                "HackerRefreshWeaponCooldownPrice",
                WatheExtendedServerConfig.refreshWeaponCooldownPrice);
            setWorldComponentField(
                world,
                cls,
                "HackerRefreshAbilityCooldownPrice",
                WatheExtendedServerConfig.refreshAbilityCooldownPrice);
            setWorldComponentField(
                world,
                cls,
                "HackerRefreshPotionEffectPrice",
                WatheExtendedServerConfig.refreshPotionEffectPrice);
          });

      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.huntingKnife.price",
              100,
              () -> WatheExtendedServerConfig.huntingKnifePrice,
              value -> {
                WatheExtendedServerConfig.set("huntingKnifePrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.poisonInjector.price",
              125,
              () -> WatheExtendedServerConfig.poisonInjectorPrice,
              value -> {
                WatheExtendedServerConfig.set("poisonInjectorPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.blowgun.price",
              175,
              () -> WatheExtendedServerConfig.blowgunPrice,
              value -> {
                WatheExtendedServerConfig.set("blowgunPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.knockoutDrug.price",
              75,
              () -> WatheExtendedServerConfig.knockoutDrugPrice,
              value -> {
                WatheExtendedServerConfig.set("knockoutDrugPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.pan.price",
              250,
              () -> WatheExtendedServerConfig.panPrice,
              value -> {
                WatheExtendedServerConfig.set("panPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.pill.price",
              300,
              () -> WatheExtendedServerConfig.pillPrice,
              value -> {
                WatheExtendedServerConfig.set("pillPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.captureDevice.price",
              100,
              () -> WatheExtendedServerConfig.captureDevicePrice,
              value -> {
                WatheExtendedServerConfig.set("captureDevicePrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.wrench.price",
              100,
              () -> WatheExtendedServerConfig.wrenchPrice,
              value -> {
                WatheExtendedServerConfig.set("wrenchPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.powerRestoration.price",
              300,
              () -> WatheExtendedServerConfig.powerRestorationPrice,
              value -> {
                WatheExtendedServerConfig.set("powerRestorationPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshWeaponCooldown.price",
              300,
              () -> WatheExtendedServerConfig.refreshWeaponCooldownPrice,
              value -> {
                WatheExtendedServerConfig.set("refreshWeaponCooldownPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshAbilityCooldown.price",
              400,
              () -> WatheExtendedServerConfig.refreshAbilityCooldownPrice,
              value -> {
                WatheExtendedServerConfig.set("refreshAbilityCooldownPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshPotionEffect.price",
              200,
              () -> WatheExtendedServerConfig.refreshPotionEffectPrice,
              value -> {
                WatheExtendedServerConfig.set("refreshPotionEffectPrice", value);
                applyAll();
              }));
    }

    if (ConfigHelper.isLoaded()) {
      register(
          () -> {
            setConfigPrice(
                "org.agmas.noellesroles.config.NoellesRolesConfig",
                "defenseVialPrice",
                WatheExtendedServerConfig.defenseVialPrice);
            setConfigPrice(
                "org.agmas.noellesroles.config.NoellesRolesConfig",
                "roleMinePrice",
                WatheExtendedServerConfig.roleMinePrice);
            setShopListPrice(
                org.agmas.noellesroles.Noellesroles.FRAMING_ROLES_SHOP,
                org.agmas.noellesroles.ModItems.DELUSION_VIAL,
                WatheExtendedServerConfig.delusionVialPrice);
          });

      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "noellesroles.defenseVialPrice",
              200,
              () -> WatheExtendedServerConfig.defenseVialPrice,
              value -> {
                WatheExtendedServerConfig.set("defenseVialPrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "noellesroles.roleMinePrice",
              100,
              () -> WatheExtendedServerConfig.roleMinePrice,
              value -> {
                WatheExtendedServerConfig.set("roleMinePrice", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "noellesroles.delusionVialPrice",
              30,
              () -> WatheExtendedServerConfig.delusionVialPrice,
              value -> {
                WatheExtendedServerConfig.set("delusionVialPrice", value);
                applyAll();
              }));
    }

    if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
      register(
          () ->
              setShopListPrice(
                  org.aussiebox.starexpress.StarryExpressConstants.MUZZLER_SHOP,
                  org.aussiebox.starexpress.item.StarryExpressItems.TAPE,
                  WatheExtendedServerConfig.tapePrice));

      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "starexpress.tape.price",
              75,
              () -> WatheExtendedServerConfig.tapePrice,
              value -> {
                WatheExtendedServerConfig.set("tapePrice", value);
                applyAll();
              }));
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
      try {
        applier.run();
      } catch (Throwable ignored) {
      }
    }
  }

  public static void applyWorldAll(World world) {
    applyAll();
    for (Consumer<World> applier : worldAppliers) {
      try {
        applier.accept(world);
      } catch (Throwable ignored) {
      }
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
    } catch (Throwable ignored) {
    }
  }

  public static void setWorldComponentField(
      World world, String componentClass, String field, int value) {
    try {
      Class<?> cls = Class.forName(componentClass);
      Object key = cls.getField("KEY").get(null);
      Object comp = key.getClass().getMethod("get", Object.class).invoke(key, world);
      comp.getClass().getField(field).set(comp, value);
    } catch (Throwable ignored) {
    }
  }
}
