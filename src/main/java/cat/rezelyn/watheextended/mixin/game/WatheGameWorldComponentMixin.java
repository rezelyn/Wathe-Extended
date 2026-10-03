package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameWorldComponent.class)
public abstract class WatheGameWorldComponentMixin {

  @Shadow @Final private World world;

  @Inject(method = "serverTick", at = @At("TAIL"))
  private void watheExtended$restoreSavedDefaultAfterRestart(CallbackInfo ci) {
    try {
      WatheExtendedWorldComponent.KEY.get(world).restoreSecretMurderMode();
    } catch (Throwable ignored) {
    }
  }
}
