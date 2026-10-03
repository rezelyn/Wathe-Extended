package cat.rezelyn.watheextended.mixin.client.keybinds;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameOptions.class, priority = 2000)
public abstract class PerspectiveKeyMixin {

  @Shadow private Perspective perspective;

  @ModifyReturnValue(method = "getPerspective", at = @At("RETURN"))
  private Perspective watheextended$restrictPerspectiveDuringGame(Perspective original) {
    MinecraftClient client = MinecraftClient.getInstance();
    boolean gameRunning =
        client.world != null && GameWorldComponent.KEY.get(client.world).isRunning();

    if (gameRunning && WatheClient.isPlayerAliveAndInSurvival()) {
      return Perspective.FIRST_PERSON;
    }

    return perspective;
  }
}
