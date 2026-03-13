package cat.rezelyn.watheextended.client.pronouns;

import net.minecraft.client.MinecraftClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PronounsCache {

    private PronounsCache() {}

    private static final Map<UUID, String> CACHE = new ConcurrentHashMap<>();
    public static String get(UUID uuid) {
        return CACHE.getOrDefault(uuid, "");
    }

    public static void set(UUID uuid, String pronouns) {
        if (pronouns == null || pronouns.isEmpty()) {
            CACHE.remove(uuid);
        } else {
            CACHE.put(uuid, pronouns);
        }
    }

    public static void clear() {
        CACHE.clear();
    }

    public static String getLocalPronouns() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return "";
        return get(client.player.getUuid());
    }
}
