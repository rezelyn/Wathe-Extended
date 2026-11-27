package cat.rezelyn.watheextended.client.mixin.hud.ability;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityOverride;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class AbilityTipMixin {

  @Inject(
      method =
          "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
      at = @At("HEAD"),
      cancellable = true)
  private void watheextended$onDrawTextWithShadow(
      TextRenderer renderer,
      Text text,
      int x,
      int y,
      int color,
      CallbackInfoReturnable<Integer> cir) {
    watheextended$override(renderer, text, color, cir);
  }

  @Inject(
      method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
      at = @At("HEAD"),
      cancellable = true)
  private void watheextended$onDrawText(
      TextRenderer renderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      CallbackInfoReturnable<Integer> cir) {
    watheextended$override(renderer, text, color, cir);
  }

  @Unique
  private void watheextended$override(
      TextRenderer renderer, Text text, int color, CallbackInfoReturnable<Integer> cir) {
    DrawContext context = (DrawContext) (Object) this;
    OptionalInt result = AbilityOverride.tryOverride(context, renderer, text, color);
    if (result.isPresent()) {
      cir.setReturnValue(result.getAsInt());
    }
  }
}
