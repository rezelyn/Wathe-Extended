package cat.rezelyn.watheextended.mixin.game.roles.swapper;

import cat.rezelyn.watheextended.game.roles.swapper.SwapperTeleportPayload;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
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

  @Inject(method = "lambda$registerPackets$13", at = @At("HEAD"), remap = false)
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
  private static boolean watheextended$allowSelectedPlayersToSwap(World world, Entity entity) {
    if (entity instanceof PlayerEntity) {
      return true;
    }
    return world.isSpaceEmpty(entity);
  }

  @Inject(
      method = "lambda$registerPackets$13",
      at =
          @At(
              value = "INVOKE",
              ordinal = 0,
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;refreshPositionAfterTeleport(DDD)V"),
      remap = false)
  private static void watheextended$wakeSleepingPair(
      SwapperC2SPacket packet,
      ServerPlayNetworking.Context context,
      CallbackInfo ci,
      @Local(ordinal = 0) PlayerEntity first,
      @Local(ordinal = 1) PlayerEntity second) {
    if (first.isSleeping()) first.wakeUp(true, true);
    if (second.isSleeping()) second.wakeUp(true, true);
  }

  @Redirect(
      method = "lambda$registerPackets$13",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;refreshPositionAfterTeleport(DDD)V"),
      require = 2,
      allow = 2,
      remap = false)
  private static void watheextended$selectedPair(
      PlayerEntity p,
      double x,
      double y,
      double z,
      SwapperC2SPacket packet,
      ServerPlayNetworking.Context context,
      @Local(ordinal = 0) Vec3d player2Origin,
      @Local(ordinal = 1) Vec3d player1Origin) {
    ServerPlayerEntity player = (ServerPlayerEntity) p;
    boolean isPlayer1 = player.getUuid().equals(packet.player());
    var pairUuid = isPlayer1 ? packet.player2() : packet.player();
    var pair = player.getWorld().getPlayerByUuid(pairUuid);
    if (pair != null
        && pair != player
        && ServerPlayNetworking.canSend(player, SwapperTeleportPayload.ID)) {
      ServerPlayNetworking.send(
          player,
          new SwapperTeleportPayload(
              pair.getId(), isPlayer1 ? player1Origin : player2Origin, new Vec3d(x, y, z)));
    }
    player.networkHandler.requestTeleport(x, y, z, player.getYaw(), player.getPitch());
  }

  private static void watheextended$stopRiding(PlayerEntity player) {
    if (player != null && player.hasVehicle()) {
      player.stopRiding();
    }
  }
}
