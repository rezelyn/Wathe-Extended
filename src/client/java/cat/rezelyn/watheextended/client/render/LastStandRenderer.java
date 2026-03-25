package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.WatheExtended;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class LastStandRenderer {

    private static final Identifier TEXTURE = WatheExtended.id("textures/gui/vignette.png");

    private static int remainingTicks = 0;
    private static int totalTicks = 0;

    private LastStandRenderer() {}

    public static void start(int total) {
        totalTicks = total;
        remainingTicks = total;
    }

    public static void stop() {
        remainingTicks = 0;
        totalTicks = 0;
    }

    public static void tick() {
        if (remainingTicks <= 0) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) {
            stop();
            return;
        }
        remainingTicks--;
    }

    public static void render(DrawContext context) {
        if (remainingTicks <= 0 || totalTicks <= 0) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) {
            stop();
            return;
        }

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        float progress = 1f - (float) remainingTicks / totalTicks;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.setShaderColor(1f, 1f, 1f, progress);
        context.drawTexture(TEXTURE, 0, 0, 0f, 0f, screenWidth, screenHeight, screenWidth, screenHeight);
        context.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}
