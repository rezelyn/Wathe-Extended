package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public class RoleNameRendererMixin {

    @Shadow
    private static float nametagAlpha;

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void watheExtended$renderPronouns(
            TextRenderer renderer,
            ClientPlayerEntity player,
            DrawContext context,
            RenderTickCounter tickCounter,
            CallbackInfo ci) {

        if (nametagAlpha <= 0.05f) return;

        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8f : 2f;

        if (!(ProjectileUtil.getCollision(player, entity -> entity instanceof PlayerEntity, range)
                instanceof EntityHitResult hit
                && hit.getEntity() instanceof PlayerEntity target)) return;

        String pronouns = PronounsCache.get(target.getUuid());
        if (pronouns.isEmpty()) return;
        if (target.isInvisible()) return;

        Text pronounsText = Text.literal(pronouns);
        int pronounsWidth = renderer.getWidth(pronounsText);
        int color = 0xAAAAAA | ((int) (nametagAlpha * 255) << 24);

        context.getMatrices().push();
        context.getMatrices().translate(
                context.getScaledWindowWidth() / 2f,
                context.getScaledWindowHeight() / 2f + 6f,
                0f);
        context.getMatrices().scale(0.6f, 0.6f, 1f);
        context.drawTextWithShadow(
                renderer, pronounsText,
                -pronounsWidth / 2,
                16 + renderer.fontHeight + 2,
                color);
        context.getMatrices().pop();
    }
}
