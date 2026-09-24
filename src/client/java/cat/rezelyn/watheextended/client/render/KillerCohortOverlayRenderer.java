package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.game.KillerCohortAccess;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.EntityHitResult;
import org.agmas.harpymodloader.Harpymodloader;

public final class KillerCohortOverlayRenderer {
    private static float overlayAlpha;
    private static PlayerEntity lastTarget;

    private KillerCohortOverlayRenderer() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.textRenderer == null) return;

        boolean showOverlay = false;
        PlayerEntity target = lastTarget;

        try {
            GameWorldComponent game = GameWorldComponent.KEY.get(client.world);
            if (game.isRunning() && isMurderMode(game)
                    && KillerCohortAccess.canSeeCohorts(game, client.player)
                    && ProjectileUtil.getCollision(client.player, entity -> entity instanceof PlayerEntity, 2f)
                    instanceof EntityHitResult hit && hit.getEntity() instanceof PlayerEntity hitPlayer
                    && KillerCohortAccess.isKillerCohort(game, hitPlayer)) {
                target = hitPlayer;
                lastTarget = hitPlayer;
                showOverlay = true;
            }
        } catch (Throwable ignored) {

        }

        float fade = tickCounter.getLastFrameDuration() / 4f;
        overlayAlpha = MathHelper.lerp(fade, overlayAlpha, showOverlay ? 1f : 0f);
        if (overlayAlpha <= 0.05f || target == null) return;

        try {
            TextRenderer renderer = client.textRenderer;
            Text label = Text.translatable("game.tip.cohort");
            int width = renderer.getWidth(label);
            int alpha = (int) (overlayAlpha * 255f) << 24;

            context.getMatrices().push();
            context.getMatrices().translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6f, 0f);
            context.getMatrices().scale(0.6f, 0.6f, 1f);
            context.getMatrices().translate(0f, 20f + renderer.fontHeight + 8f, 0f);
            context.drawTextWithShadow(renderer, label, -width / 2, 0, alpha | 0x00FF0000);
            context.getMatrices().pop();
        } catch (Throwable ignored) {

        }
    }

    private static boolean isMurderMode(GameWorldComponent game) {
        return game.getGameMode() == WatheGameModes.MURDER || game.getGameMode() == Harpymodloader.MODDED_GAMEMODE;
    }
}
