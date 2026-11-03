package cat.rezelyn.watheextended.client.mixin.hud;

import cat.rezelyn.watheextended.client.render.hud.ChatHudRenderHelper;
import dev.doctor4t.wathe.client.WatheClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class ClientChatMixin {

  @Inject(method = "isPlayerAliveAndInSurvival", at = @At("HEAD"), cancellable = true)
  private static void watheextended$overrideForChatRender(CallbackInfoReturnable<Boolean> cir) {
    if (ChatHudRenderHelper.isForcingRender()) {
      cir.setReturnValue(false);
    }
  }
}
