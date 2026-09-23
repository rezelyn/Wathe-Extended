package cat.rezelyn.watheextended.mixin.client.hud;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import net.fabricmc.loader.api.FabricLoader;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.UUID;

@Mixin(value = RoleNameRenderer.class, priority = 999)
public class RoleNameRendererMixin {

    @Unique
    private static float watheextended$pronounsAlpha;
    @Unique
    private static PlayerEntity watheextended$pronounsTarget;

    @WrapOperation(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text"), require = 0)
    private static Text watheextended$protectDisplayName(PlayerEntity target, Operation<Text> op) {
        try {
            return op.call(target);
        } catch (Throwable ignored) {
            return Text.literal(target.getGameProfile().getName());
        }
    }

    @org.spongepowered.asm.mixin.injection.Redirect(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/client/gui/RoleNameRenderer;displayCohortOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/gui/DrawContext;)V"), require = 0)
    private static void watheextended$replaceWatheCohortOverlay(TextRenderer renderer, DrawContext context) {
    }

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void watheExtended$renderPronouns(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        try {
            watheExtended$renderPronounsInternal(renderer, player, context, tickCounter);
        } catch (Throwable ignored) {
        }
    }

    private static void watheExtended$renderPronounsInternal(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter) {
        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8f : 2f;
        boolean targetAcquired = false;
        PlayerEntity target = watheextended$pronounsTarget;

        if (ProjectileUtil.getCollision(player, entity -> entity instanceof PlayerEntity, range) instanceof EntityHitResult hit && hit.getEntity() instanceof PlayerEntity hitTarget) {
            target = hitTarget;
            watheextended$pronounsTarget = hitTarget;
            targetAcquired = true;
        }

        watheextended$pronounsAlpha = MathHelper.lerp(tickCounter.getLastFrameDuration() / 4f, watheextended$pronounsAlpha, targetAcquired ? 1f : 0f);
        if (watheextended$pronounsAlpha <= 0.05f || target == null) return;

        // ignore psycho mode
        try {
            if (PlayerPsychoComponent.KEY.get(target).getPsychoTicks() > 0) return;
        } catch (Throwable ignored) {
        }

        // compat: noelle's roles morph psychosis
        if (ConfigHelper.isLoaded() && ConfigHelper.getInsanePlayersSeeMorphs(player.getWorld()) && WatheClient.moodComponent != null && WatheClient.moodComponent.isLowerThanDepressed()) {
            return;
        }

        UUID pronounsSource = watheextended$getMorphlingDisguise(target);
        if (pronounsSource == null) pronounsSource = target.getUuid();
        String pronouns = PronounsCache.get(pronounsSource);
        if (pronouns.isEmpty()) return;
        if (target.isInvisible()) return;

        Text pronounsText = Text.literal(pronouns);
        int pronounsWidth = renderer.getWidth(pronounsText);
        int color = 0xAAAAAA | ((int) (watheextended$pronounsAlpha * 255) << 24);

        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6f, 0f);
        context.getMatrices().scale(0.6f, 0.6f, 1f);
        context.drawTextWithShadow(renderer, pronounsText, -pronounsWidth / 2, 16 + renderer.fontHeight + 2, color);
        context.getMatrices().pop();
    }

    // compat: noelle's roles morphling disguise
    private static UUID watheextended$getMorphlingDisguise(PlayerEntity target) {
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return null;
        try {
            Class<?> cls = Class.forName("org.agmas.noellesroles.morphling.MorphlingPlayerComponent");
            Field keyField = cls.getDeclaredField("KEY");
            keyField.setAccessible(true);
            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentKey key = (ComponentKey) keyField.get(null);
            Object component = key.get(target);
            Field morphTicksField = cls.getDeclaredField("morphTicks");
            morphTicksField.setAccessible(true);
            if ((int) morphTicksField.get(component) <= 0) return null;
            Field disguiseField = cls.getDeclaredField("disguise");
            disguiseField.setAccessible(true);
            return (UUID) disguiseField.get(component);
        } catch (Exception ignored) {
            return null;
        }
    }
}
