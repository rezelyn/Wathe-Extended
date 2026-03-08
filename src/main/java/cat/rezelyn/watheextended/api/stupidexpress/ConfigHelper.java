package cat.rezelyn.watheextended.api.stupidexpress;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("stupid_express");
    }

    private static Object getConfig() throws Exception {
        Class<?> mainClass = Class.forName("pro.fazeclan.river.stupid_express.StupidExpress");
        return mainClass.getField("CONFIG").get(null);
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


    private static boolean readBool(String topSection, String subSection, String field, boolean def) {
        try {
            Object cfg = getConfig();
            Object section = getNestedSection(cfg, topSection, subSection);
            return (boolean) section.getClass().getField(field).get(section);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBool(String topSection, String subSection, String field, boolean value) throws Exception {
        Object cfg = getConfig();
        Object section = getNestedSection(cfg, topSection, subSection);
        section.getClass().getField(field).set(section, value);
        saveConfig(cfg);
    }

    public static boolean getNecromancerHasShop() {
        return readBool("rolesSection", "necromancerSection", "necromancerHasShop", false);
    }

    public static void setNecromancerHasShop(boolean value) throws Exception {
        writeBool("rolesSection", "necromancerSection", "necromancerHasShop", value);
    }

    public static boolean getArsonistKeepsGameGoing() {
        return readBool("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", false);
    }

    public static void setArsonistKeepsGameGoing(boolean value) throws Exception {
        writeBool("rolesSection", "arsonistSection", "arsonistKeepsGameGoing", value);
    }

    public static boolean getBodiesGlowToAmnesiac() {
        return readBool("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", true);
    }

    public static void setBodiesGlowToAmnesiac(boolean value) throws Exception {
        writeBool("rolesSection", "amnesiacSection", "bodiesGlowToAmnesiac", value);
    }

    public static boolean getAmnesiacGlowsDifferently() {
        return readBool("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", true);
    }

    public static void setAmnesiacGlowsDifferently(boolean value) throws Exception {
        writeBool("rolesSection", "amnesiacSection", "amnesiacGlowsDifferently", value);
    }

    public static boolean getLoversKnowImmediately() {
        return readBool("modifiersSection", "loversSection", "loversKnowImmediately", true);
    }

    public static void setLoversKnowImmediately(boolean value) throws Exception {
        writeBool("modifiersSection", "loversSection", "loversKnowImmediately", value);
    }

    public static boolean getLoversWinWithKillers() {
        return readBool("modifiersSection", "loversSection", "loversWinWithKillers", false);
    }

    public static void setLoversWinWithKillers(boolean value) throws Exception {
        writeBool("modifiersSection", "loversSection", "loversWinWithKillers", value);
    }

    public static boolean getLoversWinWithCivilians() {
        return readBool("modifiersSection", "loversSection", "loversWinWithCivilians", true);
    }

    public static void setLoversWinWithCivilians(boolean value) throws Exception {
        writeBool("modifiersSection", "loversSection", "loversWinWithCivilians", value);
    }

    public static boolean getLoversGlowToEachother() {
        return readBool("modifiersSection", "loversSection", "loversGlowToEachother", false);
    }

    public static void setLoversGlowToEachother(boolean value) throws Exception {
        writeBool("modifiersSection", "loversSection", "loversGlowToEachother", value);
    }
}