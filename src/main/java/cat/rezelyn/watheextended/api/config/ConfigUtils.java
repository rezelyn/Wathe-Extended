package cat.rezelyn.watheextended.api.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigUtils {

    private ConfigUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> void apply(String key, T value, World world) {
        ServerConfig.Entry<T> entry = (ServerConfig.Entry<T>) ServerConfig.entries().get(key);
        if (entry != null) entry.writeServer(world, value);
    }

    public static boolean clientBool(String key, boolean def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getBool(key, def);
        return def;
    }

    public static int clientInt(String key, int def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getInt(key, def);
        return def;
    }

    public static float clientFloat(String key, float def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getFloat(key, def);
        return def;
    }

    public static String clientString(String key, String def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getString(key, def);
        return def;
    }

    public static Object getStaticField(String className, String fieldName) throws Exception {
        return Class.forName(className).getField(fieldName).get(null);
    }

    public static void setStaticField(String className, String fieldName, Object value) throws Exception {
        Class.forName(className).getField(fieldName).set(null, value);
    }

    public static boolean readStaticBool(String className, String field, boolean def) {
        try {
            return (boolean) getStaticField(className, field);
        } catch (Throwable t) {
            return def;
        }
    }

    public static Object getField(Object obj, String fieldName) throws Exception {
        return obj.getClass().getField(fieldName).get(obj);
    }

    public static void setField(Object obj, String fieldName, Object value) throws Exception {
        obj.getClass().getField(fieldName).set(obj, value);
    }

    public static Object invoke(Object obj, String methodName) throws Exception {
        return obj.getClass().getMethod(methodName).invoke(obj);
    }

    public static Object invokeWith(Object obj, String methodName, Class<?> argType, Object value) throws Exception {
        return obj.getClass().getMethod(methodName, argType).invoke(obj, value);
    }

    public static Object getInstance(String className) throws Exception {
        Object handler = getStaticField(className, "HANDLER");
        return invoke(handler, "instance");
    }

    public static void save(String className) throws Exception {
        Object handler = getStaticField(className, "HANDLER");
        invoke(handler, "save");
    }

    public static boolean readBool(String className, String field, boolean def) {
        try {
            return (boolean) getField(getInstance(className), field);
        } catch (Throwable throwable) {
            return def;
        }
    }

    public static int readInt(String className, String field, int def) {
        try {
            return (int) getField(getInstance(className), field);
        } catch (Throwable throwable) {
            return def;
        }
    }

    public static String readString(String className, String field, String def) {
        try {
            Object val = getField(getInstance(className), field);
            return val instanceof String string ? string : def;
        } catch (Throwable throwable) {
            return def;
        }
    }

    public static void writeBool(String className, String field, boolean value) throws Exception {
        setField(getInstance(className), field, value);
        save(className);
    }

    public static void writeInt(String className, String field, int value) throws Exception {
        setField(getInstance(className), field, value);
        save(className);
    }

    public static void writeString(String className, String field, String value) throws Exception {
        setField(getInstance(className), field, value);
        save(className);
    }
}
