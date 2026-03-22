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
    public static int cleanerPlayerLimit = 10;

    private WatheExtendedServerConfig() {}

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        playerCollisionsEnabled = ClientConfig.readBool(CONFIG_FILE, "playerCollisions.enabled", true);
        rtpEnabled = ClientConfig.readBool(CONFIG_FILE, "rtp.enabled", true);
        blockProtectionEnabled = ClientConfig.readBool(CONFIG_FILE, "blockProtection.enabled", true);
        itemBoundsCheckEnabled = ClientConfig.readBool(CONFIG_FILE, "itemBoundsCheck.enabled", true);
        forbiddenLoversEnabled = ClientConfig.readBool(CONFIG_FILE, "forbiddenLovers.enabled", false);
        forbiddenLoversChance = ClientConfig.readFloat(CONFIG_FILE, "forbiddenLovers.chance", 0.25f);
        introvertedCrowdCount = ClientConfig.readInt(CONFIG_FILE, "introverted.crowdCount", 3);
        introvertedCrowdRange = ClientConfig.readFloat(CONFIG_FILE, "introverted.crowdRange", 5.0f);
        introvertedCrowdDrainMultiplier = ClientConfig.readFloat(CONFIG_FILE, "introverted.crowdDrainMultiplier", 2.0f);
        introvertedAloneDrainMultiplier = ClientConfig.readFloat(CONFIG_FILE, "introverted.aloneDrainMultiplier", 0.5f);
        taxedCoinReduction = ClientConfig.readFloat(CONFIG_FILE, "taxed.coinReduction", 0.50f);
        taxedKillThreshold = ClientConfig.readInt(CONFIG_FILE, "taxed.killThreshold", 1);
        taxedKillWindowSeconds = ClientConfig.readInt(CONFIG_FILE, "taxed.killWindowSeconds", 60);
        adaptivePenaltyReduction = ClientConfig.readFloat(CONFIG_FILE, "adaptive.penaltyReduction", 0.50f);
        adaptiveBonusMultiplier = ClientConfig.readFloat(CONFIG_FILE, "adaptive.bonusMultiplier", 0.50f);
        suppressAbilityVfxSfx = ClientConfig.readBool(CONFIG_FILE, "ability.suppressVfxSfx", false);
        cleanerPlayerLimit = ClientConfig.readInt(CONFIG_FILE, "cleaner.playerLimit", 10);
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            String content =
                    "{\n" +
                            "  \"playerCollisions\": {\n" +
                            "    // Whether player-to-player collisions are enabled during a game.\n" +
                            "    \"enabled\": " + playerCollisionsEnabled + "\n" +
                            "  },\n" +
                            "  \"rtp\": {\n" +
                            "    // Whether players are randomly teleported to a random slot when the game starts.\n" +
                            "    \"enabled\": " + rtpEnabled + "\n" +
                            "  },\n" +
                            "  \"blockProtection\": {\n" +
                            "    // Whether block interactions are protected in the mapVariables areas.\n" +
                            "    \"enabled\": " + blockProtectionEnabled + "\n" +
                            "  },\n" +
                            "  \"itemBoundsCheck\": {\n" +
                            "    // Whether items that fall outside the playArea are teleported back to the nearest player/dead body.\n" +
                            "    \"enabled\": " + itemBoundsCheckEnabled + "\n" +
                            "  },\n" +
                            "  \"forbiddenLovers\": {\n" +
                            "    // Enables the Forbidden Lovers mechanic: always have lovers pair being\n" +
                            "    // a Killer/Neutral and a non-Killer. Requires Stupid Express mod.\n" +
                            "    \"enabled\": " + forbiddenLoversEnabled + ",\n" +
                            "    // Probability (0.0–1.0) that Forbidden Lovers are actually assigned each game.\n" +
                            "    // If chance fails, no Lovers will be assigned whatsoever.\n" +
                            "    \"chance\": " + forbiddenLoversChance + "\n" +
                            "  },\n" +
                            "  \"introverted\": {\n" +
                            "    // Minimum number of nearby players (within crowdRange) for the Introverted modifier to consider the player in a crowd.\n" +
                            "    \"crowdCount\": " + introvertedCrowdCount + ",\n" +
                            "    // Radius in blocks in which other players are counted toward the crowd threshold.\n" +
                            "    \"crowdRange\": " + introvertedCrowdRange + ",\n" +
                            "    // Mood drain multiplier applied when the player is considered in a crowd.\n" +
                            "    \"crowdDrainMultiplier\": " + introvertedCrowdDrainMultiplier + ",\n" +
                            "    // Mood drain multiplier applied when the player is alone or with only one other player.\n" +
                            "    \"aloneDrainMultiplier\": " + introvertedAloneDrainMultiplier + "\n" +
                            "  },\n" +
                            "  \"taxed\": {\n" +
                            "    // Fraction of kill income deducted when a Taxed player exceeds the kill threshold.\n" +
                            "    \"coinReduction\": " + taxedCoinReduction + ",\n" +
                            "    // Number of kills within the time window before tax starts applying (default: 1 = more than 1 kill).\n" +
                            "    \"killThreshold\": " + taxedKillThreshold + ",\n" +
                            "    // Time window in seconds during which kills are counted towards the threshold (default: 60 = 1 minute).\n" +
                            "    \"killWindowSeconds\": " + taxedKillWindowSeconds + "\n" +
                            "  },\n" +
                            "  \"adaptive\": {\n" +
                            "    // Fraction penalty applied to kill income when the same method is used consecutively.\n" +
                            "    \"penaltyReduction\": " + adaptivePenaltyReduction + ",\n" +
                            "    // Fraction bonus applied to kill income when a different method is used.\n" +
                            "    \"bonusMultiplier\": " + adaptiveBonusMultiplier + "\n" +
                            "  },\n" +
                            "  \"ability\": {\n" +
                            "    // When enabled, suppresses VFX/SFX triggered by role abilities (Starstruck, Robot, Bellringer).\n" +
                            "    \"suppressVfxSfx\": " + suppressAbilityVfxSfx + "\n" +
                            "  },\n" +
                            "  \"cleaner\": {\n" +
                            "    // Minimum number of alive players required for the Cleaner's Deep Cleaning ability to be active.\n" +
                            "    // Set to 0 to disable this limit.\n" +
                            "    \"playerLimit\": " + cleanerPlayerLimit + "\n" +
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

    public static int getCleanerPlayerLimit() {
        return cleanerPlayerLimit;
    }

    public static void setCleanerPlayerLimit(int value) {
        cleanerPlayerLimit = Math.max(0, value);
        save();
    }
}
