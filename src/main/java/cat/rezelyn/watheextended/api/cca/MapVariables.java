package cat.rezelyn.watheextended.api.cca;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
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
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getPlayArea() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static Vec3i getPlayAreaOffset(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getPlayAreaOffset() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static Box getReadyArea(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getReadyArea() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static Vec3i getResetPasteOffset(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getResetPasteOffset() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getSpawnPosition(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getSpawnPos() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getSpectatorSpawnPosition(World world) {
        if (world == null) return null;
        try {
            MapVariablesWorldComponent mvc = MapVariablesWorldComponent.KEY.get(world);
            return mvc != null ? mvc.getSpectatorSpawnPos() : null;
        } catch (Throwable t) {
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
        return fmt(pos.pos.x) + " " + fmt(pos.pos.y) + " " + fmt(pos.pos.z) + " " + fmt(pos.yaw) + " " + fmt(pos.pitch);
    }

    private static String fmt(double v) {
        String s = new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        return s.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String fmt(float v) {
        String s = new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        return s.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Nullable
    public static MapVariablesWorldComponent.PosWithOrientation getReadyAreaSpawnPosition(World world) {
        if (world == null) return null;
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            return wec != null ? wec.getReadyAreaSpawnPos() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    public static Box getLobbyArea(World world) {
        if (world == null) return WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            return wec != null ? wec.getLobbyArea() : WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        } catch (Throwable t) {
            return WatheExtendedWorldComponent.DEFAULT_LOBBY_AREA;
        }
    }
}
