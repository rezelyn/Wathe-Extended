package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public final class ScreenUtils {

    private ScreenUtils() {
    }

    public static String fmt(double v) {
        String s = new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    public static String fmt(float v) {
        String s = new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    public static String boxToArgs(@Nullable Box box) {
        if (box == null) return "0 0 0 0 0 0";
        return fmt(box.minX) + " " + fmt(box.minY) + " " + fmt(box.minZ)
                + " " + fmt(box.maxX) + " " + fmt(box.maxY) + " " + fmt(box.maxZ);
    }

    public static String vec3iToArgs(@Nullable Vec3i v) {
        if (v == null) return "0 0 0";
        return v.getX() + " " + v.getY() + " " + v.getZ();
    }

    public static String posToArgs(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        return fmt(pos.pos.x) + " " + fmt(pos.pos.y) + " " + fmt(pos.pos.z)
                + " " + fmt(pos.yaw) + " " + fmt(pos.pitch);
    }

    public static Map<String, List<String>> sortByMods(Set<String> ids) {
        Map<String, List<String>> map = new TreeMap<>();
        for (String id : ids) {
            int colon = id.indexOf(':');
            String ns = colon > 0 ? id.substring(0, colon) : id;
            map.computeIfAbsent(ns, k -> new ArrayList<>()).add(id);
        }
        map.values().forEach(Collections::sort);
        return map;
    }

    public static String modsNamespace(String namespace) {
        return switch (namespace) {
            case "watheextended" -> "Wathe Extended";
            case "noellesroles" -> "Noelle's Roles";
            case "kinswathe" -> "Kin's Wathe";
            case "stupid_express" -> "Stupid Express";
            case "starexpress" -> "Starry Express";
            default -> RolesDisplay.localName(namespace + ":x").replace(" X", "").trim();
        };
    }

    public static boolean isBlacklisted(String id, Set<String> blacklist) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return blacklist.contains(local);
    }
}
