package cat.rezelyn.watheextended.mixin.fix;

import cat.rezelyn.watheextended.game.ShooterPunishment;
import dev.doctor4t.wathe.network.GunShootPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GunShootPayload.Receiver.class)
public abstract class ShooterPunishmentMixin {

    @Inject(method = "receive", at = @At("HEAD"), cancellable = true)
    private void watheextended$applyShooterPunishment(GunShootPayload payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
        if (!ShooterPunishment.shouldHandle(context.player(), payload)) {
            return;
        }

        ShooterPunishment.handle(context.player(), payload, context);
        ci.cancel();
    }
}
