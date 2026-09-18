package cat.rezelyn.watheextended.mixin.client.keybinds;

import cat.rezelyn.watheextended.client.WatheExtendedClient;
import dev.doctor4t.wathe.client.WatheClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class InstinctModeMixin {

    @Inject(method = "isInstinctEnabled", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$instinctMode(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(WatheExtendedClient.isInstinctActive());
    }
}
