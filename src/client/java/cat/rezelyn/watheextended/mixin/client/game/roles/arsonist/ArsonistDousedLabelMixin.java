package cat.rezelyn.watheextended.mixin.client.game.roles.arsonist;

import cat.rezelyn.watheextended.client.render.UnifiedNameRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public abstract class ArsonistDousedLabelMixin {

  @Inject(
      method =
          "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
      at = @At("HEAD"),
      cancellable = true,
      require = 0)
  private void watheextended$suppressStupidExpressLabel(
      TextRenderer renderer,
      Text text,
      int x,
      int y,
      int color,
      CallbackInfoReturnable<Integer> cir) {
    if (UnifiedNameRenderer.shouldSuppressStupidExpressOverlayDraw(text)) {
      cir.setReturnValue(0);
    }
  }
}
