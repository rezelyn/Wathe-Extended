package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.api.ClientConfig;
import cat.rezelyn.watheextended.api.wathe.GameStatus;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 6000)
public class JumpKeyBindingMixin {

    @Shadow private boolean pressed;
    @Shadow private int timesPressed;

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void watheextended$jumpModeIsPressed(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!((KeyBinding) (Object) this).equals(client.options.jumpKey)) return;
        if (!WatheClient.isPlayerAliveAndInSurvival()) return;

        String mode = ClientConfig.getString("watheextended.jumpMode", "DEFAULT");
        boolean allowJump = "EVERYWHERE".equals(mode) || ("LOBBY".equals(mode) && !GameStatus.State(client.world));
        cir.setReturnValue(allowJump && this.pressed);
    }

    @Inject(method = "wasPressed", at = @At("HEAD"), cancellable = true)
    private void watheextended$jumpModeWasPressed(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!((KeyBinding) (Object) this).equals(client.options.jumpKey)) return;
        if (!WatheClient.isPlayerAliveAndInSurvival()) return;

        String mode = ClientConfig.getString("watheextended.jumpMode", "DEFAULT");
        boolean allowJump = "EVERYWHERE".equals(mode) || ("LOBBY".equals(mode) && !GameStatus.State(client.world));
        boolean wasActuallyPressed = this.timesPressed > 0;
        if (wasActuallyPressed) this.timesPressed--;
        cir.setReturnValue(allowJump && wasActuallyPressed);
    }
}
