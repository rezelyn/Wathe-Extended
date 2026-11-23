package cat.rezelyn.watheextended.mixin.game.roles.necromancer;

import net.minecraft.entity.TrackedPosition;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityTrackerEntry.class)
public interface RevivalTrackedPositionAccessor {
  @Accessor("trackedPos")
  TrackedPosition watheextended$getTrackedPosition();
}
