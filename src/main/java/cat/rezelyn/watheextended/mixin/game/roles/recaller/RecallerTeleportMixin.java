package cat.rezelyn.watheextended.mixin.game.roles.recaller;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.recaller.RecallerPlayerComponent", remap = false)
public abstract class RecallerTeleportMixin {

  @Shadow @Final private PlayerEntity player;

  @Inject(method = "teleport", at = @At("HEAD"), remap = false)
  private void watheextended$stopRidingBeforeTeleport(CallbackInfo ci) {
    if (player.hasVehicle()) {
      player.stopRiding();
    }
  }
}
