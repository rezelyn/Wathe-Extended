package cat.rezelyn.watheextended.client.screen.config;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public final class ScreenUtils {

    private ScreenUtils() {
    }

    public static String rounded(double v) {
        String round = String.format("%.4f", v);
        round = round.replaceAll("0+$", "").replaceAll("\\.$", "");
        return round;
    }

    public static String boxToArgs(@Nullable Box box) {
        if (box == null) return "0 0 0 0 0 0";
        return rounded(box.minX) + " " + rounded(box.minY) + " " + rounded(box.minZ)
                + " " + rounded(box.maxX) + " " + rounded(box.maxY) + " " + rounded(box.maxZ);
    }

    public static String vec3iToArgs(@Nullable Vec3i v) {
        if (v == null) return "0 0 0";
        return v.getX() + " " + v.getY() + " " + v.getZ();
    }

    public static String posToArgs(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        return rounded(pos.pos.x) + " " + rounded(pos.pos.y) + " " + rounded(pos.pos.z)
                + " " + rounded(pos.yaw) + " " + rounded(pos.pitch);
    }
}
