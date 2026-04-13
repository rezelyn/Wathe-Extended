package cat.rezelyn.watheextended.mixin.client.hud.inventory;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.gui.StoreRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StoreRenderer.class)
public class BinglusStoreRendererMixin {

    @Inject(method = "renderHud", at = @At("RETURN"), require = 0)
    private static void watheextended$renderBinglusCoins(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, float delta, CallbackInfo ci) {
        if (!ConfigHelper.isLoaded()) return;
        try {
            GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
            if (!game.isRole(player, Noellesroles.AWESOME_BINGLUS)) return;

            int balance = PlayerShopComponent.KEY.get(player).balance;
            if (StoreRenderer.view.getTarget() != balance) {
                StoreRenderer.offsetDelta = balance > StoreRenderer.view.getTarget() ? 0.6f : -0.6f;
                StoreRenderer.view.setTarget(balance);
            }
            float r = StoreRenderer.offsetDelta > 0 ? 1 - StoreRenderer.offsetDelta : 1;
            float g = StoreRenderer.offsetDelta < 0 ? 1 + StoreRenderer.offsetDelta : 1;
            float b = 1 - Math.abs(StoreRenderer.offsetDelta);
            int colour = ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255) | 0xFF000000;

            context.getMatrices().push();
            context.getMatrices().translate(context.getScaledWindowWidth() - 12, 6, 0);
            StoreRenderer.view.render(renderer, context, 0, 0, colour, delta);
            context.getMatrices().pop();
            StoreRenderer.offsetDelta = net.minecraft.util.math.MathHelper.lerp(delta / 16.0f, StoreRenderer.offsetDelta, 0.0f);
        } catch (Throwable ignored) {
        }
    }
}
