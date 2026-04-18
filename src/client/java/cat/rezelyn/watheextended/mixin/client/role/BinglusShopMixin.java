package cat.rezelyn.watheextended.mixin.client.role;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LimitedInventoryScreen.class)
public abstract class BinglusShopMixin extends LimitedHandledScreen<PlayerScreenHandler> {

    public BinglusShopMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        throw new AssertionError();
    }

    @Inject(method = "init", at = @At("RETURN"), require = 0)
    private void watheextended$addBinglusShop(CallbackInfo ci) {
        if (!ConfigHelper.isLoaded()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        try {
            GameWorldComponent game = GameWorldComponent.KEY.get(client.player.getWorld());
            if (!game.isRunning()) return;
            if (!game.isRole(client.player, Noellesroles.AWESOME_BINGLUS)) return;
        } catch (Throwable t) {
            return;
        }

        for (int i = 0; i < GameConstants.SHOP_ENTRIES.size(); i++) {
            ShopEntry entry = GameConstants.SHOP_ENTRIES.get(i);
            if (entry.stack().isOf(WatheItems.NOTE)) {
                int x = width / 2 - 8;
                this.addDrawableChild(new LimitedInventoryScreen.StoreItemWidget((LimitedInventoryScreen) (Object) this, x, y - 46, entry, i));
                break;
            }
        }
    }
}
