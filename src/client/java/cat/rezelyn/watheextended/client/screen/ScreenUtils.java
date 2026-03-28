package cat.rezelyn.watheextended.client.screen;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.gui.image.impl.ResourceTextureImage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;

public final class ScreenUtils {

    private ScreenUtils() {
    }

    public static boolean isBlacklisted(String id, Set<String> blacklist) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return blacklist.contains(local);
    }

    public static void stage(BiConsumer<String, Screen> command, Screen parent, String key, Object value) {
        command.accept(key + " " + value, parent);
    }

    public static FloatFieldControllerBuilder floatController(Option<Float> option) {
        return FloatFieldControllerBuilder.create(option).formatValue(value -> Text.literal(format(value)));
    }

    public static String format(double v) {
        String s = new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    public static String format(float v) {
        String s = new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    public static String boxToArgs(@Nullable Box box) {
        if (box == null) return "0 0 0 0 0 0";
        return format(box.minX) + " " + format(box.minY) + " " + format(box.minZ) + " " + format(box.maxX) + " " + format(box.maxY) + " " + format(box.maxZ);
    }

    public static String vec3iToArgs(@Nullable Vec3i v) {
        if (v == null) return "0 0 0";
        return v.getX() + " " + v.getY() + " " + v.getZ();
    }

    public static String posToArgs(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        return format(pos.pos.x) + " " + format(pos.pos.y) + " " + format(pos.pos.z) + " " + format(pos.yaw) + " " + format(pos.pitch);
    }

    public static class ImageRenderer implements dev.isxander.yacl3.gui.image.ImageRenderer {
        private static final long INTERVAL_MS = 3000;

        private final ResourceTextureImage enabled;
        private final ResourceTextureImage disabled;

        public ImageRenderer(Identifier enabled, Identifier disabled, int width, int height) {
            this.enabled = new ResourceTextureImage(enabled, 0, 0, width, height, width, height);
            this.disabled = new ResourceTextureImage(disabled, 0, 0, width, height, width, height);
        }

        @Override
        public int render(DrawContext graphics, int x, int y, int width, float tick) {
            boolean state = (System.currentTimeMillis() / INTERVAL_MS) % 2 == 0;
            return (state ? enabled : disabled).render(graphics, x, y, width, tick);
        }

        @Override
        public void close() {
            enabled.close();
            disabled.close();
        }
    }
}
