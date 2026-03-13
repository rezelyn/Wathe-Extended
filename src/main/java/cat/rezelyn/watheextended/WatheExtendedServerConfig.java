package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class WatheExtendedServerConfig {

    private static final File CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("server.json5").toFile();
    public static boolean playerCollisionsEnabled = true;
    public static boolean rtpEnabled = true;
    public static boolean blockProtectionEnabled = true;
    public static boolean itemBoundsCheckEnabled = true;
    public static boolean forbiddenLoversEnabled = false;
    public static int introvertedCrowdCount = 3;
    public static float introvertedCrowdRange = 5.0f;
    public static float introvertedCrowdDrainMultiplier = 2.0f;
    public static float introvertedAloneDrainMultiplier = 0.5f;
    public static float taxedCoinReduction = 0.25f;

    private WatheExtendedServerConfig() {
    }

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
        introvertedCrowdCount = ClientConfig.readInt(CONFIG_FILE, "introverted.crowdCount", 3);
        introvertedCrowdRange = ClientConfig.readFloat(CONFIG_FILE, "introverted.crowdRange", 5.0f);
        introvertedCrowdDrainMultiplier = ClientConfig.readFloat(CONFIG_FILE, "introverted.crowdDrainMultiplier", 2.0f);
        introvertedAloneDrainMultiplier = ClientConfig.readFloat(CONFIG_FILE, "introverted.aloneDrainMultiplier", 0.5f);
        taxedCoinReduction = ClientConfig.readFloat(CONFIG_FILE, "taxed.coinReduction", 0.25f);
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
                            "    \"enabled\": " + forbiddenLoversEnabled + "\n" +
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
                            "    // Fraction of coins deducted from the player's kill/passive income.\n" +
                            "    \"coinReduction\": " + taxedCoinReduction + "\n" +
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

    public static int getAdaptiveMinUniqueMethods() {
        return adaptiveMinUniqueMethods;
    }

    public static void setAdaptiveMinUniqueMethods(int value) {
        adaptiveMinUniqueMethods = value;
        save();
    }

    public static float getAdaptiveRepeatPenalty() {
        return adaptiveRepeatPenalty;
    }

    public static void setAdaptiveRepeatPenalty(float value) {
        adaptiveRepeatPenalty = value;
        save();
    }

    public static float getAdaptiveVarietyBonus() {
        return adaptiveVarietyBonus;
    }

    public static void setAdaptiveVarietyBonus(float value) {
        adaptiveVarietyBonus = value;
        save();
    }
}
