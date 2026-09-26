package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public final class PresetManager {

    public static final int SCHEMA_VERSION = 1;
    public static final int MAX_NAME_LENGTH = 64;
    public static final int MAX_DESCRIPTION_LENGTH = 256;
    private static final Pattern SAFE_ID = Pattern.compile("[^a-z0-9_-]+");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy-HH:mm:ss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PRESET_DIRECTORY = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("presets");

    private PresetManager() {}

    public record PresetMetadata(String id, String name, String description, String authorName, UUID authorUuid, String createdAt, String updatedAt, Map<String, String> config) {
        public PresetMetadata(String id, String name, String description, String authorName, UUID authorUuid, String createdAt, String updatedAt) {
            this(id, name, description, authorName, authorUuid, createdAt, updatedAt, Map.of());
        }
    }
    public record Preset(PresetMetadata metadata, Map<String, String> config) {}
    public record SaveResult(PresetMetadata metadata, boolean overwritten) {}
    public record ApplyResult(int applied, int skipped) {}
    public static List<PresetMetadata> list() {
        List<PresetMetadata> result = new ArrayList<>();
        try {
            Files.createDirectories(PRESET_DIRECTORY);
            try (var paths = Files.list(PRESET_DIRECTORY)) {
                paths.filter(path -> path.getFileName().toString().endsWith(".json"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .forEach(path -> {
                            try {
                                result.add(read(path).metadata());
                            } catch (Exception exception) {
                                WatheExtended.LOGGER.warn("Ignoring invalid configuration preset {}", path, exception);
                            }
                        });
            }
        } catch (IOException exception) {
            WatheExtended.LOGGER.error("Unable to list configuration presets", exception);
        }
        return result;
    }

    public static SaveResult save(String name, String description, ServerPlayerEntity author, Map<String, String> config) throws IOException {
        String cleanName = normalize(name, MAX_NAME_LENGTH, "Preset");
        String cleanDescription = normalize(description, MAX_DESCRIPTION_LENGTH, "");
        PresetMetadata existing = findByName(cleanName);
        String id = existing != null ? existing.id() : uniqueId(cleanName);
        String now = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        PresetMetadata metadata = new PresetMetadata(id, cleanName, cleanDescription, author.getName().getString(), author.getUuid(), existing != null ? existing.createdAt() : now, now);
        write(new Preset(metadata, new LinkedHashMap<>(config)));
        return new SaveResult(metadata, existing != null);
    }

    public static Preset load(String id) throws IOException {
        Path path = resolvePresetPath(id);
        if (!Files.exists(path)) throw new IOException("Preset does not exist: " + id);
        return read(path);
    }

    public static void delete(String id) throws IOException {
        Files.deleteIfExists(resolvePresetPath(id));
    }

    public static ApplyResult apply(Preset preset, ServerWorld world) {
        int applied = 0;
        int skipped = 0;
        Map<String, ServerConfig.Entry<?>> entries = ServerConfig.entries();
        Map<String, String> knownChanges = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : preset.config().entrySet()) {
            if (entries.containsKey(entry.getKey())) {
                knownChanges.put(entry.getKey(), entry.getValue());
                applied++;
            } else {
                skipped++;
            }
        }

        ServerConfig.applyChanges(knownChanges, world);

        WatheExtendedServerConfig.save();
        ItemPrices.applyAll();
        ItemCooldowns.applyAll();
        return new ApplyResult(applied, skipped);
    }

    public static void sendList(MinecraftServer server) {
        ListPayload payload = buildListPayload();
        server.execute(() -> server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, payload)));
    }

    public static void sendList(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, buildListPayload());
    }

    private static ListPayload buildListPayload() {
        NbtCompound root = new NbtCompound();
        NbtList presets = new NbtList();
        for (PresetMetadata metadata : list()) presets.add(toNbt(metadata));
        root.put("presets", presets);
        return new ListPayload(root);
    }

    public static NbtCompound toNbt(PresetMetadata metadata) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", metadata.id());
        nbt.putString("name", metadata.name());
        nbt.putString("description", metadata.description());
        nbt.putString("authorName", metadata.authorName());
        nbt.putString("authorUuid", metadata.authorUuid().toString());
        nbt.putString("createdAt", metadata.createdAt());
        nbt.putString("updatedAt", metadata.updatedAt());
        NbtCompound config = new NbtCompound();
        metadata.config().forEach(config::putString);
        nbt.put("config", config);
        return nbt;
    }

    public static List<PresetMetadata> fromNbt(NbtCompound root) {
        List<PresetMetadata> result = new ArrayList<>();
        if (root == null) return result;
        if (!(root.get("presets") instanceof NbtList list)) return result;
        for (NbtElement el : list) {
            if (!(el instanceof NbtCompound nbt)) continue;
            try {
                result.add(new PresetMetadata(
                        nbt.getString("id"),
                        nbt.getString("name"),
                        nbt.getString("description"),
                        nbt.getString("authorName"),
                        UUID.fromString(nbt.getString("authorUuid")),
                        nbt.getString("createdAt"),
                        nbt.getString("updatedAt"),
                        readConfigFromNbt(nbt)
                ));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private static Map<String, String> readConfigFromNbt(NbtCompound nbt) {
        Map<String, String> config = new LinkedHashMap<>();
        if (!nbt.contains("config", NbtElement.COMPOUND_TYPE)) return config;
        NbtCompound values = nbt.getCompound("config");
        for (String key : values.getKeys()) {
            config.put(key, values.getString(key));
        }
        return config;
    }

    private static PresetMetadata findByName(String name) {
        return list().stream().filter(metadata -> metadata.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private static String uniqueId(String name) {
        String base = SAFE_ID.matcher(name.toLowerCase(Locale.ROOT).replace(' ', '-')).replaceAll("");
        if (base.isBlank()) base = "preset";
        String id = base;
        int suffix = 2;
        while (Files.exists(PRESET_DIRECTORY.resolve(id + ".json"))) id = base + "-" + suffix++;
        return id;
    }

    private static String normalize(String value, int maxLength, String fallback) {
        if (value == null) return fallback;
        String normalized = value.strip();
        if (normalized.length() > maxLength) normalized = normalized.substring(0, maxLength);
        return normalized.isBlank() ? fallback : normalized;
    }

    private static Path resolvePresetPath(String id) throws IOException {
        if (id == null || !id.matches("[a-z0-9_-]+")) throw new IOException("Invalid preset id");
        Path directory = PRESET_DIRECTORY.toAbsolutePath().normalize();
        Path path = directory.resolve(id + ".json").normalize();
        if (!directory.equals(path.getParent())) throw new IOException("Invalid preset path");
        return path;
    }

    private static Preset read(Path path) throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
        int schemaVersion = root.has("schemaVersion") ? root.get("schemaVersion").getAsInt() : 0;
        if (schemaVersion != SCHEMA_VERSION) throw new IOException("Unsupported schema version " + schemaVersion);

        UUID authorUuid;
        try {
            authorUuid = UUID.fromString(root.get("author").getAsJsonObject().get("uuid").getAsString());
        } catch (Exception exception) {
            authorUuid = new UUID(0, 0);
        }

        Map<String, String> config = new LinkedHashMap<>();
        JsonObject configObject = root.getAsJsonObject("config");
        if (configObject != null) {
            for (Map.Entry<String, JsonElement> entry : configObject.entrySet()) {
                if (!entry.getValue().isJsonNull()) config.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        JsonObject authorObject = root.getAsJsonObject("author");
        String authorName = authorObject != null && authorObject.has("name") ? authorObject.get("name").getAsString() : "Unknown";
        PresetMetadata metadata = new PresetMetadata(
                root.get("id").getAsString(),
                root.get("name").getAsString(),
                root.has("description") ? root.get("description").getAsString() : "",
                authorName,
                authorUuid,
                root.has("createdAt") ? root.get("createdAt").getAsString() : "",
                root.has("updatedAt") ? root.get("updatedAt").getAsString() : "",
                config
        );
        return new Preset(metadata, config);
    }

    private static void write(Preset preset) throws IOException {
        Files.createDirectories(PRESET_DIRECTORY);
        JsonObject root = new JsonObject();
        root.addProperty("schemaVersion", SCHEMA_VERSION);
        root.addProperty("id", preset.metadata().id());
        root.addProperty("name", preset.metadata().name());
        root.addProperty("description", preset.metadata().description());
        JsonObject author = new JsonObject();
        author.addProperty("name", preset.metadata().authorName());
        author.addProperty("uuid", preset.metadata().authorUuid().toString());
        root.add("author", author);
        root.addProperty("createdAt", preset.metadata().createdAt());
        root.addProperty("updatedAt", preset.metadata().updatedAt());
        JsonObject config = new JsonObject();
        preset.config().forEach(config::addProperty);
        root.add("config", config);

        Path target = resolvePresetPath(preset.metadata().id());
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
        try {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException exception) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public record ActionPayload(String action, String id, String name, String description) implements CustomPayload {
        public static final Id<ActionPayload> ID = new Id<>(WatheExtended.id("preset_action"));
        public static final PacketCodec<RegistryByteBuf, ActionPayload> CODEC = PacketCodec.of((value, buffer) -> {
            buffer.writeString(value.action());
            buffer.writeString(value.id() == null ? "" : value.id());
            buffer.writeString(value.name() == null ? "" : value.name());
            buffer.writeString(value.description() == null ? "" : value.description());
        }, buffer -> new ActionPayload(buffer.readString(16), buffer.readString(128), buffer.readString(MAX_NAME_LENGTH), buffer.readString(MAX_DESCRIPTION_LENGTH)));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record ListPayload(NbtCompound data) implements CustomPayload {
        public static final Id<ListPayload> ID = new Id<>(WatheExtended.id("preset_list"));
        public static final PacketCodec<RegistryByteBuf, ListPayload> CODEC = PacketCodec.of((value, buffer) -> buffer.writeNbt(value.data()), buffer -> new ListPayload(buffer.readNbt()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record ResultPayload(boolean success, String action, String message) implements CustomPayload {
        public static final Id<ResultPayload> ID = new Id<>(WatheExtended.id("preset_result"));
        public static final PacketCodec<RegistryByteBuf, ResultPayload> CODEC = PacketCodec.of((value, buffer) -> {
            buffer.writeBoolean(value.success());
            buffer.writeString(value.action());
            buffer.writeString(value.message());
        }, buffer -> new ResultPayload(buffer.readBoolean(), buffer.readString(16), buffer.readString(256)));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
