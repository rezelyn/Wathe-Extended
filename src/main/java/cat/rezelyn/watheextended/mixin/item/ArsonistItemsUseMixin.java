package cat.rezelyn.watheextended.mixin.item;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pro.fazeclan.river.stupid_express.role.arsonist.OilDousingHandler;

@Mixin(value = OilDousingHandler.class, remap = false)
public class ArsonistItemsUseMixin {

    @Redirect(method = "lambda$init$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V", ordinal = 0), require = 0)
    private static void watheextended$jerryCanCooldown(ItemCooldownManager manager, Item item, int ticks) {
        int configured = WatheExtendedServerConfig.jerryCanCooldown;
        manager.set(item, configured > 0 ? configured * 20 : ticks);
    }

    @Redirect(method = "lambda$init$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V", ordinal = 1), require = 0)
    private static void watheextended$lighterCooldown(ItemCooldownManager manager, Item item, int ticks) {
        int configured = WatheExtendedServerConfig.lighterCooldown;
        int jerryConfigured = WatheExtendedServerConfig.jerryCanCooldown;
        // if lighter cooldown is 0, fall back to jerry can configured value or becomes dynamic if also 0
        if (configured > 0) {
            manager.set(item, configured * 20);
        } else if (jerryConfigured > 0) {
            manager.set(item, jerryConfigured * 20);
        } else {
            manager.set(item, ticks);
        }
    }
}
