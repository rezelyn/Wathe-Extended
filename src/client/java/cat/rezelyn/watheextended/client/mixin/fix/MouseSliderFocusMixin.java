package cat.rezelyn.watheextended.client.mixin.fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** MC-259387: mouse interaction retains input focus, but not a keyboard focus highlight. */
@Mixin(SliderWidget.class)
public abstract class MouseSliderFocusMixin {
  @ModifyExpressionValue(
      method = "getHandleTexture",
      at =
          @At(
              value = "FIELD",
              target = "Lnet/minecraft/client/gui/widget/SliderWidget;sliderFocused:Z"))
  private boolean watheextended$keyboardHandleFocus(boolean focused) {
    return focused && MinecraftClient.getInstance().getNavigationType().isKeyboard();
  }
}
