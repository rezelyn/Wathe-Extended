package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.network.ConfigHelper;
import java.util.Set;

/**
 * Client-side configuration for Wathe Extended, stored in {@code config/watheextended/client.json}
 *
 * <p><b>For contributors:</b> to add an option, declare a {@code public static} field below.
 * Loading, saving, and get/set are handled automatically through reflection by {@link ConfigHelper}
 *
 * <ul>
 *   <li>The field name is used as the JSON key
 *   <li>The field's initial value is the default
 *   <li>Fields marked {@code final} or {@code transient} are not saved
 * </ul>
 */
public final class WatheExtendedClientConfig {

  private static final ConfigHelper CONFIG =
      new ConfigHelper(WatheExtendedClientConfig.class, "client.json");

  // HUD
  public static boolean showChatDuringGame = true;
  // Visuals
  public static boolean showWatheHud = true;
  public static boolean showSnowflakes = true;
  public static boolean showFog = true;
  // Instinct
  public static String instinctMode = "HOLD";
  public static String proneMode = "TOGGLE";
  public static String instinctHudStyle = "HALF_LEFT";
  public static float instinctHudOpacity = 0.25f;
  public static boolean alwaysShowInstinctHud = false;

  private WatheExtendedClientConfig() {}

  public static void load() {
    CONFIG.load();
  }

  public static void save() {
    CONFIG.save();
  }

  public static Set<String> keys() {
    return CONFIG.keys();
  }

  /** Returns the current value of an option, or {@code null} if it doesn't exist */
  public static Object get(String name) {
    return CONFIG.get(name);
  }

  /**
   * Sets an option and saves the config. Any {@link Number} is converted to the field's numeric
   * type. Range validation is left to the YACL screen
   *
   * @return {@code false} if the option doesn't exist or the value has the wrong type
   */
  public static boolean set(String name, Object value) {
    boolean changed = CONFIG.set(name, value);
    if (changed && "instinctMode".equals(name)) WatheExtendedClient.resetInstinctToggle();
    if (changed && "proneMode".equals(name)) WatheExtendedClient.resetProneToggle();
    return changed;
  }

  public static boolean isInstinctToggleMode() {
    return "TOGGLE".equalsIgnoreCase(instinctMode);
  }

  public static boolean isProneToggleMode() {
    return "TOGGLE".equalsIgnoreCase(proneMode);
  }
}
