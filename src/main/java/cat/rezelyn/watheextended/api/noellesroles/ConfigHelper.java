package cat.rezelyn.watheextended.api.noellesroles;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("noellesroles");
    }

    private static Object getConfigInstance() throws Exception {
        Class<?> cls = Class.forName("org.agmas.noellesroles.config.NoellesRolesConfig");
        Object handler = cls.getField("HANDLER").get(null);
        return handler.getClass().getMethod("instance").invoke(handler);
    }

    private static void saveConfig() throws Exception {
        Class<?> cls = Class.forName("org.agmas.noellesroles.config.NoellesRolesConfig");
        Object handler = cls.getField("HANDLER").get(null);
        handler.getClass().getMethod("save").invoke(handler);
    }

    private static boolean readBoolServer(String field, boolean def) {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField(field).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBoolServer(String field, boolean value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static int readIntServer(String field, int def) {
        try {
            Object cfg = getConfigInstance();
            return (int) cfg.getClass().getField(field).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeIntServer(String field, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static String readStringServer(String field, String def) {
        try {
            Object cfg = getConfigInstance();
            Object val = cfg.getClass().getField(field).get(cfg);
            return val instanceof String s ? s : def;
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeStringServer(String field, String value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        reg(Entry.globalBool("noellesroles.insanePlayersSeeMorphs", false,
                () -> readBoolServer("insanePlayersSeeMorphs", false),
                v -> {
                    try {
                        writeBoolServer("insanePlayersSeeMorphs", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalBool("noellesroles.voodooNonKillerDeaths", false,
                () -> readBoolServer("voodooNonKillerDeaths", false),
                v -> {
                    try {
                        writeBoolServer("voodooNonKillerDeaths", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalBool("noellesroles.voodooShotLikeEvil", true,
                () -> readBoolServer("voodooShotLikeEvil", true),
                v -> {
                    try {
                        writeBoolServer("voodooShotLikeEvil", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalBool("noellesroles.allowCivillianGuessers", false,
                () -> readBoolServer("allowCivillianGuessers", false),
                v -> {
                    try {
                        writeBoolServer("allowCivillianGuessers", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("noellesroles.playerCountToMakeConducterKeyVisible", 10,
                () -> readIntServer("playerCountToMakeConducterKeyVisible", 10),
                v -> {
                    try {
                        writeIntServer("playerCountToMakeConducterKeyVisible", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("noellesroles.maximumDefenseVials", 1,
                () -> readIntServer("maximumDefenseVials", 1),
                v -> {
                    try {
                        writeIntServer("maximumDefenseVials", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("noellesroles.defenseVialPrice", 200,
                () -> readIntServer("defenseVialPrice", 200),
                v -> {
                    try {
                        writeIntServer("defenseVialPrice", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("noellesroles.roleMinePrice", 100,
                () -> readIntServer("roleMinePrice", 100),
                v -> {
                    try {
                        writeIntServer("roleMinePrice", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalString("noellesroles.guesserDiesAfterIncorrectGuess", "none",
                () -> readStringServer("guesserDiesAfterIncorrectGuess", "none"),
                v -> {
                    try {
                        writeStringServer("guesserDiesAfterIncorrectGuess", v);
                    } catch (Throwable ignored) {
                    }
                }));
    }

    private static <T> void reg(Entry<T> e) {
        ServerConfig.register(e);
    }

    private static boolean c(String key, boolean def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getBool(key, def);
        return def;
    }

    private static int c(String key, int def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getInt(key, def);
        return def;
    }

    private static String cs(String key, String def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getString(key, def);
        return def;
    }

    public static boolean getInsanePlayersSeeMorphs(World world) {
        return c("noellesroles.insanePlayersSeeMorphs", false);
    }

    public static void setInsanePlayersSeeMorphs(World world, boolean value) throws Exception {
        apply("noellesroles.insanePlayersSeeMorphs", value, world);
    }

    public static boolean getVoodooNonKillerDeaths() {
        return c("noellesroles.voodooNonKillerDeaths", false);
    }

    public static void setVoodooNonKillerDeaths(boolean value) throws Exception {
        apply("noellesroles.voodooNonKillerDeaths", value, null);
    }

    public static boolean getVoodooShotLikeEvil() {
        return c("noellesroles.voodooShotLikeEvil", true);
    }

    public static void setVoodooShotLikeEvil(boolean value) throws Exception {
        apply("noellesroles.voodooShotLikeEvil", value, null);
    }

    public static int getPlayerCountToMakeConducterKeyVisible() {
        return c("noellesroles.playerCountToMakeConducterKeyVisible", 10);
    }

    public static void setPlayerCountToMakeConducterKeyVisible(int value) throws Exception {
        apply("noellesroles.playerCountToMakeConducterKeyVisible", value, null);
    }

    public static int getMaximumDefenseVials() {
        return c("noellesroles.maximumDefenseVials", 1);
    }

    public static void setMaximumDefenseVials(int v) throws Exception {
        apply("noellesroles.maximumDefenseVials", v, null);
    }

    public static int getDefenseVialPrice() {
        return c("noellesroles.defenseVialPrice", 200);
    }

    public static void setDefenseVialPrice(int v) throws Exception {
        apply("noellesroles.defenseVialPrice", v, null);
    }

    public static int getRoleMinePrice() {
        return c("noellesroles.roleMinePrice", 100);
    }

    public static void setRoleMinePrice(int v) throws Exception {
        apply("noellesroles.roleMinePrice", v, null);
    }

    public static boolean getAllowCivillianGuessers() {
        return c("noellesroles.allowCivillianGuessers", false);
    }

    public static void setAllowCivillianGuessers(boolean v) throws Exception {
        apply("noellesroles.allowCivillianGuessers", v, null);
    }

    public static String getGuesserDiesAfterIncorrectGuess() {
        return cs("noellesroles.guesserDiesAfterIncorrectGuess", "none");
    }

    public static void setGuesserDiesAfterIncorrectGuess(String v) throws Exception {
        apply("noellesroles.guesserDiesAfterIncorrectGuess", v, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> void apply(String key, T value, World world) {
        Entry<T> entry = (Entry<T>) ServerConfig.entries().get(key);
        if (entry != null) entry.writeServer(world, value);
    }
}
