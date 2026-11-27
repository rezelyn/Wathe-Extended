package cat.rezelyn.watheextended.mixin.compat;

import cat.rezelyn.watheextended.game.compat.svc.SpectatorVoicePlugin;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class SpectatorVoiceGameModeMixin {
  @Shadow @Final protected ServerPlayerEntity player;

  @Inject(method = "changeGameMode", at = @At("RETURN"))
  private void watheextended$leaveSpectatorGroup(GameMode mode, CallbackInfoReturnable<Boolean> cir) {
    if (cir.getReturnValueZ() && mode != GameMode.SPECTATOR) {
      SpectatorVoicePlugin.leaveSpectatorGroup(player);
    }
  }
}
