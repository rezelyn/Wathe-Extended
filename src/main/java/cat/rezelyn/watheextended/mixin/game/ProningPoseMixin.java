package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.game.ProneState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ProningPoseMixin implements ProneState {
  @Unique private boolean watheextended$proneRequested;

  @Override
  public boolean watheextended$isProneRequested() {
    return watheextended$proneRequested;
  }

  @Override
  public void watheextended$setProneRequested(boolean prone) {
    watheextended$proneRequested = prone;
  }

  @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
  private void watheextended$applyPronePose(CallbackInfo ci) {
    if (watheextended$proneRequested && (Object) this instanceof PlayerEntity player) {
      player.setPose(EntityPose.SWIMMING);
      ci.cancel();
    }
  }
}
