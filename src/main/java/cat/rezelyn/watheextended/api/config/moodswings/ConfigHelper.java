package cat.rezelyn.watheextended.api.config.moodswings;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.Box;

/**
 * Thin wrapper over the {@code /moodswings} commands, so the config screen never has to know their
 * syntax. Each method returns one command batch to be staged and run by the server when the config
 * is saved. Commands that depend on each other (a selection needs both corners before it can be
 * saved) are chained into one batch because staged entries have no guaranteed order
 */
public final class ConfigHelper {

  public static final String BATCH_SEPARATOR = "\n";

  private static final String TASK = "moodswings task";
  private static final String TASK_AREA = "moodswings area";
  private static final String LOCKDOWN = "moodswings lockdown";
  private static final String LOCKDOWN_AREA = LOCKDOWN + " area";

  private ConfigHelper() {}

  public static boolean isLoaded() {
    return FabricLoader.getInstance().isModLoaded("mood_swings");
  }

  // TASKS

  // Players who currently have a disabled task lose it and get a new one soon after
  public static String setTaskEnabled(String task, boolean enabled) {
    return TASK + (enabled ? " enable " : " disable ") + quote(task);
  }

  // TASK AREAS

  public static String saveTaskArea(String name, Box box) {
    return saveArea(TASK_AREA, name, box);
  }

  // Also removes any active custom task that refers to this area from all online players
  public static String removeTaskArea(String name) {
    return TASK_AREA + " remove " + quote(name);
  }

  // LOCKDOWN AREAS

  public static String setLockdownEnabled(boolean enabled) {
    return LOCKDOWN + (enabled ? " enable" : " disable");
  }

  public static String saveLockdownArea(String name, Box box) {
    return saveArea(LOCKDOWN_AREA, name, box);
  }

  public static String removeLockdownArea(String name) {
    return LOCKDOWN_AREA + " remove " + quote(name);
  }

  // SHARED

  private static String saveArea(String areaCommand, String name, Box box) {
    return String.join(
        BATCH_SEPARATOR,
        areaCommand + " pos1 " + pos(box.minX, box.minY, box.minZ),
        areaCommand + " pos2 " + pos(box.maxX, box.maxY, box.maxZ),
        areaCommand + " save " + quote(name));
  }

  private static String pos(double x, double y, double z) {
    return (int) x + " " + (int) y + " " + (int) z;
  }

  private static String quote(String name) {
    return "\"" + name.trim().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }
}
