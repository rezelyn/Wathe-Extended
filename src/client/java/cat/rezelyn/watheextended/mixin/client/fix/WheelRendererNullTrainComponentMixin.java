package cat.rezelyn.watheextended.mixin.client.fix;

import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.render.block_entity.WheelBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WheelBlockEntityRenderer.class)
public class WheelRendererNullTrainComponentMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void watheExtended$skipUntilTrainComponentIsReady(CallbackInfo ci) {
        if (WatheClient.trainComponent == null) {
            ci.cancel();
        }
    }
}
