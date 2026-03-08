package cat.rezelyn.watheextended.api.noellesroles;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("noellesroles");
    }

    private static Object getConfigInstance() throws Exception {
        Class<?> configClass = Class.forName("org.agmas.noellesroles.config.NoellesRolesConfig");
        Object handler = configClass.getField("HANDLER").get(null);
        return handler.getClass().getMethod("instance").invoke(handler);
    }

    private static void saveConfig() throws Exception {
        Class<?> configClass = Class.forName("org.agmas.noellesroles.config.NoellesRolesConfig");
        Object handler = configClass.getField("HANDLER").get(null);
        handler.getClass().getMethod("save").invoke(handler);
    }

    private static boolean readBool(String fieldName, boolean def) {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField(fieldName).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBool(String fieldName, boolean value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    private static int readInt(String fieldName, int def) {
        try {
            Object cfg = getConfigInstance();
            return (int) cfg.getClass().getField(fieldName).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeInt(String fieldName, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    private static String readString(String fieldName, String def) {
        try {
            Object cfg = getConfigInstance();
            Object val = cfg.getClass().getField(fieldName).get(cfg);
            return val instanceof String s ? s : def;
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeString(String fieldName, String value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    public static boolean getInsanePlayersSeeMorphs(World world) {
        return readBool("insanePlayersSeeMorphs", true);
    }

    public static void setInsanePlayersSeeMorphs(World world, boolean value) throws Exception {
        writeBool("insanePlayersSeeMorphs", value);
    }

    public static boolean getVoodooNonKillerDeaths() {
        return readBool("voodooNonKillerDeaths", false);
    }

    public static void setVoodooNonKillerDeaths(boolean value) throws Exception {
        writeBool("voodooNonKillerDeaths", value);
    }

    public static boolean getVoodooShotLikeEvil() {
        return readBool("voodooShotLikeEvil", true);
    }

    public static void setVoodooShotLikeEvil(boolean value) throws Exception {
        writeBool("voodooShotLikeEvil", value);
    }

    public static int getPlayerCountToMakeConducterKeyVisible() {
        return readInt("playerCountToMakeConducterKeyVisible", 10);
    }

    public static void setPlayerCountToMakeConducterKeyVisible(int value) throws Exception {
        writeInt("playerCountToMakeConducterKeyVisible", value);
    }

    public static int getMaximumDefenseVials() {
        return readInt("maximumDefenseVials", 0);
    }

    public static void setMaximumDefenseVials(int value) throws Exception {
        writeInt("maximumDefenseVials", value);
    }

    public static int getDefenseVialPrice() {
        return readInt("defenseVialPrice", 100);
    }

    public static void setDefenseVialPrice(int value) throws Exception {
        writeInt("defenseVialPrice", value);
    }

    public static int getRoleMinePrice() {
        return readInt("roleMinePrice", 100);
    }

    public static void setRoleMinePrice(int value) throws Exception {
        writeInt("roleMinePrice", value);
    }

    public static boolean getAllowCivillianGuessers() {
        return readBool("allowCivillianGuessers", false);
    }

    public static void setAllowCivillianGuessers(boolean value) throws Exception {
        writeBool("allowCivillianGuessers", value);
    }

    public static String getGuesserDiesAfterIncorrectGuess() {
        return readString("guesserDiesAfterIncorrectGuess", "none");
    }

    public static void setGuesserDiesAfterIncorrectGuess(String value) throws Exception {
        writeString("guesserDiesAfterIncorrectGuess", value);
    }
}
