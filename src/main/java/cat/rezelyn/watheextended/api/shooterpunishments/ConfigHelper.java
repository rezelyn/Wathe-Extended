package cat.rezelyn.watheextended.api.shooterpunishments;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    public static final String[] MODES = {"default", "preventGunPickup", "killShooter"};

    private static String lastKnownMode = MODES[0];

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("shooterpunishments");
    }

    public static String[] getPunishmentModes() {
        return MODES;
    }

    public static String getCurrentPunishment() {
        return lastKnownMode;
    }

    public static void setLastKnownMode(String mode) {
        lastKnownMode = mode;
    }
}
