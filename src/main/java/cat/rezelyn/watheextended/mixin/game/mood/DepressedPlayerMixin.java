package cat.rezelyn.watheextended.mixin.game.mood;

import cat.rezelyn.watheextended.game.mood.DepressedMoodState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class DepressedPlayerMixin {

  @Inject(method = "tick", at = @At("TAIL"))
  private void watheextended$trackDepressedDuration(CallbackInfo ci) {
    DepressedMoodState.tick((PlayerEntity) (Object) this);
  }
}
