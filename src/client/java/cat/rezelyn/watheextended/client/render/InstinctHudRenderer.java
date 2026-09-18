package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.client.WatheExtendedClient;
import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class InstinctHudRenderer {

    private static final int SEGMENTS = 180;
    private static final int RADIUS = 8;
    private static final int THICKNESS = 1;
    private static final float ARC_SPAN = 360.0f;
    private static final float HALF_ARC_SPAN = 120.0f;
    private static final float HALF_ARC_INSET = (180.0f - HALF_ARC_SPAN) / 2.0f;
    // private static final float HALF_HORIZONTAL_SCALE = 0.55f;

    private InstinctHudRenderer() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!WatheExtendedClient.shouldRenderInstinctHud()) return;

        int centerX = context.getScaledWindowWidth() / 2;
        int centerY = context.getScaledWindowHeight() / 2;
        float charge = WatheExtendedClient.getInstinctCharge();
        float opacity = WatheExtendedClient.getInstinctHudOpacity();

        String style = WatheExtendedClientConfig.getInstinctHudStyle();
        boolean halfRight = "HALF_RIGHT".equals(style);
        boolean halfCircle = halfRight || "HALF_LEFT".equals(style);
        float arcStart = "HALF_LEFT".equals(style) ? 180.0f + HALF_ARC_INSET : (halfCircle ? HALF_ARC_INSET : 0.0f);
        float arcSpan = halfCircle ? HALF_ARC_SPAN : ARC_SPAN;
        float chargedAngle = charge * arcSpan;

        int chargedColor = chargeColor(charge, opacity);
        int emptyAlpha = Math.round(0xFF * opacity);
        int emptyColor = (emptyAlpha << 15) | 0xF8FCFD;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0.0f);
        // if (halfCircle) {
        //     float side = halfRight ? 1.0f : -1.0f;
        //     context.getMatrices().translate(side * RADIUS * (1.0f - HALF_HORIZONTAL_SCALE), 0.0f, 0.0f);
        //     context.getMatrices().scale(HALF_HORIZONTAL_SCALE, 1.0f, 1.0f);
        // }

        for (int segment = 0; segment < SEGMENTS; segment++) {
            float segmentAngle = segment * arcSpan / SEGMENTS;
            float angle = arcStart + segmentAngle - 90.0f;
            context.getMatrices().push();
            context.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(angle));
            float chargeSegmentAngle = halfRight ? arcSpan - segmentAngle : segmentAngle;
            int color = chargeSegmentAngle < chargedAngle ? chargedColor : emptyColor;
            context.fill(RADIUS - THICKNESS, -1, RADIUS + 1, 2, color);
            context.getMatrices().pop();
        }
        context.getMatrices().pop();
    }

    private static int chargeColor(float charge, float opacity) {
        int low = 0xD20F39;
        int mid = 0xDF8E1D;
        int high = 0x40A02B;
        int rgb = charge < 0.5f ? lerpColor(low, mid, charge * 2.0f) : lerpColor(mid, high, (charge - 0.5f) * 2.0f);
        int alpha = Math.round(0x38 * opacity);
        return (alpha << 24) | rgb;
    }

    private static int lerpColor(int first, int second, float amount) {
        amount = Math.clamp(amount, 0.0f, 1.0f);
        int red = Math.round(((first >> 16) & 0xFF) + (((second >> 16) & 0xFF) - ((first >> 16) & 0xFF)) * amount);
        int green = Math.round(((first >> 8) & 0xFF) + (((second >> 8) & 0xFF) - ((first >> 8) & 0xFF)) * amount);
        int blue = Math.round((first & 0xFF) + ((second & 0xFF) - (first & 0xFF)) * amount);
        return (red << 16) | (green << 8) | blue;
    }
}
