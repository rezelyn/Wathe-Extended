package cat.rezelyn.watheextended.mixin;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoodComponent.class)
public interface PlayerMoodDirectAccessor {

    @Accessor("mood")
    void watheextended$setMoodDirect(float mood);
}
