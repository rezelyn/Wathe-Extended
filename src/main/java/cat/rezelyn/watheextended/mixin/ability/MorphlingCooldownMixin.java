package cat.rezelyn.watheextended.mixin.ability;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.morphling.MorphlingPlayerComponent", remap = false)
public class MorphlingCooldownMixin {

    @Inject(method = "stopMorph", at = @At("TAIL"), require = 0, remap = false)
    private void watheextended$applyMorphlingCooldown(CallbackInfo ci) {
        ((MorphlingPlayerComponent)(Object)this).setMorphTicks(-(WatheExtendedServerConfig.morphlingAbilityCooldown * 20));
    }
}
