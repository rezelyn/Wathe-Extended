package cat.rezelyn.watheextended.mixin.game.mood;

import cat.rezelyn.watheextended.game.mood.DepressedAbilityDisable;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.roles.judge.JudgeAbility", remap = false)
public abstract class KinsJudgeAbilityMixin {

  @Inject(method = "register", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
  private static void watheextended$disableAbilityWhenDepressed(
      @Coerce Object payload, PlayerEntity player, CallbackInfo ci) {
    if (DepressedAbilityDisable.shouldDisable(player)) ci.cancel();
  }
}
