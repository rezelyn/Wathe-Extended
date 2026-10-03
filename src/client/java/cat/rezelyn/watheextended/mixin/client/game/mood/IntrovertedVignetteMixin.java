package cat.rezelyn.watheextended.mixin.client.game.mood;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.client.render.VignetteRenderer;
import cat.rezelyn.watheextended.game.modifiers.WatheExtendedModifiers;
import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class IntrovertedVignetteMixin {

  private static final int FADE_TICKS = 20;
  private static final float FADE_IN_STEP = 1.0f / FADE_TICKS;
  private static final float FADE_OUT_STEP = 1.0f / FADE_TICKS;

  @Unique private float watheextended$introvertedVignetteOpacity;

  @Inject(method = "render", at = @At("TAIL"))
  private void watheextended$renderIntrovertedVignette(
      DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    float deltaTicks = Math.max(0.0f, tickCounter.getLastFrameDuration());
    float target = watheextended$isInsideCrowd(MinecraftClient.getInstance().player) ? 1.0f : 0.0f;
    watheextended$introvertedVignetteOpacity =
        VignetteRenderer.updateOpacity(
            watheextended$introvertedVignetteOpacity,
            target,
            FADE_IN_STEP * deltaTicks,
            FADE_OUT_STEP * deltaTicks);

    if (watheextended$introvertedVignetteOpacity <= 0.0f) return;

    VignetteRenderer.renderTintedMask(
        context, watheextended$introvertedVignetteOpacity * VignetteRenderer.INTENSITY, 0x000000);
  }

  @Unique
  private static boolean watheextended$isInsideCrowd(PlayerEntity player) {
    try {
      if (player == null || !FabricLoader.getInstance().isModLoaded("harpymodloader")) return false;
      if (WatheExtendedModifiers.INTROVERTED == null) return false;
      if (PlayerPsychoComponent.KEY.get(player).getPsychoTicks() > 0) return false;

      GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
      if (time.resetTime - time.getTime() < GameConstants.TIME_TO_FIRST_TASK) return false;

      WorldModifierComponent modifier = WorldModifierComponent.KEY.get(player.getWorld());
      if (!modifier.isModifier(player, WatheExtendedModifiers.INTROVERTED)) return false;

      float range =
          WatheExtendedServerConfig.introvertedCrowdRange
              * WatheExtendedServerConfig.introvertedCrowdRange;
      int count = 0;
      for (PlayerEntity other : player.getWorld().getPlayers()) {
        if (other == player || !GameFunctions.isPlayerAliveAndSurvival(other)) continue;
        if (other.squaredDistanceTo(player) <= range) count++;
      }
      return count >= WatheExtendedServerConfig.introvertedCrowdCount;
    } catch (Throwable ignored) {
      return false;
    }
  }
}
