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

    private static Object getWorldComponent(World world) throws Exception {
        Class<?> compClass = Class.forName("org.agmas.noellesroles.ConfigWorldComponent");
        Object key = compClass.getField("KEY").get(null);
        return key.getClass().getMethod("get", Object.class).invoke(key, world);
    }

    private static boolean readBool(String fieldName, boolean defaultValue) {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField(fieldName).get(cfg);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    private static boolean readWorldBool(World world, String fieldName, boolean defaultValue) {
        if (world == null) return readBool(fieldName, defaultValue);
        try {
            Object comp = getWorldComponent(world);
            if (comp == null) return readBool(fieldName, defaultValue);
            return (boolean) comp.getClass().getField(fieldName).get(comp);
        } catch (Throwable t) {
            return readBool(fieldName, defaultValue);
        }
    }

    private static void setWorldBool(World world, String fieldName, boolean value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) {
            comp.getClass().getField(fieldName).set(comp, value);
            comp.getClass().getMethod("sync").invoke(comp);
        }
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    public static boolean getInsanePlayersSeeMorphs(World world) {
        return readWorldBool(world, "insanePlayersSeeMorphs", true);
    }

    public static void setInsanePlayersSeeMorphs(World world, boolean value) throws Exception {
        setWorldBool(world, "insanePlayersSeeMorphs", value);
    }
}
