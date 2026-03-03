package cat.rezelyn.watheextended.api.hml;

import blue.endless.jankson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConfigHelper {

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("harpymodloader.json5").toFile();

    private ConfigHelper() {
    }

    private static HarpyModLoaderConfig getInstance() {
        return HarpyModLoaderConfig.HANDLER.instance();
    }

    private static void save() {
        HarpyModLoaderConfig.HANDLER.save();
    }

    private static List<String> readStringList(String key) {
        try {
            if (!CONFIG_FILE.exists()) return List.of();
            JsonObject obj = Jankson.builder().build().load(CONFIG_FILE);
            JsonElement el = obj.get(key);
            if (!(el instanceof JsonArray arr)) return List.of();
            List<String> result = new ArrayList<>();
            for (JsonElement item : arr)
                if (item instanceof JsonPrimitive prim) result.add(prim.asString());
            return Collections.unmodifiableList(result);
        } catch (Throwable ignored) {
            return List.of();
        }
    }

    private static int readInt(String key, int defaultValue) {
        try {
            if (!CONFIG_FILE.exists()) return defaultValue;
            JsonObject obj = Jankson.builder().build().load(CONFIG_FILE);
            JsonElement el = obj.get(key);
            if (el instanceof JsonPrimitive prim) return Integer.parseInt(prim.asString());
        } catch (Throwable ignored) {
        }
        return defaultValue;
    }

    public static List<String> getDisabledRoles() {
        try {
            List<String> live = getInstance().disabled;
            if (live != null) return Collections.unmodifiableList(live);
        } catch (Throwable ignored) {
        }
        return readStringList("disabled");
    }

    public static List<String> getDisabledModifiers() {
        try {
            List<String> live = getInstance().disabledModifiers;
            if (live != null) return Collections.unmodifiableList(live);
        } catch (Throwable ignored) {
        }
        return readStringList("disabledModifiers");
    }

    public static int getModifierMaximum() {
        try {
            return getInstance().modifierMaximum;
        } catch (Throwable ignored) {
        }
        return readInt("modifierMaximum", 1);
    }

    public static void setModifierMaximum(int value) {
        try {
            getInstance().modifierMaximum = value;
            save();
        } catch (Throwable ignored) {
        }
    }

    public static int getModifierMultiplier() {
        try {
            return getInstance().modifierMultiplier;
        } catch (Throwable ignored) {
        }
        return readInt("modifierMultiplier", 1);
    }

    public static void setModifierMultiplier(int value) {
        try {
            getInstance().modifierMultiplier = value;
            save();
        } catch (Throwable ignored) {
        }
    }
}
