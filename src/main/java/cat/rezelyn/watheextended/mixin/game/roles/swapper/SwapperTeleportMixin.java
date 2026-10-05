package cat.rezelyn.watheextended.mixin.game.roles.swapper;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.agmas.noellesroles.packet.SwapperC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.Noellesroles", remap = false)
public class SwapperTeleportMixin {

  @Inject(
      method = "lambda$registerPackets$13",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/entity/Entity;)Z",
              ordinal = 0),
      remap = false)
  private static void watheextended$stopRidingBeforeSwap(
      SwapperC2SPacket packet, ServerPlayNetworking.Context context, CallbackInfo ci) {
    World world = context.player().getWorld();
    PlayerEntity first = world.getPlayerByUuid(packet.player());
    PlayerEntity second = world.getPlayerByUuid(packet.player2());

    watheextended$stopRiding(first);
    watheextended$stopRiding(second);
  }

  private static void watheextended$stopRiding(PlayerEntity player) {
    if (player != null && player.hasVehicle()) {
      player.stopRiding();
    }
  }
}
