package cat.rezelyn.watheextended.mixin.fix;

import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InfectedPlayerComponent.class, remap = false)
public class InfectedNullGuardMixin {

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void watheextended$guardNullInfector(CallbackInfo ci) {
        if (((InfectedPlayerComponent) (Object) this).infector == null) {
            ((InfectedPlayerComponent) (Object) this).reset();
            ci.cancel();
        }
    }
}
