package cat.rezelyn.watheextended.mixin.fix;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class CursorSlotMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void watheextended$preventGunPickup(PlayerEntity player, CallbackInfo ci) {
        if (player.isCreative()) return;

        ItemStack groundStack = ((ItemEntity) (Object) this).getStack();
        if (!groundStack.isIn(WatheItemTags.GUNS)) return;

        GameWorldComponent game;
        try {
            game = GameWorldComponent.KEY.get(player.getWorld());
        } catch (Throwable ignored) {
            return;
        }
        if (game == null || !game.isRunning()) return;

        if ("PREVENT_PICKUP".equals(WatheExtendedServerConfig.getShootInnocentPunishmentMode())
                && groundStack.isOf(WatheItems.REVOLVER)
                && WatheExtendedWorldComponent.KEY.get(player.getWorld()).isRevolverPickupBlocked(player.getUuid())) {
            ci.cancel();
            return;
        }

        ItemStack cursor = player.currentScreenHandler.getCursorStack();
        if (!cursor.isEmpty() && cursor.isIn(WatheItemTags.GUNS)) {
            ci.cancel(); // shxnji guessed it
        }
    }
}
