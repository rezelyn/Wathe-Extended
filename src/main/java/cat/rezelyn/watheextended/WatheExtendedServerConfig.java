package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class WatheExtendedServerConfig {

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("server.json5").toFile();
    public static boolean playerCollisionsEnabled = true;
    public static boolean rtpEnabled = true;
    public static boolean blockProtectionEnabled = true;
    public static boolean itemBoundsCheckEnabled = true;
    public static boolean forbiddenLoversEnabled = false;
    public static float forbiddenLoversChance = 0.25f;
    public static int introvertedCrowdCount = 3;
    public static float introvertedCrowdRange = 5.0f;
    public static float introvertedCrowdDrainMultiplier = 2.0f;
    public static float introvertedAloneDrainMultiplier = 0.5f;
    public static float taxedCoinReduction = 0.50f;
    public static int taxedKillThreshold = 1;
    public static int taxedKillWindowSeconds = 60;
    public static float adaptivePenaltyReduction = 0.50f;
    public static float adaptiveBonusMultiplier = 0.50f;
    public static boolean suppressAbilityVfxSfx = false;
    public static String jumpMode = "LOBBY";
    public static int cleanerPlayerLimit = 10;
    public static boolean cleanerAcidBarrelCoinBonusEnabled = false;
    public static int cleanerAcidBarrelCoins = 50;
    public static int killIncreaseTime = 60;
    public static boolean lastStandEnabled = false;
    public static int lastStandCooldown = 30;
    public static int grenadeCooldown = 90;
    public static int knifeCooldown = 60;
    public static int revolverCooldown = 10;
    public static int psychoModeCooldown = 300;
    public static int lockpickCooldown = 180;
    public static int crowbarCooldown = 10;
    public static int bodyBagCooldown = 300;
    public static int blackoutCooldown = 300;
    public static int sulfuricAcidBarrelCooldown = 60;
    public static int huntingKnifeCooldown = 45;
    public static int medicalKitCooldown = 60;
    public static int panCooldown = 45;
    public static int poisonInjectorCooldown = 60;
    public static int pillCooldown = 180;
    public static int blowgunCooldown = 60;
    public static int knockoutDrugCooldown = 60;
    public static int knifePrice = 100;
    public static int revolverPrice = 300;
    public static int grenadePrice = 350;
    public static int psychoModePrice = 300;
    public static int poisonVialPrice = 100;
    public static int scorpionPrice = 50;
    public static int firecrackerPrice = 10;
    public static int lockpickPrice = 50;
    public static int crowbarPrice = 25;
    public static int bodyBagPrice = 200;
    public static int blackoutPrice = 200;
    public static int notePrice = 10;
    public static int huntingKnifePrice = 100;
    public static int poisonInjectorPrice = 125;
    public static int blowgunPrice = 175;
    public static int knockoutDrugPrice = 75;
    public static int panPrice = 250;
    public static int pillPrice = 300;
    public static int captureDevicePrice = 100;
    public static int captureDeviceCooldown = 60;
    public static int wrenchPrice = 100;
    public static int wrenchCooldown = 120;
    public static int powerRestorationPrice = 300;
    public static int powerRestorationCooldown = 180;
    public static int refreshWeaponCooldownPrice = 300;
    public static int refreshWeaponCooldownCooldown = 180;
    public static int refreshAbilityCooldownPrice = 400;
    public static int refreshAbilityCooldownCooldown = 300;
    public static int refreshPotionEffectPrice = 200;
    public static int refreshPotionEffectCooldown = 180;
    public static int delusionVialPrice = 30;
    public static int defenseVialPrice = 200;
    public static int roleMinePrice = 100;
    public static int tapePrice = 75;
    public static int tapeCooldown = 20;
    public static int jerryCanCooldown = 0;
    public static int lighterCooldown = 0;

    private WatheExtendedServerConfig() {}

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        ClientConfig.Reader config = ClientConfig.reader(CONFIG_FILE);
        playerCollisionsEnabled = config.getBool("gamerules.playerCollisions", true);
        rtpEnabled = config.getBool("gamerules.randomTeleportation", true);
        blockProtectionEnabled = config.getBool("gamerules.worldProtection", true);
        itemBoundsCheckEnabled = config.getBool("gamerules.itemBoundsCheck", true);
        suppressAbilityVfxSfx = config.getBool("gamerules.suppressVfxSfx", false);
        jumpMode = config.getString("gamerules.jumpMode", "LOBBY");
        killIncreaseTime = config.getInt("gamerules.killIncreaseSeconds", 60);
        lastStandEnabled = config.getBool("gamerules.lastStandEnabled", false);
        lastStandCooldown = config.getInt("gamerules.lastStandDuration", 30);
        knifeCooldown = config.getInt("items.knife.cooldown", 60);
        revolverCooldown = config.getInt("items.revolver.cooldown", 10);
        grenadeCooldown = config.getInt("items.grenade.cooldown", 90);
        psychoModeCooldown = config.getInt("items.psychoMode.cooldown", 300);
        lockpickCooldown = config.getInt("items.lockpick.cooldown", 180);
        crowbarCooldown = config.getInt("items.crowbar.cooldown", 10);
        bodyBagCooldown = config.getInt("items.bodyBag.cooldown", 300);
        blackoutCooldown = config.getInt("items.blackout.cooldown", 300);
        sulfuricAcidBarrelCooldown = config.getInt("addons.kinswathe.sulfuricAcidBarrel.cooldown", 60);
        huntingKnifeCooldown = config.getInt("addons.kinswathe.huntingKnife.cooldown", 45);
        medicalKitCooldown = config.getInt("addons.kinswathe.medicalKit.cooldown", 60);
        panCooldown = config.getInt("addons.kinswathe.pan.cooldown", 45);
        poisonInjectorCooldown = config.getInt("addons.kinswathe.poisonInjector.cooldown", 60);
        pillCooldown = config.getInt("addons.kinswathe.pill.cooldown", 180);
        blowgunCooldown = config.getInt("addons.kinswathe.blowgun.cooldown", 60);
        knockoutDrugCooldown = config.getInt("addons.kinswathe.knockoutDrug.cooldown", 60);
        knifePrice = config.getInt("items.knife.price", 100);
        revolverPrice = config.getInt("items.revolver.price", 300);
        grenadePrice = config.getInt("items.grenade.price", 350);
        psychoModePrice = config.getInt("items.psychoMode.price", 300);
        poisonVialPrice = config.getInt("items.poison_vial.price", 100);
        scorpionPrice = config.getInt("items.scorpion.price", 50);
        firecrackerPrice = config.getInt("items.firecracker.price", 10);
        lockpickPrice = config.getInt("items.lockpick.price", 50);
        crowbarPrice = config.getInt("items.crowbar.price", 25);
        bodyBagPrice = config.getInt("items.bodyBag.price", 200);
        blackoutPrice = config.getInt("items.blackout.price", 200);
        notePrice = config.getInt("items.note.price", 10);
        huntingKnifePrice = config.getInt("addons.kinswathe.huntingKnife.price", 100);
        poisonInjectorPrice = config.getInt("addons.kinswathe.poisonInjector.price", 125);
        blowgunPrice = config.getInt("addons.kinswathe.blowgun.price", 175);
        knockoutDrugPrice = config.getInt("addons.kinswathe.knockoutDrug.price", 75);
        panPrice = config.getInt("addons.kinswathe.pan.price", 250);
        pillPrice = config.getInt("addons.kinswathe.pill.price", 300);
        captureDevicePrice = config.getInt("addons.kinswathe.captureDevice.price", 100);
        captureDeviceCooldown = config.getInt("addons.kinswathe.captureDevice.cooldown", 60);
        wrenchPrice = config.getInt("addons.kinswathe.wrench.price", 100);
        wrenchCooldown = config.getInt("addons.kinswathe.wrench.cooldown", 120);
        powerRestorationPrice = config.getInt("addons.kinswathe.powerRestoration.price", 300);
        powerRestorationCooldown = config.getInt("addons.kinswathe.powerRestoration.cooldown", 180);
        refreshWeaponCooldownPrice = config.getInt("addons.kinswathe.refreshWeaponCooldown.price", 300);
        refreshWeaponCooldownCooldown = config.getInt("addons.kinswathe.refreshWeaponCooldown.cooldown", 180);
        refreshAbilityCooldownPrice = config.getInt("addons.kinswathe.refreshAbilityCooldown.price", 400);
        refreshAbilityCooldownCooldown = config.getInt("addons.kinswathe.refreshAbilityCooldown.cooldown", 300);
        refreshPotionEffectPrice = config.getInt("addons.kinswathe.refreshPotionEffect.price", 200);
        refreshPotionEffectCooldown = config.getInt("addons.kinswathe.refreshPotionEffect.cooldown", 180);
        delusionVialPrice = config.getInt("addons.noellesroles.delusionVial.price", 30);
        defenseVialPrice = config.getInt("addons.noellesroles.defenseVial.price", 200);
        roleMinePrice = config.getInt("addons.noellesroles.roleMine.price", 100);
        tapePrice = config.getInt("addons.starexpress.tape.price", 75);
        tapeCooldown = config.getInt("addons.starexpress.tape.cooldown", 20);
        jerryCanCooldown = config.getInt("addons.stupid_express.jerryCan.cooldown", 0);
        lighterCooldown = config.getInt("addons.stupid_express.lighter.cooldown", 0);
        introvertedCrowdCount = config.getInt("modifiers.introverted.crowdCount", 3);
        introvertedCrowdRange = config.getFloat("modifiers.introverted.crowdRange", 5.0f);
        introvertedCrowdDrainMultiplier = config.getFloat("modifiers.introverted.crowdDrainMultiplier", 2.0f);
        introvertedAloneDrainMultiplier = config.getFloat("modifiers.introverted.aloneDrainMultiplier", 0.5f);
        taxedCoinReduction = config.getFloat("modifiers.taxed.coinReduction", 0.50f);
        taxedKillThreshold = config.getInt("modifiers.taxed.killThreshold", 1);
        taxedKillWindowSeconds = config.getInt("modifiers.taxed.killWindowSeconds", 60);
        adaptivePenaltyReduction = config.getFloat("modifiers.adaptive.penaltyReduction", 0.50f);
        adaptiveBonusMultiplier = config.getFloat("modifiers.adaptive.bonusMultiplier", 0.50f);
        forbiddenLoversEnabled = config.getBool("modifiers.lovers.forbiddenLovers", false);
        forbiddenLoversChance = config.getFloat("modifiers.lovers.chance", 0.25f);
        cleanerPlayerLimit = config.getInt("roles.cleaner.playerLimit", 10);
        cleanerAcidBarrelCoinBonusEnabled = config.getBool("roles.cleaner.acidBarrelCoinBonusEnabled", false);
        cleanerAcidBarrelCoins = config.getInt("roles.cleaner.acidBarrelCoins", 50);
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            String content =
                    "{\n" +
                    "  \"gamerules\": {\n" +
                    "    // Do player-to-player collisions are enabled during a game?\n" +
                    "    // Default: true\n" +
                    "    \"playerCollisions\": " + playerCollisionsEnabled + ",\n" +
                    "    // Do players are randomly teleported to a random slot when the game starts?\n" +
                    "    // Default: false\n" +
                    "    \"randomTeleportation\": " + rtpEnabled + ",\n" +
                    "    // Do block interactions are protected in the mapVariables areas?\n" +
                    "    // Default: false\n" +
                    "    \"worldProtection\": " + blockProtectionEnabled + ",\n" +
                    "    // Do items that fall outside the playArea are teleported back to the nearest player/dead body?\n" +
                    "    // Default: true\n" +
                    "    \"itemBoundsCheck\": " + itemBoundsCheckEnabled + ",\n" +
                    "    // When enabled, suppresses VFX/SFX triggered by role abilities (Starstruck, Robot, Bellringer).\n" +
                    "    // Default: false\n" +
                    "    \"suppressVfxSfx\": " + suppressAbilityVfxSfx + ",\n" +
                    "    // Time in seconds added to the game timer when a player is killed.\n" +
                    "    // Set to 0 to disable kill time addition.\n" +
                    "    // Default: 60\n" +
                    "    \"killIncreaseSeconds\": " + killIncreaseTime + ",\n" +
                    "    // When enabled, players targeted by instakill methods get a countdown before they die.\n" +
                    "    // Default: false\n" +
                    "    \"lastStandEnabled\": " + lastStandEnabled + ",\n" +
                    "    // Duration in seconds of the Last Stand countdown before the player dies.\n" +
                    "    // Default: 30\n" +
                    "    \"lastStandDuration\": " + lastStandCooldown + ",\n" +
                    "    // Controls when players are allowed to jump.\n" +
                    "    // Options: DEFAULT (Wathe default behavior), LOBBY (only in lobby), EVERYWHERE (always)\n" +
                    "    // Default: LOBBY\n" +
                    "    \"jumpMode\": \"" + jumpMode + "\"\n" +
                    "  },\n" +
                    "  \"items\": {\n" +
                    "    \"knife\": {\n" +
                    "      // Cooldown in seconds applied to the Knife after it is used to kill a player.\n" +
                    "      // Default: 60\n" +
                    "      \"cooldown\": " + knifeCooldown + ",\n" +
                    "      // Price of the Knife in the shop.\n" +
                    "      // Default: 100\n" +
                    "      \"price\": " + knifePrice + "\n" +
                    "    },\n" +
                    "    \"revolver\": {\n" +
                    "      // Cooldown in seconds applied to the Revolver after it is fired.\n" +
                    "      // Default: 10\n" +
                    "      \"cooldown\": " + revolverCooldown + ",\n" +
                    "      // Price of the Revolver in the shop.\n" +
                    "      // Default: 300\n" +
                    "      \"price\": " + revolverPrice + "\n" +
                    "    },\n" +
                    "    \"grenade\": {\n" +
                    "      // Cooldown in seconds after the Grenade is thrown.\n" +
                    "      // Default: 90\n" +
                    "      \"cooldown\": " + grenadeCooldown + ",\n" +
                    "      // Price of the Grenade in the shop.\n" +
                    "      // Default: 350\n" +
                    "      \"price\": " + grenadePrice + "\n" +
                    "    },\n" +
                    "    \"psychoMode\": {\n" +
                    "      // Cooldown in seconds applied to Psycho Mode after it is activated.\n" +
                    "      // Default: 300\n" +
                    "      \"cooldown\": " + psychoModeCooldown + ",\n" +
                    "      // Price of Psycho Mode in the shop.\n" +
                    "      // Default: 300\n" +
                    "      \"price\": " + psychoModePrice + "\n" +
                    "    },\n" +
                    "    \"lockpick\": {\n" +
                    "      // Cooldown in seconds applied to the Lockpick after it is used to jam a door.\n" +
                    "      // Default: 180\n" +
                    "      \"cooldown\": " + lockpickCooldown + ",\n" +
                    "      // Price of the Lockpick in the shop.\n" +
                    "      // Default: 50\n" +
                    "      \"price\": " + lockpickPrice + "\n" +
                    "    },\n" +
                    "    \"crowbar\": {\n" +
                    "      // Cooldown in seconds applied to the Crowbar after it is used to pry open a door.\n" +
                    "      // Default: 10\n" +
                    "      \"cooldown\": " + crowbarCooldown + ",\n" +
                    "      // Price of the Crowbar in the shop.\n" +
                    "      // Default: 25\n" +
                    "      \"price\": " + crowbarPrice + "\n" +
                    "    },\n" +
                    "    \"bodyBag\": {\n" +
                    "      // Cooldown in seconds applied to the Body Bag after it is used to dispose of a body.\n" +
                    "      // Default: 300\n" +
                    "      \"cooldown\": " + bodyBagCooldown + ",\n" +
                    "      // Price of the Body Bag in the shop.\n" +
                    "      // Default: 200\n" +
                    "      \"price\": " + bodyBagPrice + "\n" +
                    "    },\n" +
                    "    \"blackout\": {\n" +
                    "      // Cooldown in seconds applied to Blackout after it is triggered.\n" +
                    "      // Default: 300\n" +
                    "      \"cooldown\": " + blackoutCooldown + ",\n" +
                    "      // Price of Blackout in the shop.\n" +
                    "      // Default: 200\n" +
                    "      \"price\": " + blackoutPrice + "\n" +
                    "    },\n" +
                    "    \"note\": {\n" +
                    "      // Price of the Note in the shop.\n" +
                    "      // Default: 10\n" +
                    "      \"price\": " + notePrice + "\n" +
                    "    },\n" +
                    "    \"poison_vial\": {\n" +
                    "      // Price of the Poison Vial in the shop.\n" +
                    "      // Default: 100\n" +
                    "      \"price\": " + poisonVialPrice + "\n" +
                    "    },\n" +
                    "    \"scorpion\": {\n" +
                    "      // Price of the Scorpion in the shop.\n" +
                    "      // Default: 50\n" +
                    "      \"price\": " + scorpionPrice + "\n" +
                    "    },\n" +
                    "    \"firecracker\": {\n" +
                    "      // Price of the Firecracker in the shop.\n" +
                    "      // Default: 10\n" +
                    "      \"price\": " + firecrackerPrice + "\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"modifiers\": {\n" +
                    "    \"introverted\": {\n" +
                    "      // Minimum number of nearby players (within crowdRange) for the Introverted modifier to consider the player in a crowd.\n" +
                    "      // Default: 3\n" +
                    "      \"crowdCount\": " + introvertedCrowdCount + ",\n" +
                    "      // Radius in blocks in which other players are counted toward the crowd threshold.\n" +
                    "      // Default: 5.0\n" +
                    "      \"crowdRange\": " + introvertedCrowdRange + ",\n" +
                    "      // Mood drain multiplier applied when the player is considered in a crowd.\n" +
                    "      // Higher values mean more drain, 0 means no drain, 2 means double drain.\n" +
                    "      // Default: 2.0\n" +
                    "      \"crowdDrainMultiplier\": " + introvertedCrowdDrainMultiplier + ",\n" +
                    "      // Mood drain multiplier applied when the player is alone or with only one other player.\n" +
                    "      // Higher values mean more drain, 0 means no drain, 1 means normal drain.\n" +
                    "      // Default: 0.5\n" +
                    "      \"aloneDrainMultiplier\": " + introvertedAloneDrainMultiplier + "\n" +
                    "    },\n" +
                    "    \"taxed\": {\n" +
                    "      // Fraction of kill income deducted when a Taxed player exceeds the kill threshold.\n" +
                    "      // Higher values mean more tax, 0 means no tax, 1 means no income at all.\n" +
                    "      // Default: 0.5\n" +
                    "      \"coinReduction\": " + taxedCoinReduction + ",\n" +
                    "      // Number of kills within the time window before tax starts applying.\n" +
                    "      // Default: 1\n" +
                    "      \"killThreshold\": " + taxedKillThreshold + ",\n" +
                    "      // Time window in seconds during which kills are counted towards the threshold.\n" +
                    "      // Default: 60\n" +
                    "      \"killWindowSeconds\": " + taxedKillWindowSeconds + "\n" +
                    "    },\n" +
                    "    \"adaptive\": {\n" +
                    "      // Fraction penalty applied to kill income when the same method is used consecutively.\n" +
                    "      // Higher values mean more penalty, 0 means no penalty, 1 means no income at all.\n" +
                    "      // Default: 0.5\n" +
                    "      \"penaltyReduction\": " + adaptivePenaltyReduction + ",\n" +
                    "      // Fraction bonus applied to kill income when a different method is used.\n" +
                    "      // Higher values mean more bonus, 0 means no bonus, 1 means double income.\n" +
                    "      // Default: 0.5\n" +
                    "      \"bonusMultiplier\": " + adaptiveBonusMultiplier + "\n" +
                    "    },\n" +
                    "    \"lovers\": {\n" +
                    "      // Enables the Forbidden Lovers mechanic: always have lovers pair being\n" +
                    "      // a Killer/Neutral and a non-Killer. Requires Stupid Express mod.\n" +
                    "      // Default: false\n" +
                    "      \"forbiddenLovers\": " + forbiddenLoversEnabled + ",\n" +
                    "      // Probability (0.0-1.0) that Forbidden Lovers are to be assigned each game.\n" +
                    "      // If chance fails, no Lovers will be assigned whatsoever.\n" +
                    "      // Default: 0.25\n" +
                    "      \"chance\": " + forbiddenLoversChance + "\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"roles\": {\n" +
                    "    \"cleaner\": {\n" +
                    "      // Minimum number of alive players required for the Cleaner's Deep Cleaning ability to be active.\n" +
                    "      // Set to 0 to disable this limit.\n" +
                    "      // Default: 10\n" +
                    "      \"playerLimit\": " + cleanerPlayerLimit + ",\n" +
                    "      // When enabled, the Cleaner receives extra coins upon using the Sulfuric Acid Barrel on a dead body.\n" +
                    "      // Default: false\n" +
                    "      \"acidBarrelCoinBonusEnabled\": " + cleanerAcidBarrelCoinBonusEnabled + ",\n" +
                    "      // Extra coins granted to the Cleaner when using the Sulfuric Acid Barrel.\n" +
                    "      // Default: 50\n" +
                    "      \"acidBarrelCoins\": " + cleanerAcidBarrelCoins + "\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"addons\": {\n" +
                    "    \"kinswathe\": {\n" +
                    "      \"sulfuricAcidBarrel\": {\n" +
                    "        // Cooldown in seconds applied to the Sulfuric Acid Barrel after it is used.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + sulfuricAcidBarrelCooldown + "\n" +
                    "      },\n" +
                    "      \"huntingKnife\": {\n" +
                    "        // Cooldown in seconds applied to the Hunting Knife after it is used.\n" +
                    "        // Default: 45\n" +
                    "        \"cooldown\": " + huntingKnifeCooldown + ",\n" +
                    "        // Price of the Hunting Knife in the shop.\n" +
                    "        // Default: 100\n" +
                    "        \"price\": " + huntingKnifePrice + "\n" +
                    "      },\n" +
                    "      \"medicalKit\": {\n" +
                    "        // Cooldown in seconds applied to the Medical Kit after it is used.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + medicalKitCooldown + "\n" +
                    "      },\n" +
                    "      \"pan\": {\n" +
                    "        // Cooldown in seconds applied to the Pan after it is used.\n" +
                    "        // Default: 45\n" +
                    "        \"cooldown\": " + panCooldown + ",\n" +
                    "        // Price of the Pan in the shop.\n" +
                    "        // Default: 250\n" +
                    "        \"price\": " + panPrice + "\n" +
                    "      },\n" +
                    "      \"poisonInjector\": {\n" +
                    "        // Cooldown in seconds applied to the Poison Injector after it is used.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + poisonInjectorCooldown + ",\n" +
                    "        // Price of the Poison Injector in the shop.\n" +
                    "        // Default: 125\n" +
                    "        \"price\": " + poisonInjectorPrice + "\n" +
                    "      },\n" +
                    "      \"pill\": {\n" +
                    "        // Cooldown in seconds applied to the Pill after it is used.\n" +
                    "        // Default: 180\n" +
                    "        \"cooldown\": " + pillCooldown + ",\n" +
                    "        // Price of the Pill in the shop.\n" +
                    "        // Default: 300\n" +
                    "        \"price\": " + pillPrice + "\n" +
                    "      },\n" +
                    "      \"blowgun\": {\n" +
                    "        // Cooldown in seconds applied to the Blowgun after it is used.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + blowgunCooldown + ",\n" +
                    "        // Price of the Blowgun in the shop.\n" +
                    "        // Default: 175\n" +
                    "        \"price\": " + blowgunPrice + "\n" +
                    "      },\n" +
                    "      \"knockoutDrug\": {\n" +
                    "        // Cooldown in seconds applied to the Knockout Drug after it is used.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + knockoutDrugCooldown + ",\n" +
                    "        // Price of the Knockout Drug in the shop.\n" +
                    "        // Default: 75\n" +
                    "        \"price\": " + knockoutDrugPrice + "\n" +
                    "      },\n" +
                    "      \"captureDevice\": {\n" +
                    "        // Cooldown in seconds applied to the Capture Device after it is placed.\n" +
                    "        // Default: 60\n" +
                    "        \"cooldown\": " + captureDeviceCooldown + ",\n" +
                    "        // Price of the Capture Device in the Technician's shop.\n" +
                    "        // Default: 100\n" +
                    "        \"price\": " + captureDevicePrice + "\n" +
                    "      },\n" +
                    "      \"wrench\": {\n" +
                    "        // Cooldown in seconds applied to the Wrench after it is used.\n" +
                    "        // Default: 120\n" +
                    "        \"cooldown\": " + wrenchCooldown + ",\n" +
                    "        // Price of the Wrench in the Technician's shop.\n" +
                    "        // Default: 100\n" +
                    "        \"price\": " + wrenchPrice + "\n" +
                    "      },\n" +
                    "      \"powerRestoration\": {\n" +
                    "        // Cooldown in seconds applied to Power Restoration after it is purchased.\n" +
                    "        // Default: 180\n" +
                    "        \"cooldown\": " + powerRestorationCooldown + ",\n" +
                    "        // Price of Power Restoration in the Technician's shop.\n" +
                    "        // Default: 300\n" +
                    "        \"price\": " + powerRestorationPrice + "\n" +
                    "      },\n" +
                    "      \"refreshWeaponCooldown\": {\n" +
                    "        // Cooldown in seconds applied to Refresh Weapon Cooldown after it is purchased.\n" +
                    "        // Default: 180\n" +
                    "        \"cooldown\": " + refreshWeaponCooldownCooldown + ",\n" +
                    "        // Price of Refresh Weapon Cooldown in the Hacker's shop.\n" +
                    "        // Default: 300\n" +
                    "        \"price\": " + refreshWeaponCooldownPrice + "\n" +
                    "      },\n" +
                    "      \"refreshAbilityCooldown\": {\n" +
                    "        // Cooldown in seconds applied to Refresh Ability Cooldown after it is purchased.\n" +
                    "        // Default: 300\n" +
                    "        \"cooldown\": " + refreshAbilityCooldownCooldown + ",\n" +
                    "        // Price of Refresh Ability Cooldown in the Hacker's shop.\n" +
                    "        // Default: 400\n" +
                    "        \"price\": " + refreshAbilityCooldownPrice + "\n" +
                    "      },\n" +
                    "      \"refreshPotionEffect\": {\n" +
                    "        // Cooldown in seconds applied to Refresh Potion Effect after it is purchased.\n" +
                    "        // Default: 180\n" +
                    "        \"cooldown\": " + refreshPotionEffectCooldown + ",\n" +
                    "        // Price of Refresh Potion Effect in the Hacker's shop.\n" +
                    "        // Default: 200\n" +
                    "        \"price\": " + refreshPotionEffectPrice + "\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"noellesroles\": {\n" +
                    "      \"defenseVial\": {\n" +
                    "        // Price of the Defense Vial in the Bartender's shop.\n" +
                    "        // Default: 200\n" +
                    "        \"price\": " + defenseVialPrice + "\n" +
                    "      },\n" +
                    "      \"delusionVial\": {\n" +
                    "        // Price of the Delusion Vial in the Framing Roles shop.\n" +
                    "        // Default: 30\n" +
                    "        \"price\": " + delusionVialPrice + "\n" +
                    "      },\n" +
                "      \"roleMine\": {\n" +
                "        // Price of the Role Mine in the Trapper's shop.\n" +
                "        // Default: 100\n" +
                "        \"price\": " + roleMinePrice + "\n" +
                "      }\n" +
                "    },\n" +
                "    \"starexpress\": {\n" +
                "      \"tape\": {\n" +
                "        // Price of the Tape in the Muzzler's shop.\n" +
                "        // Default: 75\n" +
                "        \"price\": " + tapePrice + ",\n" +
                "        // Cooldown in seconds of the Tape item when the Muzzler uses it on a player.\n" +
                "        // Default: 20\n" +
                "        \"cooldown\": " + tapeCooldown + "\n" +
                "      }\n" +
                "    },\n" +
                "    \"stupid_express\": {\n" +
                "      \"jerryCan\": {\n" +
                "        // Fixed cooldown in seconds applied to the Jerry Can after dousing a player.\n" +
                "        // Set to 0 to use Stupid Express's default dynamic formula (45 - 1.66 * playerCount seconds, min 20s).\n" +
                "        // Default: 0\n" +
                "        \"cooldown\": " + jerryCanCooldown + "\n" +
                "      },\n" +
                "      \"lighter\": {\n" +
                "        // Fixed cooldown in seconds applied to the Lighter after igniting oiled players.\n" +
                "        // Set to 0 to use the same dynamic cooldown as the Jerry Can.\n" +
                "        // Default: 0\n" +
                "        \"cooldown\": " + lighterCooldown + "\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                    "}\n";
            Files.writeString(CONFIG_FILE.toPath(), content);
        } catch (IOException ignored) {
        }
    }

    public static boolean isPlayerCollisionsEnabled() {
        return playerCollisionsEnabled;
    }

    public static void setPlayerCollisionsEnabled(boolean value) {
        playerCollisionsEnabled = value;
        save();
    }

    public static boolean isRtpEnabled() {
        return rtpEnabled;
    }

    public static void setRtpEnabled(boolean value) {
        rtpEnabled = value;
        save();
    }

    public static boolean isBlockProtectionEnabled() {
        return blockProtectionEnabled;
    }

    public static void setBlockProtectionEnabled(boolean value) {
        blockProtectionEnabled = value;
        save();
    }

    public static boolean isItemBoundsCheckEnabled() {
        return itemBoundsCheckEnabled;
    }

    public static void setItemBoundsCheckEnabled(boolean value) {
        itemBoundsCheckEnabled = value;
        save();
    }

    public static boolean isForbiddenLoversEnabled() {
        return forbiddenLoversEnabled;
    }

    public static void setForbiddenLoversEnabled(boolean value) {
        forbiddenLoversEnabled = value;
        save();
    }

    public static float getForbiddenLoversChance() {
        return forbiddenLoversChance;
    }

    public static void setForbiddenLoversChance(float value) {
        forbiddenLoversChance = Math.max(0.0f, Math.min(1.0f, value));
        save();
    }

    public static int getIntrovertedCrowdCount() {
        return introvertedCrowdCount;
    }

    public static void setIntrovertedCrowdCount(int value) {
        introvertedCrowdCount = value;
        save();
    }

    public static float getIntrovertedCrowdRange() {
        return introvertedCrowdRange;
    }

    public static void setIntrovertedCrowdRange(float value) {
        introvertedCrowdRange = value;
        save();
    }

    public static float getIntrovertedCrowdDrainMultiplier() {
        return introvertedCrowdDrainMultiplier;
    }

    public static void setIntrovertedCrowdDrainMultiplier(float value) {
        introvertedCrowdDrainMultiplier = value;
        save();
    }

    public static float getIntrovertedAloneDrainMultiplier() {
        return introvertedAloneDrainMultiplier;
    }

    public static void setIntrovertedAloneDrainMultiplier(float value) {
        introvertedAloneDrainMultiplier = value;
        save();
    }

    public static float getTaxedCoinReduction() {
        return taxedCoinReduction;
    }

    public static void setTaxedCoinReduction(float value) {
        taxedCoinReduction = value;
        save();
    }

    public static int getTaxedKillThreshold() {
        return taxedKillThreshold;
    }

    public static void setTaxedKillThreshold(int value) {
        taxedKillThreshold = value;
        save();
    }

    public static int getTaxedKillWindowSeconds() {
        return taxedKillWindowSeconds;
    }

    public static void setTaxedKillWindowSeconds(int value) {
        taxedKillWindowSeconds = value;
        save();
    }

    public static float getAdaptivePenaltyReduction() {
        return adaptivePenaltyReduction;
    }

    public static void setAdaptivePenaltyReduction(float value) {
        adaptivePenaltyReduction = value;
        save();
    }

    public static float getAdaptiveBonusMultiplier() {
        return adaptiveBonusMultiplier;
    }

    public static void setAdaptiveBonusMultiplier(float value) {
        adaptiveBonusMultiplier = value;
        save();
    }

    public static boolean isSuppressAbilityVfxSfx() {
        return suppressAbilityVfxSfx;
    }

    public static void setSuppressAbilityVfxSfx(boolean value) {
        suppressAbilityVfxSfx = value;
        save();
    }

    public static String getJumpMode() {
        return jumpMode;
    }

    public static void setJumpMode(String value) {
        if ("DEFAULT".equals(value) || "LOBBY".equals(value) || "EVERYWHERE".equals(value)) jumpMode = value;
        save();
    }

    public static int getCleanerPlayerLimit() {
        return cleanerPlayerLimit;
    }

    public static void setCleanerPlayerLimit(int value) {
        cleanerPlayerLimit = Math.max(0, value);
        save();
    }

    public static boolean isCleanerAcidBarrelCoinBonusEnabled() {
        return cleanerAcidBarrelCoinBonusEnabled;
    }

    public static void setCleanerAcidBarrelCoinBonusEnabled(boolean value) {
        cleanerAcidBarrelCoinBonusEnabled = value;
        save();
    }

    public static int getCleanerAcidBarrelCoins() {
        return cleanerAcidBarrelCoins;
    }

    public static void setCleanerAcidBarrelCoins(int value) {
        cleanerAcidBarrelCoins = Math.max(0, value);
        save();
    }

    public static int getKillIncreaseTime() {
        return killIncreaseTime;
    }

    public static void setKillIncreaseTime(int value) {
        killIncreaseTime = Math.max(0, value);
        save();
    }

    public static boolean isLastStandEnabled() {
        return lastStandEnabled;
    }

    public static void setLastStandEnabled(boolean value) {
        lastStandEnabled = value;
        save();
    }

    public static int getLastStandCooldown() {
        return lastStandCooldown;
    }

    public static void setLastStandCooldown(int value) {
        lastStandCooldown = Math.max(1, value);
        save();
    }

    public static int getGrenadeCooldown() {
        return grenadeCooldown;
    }

    public static void setGrenadeCooldown(int value) {
        grenadeCooldown = Math.max(0, value);
        save();
    }

    public static int getKnifeCooldown() {
        return knifeCooldown;
    }

    public static void setKnifeCooldown(int value) {
        knifeCooldown = Math.max(0, value);
        save();
    }

    public static int getRevolverCooldown() {
        return revolverCooldown;
    }

    public static void setRevolverCooldown(int value) {
        revolverCooldown = Math.max(0, value);
        save();
    }

    public static int getPsychoModeCooldown() {
        return psychoModeCooldown;
    }

    public static void setPsychoModeCooldown(int value) {
        psychoModeCooldown = Math.max(0, value);
        save();
    }

    public static int getLockpickCooldown() {
        return lockpickCooldown;
    }

    public static void setLockpickCooldown(int value) {
        lockpickCooldown = Math.max(0, value);
        save();
    }

    public static int getCrowbarCooldown() {
        return crowbarCooldown;
    }

    public static void setCrowbarCooldown(int value) {
        crowbarCooldown = Math.max(0, value);
        save();
    }

    public static int getBodyBagCooldown() {
        return bodyBagCooldown;
    }

    public static void setBodyBagCooldown(int value) {
        bodyBagCooldown = Math.max(0, value);
        save();
    }

    public static int getBlackoutCooldown() {
        return blackoutCooldown;
    }

    public static void setBlackoutCooldown(int value) {
        blackoutCooldown = Math.max(0, value);
        save();
    }

    public static int getSulfuricAcidBarrelCooldown() {
        return sulfuricAcidBarrelCooldown;
    }

    public static void setSulfuricAcidBarrelCooldown(int value) {
        sulfuricAcidBarrelCooldown = Math.max(0, value);
        save();
    }

    public static int getHuntingKnifeCooldown() {
        return huntingKnifeCooldown;
    }

    public static void setHuntingKnifeCooldown(int value) {
        huntingKnifeCooldown = Math.max(0, value);
        save();
    }

    public static int getMedicalKitCooldown() {
        return medicalKitCooldown;
    }

    public static void setMedicalKitCooldown(int value) {
        medicalKitCooldown = Math.max(0, value);
        save();
    }

    public static int getPanCooldown() {
        return panCooldown;
    }

    public static void setPanCooldown(int value) {
        panCooldown = Math.max(0, value);
        save();
    }

    public static int getPoisonInjectorCooldown() {
        return poisonInjectorCooldown;
    }

    public static void setPoisonInjectorCooldown(int value) {
        poisonInjectorCooldown = Math.max(0, value);
        save();
    }

    public static int getPillCooldown() {
        return pillCooldown;
    }

    public static void setPillCooldown(int value) {
        pillCooldown = Math.max(0, value);
        save();
    }

    public static int getBlowgunCooldown() {
        return blowgunCooldown;
    }

    public static void setBlowgunCooldown(int value) {
        blowgunCooldown = Math.max(0, value);
        save();
    }

    public static int getKnockoutDrugCooldown() {
        return knockoutDrugCooldown;
    }

    public static void setKnockoutDrugCooldown(int value) {
        knockoutDrugCooldown = Math.max(0, value);
        save();
    }

    public static int getKnifePrice() {
        return knifePrice;
    }

    public static void setKnifePrice(int v) {
        knifePrice = Math.max(0, v);
        save();
    }

    public static int getRevolverPrice() {
        return revolverPrice;
    }

    public static void setRevolverPrice(int v) {
        revolverPrice = Math.max(0, v);
        save();
    }

    public static int getGrenadePrice() {
        return grenadePrice;
    }

    public static void setGrenadePrice(int v) {
        grenadePrice = Math.max(0, v);
        save();
    }

    public static int getPsychoModePrice() {
        return psychoModePrice;
    }

    public static void setPsychoModePrice(int v) {
        psychoModePrice = Math.max(0, v);
        save();
    }

    public static int getPoisonVialPrice() {
        return poisonVialPrice;
    }

    public static void setPoisonVialPrice(int v) {
        poisonVialPrice = Math.max(0, v);
        save();
    }

    public static int getScorpionPrice() {
        return scorpionPrice;
    }

    public static void setScorpionPrice(int v) {
        scorpionPrice = Math.max(0, v);
        save();
    }

    public static int getFirecrackerPrice() {
        return firecrackerPrice;
    }

    public static void setFirecrackerPrice(int v) {
        firecrackerPrice = Math.max(0, v);
        save();
    }

    public static int getLockpickPrice() {
        return lockpickPrice;
    }

    public static void setLockpickPrice(int v) {
        lockpickPrice = Math.max(0, v);
        save();
    }

    public static int getCrowbarPrice() {
        return crowbarPrice;
    }

    public static void setCrowbarPrice(int v) {
        crowbarPrice = Math.max(0, v);
        save();
    }

    public static int getBodyBagPrice() {
        return bodyBagPrice;
    }

    public static void setBodyBagPrice(int v) {
        bodyBagPrice = Math.max(0, v);
        save();
    }

    public static int getBlackoutPrice() {
        return blackoutPrice;
    }

    public static void setBlackoutPrice(int v) {
        blackoutPrice = Math.max(0, v);
        save();
    }

    public static int getNotePrice() {
        return notePrice;
    }

    public static void setNotePrice(int v) {
        notePrice = Math.max(0, v);
        save();
    }

    public static int getHuntingKnifePrice() {
        return huntingKnifePrice;
    }

    public static void setHuntingKnifePrice(int v) {
        huntingKnifePrice = Math.max(0, v);
        save();
    }

    public static int getPoisonInjectorPrice() {
        return poisonInjectorPrice;
    }

    public static void setPoisonInjectorPrice(int v) {
        poisonInjectorPrice = Math.max(0, v);
        save();
    }

    public static int getBlowgunPrice() {
        return blowgunPrice;
    }

    public static void setBlowgunPrice(int v) {
        blowgunPrice = Math.max(0, v);
        save();
    }

    public static int getKnockoutDrugPrice() {
        return knockoutDrugPrice;
    }

    public static void setKnockoutDrugPrice(int v) {
        knockoutDrugPrice = Math.max(0, v);
        save();
    }

    public static int getPanPrice() {
        return panPrice;
    }

    public static void setPanPrice(int v) {
        panPrice = Math.max(0, v);
        save();
    }

    public static int getPillPrice() {
        return pillPrice;
    }

    public static void setPillPrice(int v) {
        pillPrice = Math.max(0, v);
        save();
    }

    public static int getDelusionVialPrice() {
        return delusionVialPrice;
    }

    public static void setDelusionVialPrice(int v) {
        delusionVialPrice = Math.max(0, v);
        save();
    }

    public static int getDefenseVialPrice() {
        return defenseVialPrice;
    }

    public static void setDefenseVialPrice(int v) {
        defenseVialPrice = Math.max(0, v);
        save();
    }

    public static int getRoleMinePrice() {
        return roleMinePrice;
    }

    public static void setRoleMinePrice(int v) {
        roleMinePrice = Math.max(0, v);
        save();
    }

    public static int getCaptureDevicePrice() {
        return captureDevicePrice;
    }

    public static void setCaptureDevicePrice(int v) {
        captureDevicePrice = Math.max(0, v);
        save();
    }

    public static int getCaptureDeviceCooldown() {
        return captureDeviceCooldown;
    }

    public static void setCaptureDeviceCooldown(int v) {
        captureDeviceCooldown = Math.max(0, v);
        save();
    }

    public static int getWrenchPrice() {
        return wrenchPrice;
    }

    public static void setWrenchPrice(int v) {
        wrenchPrice = Math.max(0, v);
        save();
    }

    public static int getWrenchCooldown() {
        return wrenchCooldown;
    }

    public static void setWrenchCooldown(int v) {
        wrenchCooldown = Math.max(0, v);
        save();
    }

    public static int getPowerRestorationPrice() {
        return powerRestorationPrice;
    }

    public static void setPowerRestorationPrice(int v) {
        powerRestorationPrice = Math.max(0, v);
        save();
    }

    public static int getPowerRestorationCooldown() {
        return powerRestorationCooldown;
    }

    public static void setPowerRestorationCooldown(int v) {
        powerRestorationCooldown = Math.max(0, v);
        save();
    }

    public static int getRefreshWeaponCooldownPrice() {
        return refreshWeaponCooldownPrice;
    }

    public static void setRefreshWeaponCooldownPrice(int v) {
        refreshWeaponCooldownPrice = Math.max(0, v);
        save();
    }

    public static int getRefreshWeaponCooldownCooldown() {
        return refreshWeaponCooldownCooldown;
    }

    public static void setRefreshWeaponCooldownCooldown(int v) {
        refreshWeaponCooldownCooldown = Math.max(0, v);
        save();
    }

    public static int getRefreshAbilityCooldownPrice() {
        return refreshAbilityCooldownPrice;
    }

    public static void setRefreshAbilityCooldownPrice(int v) {
        refreshAbilityCooldownPrice = Math.max(0, v);
        save();
    }

    public static int getRefreshAbilityCooldownCooldown() {
        return refreshAbilityCooldownCooldown;
    }

    public static void setRefreshAbilityCooldownCooldown(int v) {
        refreshAbilityCooldownCooldown = Math.max(0, v);
        save();
    }

    public static int getRefreshPotionEffectPrice() {
        return refreshPotionEffectPrice;
    }

    public static void setRefreshPotionEffectPrice(int v) {
        refreshPotionEffectPrice = Math.max(0, v);
        save();
    }

    public static int getRefreshPotionEffectCooldown() {
        return refreshPotionEffectCooldown;
    }

    public static void setRefreshPotionEffectCooldown(int v) {
        refreshPotionEffectCooldown = Math.max(0, v);
        save();
    }

    public static int getTapePrice() {
        return tapePrice;
    }

    public static void setTapePrice(int v) {
        tapePrice = Math.max(0, v);
        save();
    }

    public static int getTapeCooldown() {
        return tapeCooldown;
    }

    public static void setTapeCooldown(int v) {
        tapeCooldown = Math.max(0, v);
        save();
    }

    public static int getJerryCanCooldown() {
        return jerryCanCooldown;
    }

    public static void setJerryCanCooldown(int v) {
        jerryCanCooldown = Math.max(0, v);
        save();
    }

    public static int getLighterCooldown() {
        return lighterCooldown;
    }

    public static void setLighterCooldown(int v) {
        lighterCooldown = Math.max(0, v);
        save();
    }
}
