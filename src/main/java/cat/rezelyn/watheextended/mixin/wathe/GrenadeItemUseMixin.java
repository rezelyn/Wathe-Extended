package cat.rezelyn.watheextended.mixin.wathe;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import dev.doctor4t.wathe.item.GrenadeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrenadeItem.class)
public class GrenadeItemUseMixin {

    @Inject(method = "use", at = @At("TAIL"))
    private void watheextended$applyGrenadeCooldown(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!player.isCreative()) {
            player.getItemCooldownManager().set((Item) (Object) this, WatheExtendedServerConfig.grenadeCooldown * 20);
        }
    }
}
