package cat.rezelyn.watheextended.api.shooterpunishments;

import cat.rezelyn.watheextended.api.ClientConfig;
import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.api.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    public static final String[] MODES = {"default", "preventGunPickup", "killShooter"};
    private static String lastKnownMode = MODES[0];

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("shooterpunishments");
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        ServerConfig.register(Entry.globalString("shooterpunishments.currentMode", MODES[0], () -> lastKnownMode, v -> lastKnownMode = v));
    }

    public static String[] getPunishmentModes() {
        return MODES;
    }

    public static String getCurrentPunishment() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && ClientConfig.isRemoteServer()) {
            return ClientConfig.getString("shooterpunishments.currentMode", lastKnownMode);
        }
        return lastKnownMode;
    }

    public static void setLastKnownMode(String mode) {
        lastKnownMode = mode;
    }
}
