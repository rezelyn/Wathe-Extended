package cat.rezelyn.watheextended.mixin.shop;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShopEntry.class)
public class ShopEntryBinglusMixin {

    @Redirect(method = "onBuy", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/cca/GameWorldComponent;canUseKillerFeatures(Lnet/minecraft/entity/player/PlayerEntity;)Z"), require = 0)
    private boolean watheextended$allowBinglusOnBuy(GameWorldComponent game, PlayerEntity player) {
        if (game.canUseKillerFeatures(player)) return true;
        if (!ConfigHelper.isLoaded()) return false;
        try {
            return game.isRole(player, Noellesroles.AWESOME_BINGLUS);
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Redirect(method = "onBuy", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/util/ShopEntry;insertStackInFreeSlot(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Z"), require = 0)
    private boolean watheextended$binglusNoteStack(PlayerEntity player, ItemStack stack) {
        if (!ConfigHelper.isLoaded() || !stack.isOf(WatheItems.NOTE))
            return ShopEntry.insertStackInFreeSlot(player, stack);
        try {
            if (!GameWorldComponent.KEY.get(player.getWorld()).isRole(player, Noellesroles.AWESOME_BINGLUS))
                return ShopEntry.insertStackInFreeSlot(player, stack);
            for (int i = 0; i < 9; i++) {
                ItemStack slot = player.getInventory().getStack(i);
                if (slot.isOf(WatheItems.NOTE) && slot.getCount() < 64) {
                    slot.increment(1);
                    return true;
                }
            }
            return ShopEntry.insertStackInFreeSlot(player, new ItemStack(WatheItems.NOTE, 1));
        } catch (Throwable ignored) {
        }
        return ShopEntry.insertStackInFreeSlot(player, stack);
    }
}
