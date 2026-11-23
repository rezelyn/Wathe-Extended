package cat.rezelyn.watheextended.mixin.game.roles.necromancer;

import java.util.List;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.server.world.ServerChunkLoadingManager$EntityTracker")
public interface RevivalEntityTrackerAccessor {
  @Accessor("entry")
  EntityTrackerEntry watheextended$getEntry();

  @Invoker("stopTracking")
  void watheextended$stopTracking(ServerPlayerEntity observer);

  @Invoker("updateTrackedStatus")
  void watheextended$updateTrackedStatus(List<ServerPlayerEntity> players);
}
