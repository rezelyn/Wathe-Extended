package cat.rezelyn.watheextended.api.stupidexpress;

import cat.rezelyn.watheextended.api.ClientConfig;
import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.api.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("stupid_express");
    }

    private static Object getConfig() throws Exception {
        Class<?> cls = Class.forName("pro.fazeclan.river.stupid_express.StupidExpress");
        return cls.getField("CONFIG").get(null);
    }

    private static Object getNestedSection(Object cfg, String topField, String subField) throws Exception {
        Object top = cfg.getClass().getField(topField).get(cfg);
        return top.getClass().getField(subField).get(top);
    }

    private static void saveConfig(Object cfg) {
        try {
            cfg.getClass().getMethod("save").invoke(cfg);
        } catch (Throwable ignored) {
        }
    }

    private static boolean readBoolServer(String topSection, String subSection, String field, boolean def) {
        try {
            Object cfg = getConfig();
            Object section = getNestedSection(cfg, topSection, subSection);
            return (boolean) section.getClass().getField(field).get(section);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBoolServer(String topSection, String subSection, String field, boolean value) throws Exception {
        Object cfg = getConfig();
        Object section = getNestedSection(cfg, topSection, subSection);
        section.getClass().getField(field).set(section, value);
        saveConfig(cfg);
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        reg(Entry.globalBool("stupidexpress.necromancerHasShop", false, () -> readBoolServer("rolesSection", "necromancerSection", "necromancerHasShop", false), v -> {
            try {
                writeBoolServer("rolesSection", "necromancerSection", "necromancerHasShop", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.arsonistKeepsGameGoing", true, () -> readBoolServer("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", true), v -> {
            try {
                writeBoolServer("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.bodiesGlowToAmnesiac", true, () -> readBoolServer("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", true), v -> {
            try {
                writeBoolServer("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.amnesiacGlowsDifferently", false, () -> readBoolServer("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", false), v -> {
            try {
                writeBoolServer("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.loversKnowImmediately", true, () -> readBoolServer("modifiersSection", "loversSection", "loversKnowImmediately", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversKnowImmediately", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.loversWinWithKillers", false, () -> readBoolServer("modifiersSection", "loversSection", "loversWinWithKillers", false), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversWinWithKillers", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.loversWinWithCivilians", true, () -> readBoolServer("modifiersSection", "loversSection", "loversWinWithCivilians", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversWinWithCivilians", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("stupidexpress.loversGlowToEachother", true, () -> readBoolServer("modifiersSection", "loversSection", "loversGlowToEachother", true), v -> {
            try {
                writeBoolServer("modifiersSection", "loversSection", "loversGlowToEachother", v);
            } catch (Throwable ignored) {
            }
        }));
    }

    private static <T> void reg(Entry<T> e) {
        ServerConfig.register(e);
    }

    private static boolean c(String key, boolean def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getBool(key, def);
        return def;
    }

    public static boolean getNecromancerHasShop() {
        return c("stupidexpress.necromancerHasShop", false);
    }

    public static void setNecromancerHasShop(boolean v) throws Exception {
        apply("stupidexpress.necromancerHasShop", v);
    }

    public static boolean getArsonistKeepsGameGoing() {
        return c("stupidexpress.arsonistKeepsGameGoing", true);
    }

    public static void setArsonistKeepsGameGoing(boolean v) throws Exception {
        apply("stupidexpress.arsonistKeepsGameGoing", v);
    }

    public static boolean getBodiesGlowToAmnesiac() {
        return c("stupidexpress.bodiesGlowToAmnesiac", true);
    }

    public static void setBodiesGlowToAmnesiac(boolean v) throws Exception {
        apply("stupidexpress.bodiesGlowToAmnesiac", v);
    }

    public static boolean getAmnesiacGlowsDifferently() {
        return c("stupidexpress.amnesiacGlowsDifferently", false);
    }

    public static void setAmnesiacGlowsDifferently(boolean v) throws Exception {
        apply("stupidexpress.amnesiacGlowsDifferently", v);
    }

    public static boolean getLoversKnowImmediately() {
        return c("stupidexpress.loversKnowImmediately", true);
    }

    public static void setLoversKnowImmediately(boolean v) throws Exception {
        apply("stupidexpress.loversKnowImmediately", v);
    }

    public static boolean getLoversWinWithKillers() {
        return c("stupidexpress.loversWinWithKillers", false);
    }

    public static void setLoversWinWithKillers(boolean v) throws Exception {
        apply("stupidexpress.loversWinWithKillers", v);
    }

    public static boolean getLoversWinWithCivilians() {
        return c("stupidexpress.loversWinWithCivilians", true);
    }

    public static void setLoversWinWithCivilians(boolean v) throws Exception {
        apply("stupidexpress.loversWinWithCivilians", v);
    }

    public static boolean getLoversGlowToEachother() {
        return c("stupidexpress.loversGlowToEachother", true);
    }

    public static void setLoversGlowToEachother(boolean v) throws Exception {
        apply("stupidexpress.loversGlowToEachother", v);
    }

    @SuppressWarnings("unchecked")
    private static <T> void apply(String key, T value) {
        Entry<T> entry = (Entry<T>) ServerConfig.entries().get(key);
        if (entry != null) entry.writeServer(null, value);
    }
}
