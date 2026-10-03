package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.network.ConfigHelper;
import java.util.*;

/**
 * Server-side configuration for Wathe: Extended, stored in {@code config/watheextended/server.json}
 *
 * <p><b>For contributors:</b> to add an option, declare a {@code public static} field below.
 * Loading, saving, and get/set are handled automatically through reflection by {@link ConfigHelper}
 *
 * <ul>
 *   <li>The field name is used as the JSON key
 *   <li>The field's initial value is the default
 *   <li>Fields marked {@code final} or {@code transient} are not saved
 * </ul>
 */
public final class WatheExtendedServerConfig {

  // GENERAL

  // Roleplay items
  public static final Map<String, Boolean> ROLEPLAY_ITEM_DEFAULTS = createRoleplayItemDefaults();
  private static final ConfigHelper CONFIG =
      new ConfigHelper(WatheExtendedServerConfig.class, "server.json");
  // World
  public static boolean playerCollisionsEnabled = true;
  public static boolean rtpEnabled = true;
  public static boolean blockProtectionEnabled = true;
  public static boolean itemBoundsCheckEnabled = true;
  // Jump Mode
  public static String jumpMode = "LOBBY";
  // Shooter Punishment
  public static String shootInnocentPunishmentMode = "DEFAULT";
  // Last Stand
  public static boolean lastStandEnabled = false;
  public static int lastStandCooldown = 30;
  // Instinct Mechanic
  public static float instinctCapacity = 1200.0f;
  public static float instinctDrainRate = 60.0f;
  public static float instinctReloadRate = 20.0f;
  // Income
  public static boolean adjustPassiveIncome = false;
  public static int basePassiveIncome = 5;
  public static int maxPassiveIncomeDistance = 10;
  public static int minPassiveIncome = 0;
  // Mood System
  public static boolean moodDisableAbilityWhenDepressed = true;
  public static int moodDisableAbilityWhenDepressedDelay = 0;
  public static boolean moodDisableSprintingWhenDepressed = true;

  // ROLES
  // Suppress ability VFX/SFX
  public static boolean suppressAbilityVfxSfx = false;
  // Kill Time Increase
  public static int killIncreaseTime = 60;
  // Morphling
  public static boolean morphlingCanCancelAbility = true;
  public static int morphlingAbilityDuration = 35;
  public static int morphlingAbilityCooldown = 60;
  // Phantom
  public static boolean phantomCanCancelAbility = true;
  public static int phantomAbilityDuration = 30;
  public static int phantomAbilityCooldown = 60;
  // Cleaner
  public static int cleanerPlayerLimit = 10;
  public static boolean cleanerAcidBarrelCoinBonusEnabled = false;
  public static int cleanerAcidBarrelCoins = 50;
  // Dreamer
  public static boolean dreamerImprintNotificationEnabled = true;

  // MODIFIERS
  // Arsonist
  public static boolean arsonistDousedNotificationEnabled = true;
  public static int arsonistDousedNotificationDelay = 10;
  // Forbidden Lovers
  public static boolean forbiddenLoversEnabled = false;
  public static float forbiddenLoversChance = 0.25f;
  // Introverted
  public static int introvertedCrowdCount = 3;
  public static float introvertedCrowdRange = 5.0f;
  public static float introvertedCrowdDrainMultiplier = 2.0f;
  public static float introvertedAloneDrainMultiplier = 0.5f;
  // Taxed
  public static float taxedCoinReduction = 0.50f;
  public static int taxedKillThreshold = 1;
  public static int taxedKillWindowSeconds = 60;

  // ITEMS
  // Adaptive
  public static float adaptivePenaltyReduction = 0.50f;
  public static float adaptiveBonusMultiplier = 0.50f;
  // Knife
  public static int knifePrice = 100;
  public static int knifeCooldown = 60;
  // Revolver
  public static int revolverPrice = 300;
  public static int revolverCooldown = 10;
  // Grenade
  public static int grenadePrice = 350;
  public static int grenadeCooldown = 90;
  // Psycho Mode
  public static int psychoModePrice = 300;
  public static int psychoModeCooldown = 300;
  // Poison Vial
  public static int poisonVialPrice = 100;
  // Scorpion
  public static int scorpionPrice = 50;
  // Firecracker
  public static int firecrackerPrice = 10;
  // Lockpick
  public static int lockpickPrice = 50;
  public static int lockpickCooldown = 180;
  // Crowbar
  public static int crowbarPrice = 25;
  public static int crowbarCooldown = 10;
  // Bodybag
  public static int bodyBagPrice = 200;
  public static int bodyBagCooldown = 300;
  // Blackout
  public static int blackoutPrice = 200;
  public static int blackoutCooldown = 300;
  // Note
  public static int notePrice = 10;
  // Delusion Vial
  public static int delusionVialPrice = 30;
  // Defense Vial
  public static int defenseVialPrice = 200;
  // Tape
  public static int tapePrice = 75;
  public static int tapeCooldown = 20;
  // Sulfuric Acid Barrel
  public static int sulfuricAcidBarrelCooldown = 60;
  // Hunting Knife
  public static int huntingKnifePrice = 100;
  public static int huntingKnifeCooldown = 45;
  // Medical Kit
  public static int medicalKitCooldown = 60;
  // Pan
  public static int panPrice = 250;
  public static int panCooldown = 45;
  // Poison Injector
  public static int poisonInjectorPrice = 125;
  public static int poisonInjectorCooldown = 60;
  // Pill
  public static int pillPrice = 300;
  public static int pillCooldown = 180;
  // Blowgun
  public static int blowgunPrice = 175;
  public static int blowgunCooldown = 60;
  // Knockout Drug
  public static int knockoutDrugPrice = 75;
  public static int knockoutDrugCooldown = 60;
  // Wrench
  public static int wrenchPrice = 100;
  public static int wrenchCooldown = 120;
  // Capture Device
  public static int captureDevicePrice = 100;
  public static int captureDeviceCooldown = 60;
  // Power Restoration
  public static int powerRestorationPrice = 300;
  public static int powerRestorationCooldown = 180;
  // Refresh Weapon Cooldown
  public static int refreshWeaponCooldownPrice = 300;
  public static int refreshWeaponCooldownCooldown = 180;
  // Refresh Ability Cooldown
  public static int refreshAbilityCooldownPrice = 400;
  public static int refreshAbilityCooldownCooldown = 300;
  // Refresh Potion Effect
  public static int refreshPotionEffectPrice = 200;
  public static int refreshPotionEffectCooldown = 180;
  // Role Mine
  public static int roleMinePrice = 100;
  // Jerry Can
  public static int jerryCanCooldown = 0;
  // Lighter
  public static int lighterCooldown = 0;
  public static Map<String, Boolean> roleplayItems = new LinkedHashMap<>(ROLEPLAY_ITEM_DEFAULTS);

  private WatheExtendedServerConfig() {}

  public static void load() {
    CONFIG.load();
  }

  public static void save() {
    CONFIG.save();
  }

  public static Set<String> keys() {
    return CONFIG.keys();
  }

  /** Returns the current value of an option, or {@code null} if it doesn't exist. */
  public static Object get(String name) {
    return CONFIG.get(name);
  }

  /**
   * Sets an option and saves the config. Any {@link Number} is converted to the field's numeric
   * type. Range validation is left to the YACL screen
   *
   * @return {@code false} if the option doesn't exist or the value has the wrong type
   */
  public static boolean set(String name, Object value) {
    return CONFIG.set(name, value);
  }

  public static boolean isRoleplayItemEnabled(String id) {
    return roleplayItems.getOrDefault(id, false);
  }

  public static void setRoleplayItemEnabled(String id, boolean value) {
    roleplayItems.put(id, value);
    save();
  }

  private static Map<String, Boolean> createRoleplayItemDefaults() {
    Map<String, Boolean> defaults = new LinkedHashMap<>();
    for (String id :
        List.of(
            "cigar",
            "cigarette",
            "highball",
            "coal_coke",
            "flow_dust",
            "charge_dust",
            "pocket_watch")) {
      defaults.put(id, true);
    }
    for (String id : List.of("tmotl", "trhm", "asis", "tmrm", "tm", "tmotyr")) {
      defaults.put(id, false);
    }
    return Collections.unmodifiableMap(defaults);
  }
}
