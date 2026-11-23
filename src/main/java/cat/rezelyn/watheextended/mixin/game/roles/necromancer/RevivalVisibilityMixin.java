package cat.rezelyn.watheextended.mixin.game.roles.necromancer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pro.fazeclan.river.stupid_express.role.necromancer.RevivalSelectionHandler;

@Mixin(value = RevivalSelectionHandler.class, remap = false)
public class RevivalVisibilityMixin {
  @WrapOperation(
      method = "lambda$init$0",
      at = @At(value = "INVOKE", target =
          "Lnet/minecraft/server/network/ServerPlayerEntity;changeGameMode(Lnet/minecraft/world/GameMode;)Z"))
  private static boolean watheextended$revealAtBody(
      ServerPlayerEntity player, GameMode mode, Operation<Boolean> original) {
    var world = player.getServerWorld();
    var tracking = (RevivalChunkTrackingAccessor) world.getChunkManager().chunkLoadingManager;
    var tracker = tracking.watheextended$getEntityTrackers().get(player.getId());
    if (tracker == null || !player.isSpectator()) return original.call(player, mode);

    for (var observer : world.getPlayers()) tracker.watheextended$stopTracking(observer);
    var entry = (RevivalTrackedPositionAccessor) tracker.watheextended$getEntry();
    entry.watheextended$getTrackedPosition().setPos(player.getSyncedPos());
    player.setCameraEntity(player);
    boolean changed = original.call(player, mode);
    tracker.watheextended$updateTrackedStatus(world.getPlayers());
    return changed;
  }
}
