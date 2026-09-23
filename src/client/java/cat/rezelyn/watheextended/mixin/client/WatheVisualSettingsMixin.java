package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.TrainWorldComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrainWorldComponent.class)
public abstract class WatheVisualSettingsMixin {

    @ModifyReturnValue(method = "hasHud", at = @At("RETURN"))
    private boolean watheextended$applyHudPreference(boolean original) {
        return WatheExtendedClientConfig.getShowWatheHud();
    }

    @ModifyReturnValue(method = "isSnowing", at = @At("RETURN"))
    private boolean watheextended$applySnowflakePreference(boolean original) {
        return WatheExtendedClientConfig.getShowSnowflakes();
    }

    @ModifyReturnValue(method = "isFoggy", at = @At("RETURN"))
    private boolean watheextended$applyFogPreference(boolean original) {
        return WatheExtendedClientConfig.getShowFog();
    }
}
