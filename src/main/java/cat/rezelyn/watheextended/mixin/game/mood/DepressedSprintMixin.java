package cat.rezelyn.watheextended.mixin.game.mood;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.game.mood.DepressedMoodState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class DepressedSprintMixin {

  @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
  private void watheextended$preventSprintWhileDepressed(boolean sprinting, CallbackInfo ci) {
    if (sprinting
        && WatheExtendedServerConfig.moodDisableSprintingWhenDepressed
        && (Object) this instanceof PlayerEntity player
        && DepressedMoodState.isDepressed(player)) ci.cancel();
  }
}
