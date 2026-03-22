package cat.rezelyn.watheextended.api.config;

import blue.endless.jankson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientConfig {

    private ClientConfig() {}

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean remoteServer = false;

    public static void setRemoteServer(boolean remote) {
        remoteServer = remote;
        if (!remote) CACHE.clear();
    }

    public static boolean isRemoteServer() {
        return remoteServer;
    }

    public static void update(NbtCompound data) {
        CACHE.clear();
        for (String key : data.getKeys()) CACHE.put(key, data.getString(key));
    }

    public static void clear() {
        remoteServer = false;
        CACHE.clear();
    }

    public static boolean getBool(String key, boolean def) {
        String v = CACHE.get(key);
        return v == null ? def : Boolean.parseBoolean(v);
    }

    public static int getInt(String key, int def) {
        String v = CACHE.get(key);
        if (v == null) return def;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static float getFloat(String key, float def) {
        String v = CACHE.get(key);
        if (v == null) return def;
        try {
            return Float.parseFloat(v);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getString(String key, String def) {
        return CACHE.getOrDefault(key, def);
    }

    public static List<String> getStringList(String key) {
        String v = CACHE.get(key);
        if (v == null || v.isEmpty()) return Collections.emptyList();
        return Arrays.asList(v.split(",", -1));
    }

    public static boolean readBool(File file, String dotKey, boolean def) {
        try {
            JsonElement el = resolve(load(file), dotKey);
            if (el instanceof JsonPrimitive p) return Boolean.parseBoolean(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    public static int readInt(File file, String dotKey, int def) {
        try {
            JsonElement el = resolve(load(file), dotKey);
            if (el instanceof JsonPrimitive p) return Integer.parseInt(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    public static float readFloat(File file, String dotKey, float def) {
        try {
            JsonElement el = resolve(load(file), dotKey);
            if (el instanceof JsonPrimitive p) return Float.parseFloat(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    public static String readString(File file, String dotKey, String def) {
        try {
            JsonElement el = resolve(load(file), dotKey);
            if (el instanceof JsonPrimitive p) return p.asString();
        } catch (Throwable ignored) {
        }
        return def;
    }

    public static List<String> readStringList(File file, String dotKey) {
        try {
            JsonElement el = resolve(load(file), dotKey);
            if (!(el instanceof JsonArray arr)) return List.of();
            List<String> out = new ArrayList<>(arr.size());
            for (JsonElement item : arr) if (item instanceof JsonPrimitive p) out.add(p.asString());
            return Collections.unmodifiableList(out);
        } catch (Throwable ignored) {
            return List.of();
        }
    }

    public static File configFile(String filename) {
        return FabricLoader.getInstance().getConfigDir().resolve(filename).toFile();
    }

    public static File configFile(String dir, String filename) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir).resolve(filename).toFile();
    }

    private static JsonObject load(File file) throws Exception {
        if (!file.exists()) throw new IllegalStateException("missing: " + file);
        return Jankson.builder().build().load(file);
    }

    private static JsonElement resolve(JsonObject root, String dotKey) {
        String[] parts = dotKey.split("\\.", 2);
        JsonElement el = root.get(parts[0]);
        if (parts.length == 1 || el == null) return el;
        if (!(el instanceof JsonObject nested)) return null;
        return resolve(nested, parts[1]);
    }
}
