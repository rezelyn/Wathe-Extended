package cat.rezelyn.watheextended.mixin.game.mood;

import cat.rezelyn.watheextended.game.mood.DepressedAbilityDisable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.Noellesroles", remap = false)
public abstract class DepressedAbilityMixin {

  @Inject(
      method = "lambda$registerPackets$16",
      at = @At("HEAD"),
      cancellable = true,
      require = 0,
      remap = false)
  private static void watheextended$blockAbilityWhenSeverelyDepressed(
      AbilityC2SPacket payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
    ServerPlayerEntity player = context.player();
    if (DepressedAbilityDisable.shouldDisable(player)) ci.cancel();
  }
}
