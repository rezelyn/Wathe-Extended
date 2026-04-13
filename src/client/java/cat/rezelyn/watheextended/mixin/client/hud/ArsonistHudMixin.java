package cat.rezelyn.watheextended.mixin.client.hud;

import cat.rezelyn.watheextended.api.GameStatus;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.constants.SERoles;
import pro.fazeclan.river.stupid_express.role.arsonist.cca.DousedPlayerComponent;

@Environment(EnvType.CLIENT)
@Mixin(value = RoleNameRenderer.class)
public class ArsonistHudMixin {

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void watheextended$renderDousedCount(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        try {
            if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) return;
            if (!GameStatus.isActive(player.getWorld())) return;
            if (GameFunctions.isPlayerSpectatingOrCreative(player)) return;
            if (!GameWorldComponent.KEY.get(player.getWorld()).isRole(player, SERoles.ARSONIST)) return;

            int aliveCount = 0;
            int dousedCount = 0;

            for (PlayerEntity p : player.getWorld().getPlayers()) {
                if (!GameFunctions.isPlayerAliveAndSurvival(p)) continue;
                aliveCount++;
                if (DousedPlayerComponent.KEY.get(p).isDoused()) dousedCount++;
            }

            int required = (int) (aliveCount * 0.3);
            Text text = Text.literal("🔥 " + dousedCount + "/" + required + " doused players");

            int color = 0xfc9526;
            int fade = GameWorldComponent.KEY.get(player.getWorld()).getFade();
            if (fade > 0) {
                int alpha = (int) (255.0f * Math.max(0.0f, 1.0f - fade / (float) GameConstants.FADE_TIME));
                if (alpha <= 3) return;
                color = (alpha << 24) | (color & 0xFFFFFF);
            }

            int textWidth = renderer.getWidth(text);
            context.drawText(renderer, text, context.getScaledWindowWidth() - textWidth - 5, context.getScaledWindowHeight() - 12, color, true);
        } catch (Throwable ignored) {}
    }
}
