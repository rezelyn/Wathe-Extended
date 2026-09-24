package cat.rezelyn.watheextended.mixin.fix;

import cat.rezelyn.watheextended.game.ExtraItemsRoundStart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sir.nicholascooke.watheextraitems.wathe.ExtraItemsGameHooks;

// replaces Wathe Extra Items' round-start pocket watch with an item chosen by ExtraItemsRoundStart
@Mixin(value = ExtraItemsGameHooks.class, remap = false)
public class ExtraItemsRoundStartMixin {

    @WrapOperation(method = "onWorldTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;giveItemStack(Lnet/minecraft/item/ItemStack;)Z", remap = true), remap = false)
    private static boolean watheextended$giveChosenItem(ServerPlayerEntity player, ItemStack stack, Operation<Boolean> original) {
        ItemStack chosen = ExtraItemsRoundStart.pickItem(player);
        return !chosen.isEmpty() && original.call(player, chosen);
    }
}
