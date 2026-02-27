package cat.rezelyn.watheextended.mixin.client.starexpress;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.aussiebox.starexpress.client.gui.widget.GuidebookButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuidebookButtonWidget.class)
public abstract class GuidebookButtonMixin extends ClickableWidget {

    protected GuidebookButtonMixin() {
        super(0, 0, 0, 0, net.minecraft.text.Text.empty());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void watheExtended$disable(CallbackInfo ci) {
        this.active = false;
        this.visible = false;
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void watheExtended$cancelRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
    }
}
