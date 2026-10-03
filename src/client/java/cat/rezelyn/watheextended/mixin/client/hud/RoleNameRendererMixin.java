package cat.rezelyn.watheextended.mixin.client.hud;

import cat.rezelyn.watheextended.client.render.UnifiedNameRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.harpymodloader.client.HarpymodloaderClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RoleNameRenderer.class, priority = 999)
public class RoleNameRendererMixin {

  @Inject(method = "renderHud", at = @At("HEAD"), require = 0)
  private static void watheextended$clearStaleHarpySpectatorTitle(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    if (player.isSpectator() || player.isCreative()) {
      HarpymodloaderClient.hudRole = null;
      HarpymodloaderClient.modifiers = null;
    }
  }

  /** Wathe's target draw remains in place for compatibility, but its player line is hidden */
  @WrapOperation(
      method = "renderHud",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"),
      require = 0)
  private static Text watheextended$replaceVanillaUsername(
      PlayerEntity target, Operation<Text> original) {
    try {
      original.call(target);
    } catch (Throwable ignored) {
    }
    return Text.empty();
  }

  /** HML uses this local after the same target lookup to draw its role/modifier title. */
  @Inject(
      method = "renderHud",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text;",
              shift = At.Shift.AFTER),
      require = 0)
  private static void watheextended$clearHarpySpectatorTitle(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    if (player.isSpectator() || player.isCreative()) {
      HarpymodloaderClient.hudRole = null;
      HarpymodloaderClient.modifiers = null;
    }
  }

  /** Killer Cohort text is included by {@link UnifiedNameRenderer} */
  @Redirect(
      method = "renderHud",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ldev/doctor4t/wathe/client/gui/RoleNameRenderer;displayCohortOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/gui/DrawContext;)V"),
      require = 0)
  private static void watheextended$replaceWatheCohortOverlay(
      TextRenderer renderer, DrawContext context) {}

  /**
   * Replaces KinsWathe's independent Killer Cohort text and Hacker draws when the unified rows
   * cover them
   */
  @WrapOperation(
      method = "renderHud",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"),
      require = 0)
  private static int watheextended$replaceKinsWatheOverlays(
      DrawContext context,
      TextRenderer renderer,
      Text text,
      int x,
      int y,
      int color,
      Operation<Integer> original) {
    if (FabricLoader.getInstance().isModLoaded("kinswathe")
        && UnifiedNameRenderer.shouldSuppressKinsOverlayDraw(text)) {
      return 0;
    }
    return original.call(context, renderer, text, x, y, color);
  }

  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderUnifiedName(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    if (MinecraftClient.getInstance().world == null) return;
    try {
      UnifiedNameRenderer.render(renderer, player, context, tickCounter);
    } catch (Throwable ignored) {
    }
  }
}
