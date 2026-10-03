package cat.rezelyn.watheextended.mixin.client.keybinds;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyBinding.class, priority = 2000)
public class ChatKeyMixin {

  @ModifyReturnValue(method = "shouldSuppressKey", at = @At("RETURN"), remap = false)
  private boolean watheextended$restoreChatKeys(boolean original) {
    MinecraftClient client = MinecraftClient.getInstance();
    KeyBinding input = (KeyBinding) (Object) this;
    if (input.equals(client.options.togglePerspectiveKey)) {
      return WatheClient.gameComponent != null && WatheClient.gameComponent.isRunning();
    }

    if (!original) return false;
    if (client.player == null) return true;

    boolean isChatOrCommand =
        input.equals(client.options.chatKey) || input.equals(client.options.commandKey);

    if (client.player.hasPermissionLevel(2)) {
      return !isChatOrCommand;
    }

    if (WatheExtendedClientConfig.showChatDuringGame && isChatOrCommand) {
      return false;
    }

    return true;
  }
}
