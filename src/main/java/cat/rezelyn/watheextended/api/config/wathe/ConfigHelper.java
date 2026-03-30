package cat.rezelyn.watheextended.api.config.wathe;

import cat.rezelyn.watheextended.api.config.ConfigUtils;

public final class ConfigHelper {

    private ConfigHelper() {}

    private static final String CONFIG_CLASS = "dev.doctor4t.wathe.WatheConfig";

    private static void writeChanges() {
        try {
            Class<?> cls = Class.forName(CONFIG_CLASS);
            Object instance = cls.getDeclaredConstructor().newInstance();
            ConfigUtils.invokeWith(instance, "writeChanges", String.class, "wathe");
        } catch (Throwable ignored) {
        }
    }

    private static boolean readBool(String field, boolean def) {
        return ConfigUtils.readStaticBool(CONFIG_CLASS, field, def);
    }

    private static void writeBool(String field, boolean value) {
        try {
            ConfigUtils.setStaticField(CONFIG_CLASS, field, value);
            writeChanges();
        } catch (Throwable ignored) {
        }
    }

    public static boolean getUltraPerfMode() {
        return readBool("ultraPerfMode", false);
    }

    public static void setUltraPerfMode(boolean value) {
        writeBool("ultraPerfMode", value);
    }

    public static boolean getDisableScreenShake() {
        return readBool("disableScreenShake", false);
    }

    public static void setDisableScreenShake(boolean value) {
        writeBool("disableScreenShake", value);
    }
}
