package cat.rezelyn.watheextended.mixin.game.roles.necromancer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChunkLoadingManager.class)
public interface RevivalChunkTrackingAccessor {
  @Accessor("entityTrackers")
  Int2ObjectMap<RevivalEntityTrackerAccessor> watheextended$getEntityTrackers();
}
