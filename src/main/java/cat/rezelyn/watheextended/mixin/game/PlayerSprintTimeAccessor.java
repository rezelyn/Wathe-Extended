package cat.rezelyn.watheextended.mixin.game;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface PlayerSprintTimeAccessor {

  @Accessor(value = "sprintingTicks", remap = false)
  void watheextended$setSprintingTicks(float ticks);
}
