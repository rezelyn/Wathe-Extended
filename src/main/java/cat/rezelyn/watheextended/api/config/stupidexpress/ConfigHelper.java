package cat.rezelyn.watheextended.api.config.stupidexpress;

import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("stupid_express");
    }

    private static final String CONFIG_CLASS = "pro.fazeclan.river.stupid_express.StupidExpress";

    private static boolean readBoolServer(String topSection, String subSection, String field, boolean def) {
        try {
            Object cfg = ConfigUtils.getStaticField(CONFIG_CLASS, "CONFIG");
            Object section = ConfigUtils.getField(ConfigUtils.getField(cfg, topSection), subSection);
            return (boolean) ConfigUtils.getField(section, field);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBoolServer(String topSection, String subSection, String field, boolean value) throws Exception {
        Object cfg = ConfigUtils.getStaticField(CONFIG_CLASS, "CONFIG");
        Object section = ConfigUtils.getField(ConfigUtils.getField(cfg, topSection), subSection);
        ConfigUtils.setField(section, field, value);
        try { ConfigUtils.invoke(cfg, "save"); } catch (Throwable ignored) {}
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        ServerConfig.register(Entry.globalBool("stupidexpress.necromancerHasShop", false, () -> readBoolServer("rolesSection", "necromancerSection", "necromancerHasShop", false), v -> {
            try {
                writeBoolServer("rolesSection", "necromancerSection", "necromancerHasShop", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.arsonistKeepsGameGoing", true, () -> readBoolServer("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", true), v -> {
            try {
                writeBoolServer("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.bodiesGlowToAmnesiac", true, () -> readBoolServer("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", true), v -> {
            try {
                writeBoolServer("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.amnesiacGlowsDifferently", false, () -> readBoolServer("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", false), v -> {
            try {
                writeBoolServer("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.loversKnowImmediately", true, () -> readBoolServer("modifiersSection", "loversSection", "loversKnowImmediately", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversKnowImmediately", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.loversWinWithKillers", false, () -> readBoolServer("modifiersSection", "loversSection", "loversWinWithKillers", false), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversWinWithKillers", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.loversWinWithCivilians", true, () -> readBoolServer("modifiersSection", "loversSection", "loversWinWithCivilians", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversWinWithCivilians", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("stupidexpress.loversGlowToEachother", true, () -> readBoolServer("modifiersSection", "loversSection", "loversGlowToEachother", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversGlowToEachother", v);
            } catch (Throwable ignored) {
            }
        }));
    }

    public static boolean getNecromancerHasShop() {
        return ConfigUtils.clientBool("stupidexpress.necromancerHasShop", false);
    }

    public static void setNecromancerHasShop(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.necromancerHasShop", v, null);
    }

    public static boolean getArsonistKeepsGameGoing() {
        return ConfigUtils.clientBool("stupidexpress.arsonistKeepsGameGoing", true);
    }

    public static void setArsonistKeepsGameGoing(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.arsonistKeepsGameGoing", v, null);
    }

    public static boolean getBodiesGlowToAmnesiac() {
        return ConfigUtils.clientBool("stupidexpress.bodiesGlowToAmnesiac", true);
    }

    public static void setBodiesGlowToAmnesiac(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.bodiesGlowToAmnesiac", v, null);
    }

    public static boolean getAmnesiacGlowsDifferently() {
        return ConfigUtils.clientBool("stupidexpress.amnesiacGlowsDifferently", false);
    }

    public static void setAmnesiacGlowsDifferently(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.amnesiacGlowsDifferently", v, null);
    }

    public static boolean getLoversKnowImmediately() {
        return ConfigUtils.clientBool("stupidexpress.loversKnowImmediately", true);
    }

    public static void setLoversKnowImmediately(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.loversKnowImmediately", v, null);
    }

    public static boolean getLoversWinWithKillers() {
        return ConfigUtils.clientBool("stupidexpress.loversWinWithKillers", false);
    }

    public static void setLoversWinWithKillers(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.loversWinWithKillers", v, null);
    }

    public static boolean getLoversWinWithCivilians() {
        return ConfigUtils.clientBool("stupidexpress.loversWinWithCivilians", true);
    }

    public static void setLoversWinWithCivilians(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.loversWinWithCivilians", v, null);
    }

    public static boolean getLoversGlowToEachother() {
        return ConfigUtils.clientBool("stupidexpress.loversGlowToEachother", true);
    }

    public static void setLoversGlowToEachother(boolean v) throws Exception {
        ConfigUtils.apply("stupidexpress.loversGlowToEachother", v, null);
    }
}
