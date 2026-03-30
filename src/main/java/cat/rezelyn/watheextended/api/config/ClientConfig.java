package cat.rezelyn.watheextended.api.config;

import blue.endless.jankson.*;
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
        String value = CACHE.get(key);
        return value == null ? def : Boolean.parseBoolean(value);
    }

    public static int getInt(String key, int def) {
        String value = CACHE.get(key);
        if (value == null) return def;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return def;
        }
    }

    public static float getFloat(String key, float def) {
        String value = CACHE.get(key);
        if (value == null) return def;
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException exception) {
            return def;
        }
    }

    public static String getString(String key, String def) {
        return CACHE.getOrDefault(key, def);
    }

    public static List<String> getStringList(String key) {
        String value = CACHE.get(key);
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return Arrays.asList(value.split(",", -1));
    }

    // typed access to a synced entry from the cache, with automatic deserialization and fallback to default
    public static <T> T get(ServerConfig.Entry<T> entry) {
        String raw = CACHE.get(entry.key);
        return raw != null ? entry.deserialize(raw) : entry.defaultValue;
    }

    // parses the file once and returns a reader for multiple key lookups
    public static Reader reader(File file) {
        try {
            return new Reader(load(file));
        } catch (Throwable ignored) {
            return new Reader(null);
        }
    }

    public static boolean readBool(File file, String dotKey, boolean def) {
        return reader(file).getBool(dotKey, def);
    }

    public static int readInt(File file, String dotKey, int def) {
        return reader(file).getInt(dotKey, def);
    }

    public static float readFloat(File file, String dotKey, float def) {
        return reader(file).getFloat(dotKey, def);
    }

    public static String readString(File file, String dotKey, String def) {
        return reader(file).getString(dotKey, def);
    }

    private static JsonObject load(File file) throws Exception {
        if (!file.exists()) throw new IllegalStateException("missing: " + file);
        return Jankson.builder().build().load(file);
    }

    private static JsonElement resolve(JsonObject root, String dotKey) {
        String[] parts = dotKey.split("\\.", 2);
        JsonElement element = root.get(parts[0]);
        if (parts.length == 1 || element == null) return element;
        if (!(element instanceof JsonObject nested)) return null;
        return resolve(nested, parts[1]);
    }

    public static final class Reader {
        private final JsonObject json;

        Reader(JsonObject json) {
            this.json = json;
        }

        public boolean getBool(String dotKey, boolean def) {
            if (json == null) return def;
            try {
                JsonElement e = resolve(json, dotKey);
                return e instanceof JsonPrimitive p ? Boolean.parseBoolean(p.asString()) : def;
            } catch (Throwable t) {
                return def;
            }
        }

        public int getInt(String dotKey, int def) {
            if (json == null) return def;
            try {
                JsonElement e = resolve(json, dotKey);
                if (!(e instanceof JsonPrimitive p)) return def;
                return Integer.parseInt(p.asString());
            } catch (Throwable t) {
                return def;
            }
        }

        public float getFloat(String dotKey, float def) {
            if (json == null) return def;
            try {
                JsonElement e = resolve(json, dotKey);
                if (!(e instanceof JsonPrimitive p)) return def;
                return Float.parseFloat(p.asString());
            } catch (Throwable t) {
                return def;
            }
        }

        public String getString(String dotKey, String def) {
            if (json == null) return def;
            try {
                JsonElement e = resolve(json, dotKey);
                return e instanceof JsonPrimitive p ? p.asString() : def;
            } catch (Throwable t) {
                return def;
            }
        }
    }
}
