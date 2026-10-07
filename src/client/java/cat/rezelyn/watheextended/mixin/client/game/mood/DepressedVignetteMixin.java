package cat.rezelyn.watheextended.mixin.client.game.mood;

import cat.rezelyn.watheextended.client.render.VignetteRenderer;
import cat.rezelyn.watheextended.game.mood.DepressedMoodState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class DepressedVignetteMixin {

  private static final int FADE_TICKS = 15 * 20;
  private static final float FADE_IN_STEP = 0.005f;
  private static final float FADE_OUT_STEP = 0.025f;

  @Unique private float watheextended$depressedVignetteOpacity;

  @Inject(method = "render", at = @At("TAIL"))
  private void watheextended$renderDepressedVignette(
      DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    var player = MinecraftClient.getInstance().player;
    float target =
        DepressedMoodState.isDepressed(player)
            ? VignetteRenderer.INTENSITY
                * Math.min(1f, DepressedMoodState.ticks(player) / (float) FADE_TICKS)
            : 0f;

    watheextended$depressedVignetteOpacity =
        VignetteRenderer.updateOpacity(
            watheextended$depressedVignetteOpacity,
            target,
            FADE_IN_STEP,
            target == 0f ? FADE_OUT_STEP : 0f);
    if (watheextended$depressedVignetteOpacity <= 0f) return;

    VignetteRenderer.renderTintedMask(context, watheextended$depressedVignetteOpacity, 0x323353);
  }
}
