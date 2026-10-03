package cat.rezelyn.watheextended.api;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.text.Text;
import org.agmas.harpymodloader.Harpymodloader;

public final class RolesDisplay {

  private RolesDisplay() {}

  public static Map<String, RoleDisplay> get() {
    Map<String, RoleDisplay> result = new LinkedHashMap<>();
    for (Role role : WatheRoles.ROLES) {
      if (role == null || role.identifier() == null) continue;
      String id = role.identifier().toString();
      Text name;
      try {
        name = Harpymodloader.getRoleName(role);
      } catch (Throwable ignored) {
        name = Text.literal(prettyName(id));
      }
      name = cleanName(name);
      Side side = resolveSide(role);
      result.put(id, new RoleDisplay(id, name, role.color(), side));
    }
    return Collections.unmodifiableMap(result);
  }

  private static Side resolveSide(Role role) {
    try {
      if (role.canUseKiller()) return Side.KILLER;
      if (role.isInnocent()) return Side.INNOCENT;
    } catch (Throwable ignored) {
    }
    return Side.NEUTRAL;
  }

  public static String prettyName(String id) {
    int colon = id.indexOf(':');
    String raw = colon >= 0 ? id.substring(colon + 1) : id;
    String[] parts = raw.split("[_\\-]");
    StringBuilder string = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty()) continue;
      if (!string.isEmpty()) string.append(' ');
      string.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
    }
    return string.isEmpty() ? raw : string.toString();
  }

  public static Text cleanName(Text text) {
    String value = text.getString();
    return value.indexOf('!') < 0
        ? text
        : Text.literal(value.replace("!", "")).setStyle(text.getStyle());
  }

  public enum Side {
    KILLER,
    INNOCENT,
    NEUTRAL
  }

  public record RoleDisplay(String id, Text display, int color, Side side) {}
}
