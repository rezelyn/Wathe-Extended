package cat.rezelyn.watheextended.mixin.client.keybinds;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyBinding.class, priority = 9000)
public abstract class KeyJumpMixin {

    @Shadow private boolean pressed;
    @Shadow private int timesPressed;

    @ModifyReturnValue(method = "isPressed", at = @At("RETURN"))
    private boolean watheextended$jumpModeIsPressed(boolean input) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return input;

        KeyBinding key = (KeyBinding) (Object) this;
        if (!key.equals(client.options.jumpKey)) return input;
        if (!WatheClient.isPlayerAliveAndInSurvival()) return input;

        String mode = ClientConfig.getString("watheextended.jumpMode", "LOBBY");
        if ("EVERYWHERE".equals(mode)) return this.pressed || input;
        if ("LOBBY".equals(mode)) return !GameStatus.State(client.world) && (this.pressed || input);
        return input;
    }

    @ModifyReturnValue(method = "wasPressed", at = @At("RETURN"))
    private boolean watheextended$jumpModeWasPressed(boolean input) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return input;

        KeyBinding key = (KeyBinding) (Object) this;
        if (!key.equals(client.options.jumpKey)) return input;
        if (!WatheClient.isPlayerAliveAndInSurvival()) return input;

        String mode = ClientConfig.getString("watheextended.jumpMode", "LOBBY");
        if ("EVERYWHERE".equals(mode)) return this.timesPressed > 0 || input;
        if ("LOBBY".equals(mode)) {
            if (GameStatus.State(client.world)) {
                if (this.timesPressed > 0) this.timesPressed = 0;
                return false;
            }
            return this.timesPressed > 0 || input;
        }
        return input;
    }
}
