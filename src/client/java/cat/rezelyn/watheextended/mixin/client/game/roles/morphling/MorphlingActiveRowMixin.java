package cat.rezelyn.watheextended.mixin.client.game.roles.morphling;

import cat.rezelyn.watheextended.compat.AddonModCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "org.agmas.noellesroles.client.ui.MorphlingPlayerWidget", remap = false)
public abstract class MorphlingActiveRowMixin {

  @Inject(method = "renderWidget", at = @At("HEAD"))
  private void watheextended$disableActiveMorphlingTargets(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    boolean morphlingActive =
        AddonModCompat.isMorphlingActive(MinecraftClient.getInstance().player);
    ((ClickableWidget) (Object) this).active = !morphlingActive;
  }

  @Redirect(
      method = "renderWidget",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lorg/agmas/noellesroles/morphling/MorphlingPlayerComponent;getMorphTicks()I",
              remap = false),
      require = 0)
  private int watheextended$renderActiveAsDisabled(MorphlingPlayerComponent component) {
    int morphTicks = component.getMorphTicks();
    return morphTicks > 0 ? -morphTicks : morphTicks;
  }

  @Redirect(
      method = "renderWidget",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
      require = 0)
  private int watheextended$hideActiveDuration(
      DrawContext context,
      TextRenderer textRenderer,
      String text,
      int x,
      int y,
      int color,
      boolean shadow) {
    if (AddonModCompat.isMorphlingActive(MinecraftClient.getInstance().player)) return 0;
    return context.drawText(textRenderer, text, x, y, color, shadow);
  }
}
