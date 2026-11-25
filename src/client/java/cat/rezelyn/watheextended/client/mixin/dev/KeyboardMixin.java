package cat.rezelyn.watheextended.client.mixin.dev;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Keyboard.class, priority = 1100)
abstract class KeyboardMixin {
  @TargetHandler(
      mixin = "dev.doctor4t.wathe.mixin.client.restrictions.KeyboardMixin",
      name = "wathe$disableF3Keybinds")
  @ModifyExpressionValue(
      method = "@MixinSquared:Handler",
      at =
          @At(
              value = "INVOKE",
              target = "Ldev/doctor4t/wathe/client/WatheClient;isPlayerAliveAndInSurvival()Z"),
      remap = false)
  private boolean watheextended$allowAdventureDebugKeys(boolean restricted) {
    var interactionManager = MinecraftClient.getInstance().interactionManager;
    return restricted
        && (interactionManager == null
            || interactionManager.getCurrentGameMode() != GameMode.ADVENTURE);
  }
}
