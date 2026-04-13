package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Entity.class, priority = 1100)
public class PlayerCollisionMixin {

    @WrapMethod(method = "collidesWith")
    private boolean watheExtended$collidesWith(Entity other, Operation<Boolean> original) {
        Entity self = (Entity) (Object) this;

        if (other instanceof PlayerEntity player && self instanceof PlayerEntity) {
            WatheExtendedWorldComponent world = WatheExtendedWorldComponent.KEY.get(player.getWorld());
            if (!world.isPlayerCollisionsEnabled()) {
                return false;
            }
        }

        return original.call(other);
    }
}
