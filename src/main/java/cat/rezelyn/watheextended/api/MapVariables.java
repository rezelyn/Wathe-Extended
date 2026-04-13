package cat.rezelyn.watheextended.api;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MapVariables {

    @Nullable
    public static Box getPlayArea(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getPlayArea() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Nullable
    public static Vec3i getPlayAreaOffset(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getPlayAreaOffset() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Nullable
    public static Box getReadyArea(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getReadyArea() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Nullable
    public static Vec3i getResetPasteOffset(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getResetPasteOffset() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getSpawnPosition(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getSpawnPos() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getSpectatorSpawnPosition(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent component = MapVariablesWorldComponent.KEY.get(world);
            return component != null ? component.getSpectatorSpawnPos() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static String formatBox(@Nullable Box box) {
        if (box == null) return "N/A";
        return (int) box.minX + " " + (int) box.minY + " " + (int) box.minZ + " → " + (int) box.maxX + " " + (int) box.maxY + " " + (int) box.maxZ;
    }

    public static String formatVec3i(@Nullable Vec3i vec) {
        if (vec == null) return "N/A";
        return vec.getX() + " " + vec.getY() + " " + vec.getZ();
    }

    public static String formatPosWithOrientation(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "N/A";
        return format(pos.pos.x) + " " + format(pos.pos.y) + " " + format(pos.pos.z) + " " + format(pos.yaw) + " " + format(pos.pitch);
    }

    private static String format(double v) {
        String string = new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        return string.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String format(float v) {
        String string = new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        return string.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getReadyAreaSpawnPosition(World world) {
        if (world == null) return null;
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            return component != null ? component.getReadyAreaSpawnPos() : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static Box getLobbyArea(World world) {
        if (world == null) return WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            return component != null ? component.getLobbyArea() : WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        } catch (Throwable throwable) {
            return WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        }
    }
}
