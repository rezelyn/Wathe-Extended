package cat.rezelyn.watheextended.mixin.client.wathe;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyBinding.class, priority = 2000)
public class KeyBindingChatRestoreMixin {

    @ModifyReturnValue(method = "shouldSuppressKey", at = @At("RETURN"))
    private boolean watheextended$restoreChatKeysForOps(boolean original) {
        if (!original) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !client.player.hasPermissionLevel(2)) {
            return true;
        }

        KeyBinding self = (KeyBinding) (Object) this;
        boolean isChatOrCommand = self.equals(client.options.chatKey)
                || self.equals(client.options.commandKey);
        return !isChatOrCommand;
    }
}
