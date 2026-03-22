package cat.rezelyn.watheextended.api.wathe;

public final class ConfigHelper {

    private ConfigHelper() {}

    private static Class<?> getConfigClass() throws Exception {
        return Class.forName("dev.doctor4t.wathe.WatheConfig");
    }

    private static void writeChanges() {
        try {
            Class<?> cls = getConfigClass();
            Object instance = cls.getDeclaredConstructor().newInstance();
            cls.getMethod("writeChanges", String.class).invoke(instance, "wathe");
        } catch (Throwable ignored) {
        }
    }

    private static boolean readBool(String field, boolean defaultValue) {
        try {
            return (boolean) getConfigClass().getField(field).get(null);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    private static void writeBool(String field, boolean value) {
        try {
            getConfigClass().getField(field).set(null, value);
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
