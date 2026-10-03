package cat.rezelyn.watheextended.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public final class LastStandRenderer {

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

    float progress = 1f - (float) remainingTicks / totalTicks;
    VignetteRenderer.renderTintedMask(context, progress, 0x8B0000);
  }
}
