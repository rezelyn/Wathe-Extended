package cat.rezelyn.watheextended.network;

import com.google.gson.*;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads/saves every non-final {@code static} field of a class to {@code
 * config/watheextended/<fileName>} as plain JSON
 *
 * <p>Missing keys keep their defaults, invalid values are skipped, and a file that can't be parsed
 * is moved to {@code <fileName>.bak} before defaults are written. {@link Map} fields are merged
 * with their current (default) content instead of replaced. Range validation is left to YACL
 */
public final class ConfigHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger("watheextended");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final Path path;
  private final Map<String, Field> fields;

  public ConfigHelper(Class<?> owner, String fileName) {
    this.path =
        FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve(fileName);
    this.fields = scanFields(owner);
  }

  @SuppressWarnings("unchecked")
  private static void apply(Field field, Object value) throws ReflectiveOperationException {
    if (value == null) return;
    Class<?> type = field.getType();
    if (value instanceof Number n) {
      if (type == int.class) value = n.intValue();
      else if (type == float.class) value = n.floatValue();
    } else if (value instanceof Map<?, ?> map) {
      ((Map<Object, Object>) field.get(null)).putAll(map); // merge so defaults are kept
      return;
    }
    field.set(null, value);
  }

  private static Object read(Field field) {
    try {
      return field.get(null);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private static Map<String, Field> scanFields(Class<?> owner) {
    Map<String, Field> fields = new LinkedHashMap<>();
    for (Field field : owner.getDeclaredFields()) {
      int mod = field.getModifiers();
      if (!Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isTransient(mod)) continue;
      field.setAccessible(true);
      fields.put(field.getName(), field);
    }
    return Collections.unmodifiableMap(fields);
  }

  private boolean exists() {
    return Files.exists(path);
  }

  /** Reads the file (if any) into the fields, then saves so new options are added to it */
  public void load() {
    if (exists()) {
      try (Reader reader = Files.newBufferedReader(path)) {
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
          JsonElement element = json.get(entry.getKey());
          if (element == null) continue;
          try {
            apply(entry.getValue(), GSON.fromJson(element, entry.getValue().getGenericType()));
          } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn(
                "Invalid value for '{}' in {}, keeping {}",
                entry.getKey(),
                path.getFileName(),
                get(entry.getKey()));
          }
        }
      } catch (IOException | RuntimeException e) {
        LOGGER.error("Could not read {}, backing it up and using defaults", path, e);
        backup();
      }
    }
    save();
  }

  public void save() {
    try {
      JsonObject json = new JsonObject();
      for (Map.Entry<String, Field> entry : fields.entrySet()) {
        json.add(entry.getKey(), GSON.toJsonTree(read(entry.getValue())));
      }
      Files.createDirectories(path.getParent());
      Files.writeString(path, GSON.toJson(json));
    } catch (IOException | RuntimeException e) {
      LOGGER.error("Could not save {}", path, e);
    }
  }

  /** Names of every option (JSON keys/field names) */
  public Set<String> keys() {
    return Collections.unmodifiableSet(fields.keySet());
  }

  /** Current value of an option, or {@code null} if it doesnt exist */
  public Object get(String name) {
    Field field = fields.get(name);
    return field == null ? null : read(field);
  }

  /**
   * Sets an option and saves. Numbers are converted to the field's numeric type
   *
   * @return {@code false} if the option doesn't exist or the value has the wrong type
   */
  public boolean set(String name, Object value) {
    Field field = fields.get(name);
    if (field == null) return false;
    try {
      apply(field, value);
    } catch (ReflectiveOperationException | RuntimeException e) {
      return false;
    }
    save();
    return true;
  }

  private void backup() {
    try {
      Files.move(
          path,
          path.resolveSibling(path.getFileName() + ".bak"),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ignored) {
    }
  }
}
