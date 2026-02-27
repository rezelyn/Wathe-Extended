package cat.rezelyn.watheextended.api.wathe;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import net.minecraft.text.Text;
import org.agmas.harpymodloader.Harpymodloader;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RolesDisplay {

    public enum Side { KILLER, INNOCENT, NEUTRAL }

    private RolesDisplay() {
    }

    public static Map<String, RoleDisplay> get() {
        Map<String, RoleDisplay> result = new LinkedHashMap<>();
        for (Role role : WatheRoles.ROLES) {
            if (role == null || role.identifier() == null) continue;
            String id = role.identifier().toString();
            Text name = resolveName(role, id);
            int color = resolveColor(role);
            Side side = resolveSide(role);
            result.put(id, new RoleDisplay(id, name, color, side));
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

    private static Text resolveName(Role role, String fallbackId) {
        try {
            return Harpymodloader.getRoleName(role);
        } catch (Throwable ignored) {
        }
        return Text.literal(localName(fallbackId));
    }

    private static int resolveColor(Role role) {
        try {
            return role.color();
        } catch (Throwable ignored) {
            return 0xFFFFFF;
        }
    }

    public static String localName(String id) {
        int colon = id.indexOf(':');
        String raw = colon >= 0 ? id.substring(colon + 1) : id;
        String[] parts = raw.split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.isEmpty() ? raw : sb.toString();
    }

    public record RoleDisplay(String id, Text display, int color, Side side) {
    }
}
