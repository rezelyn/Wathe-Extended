package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.index.*;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import java.util.function.*;

/**
 * Exposes every setting of this mod through {@link ServerConfig}
 *
 * <p>Two kinds of entries exist:
 *
 * <ul>
 *   <li><b>world</b> entries live in {@link WatheExtendedWorldComponent} and are stored per world
 *   <li><b>global</b> entries live in {@link WatheExtendedServerConfig} and are stored in the
 *       config file
 * </ul>
 *
 * Registry keys are part of the wire/preset format, so renaming one breaks saved presets
 */
public final class ConfigEntries {

  private ConfigEntries() {}

  public static void register() {
    registerWorldEntries();
    registerGlobalEntries();

    WatheExtendedServerConfig.ROLEPLAY_ITEM_DEFAULTS.forEach(
        (id, def) ->
            ServerConfig.register(
                ServerConfig.Entry.globalBool(
                    "watheextended.roleplayItems." + id,
                    def,
                    () -> WatheExtendedServerConfig.isRoleplayItemEnabled(id),
                    value -> WatheExtendedServerConfig.setRoleplayItemEnabled(id, value))));
  }

  /** Registers the per-world entries (map setup and game rule toggles) */
  private static void registerWorldEntries() {
    // Map Effect
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.gameMode",
            "MODDED_MURDER",
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameModeSelection(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setConfiguredGameMode(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.gameTime",
            "NIGHT",
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameTimeOfDay(),
            (world, value) -> WatheExtendedWorldComponent.KEY.get(world).setGameTimeOfDay(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldBool(
            "watheextended.map.generic",
            false,
            world -> WatheExtendedWorldComponent.KEY.get(world).isGenericMapEffectEnabled(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setGenericMapEffectEnabled(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.lobbyTime",
            "DAY",
            world -> WatheExtendedWorldComponent.KEY.get(world).getLobbyTimeOfDay(),
            (world, value) -> WatheExtendedWorldComponent.KEY.get(world).setLobbyTimeOfDay(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldInt(
            "watheextended.map.duration",
            10,
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameDurationMinutes(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setGameDurationMinutes(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldInt(
            "watheextended.secretMurderChance",
            10,
            world ->
                Math.round(GameWorldComponent.KEY.get(world).getSecretMurderRoundChance() * 100),
            (world, value) ->
                GameWorldComponent.KEY
                    .get(world)
                    .setSecretMurderRoundChance(Math.clamp(value, 0, 100) / 100.0f)));

    // Gamerules
    registerWorldFlag(
        "watheextended.playerCollisions",
        true,
        WatheExtendedWorldComponent::isPlayerCollisionsEnabled,
        WatheExtendedWorldComponent::setPlayerCollisionsEnabled);
    registerWorldFlag(
        "watheextended.rtpEnabled",
        false,
        WatheExtendedWorldComponent::isRtpEnabled,
        WatheExtendedWorldComponent::setRtpEnabled);
    registerWorldFlag(
        "watheextended.blockProtection",
        false,
        WatheExtendedWorldComponent::isBlockInteractionsProtected,
        WatheExtendedWorldComponent::setBlockInteractionsProtected);
    registerWorldFlag(
        "watheextended.itemBoundsCheck",
        true,
        WatheExtendedWorldComponent::isItemBoundsCheckEnabled,
        WatheExtendedWorldComponent::setItemBoundsCheckEnabled);
    registerWorldFlag(
        "watheextended.forbiddenLovers",
        false,
        WatheExtendedWorldComponent::isForbiddenLoversEnabled,
        WatheExtendedWorldComponent::setForbiddenLoversEnabled);
  }

  /** Registers the global entries, grouped by feature */
  private static void registerGlobalEntries() {
    // General
    registerFloat(
        "watheextended.forbiddenLovers.chance",
        0.25f,
        () -> WatheExtendedServerConfig.forbiddenLoversChance,
        "forbiddenLoversChance");
    registerBool(
        "watheextended.suppressAbilityVfxSfx",
        false,
        () -> WatheExtendedServerConfig.suppressAbilityVfxSfx,
        "suppressAbilityVfxSfx");
    registerString(
        "watheextended.jumpMode", "LOBBY", () -> WatheExtendedServerConfig.jumpMode, "jumpMode");
    registerString(
        "watheextended.shootInnocentPunishmentMode",
        "DEFAULT",
        () -> WatheExtendedServerConfig.shootInnocentPunishmentMode,
        "shootInnocentPunishmentMode");
    registerInt(
        "watheextended.killIncreaseTime",
        60,
        () -> WatheExtendedServerConfig.killIncreaseTime,
        "killIncreaseTime");

    // Introverted Modifier
    registerInt(
        "watheextended.introverted.crowdCount",
        3,
        () -> WatheExtendedServerConfig.introvertedCrowdCount,
        "introvertedCrowdCount");
    registerFloat(
        "watheextended.introverted.crowdRange",
        5.0f,
        () -> WatheExtendedServerConfig.introvertedCrowdRange,
        "introvertedCrowdRange");
    registerFloat(
        "watheextended.introverted.crowdDrainMultiplier",
        2.0f,
        () -> WatheExtendedServerConfig.introvertedCrowdDrainMultiplier,
        "introvertedCrowdDrainMultiplier");
    registerFloat(
        "watheextended.introverted.aloneDrainMultiplier",
        0.5f,
        () -> WatheExtendedServerConfig.introvertedAloneDrainMultiplier,
        "introvertedAloneDrainMultiplier");

    // Taxed Modifier
    registerFloat(
        "watheextended.taxed.coinReduction",
        0.50f,
        () -> WatheExtendedServerConfig.taxedCoinReduction,
        "taxedCoinReduction");
    registerInt(
        "watheextended.taxed.killThreshold",
        1,
        () -> WatheExtendedServerConfig.taxedKillThreshold,
        "taxedKillThreshold");
    registerInt(
        "watheextended.taxed.killWindowSeconds",
        60,
        () -> WatheExtendedServerConfig.taxedKillWindowSeconds,
        "taxedKillWindowSeconds");

    // Adaptive Modifier
    registerFloat(
        "watheextended.adaptive.penaltyReduction",
        0.50f,
        () -> WatheExtendedServerConfig.adaptivePenaltyReduction,
        "adaptivePenaltyReduction");
    registerFloat(
        "watheextended.adaptive.bonusMultiplier",
        0.50f,
        () -> WatheExtendedServerConfig.adaptiveBonusMultiplier,
        "adaptiveBonusMultiplier");

    // Cleaner
    registerInt(
        "watheextended.cleaner.playerLimit",
        10,
        () -> WatheExtendedServerConfig.cleanerPlayerLimit,
        "cleanerPlayerLimit");
    registerInt(
        "watheextended.cleaner.acidBarrelCoins",
        50,
        () -> WatheExtendedServerConfig.cleanerAcidBarrelCoins,
        "cleanerAcidBarrelCoins",
        ItemPrices::applyAll);
    registerBool(
        "watheextended.cleaner.acidBarrelCoinBonusEnabled",
        false,
        () -> WatheExtendedServerConfig.cleanerAcidBarrelCoinBonusEnabled,
        "cleanerAcidBarrelCoinBonusEnabled",
        ItemPrices::applyAll);

    // Morphling
    registerBool(
        "watheextended.morphling.canCancelAbility",
        true,
        () -> WatheExtendedServerConfig.morphlingCanCancelAbility,
        "morphlingCanCancelAbility");
    registerInt(
        "watheextended.morphling.abilityDuration",
        35,
        () -> WatheExtendedServerConfig.morphlingAbilityDuration,
        "morphlingAbilityDuration");
    registerInt(
        "watheextended.morphling.abilityCooldown",
        60,
        () -> WatheExtendedServerConfig.morphlingAbilityCooldown,
        "morphlingAbilityCooldown");

    // Phantom
    registerBool(
        "watheextended.phantom.canCancelAbility",
        true,
        () -> WatheExtendedServerConfig.phantomCanCancelAbility,
        "phantomCanCancelAbility");
    registerInt(
        "watheextended.phantom.abilityDuration",
        30,
        () -> WatheExtendedServerConfig.phantomAbilityDuration,
        "phantomAbilityDuration");
    registerInt(
        "watheextended.phantom.abilityCooldown",
        0,
        () -> WatheExtendedServerConfig.phantomAbilityCooldown,
        "phantomAbilityCooldown");

    // Mood
    registerBool(
        "watheextended.mood.depressedAbilityDisableEnabled",
        false,
        () -> WatheExtendedServerConfig.moodDisableAbilityWhenDepressed,
        "depressedAbilityDisableEnabled");
    registerInt(
        "watheextended.mood.depressedAbilityDisableTime",
        30,
        () -> WatheExtendedServerConfig.moodDisableAbilityWhenDepressedDelay,
        "depressedAbilityDisableTime");
    registerBool(
        "watheextended.mood.depressedPreventSprintEnabled",
        true,
        () -> WatheExtendedServerConfig.moodDisableSprintingWhenDepressed,
        "depressedPreventSprintEnabled");

    // Instinct
    registerFloat(
        "watheextended.instinct.capacity",
        1200.0f,
        () -> WatheExtendedServerConfig.instinctCapacity,
        "instinctCapacity");
    registerFloat(
        "watheextended.instinct.drainRate",
        60.0f,
        () -> WatheExtendedServerConfig.instinctDrainRate,
        "instinctDrainRate");
    registerFloat(
        "watheextended.instinct.reloadRate",
        20.0f,
        () -> WatheExtendedServerConfig.instinctReloadRate,
        "instinctReloadRate");

    // Passive Income Balance
    registerInt(
        "watheextended.balance.basePassiveIncome",
        5,
        () -> WatheExtendedServerConfig.basePassiveIncome,
        "basePassiveIncome");
    registerBool(
        "watheextended.balance.adjustPassiveIncome",
        false,
        () -> WatheExtendedServerConfig.adjustPassiveIncome,
        "adjustPassiveIncome");
    registerInt(
        "watheextended.balance.maxPassiveIncomeDistance",
        10,
        () -> WatheExtendedServerConfig.maxPassiveIncomeDistance,
        "maxPassiveIncomeDistance");
    registerInt(
        "watheextended.balance.minPassiveIncome",
        0,
        () -> WatheExtendedServerConfig.minPassiveIncome,
        "minPassiveIncome");

    // Last Stand
    registerBool(
        "watheextended.lastStand.enabled",
        false,
        () -> WatheExtendedServerConfig.lastStandEnabled,
        "lastStandEnabled");
    registerInt(
        "watheextended.lastStand.cooldown",
        30,
        () -> WatheExtendedServerConfig.lastStandCooldown,
        "lastStandCooldown");

    // Notifications
    registerBool(
        "watheextended.dreamer.imprintNotificationEnabled",
        true,
        () -> WatheExtendedServerConfig.dreamerImprintNotificationEnabled,
        "dreamerImprintNotificationEnabled");
    registerBool(
        "watheextended.arsonist.dousedNotificationEnabled",
        true,
        () -> WatheExtendedServerConfig.arsonistDousedNotificationEnabled,
        "arsonistDousedNotificationEnabled");
    registerInt(
        "watheextended.arsonist.dousedNotificationDelay",
        10,
        () -> WatheExtendedServerConfig.arsonistDousedNotificationDelay,
        "arsonistDousedNotificationDelay");
  }

  /**
   * Registers a per-world boolean entry backed by {@link WatheExtendedWorldComponent}. If the
   * component cannot be read or written (e.g. the world is not fully set up), the default is
   * reported and writes are ignored
   *
   * @param key registry key
   * @param def default value, also returned when the component is unavailable
   * @param getter reads the flag from the component
   * @param setter writes the flag to the component
   */
  private static void registerWorldFlag(
      String key,
      boolean def,
      Predicate<WatheExtendedWorldComponent> getter,
      BiConsumer<WatheExtendedWorldComponent, Boolean> setter) {
    ServerConfig.register(
        ServerConfig.Entry.worldBool(
            key,
            def,
            world -> {
              try {
                return getter.test(WatheExtendedWorldComponent.KEY.get(world));
              } catch (Throwable throwable) {
                return def;
              }
            },
            (world, value) -> {
              try {
                setter.accept(WatheExtendedWorldComponent.KEY.get(world), value);
              } catch (Throwable ignored) {
              }
            }));
  }

  /**
   * Registers a global int entry
   *
   * @param key registry key
   * @param def default value
   * @param field name of the {@link WatheExtendedServerConfig} field, used for writes
   */
  private static void registerInt(String key, int def, IntSupplier getter, String field) {
    registerInt(key, def, getter, field, () -> {});
  }

  /**
   * Same as {@link #registerInt(String, int, IntSupplier, String)}, running {@code afterSet} after
   * each write
   */
  private static void registerInt(
      String key, int def, IntSupplier getter, String field, Runnable afterSet) {
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            key,
            def,
            () -> getter.getAsInt(),
            value -> {
              WatheExtendedServerConfig.set(field, value);
              afterSet.run();
            }));
  }

  /** Registers a global float entry. See {@link #registerInt(String, int, IntSupplier, String)} */
  private static void registerFloat(String key, float def, FloatSupplier getter, String field) {
    ServerConfig.register(
        ServerConfig.Entry.globalFloat(
            key,
            def,
            () -> getter.getAsFloat(),
            value -> WatheExtendedServerConfig.set(field, value)));
  }

  /**
   * Registers a global boolean entry. See {@link #registerInt(String, int, IntSupplier, String)}
   */
  private static void registerBool(String key, boolean def, BooleanSupplier getter, String field) {
    registerBool(key, def, getter, field, () -> {});
  }

  /**
   * Same as {@link #registerBool(String, boolean, BooleanSupplier, String)}, running {@code
   * afterSet} after each write
   */
  private static void registerBool(
      String key, boolean def, BooleanSupplier getter, String field, Runnable afterSet) {
    ServerConfig.register(
        ServerConfig.Entry.globalBool(
            key,
            def,
            () -> getter.getAsBoolean(),
            value -> {
              WatheExtendedServerConfig.set(field, value);
              afterSet.run();
            }));
  }

  /** Registers a global string entry. See {@link #registerInt(String, int, IntSupplier, String)} */
  private static void registerString(
      String key, String def, Supplier<String> getter, String field) {
    ServerConfig.register(
        ServerConfig.Entry.globalString(
            key, def, () -> getter.get(), value -> WatheExtendedServerConfig.set(field, value)));
  }

  @FunctionalInterface
  private interface FloatSupplier {
    float getAsFloat();
  }
}
