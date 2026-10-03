package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.network.ServerConfig;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class ItemCooldowns {

  private static final List<Runnable> appliers = new ArrayList<>();

  private ItemCooldowns() {}

  public static void registerAll() {
    register(
        () -> {
          setCooldown(WatheItems.KNIFE, WatheExtendedServerConfig.knifeCooldown);
          setCooldown(WatheItems.REVOLVER, WatheExtendedServerConfig.revolverCooldown);
          setCooldown(WatheItems.PSYCHO_MODE, WatheExtendedServerConfig.psychoModeCooldown);
          setCooldown(WatheItems.LOCKPICK, WatheExtendedServerConfig.lockpickCooldown);
          setCooldown(WatheItems.CROWBAR, WatheExtendedServerConfig.crowbarCooldown);
          setCooldown(WatheItems.BODY_BAG, WatheExtendedServerConfig.bodyBagCooldown);
          setCooldown(WatheItems.BLACKOUT, WatheExtendedServerConfig.blackoutCooldown);
        });

    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.grenade.cooldown",
            90,
            () -> WatheExtendedServerConfig.grenadeCooldown,
            value -> WatheExtendedServerConfig.set("grenadeCooldown", value)));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.knife.cooldown",
            60,
            () -> WatheExtendedServerConfig.knifeCooldown,
            value -> {
              WatheExtendedServerConfig.set("knifeCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.revolver.cooldown",
            10,
            () -> WatheExtendedServerConfig.revolverCooldown,
            value -> {
              WatheExtendedServerConfig.set("revolverCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.psychoMode.cooldown",
            300,
            () -> WatheExtendedServerConfig.psychoModeCooldown,
            value -> {
              WatheExtendedServerConfig.set("psychoModeCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.lockpick.cooldown",
            180,
            () -> WatheExtendedServerConfig.lockpickCooldown,
            value -> {
              WatheExtendedServerConfig.set("lockpickCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.crowbar.cooldown",
            10,
            () -> WatheExtendedServerConfig.crowbarCooldown,
            value -> {
              WatheExtendedServerConfig.set("crowbarCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.bodyBag.cooldown",
            300,
            () -> WatheExtendedServerConfig.bodyBagCooldown,
            value -> {
              WatheExtendedServerConfig.set("bodyBagCooldown", value);
              applyAll();
            }));
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            "watheextended.blackout.cooldown",
            300,
            () -> WatheExtendedServerConfig.blackoutCooldown,
            value -> {
              WatheExtendedServerConfig.set("blackoutCooldown", value);
              applyAll();
            }));

    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      register(
          () -> {
            setCooldown(
                "kinswathe",
                "sulfuric_acid_barrel",
                WatheExtendedServerConfig.sulfuricAcidBarrelCooldown);
            setCooldown(
                "kinswathe", "hunting_knife", WatheExtendedServerConfig.huntingKnifeCooldown);
            setCooldown("kinswathe", "medical_kit", WatheExtendedServerConfig.medicalKitCooldown);
            setCooldown("kinswathe", "pan", WatheExtendedServerConfig.panCooldown);
            setCooldown(
                "kinswathe", "poison_injector", WatheExtendedServerConfig.poisonInjectorCooldown);
            setCooldown("kinswathe", "pill", WatheExtendedServerConfig.pillCooldown);
            setCooldown("kinswathe", "blowgun", WatheExtendedServerConfig.blowgunCooldown);
            setCooldown(
                "kinswathe", "knockout_drug", WatheExtendedServerConfig.knockoutDrugCooldown);
            setCooldown(
                "kinswathe", "capture_device", WatheExtendedServerConfig.captureDeviceCooldown);
            setCooldown("kinswathe", "wrench", WatheExtendedServerConfig.wrenchCooldown);
            setCooldown(
                "kinswathe",
                "icon_power_restoration",
                WatheExtendedServerConfig.powerRestorationCooldown);
            setCooldown(
                "kinswathe",
                "icon_weapon_cooldown_refresh",
                WatheExtendedServerConfig.refreshWeaponCooldownCooldown);
            setCooldown(
                "kinswathe",
                "icon_ability_cooldown_refresh",
                WatheExtendedServerConfig.refreshAbilityCooldownCooldown);
            setCooldown(
                "kinswathe",
                "icon_potion_effect_refresh",
                WatheExtendedServerConfig.refreshPotionEffectCooldown);
          });

      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.sulfuricAcidBarrel.cooldown",
              60,
              () -> WatheExtendedServerConfig.sulfuricAcidBarrelCooldown,
              value -> {
                WatheExtendedServerConfig.set("sulfuricAcidBarrelCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.huntingKnife.cooldown",
              45,
              () -> WatheExtendedServerConfig.huntingKnifeCooldown,
              value -> {
                WatheExtendedServerConfig.set("huntingKnifeCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.medicalKit.cooldown",
              60,
              () -> WatheExtendedServerConfig.medicalKitCooldown,
              value -> {
                WatheExtendedServerConfig.set("medicalKitCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.pan.cooldown",
              45,
              () -> WatheExtendedServerConfig.panCooldown,
              value -> {
                WatheExtendedServerConfig.set("panCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.poisonInjector.cooldown",
              60,
              () -> WatheExtendedServerConfig.poisonInjectorCooldown,
              value -> {
                WatheExtendedServerConfig.set("poisonInjectorCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.pill.cooldown",
              180,
              () -> WatheExtendedServerConfig.pillCooldown,
              value -> {
                WatheExtendedServerConfig.set("pillCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.blowgun.cooldown",
              60,
              () -> WatheExtendedServerConfig.blowgunCooldown,
              value -> {
                WatheExtendedServerConfig.set("blowgunCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.knockoutDrug.cooldown",
              60,
              () -> WatheExtendedServerConfig.knockoutDrugCooldown,
              value -> {
                WatheExtendedServerConfig.set("knockoutDrugCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.captureDevice.cooldown",
              60,
              () -> WatheExtendedServerConfig.captureDeviceCooldown,
              value -> {
                WatheExtendedServerConfig.set("captureDeviceCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.wrench.cooldown",
              120,
              () -> WatheExtendedServerConfig.wrenchCooldown,
              value -> {
                WatheExtendedServerConfig.set("wrenchCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.powerRestoration.cooldown",
              180,
              () -> WatheExtendedServerConfig.powerRestorationCooldown,
              value -> {
                WatheExtendedServerConfig.set("powerRestorationCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshWeaponCooldown.cooldown",
              180,
              () -> WatheExtendedServerConfig.refreshWeaponCooldownCooldown,
              value -> {
                WatheExtendedServerConfig.set("refreshWeaponCooldownCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshAbilityCooldown.cooldown",
              300,
              () -> WatheExtendedServerConfig.refreshAbilityCooldownCooldown,
              value -> {
                WatheExtendedServerConfig.set("refreshAbilityCooldownCooldown", value);
                applyAll();
              }));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "watheextended.refreshPotionEffect.cooldown",
              180,
              () -> WatheExtendedServerConfig.refreshPotionEffectCooldown,
              value -> {
                WatheExtendedServerConfig.set("refreshPotionEffectCooldown", value);
                applyAll();
              }));
    }

    if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
      register(
          () ->
              cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper
                  .applyMuzzlerTapeCooldown(WatheExtendedServerConfig.tapeCooldown));

      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "starexpress.tapeCooldown",
              20,
              () -> WatheExtendedServerConfig.tapeCooldown,
              value -> {
                WatheExtendedServerConfig.set("tapeCooldown", value);
                applyAll();
              }));
    }

    if (ConfigHelper.isLoaded()) {
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "stupidexpress.jerryCan.cooldown",
              0,
              () -> WatheExtendedServerConfig.jerryCanCooldown,
              value -> WatheExtendedServerConfig.set("jerryCanCooldown", value)));
      ServerConfig.register(
          ServerConfig.Entry.globalInt(
              "stupidexpress.lighter.cooldown",
              0,
              () -> WatheExtendedServerConfig.lighterCooldown,
              value -> WatheExtendedServerConfig.set("lighterCooldown", value)));
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
