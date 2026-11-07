package cat.rezelyn.watheextended.client.mixin.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** MC-259387: mouse interaction retains input focus, but not a keyboard focus highlight. */
@Mixin(ClickableWidget.class)
public abstract class MouseWidgetFocusMixin {
  @Unique private boolean watheextended$rendering;

  @WrapMethod(method = "render")
  private void watheextended$renderWithNavigationFocus(
      DrawContext context, int mouseX, int mouseY, float delta, Operation<Void> original) {
    boolean previous = watheextended$rendering;
    watheextended$rendering = true;
    try {
      original.call(context, mouseX, mouseY, delta);
    } finally {
      watheextended$rendering = previous;
    }
  }

  @Inject(method = "isFocused", at = @At("RETURN"), cancellable = true)
  private void watheextended$hideMouseFocus(CallbackInfoReturnable<Boolean> cir) {
    Object widget = this;
    if (watheextended$rendering
        && (widget instanceof PressableWidget || widget instanceof SliderWidget)
        && !MinecraftClient.getInstance().getNavigationType().isKeyboard()) {
      cir.setReturnValue(false);
    }
  }
}
