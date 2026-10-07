package cat.rezelyn.watheextended.mixin.game.roles.swapper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.packet.SwapperC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.Noellesroles", remap = false)
public class SwapperTeleportMixin {

  @Inject(
      method = "lambda$registerPackets$13",
      at = @At("HEAD"),
      remap = false)
  private static void watheextended$stopRidingBeforeSwap(
      SwapperC2SPacket packet, ServerPlayNetworking.Context context, CallbackInfo ci) {
    World world = context.player().getWorld();

    if (!GameWorldComponent.KEY.get(world).isRole(context.player(), Noellesroles.SWAPPER)) {
      return;
    }

    PlayerEntity first = world.getPlayerByUuid(packet.player());
    PlayerEntity second = world.getPlayerByUuid(packet.player2());

    watheextended$stopRiding(first);
    watheextended$stopRiding(second);
  }

  @Redirect(
      method = "lambda$registerPackets$13",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/entity/Entity;)Z"),
      allow = 2,
      remap = false)
  private static boolean watheextended$allowSelectedPlayersToSwap(
      World world, Entity entity) {
    if (entity instanceof PlayerEntity) {
      return true;
    }
    return world.isSpaceEmpty(entity);
  }

  private static void watheextended$stopRiding(PlayerEntity player) {
    if (player != null && player.hasVehicle()) {
      player.stopRiding();
    }
  }
}
