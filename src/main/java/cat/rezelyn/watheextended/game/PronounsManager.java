package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtended;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PronounsManager {

    public static final int MAX_LENGTH = 32;
    private static final Map<UUID, String> PRONOUNS = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_FILE = FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("cache").resolve("pronouns.json").toFile();

    private PronounsManager() {}

    public static String get(UUID uuid) {
        return PRONOUNS.getOrDefault(uuid, "");
    }
    public static Map<UUID, String> getAll() {
        return Collections.unmodifiableMap(PRONOUNS);
    }

    public static void set(UUID uuid, String pronouns) {
        if (pronouns == null || pronouns.isBlank()) {
            PRONOUNS.remove(uuid);
        } else {
            String sanitized = pronouns.strip();
            if (sanitized.length() > MAX_LENGTH) sanitized = sanitized.substring(0, MAX_LENGTH);
            PRONOUNS.put(uuid, sanitized);
        }
        save();
    }

    public static void clear(UUID uuid) {
        PRONOUNS.remove(uuid);
        save();
    }

    public static void load() {
        try {
            if (!DATA_FILE.exists()) {
                DATA_FILE.getParentFile().mkdirs();
                return;
            }
            try (FileReader reader = new FileReader(DATA_FILE)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonObject()) return;
                PRONOUNS.clear();
                for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(entry.getKey());
                        String pronouns = entry.getValue().getAsString();
                        if (!pronouns.isBlank()) PRONOUNS.put(uuid, pronouns);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void save() {
        try {
            DATA_FILE.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            PRONOUNS.forEach((uuid, p) -> json.addProperty(uuid.toString(), p));
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public record UpdatePayload(String pronouns) implements CustomPayload {
        public static final Id<UpdatePayload> ID = new Id<>(WatheExtended.id("pronouns_update"));
        public static final PacketCodec<RegistryByteBuf, UpdatePayload> CODEC = PacketCodec.of((value, buffer) -> buffer.writeString(value.pronouns()), buffer -> new UpdatePayload(buffer.readString(MAX_LENGTH + 1)));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record SyncPayload(UUID uuid, String pronouns) implements CustomPayload {
        public static final Id<SyncPayload> ID = new Id<>(WatheExtended.id("pronouns_sync"));
        public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.of((value, buffer) -> {
            buffer.writeUuid(value.uuid());
            buffer.writeString(value.pronouns());
        }, buffer -> new SyncPayload(buffer.readUuid(), buffer.readString(MAX_LENGTH + 1)));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
