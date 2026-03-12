package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.client.util.ChatHudRenderHelper;
import dev.doctor4t.wathe.client.WatheClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class WatheClientChatMixin {

    @Inject(method = "isPlayerAliveAndInSurvival", at = @At("HEAD"), cancellable = true)
    private static void watheextended$overrideForChatRender(CallbackInfoReturnable<Boolean> cir) {
        if (ChatHudRenderHelper.isForcingRender()) {
            cir.setReturnValue(false);
        }
    }
}
