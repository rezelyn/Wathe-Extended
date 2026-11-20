package cat.rezelyn.watheextended.network;

import cat.rezelyn.orelib.api.preset.PresetInfo;
import cat.rezelyn.orelib.api.preset.PresetStorage;
import cat.rezelyn.orelib.api.preset.StoredPreset;
import cat.rezelyn.watheextended.WatheExtended;
import com.google.gson.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.UnaryOperator;

/** Adapts Wathe's existing one-file-per-preset schema to OreLib's live preset store. */
public final class PresetManager implements PresetStorage {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final DateTimeFormatter LEGACY_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy-HH:mm:ss");
  private final Path directory;
  private final String authorName;
  private final UUID authorUuid;
  private final Set<String> roles;
  private final Set<String> modifiers;

  PresetManager(Path directory, String authorName, UUID authorUuid, Set<String> roles, Set<String> modifiers) {
    this.directory = directory.toAbsolutePath().normalize();
    this.authorName = authorName;
    this.authorUuid = authorUuid;
    this.roles = Set.copyOf(roles);
    this.modifiers = Set.copyOf(modifiers);
  }

  private record FilePreset(JsonObject document, StoredPreset preset) {}

  @Override
  public List<StoredPreset> list() {
    return readFiles().values().stream().map(FilePreset::preset).toList();
  }

  @Override
  public void update(UnaryOperator<List<StoredPreset>> operation) {
    // OreLib's server-thread operations each mutate one file. Check that contract before writing.
    var existing = readFiles();
    var updated = new LinkedHashMap<String, StoredPreset>();
    for (var preset : operation.apply(existing.values().stream().map(FilePreset::preset).toList())) {
      if (updated.putIfAbsent(preset.id(), preset) != null)
        throw new IllegalArgumentException("Duplicate preset ID: " + preset.id());
    }
    var changed = new LinkedHashSet<>(existing.keySet());
    changed.addAll(updated.keySet());
    changed.removeIf(id -> existing.containsKey(id) && existing.get(id).preset().equals(updated.get(id)));
    if (changed.isEmpty()) return;
    if (changed.size() != 1) throw new IllegalArgumentException("Expected a single preset mutation");
    String id = changed.iterator().next();
    try {
      var target = resolvePresetPath(id);
      if (!updated.containsKey(id)) {
        Files.delete(target);
        return;
      }
      if (!existing.containsKey(id) && Files.exists(target))
        throw new IOException("Cannot replace unreadable preset: " + id);
      write(target, existing.get(id), updated.get(id));
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  private Map<String, FilePreset> readFiles() {
    var result = new LinkedHashMap<String, FilePreset>();
    if (Files.notExists(directory)) return result;
    try (var files = Files.list(directory)) {
      for (Path path : files.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toList()) {
        try {
          JsonObject doc = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
          if (doc.get("schemaVersion").getAsInt() != 1) throw new IllegalArgumentException("Unsupported preset version");
          String id = doc.get("id").getAsString();
          if (!resolvePresetPath(id).equals(path)) throw new IllegalArgumentException("Preset filename and ID disagree");
          JsonObject author = doc.getAsJsonObject("author");
          var info = new PresetInfo(author == null ? "Unknown" : text(author, "name"),
              iso(text(doc, "createdAt")), iso(text(doc, "updatedAt")));
          var values = new LinkedHashMap<String, JsonElement>();
          var config = doc.getAsJsonObject("config");
          if (config != null) {
            config.asMap().forEach((key, value) -> {
              if (value.isJsonNull()) return;
              String raw = value.getAsString();
              if (key.equals("hml.disabled")) expand(values, "role.enabled.", roles, raw);
              else if (key.equals("hml.disabledModifiers")) expand(values, "modifier.enabled.", modifiers, raw);
              else if ((key.startsWith("role.enabled.") || key.startsWith("modifier.enabled."))
                  && (raw.equals("true") || raw.equals("false"))) values.put(key, new JsonPrimitive(Boolean.parseBoolean(raw)));
              else values.put(key, new JsonPrimitive(raw));
            });
          }
          var preset = new StoredPreset(id, doc.get("name").getAsString(), text(doc, "description"), values, info);
          result.put(id, new FilePreset(doc, preset));
        } catch (RuntimeException | IOException exception) {
          WatheExtended.LOGGER.warn("Ignoring invalid configuration preset {}", path, exception);
        }
      }
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
    return result;
  }

  private static void expand(Map<String, JsonElement> values, String prefix, Set<String> known, String csv) {
    for (String id : known) values.put(prefix + id, new JsonPrimitive(true));
    for (String id : csv.split(",")) {
      if (!id.isEmpty()) values.put(prefix + id, new JsonPrimitive(false));
    }
  }

  private static String text(JsonObject object, String name) {
    return object.has(name) ? object.get(name).getAsString() : "";
  }

  private static String iso(String original) {
    try {
      return Instant.parse(original).toString();
    } catch (DateTimeParseException exception) {
      try {
        return LocalDateTime.parse(original, LEGACY_DATE).atZone(ZoneId.systemDefault()).toInstant().toString();
      } catch (DateTimeParseException unknownFormat) {
        return "";
      }
    }
  }

  private Path resolvePresetPath(String id) throws IOException {
    if (id == null || !id.matches("[a-z0-9_-]+")) throw new IOException("Invalid preset id");

    Path path = directory.resolve(id + ".json").normalize();
    if (!directory.equals(path.getParent())) throw new IOException("Invalid preset path");
    return path;
  }

  private void write(Path path, FilePreset existing, StoredPreset replacement) throws IOException {
    JsonObject doc = existing == null ? new JsonObject() : existing.document().deepCopy();
    doc.addProperty("id", replacement.id());
    doc.addProperty("schemaVersion", 1);
    doc.addProperty("name", replacement.name());
    doc.addProperty("description", replacement.description());
    if (existing == null) {
      var author = new JsonObject();
      author.addProperty("name", authorName);
      author.addProperty("uuid", authorUuid.toString());
      doc.add("author", author);
      doc.addProperty("createdAt", replacement.metadata().createdAt());
    }
    doc.addProperty("updatedAt", replacement.metadata().updatedAt());
    var values = new JsonObject();
    replacement.values().forEach((key, value) -> values.addProperty(key, value.getAsString()));
    doc.add("config", values);
    Files.createDirectories(directory);
    Path temporary = Files.createTempFile(directory, ".preset-", ".tmp");
    try {
      Files.writeString(temporary, GSON.toJson(doc), StandardCharsets.UTF_8);
      try {
        Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException unsupported) {
        Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(temporary);
    }
  }
}
