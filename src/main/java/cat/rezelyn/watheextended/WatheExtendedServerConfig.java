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
}
