package cat.rezelyn.watheextended.mixin.game.roles.arsonist;

import cat.rezelyn.watheextended.game.roles.arsonist.ArsonistDousedNotification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
    targets = "pro.fazeclan.river.stupid_express.role.arsonist.cca.DousedPlayerComponent",
    remap = false)
public abstract class DousedPlayerNotificationMixin {

  @Shadow @Final private PlayerEntity player;
  @Shadow private boolean doused;
  @Unique private boolean watheextended$wasDoused;

  @Inject(method = "setDoused", at = @At("HEAD"), require = 0, remap = false)
  private void watheextended$captureDousedState(boolean value, CallbackInfo ci) {
    watheextended$wasDoused = doused;
  }

  @Inject(method = "setDoused", at = @At("TAIL"), require = 0, remap = false)
  private void watheextended$queueDousedNotice(boolean value, CallbackInfo ci) {
    if (value && !watheextended$wasDoused && player instanceof ServerPlayerEntity serverPlayer) {
      ArsonistDousedNotification.schedule(serverPlayer);
    }
  }
}
