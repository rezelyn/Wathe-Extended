package cat.rezelyn.watheextended.mixin.fix;

import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <b>Fix:</b> Always checks {@link AllowPlayerDeath} in {@link GameFunctions#killPlayer}, so
 * players with active defense shields can no longer trigger role conversions conditions <i>(e.g.
 * Executioner, Initiate)</i> from a kill that should have been blocked.
 */
@Mixin(value = GameFunctions.class, priority = 500)
public class KillShieldConditionMixin {

  @Inject(
      method =
          "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
      at = @At("HEAD"),
      cancellable = true)
  private static void watheextended$checkAllowPlayerDeath(
      PlayerEntity victim,
      boolean spawnBody,
      @Nullable PlayerEntity killer,
      Identifier deathReason,
      CallbackInfo ci) {
    if (!AllowPlayerDeath.EVENT.invoker().allowDeath(victim, killer, deathReason)) {
      ci.cancel();
    }
  }
}
