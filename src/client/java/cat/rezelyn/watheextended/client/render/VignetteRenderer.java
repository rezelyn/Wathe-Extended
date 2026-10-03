package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.WatheExtended;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public final class VignetteRenderer {

  public static final float INTENSITY = 0.65f;
  private static final Identifier TINT_MASK = WatheExtended.id("textures/gui/vignette.png");

  private VignetteRenderer() {}

  public static void render(DrawContext context, Identifier texture, float intensity, Mode mode) {
    render(context, texture, intensity, 0xFFFFFF, mode);
  }

  /**
   * Renders a vignette tinted with an RGB hex color (for example, {@code 0xFF0000} for red).
   * For a clean tint in {@link Mode#ALPHA_OVERLAY}, use a white vignette mask with transparency.
   */
  public static void render(
      DrawContext context, Identifier texture, float intensity, int hexColor, Mode mode) {
    int width = context.getScaledWindowWidth();
    int height = context.getScaledWindowHeight();
    float amount = MathHelper.clamp(intensity, 0.0f, 1.0f);
    float red = ((hexColor >> 16) & 0xFF) / 255.0f;
    float green = ((hexColor >> 8) & 0xFF) / 255.0f;
    float blue = (hexColor & 0xFF) / 255.0f;

    RenderSystem.enableBlend();
    if (mode == Mode.DARKEN_FROM_TEXTURE) {
      RenderSystem.blendFunc(
          GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
      context.setShaderColor(red * amount, green * amount, blue * amount, 1.0f);
    } else {
      RenderSystem.defaultBlendFunc();
      context.setShaderColor(red, green, blue, amount);
    }

    context.drawTexture(texture, 0, 0, 0.0f, 0.0f, width, height, width, height);
    context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    RenderSystem.defaultBlendFunc();
    RenderSystem.disableBlend();
  }

  public static void renderTintedMask(DrawContext context, float intensity, int hexColor) {
    render(context, TINT_MASK, intensity, hexColor, Mode.ALPHA_OVERLAY);
  }

  public static float updateOpacity(
      float current, float target, float fadeInStep, float fadeOutStep) {
    float boundedTarget = MathHelper.clamp(target, 0.0f, 1.0f);
    if (boundedTarget > current) {
      return Math.min(boundedTarget, current + Math.max(0.0f, fadeInStep));
    }
    return Math.max(boundedTarget, current - Math.max(0.0f, fadeOutStep));
  }

  public enum Mode {
    ALPHA_OVERLAY,
    DARKEN_FROM_TEXTURE
  }
}
