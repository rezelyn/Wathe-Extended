package cat.rezelyn.watheextended.mixin.client.hud;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.client.gui.MoodRenderer;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(value = MoodRenderer.class)
public class MoodRendererMixin {

    private static final Random watheExtended$rng = new Random();
    private static float watheExtended$fadeProgress = 0f;
    private static final float FADE_SPEED = 2.75f;

    @Inject(method = "renderHud", at = @At("HEAD"))
    private static void watheExtended$updateFade(PlayerEntity player, TextRenderer text, DrawContext context, RenderTickCounter tick, CallbackInfo ci) {
        float delta = tick.getLastFrameDuration() / 20.0f;
        if (watheExtended$isAnxious(player)) {
            watheExtended$fadeProgress = Math.min(1f, watheExtended$fadeProgress + delta * FADE_SPEED);
        } else {
            watheExtended$fadeProgress = Math.max(0f, watheExtended$fadeProgress - delta * FADE_SPEED);
        }
    }

    // task text
    @WrapOperation(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 0))
    private static int watheExtended$shakeTaskText(DrawContext context, TextRenderer renderer, Text text, int x, int y, int color, Operation<Integer> op) {
        if (watheExtended$fadeProgress <= 0f) return op.call(context, renderer, text, x, y, color);
        context.getMatrices().push();
        context.getMatrices().translate(watheExtended$offset(1, false), watheExtended$offset(1, true), 0f);
        int result = op.call(context, renderer, text, x, y, color);
        context.getMatrices().pop();
        return result;
    }

    // icon
    @WrapOperation(method = "renderCivilian", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
    private static void watheExtended$shakeMoodIconCivilian(DrawContext context, Identifier texture, int x, int y, int w, int h, Operation<Void> op) {
        if (watheExtended$fadeProgress <= 0f) {
            op.call(context, texture, x, y, w, h);
            return;
        }
        context.getMatrices().push();
        context.getMatrices().translate(watheExtended$offset(2, false), watheExtended$offset(2, true), 0f);
        op.call(context, texture, x, y, w, h);
        context.getMatrices().pop();
    }

    // bar
    @WrapOperation(method = "renderCivilian", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    private static void watheExtended$shakeMoodBarCivilian(DrawContext context, int x1, int y1, int x2, int y2, int color, Operation<Void> op) {
        if (watheExtended$fadeProgress <= 0f) {
            op.call(context, x1, y1, x2, y2, color);
            return;
        }
        context.getMatrices().push();
        context.getMatrices().translate(0f, watheExtended$offset(3, true), 0f);
        op.call(context, x1, y1, x2, y2, color);
        context.getMatrices().pop();
    }

    private static float watheExtended$offset(int slot, boolean isY) {
        long time = System.currentTimeMillis();
        double freq;
        if (!isY) {
            freq = slot == 1 ? 0.018 : (slot == 2 ? 0.023 : 0.014);
        } else {
            freq = slot == 1 ? 0.027 : (slot == 2 ? 0.032 : 0.021);
        }
        double sine = isY ? Math.cos(time * freq) : Math.sin(time * freq);
        watheExtended$rng.setSeed(time / 55L + slot + (isY ? 10 : 0));
        return (float) ((sine * 0.22 + watheExtended$rng.nextGaussian() / 25.0) * watheExtended$fadeProgress);
    }

    private static boolean watheExtended$isAnxious(PlayerEntity player) {
        try {
            if (!FabricLoader.getInstance().isModLoaded("harpymodloader")) return false;
            if (WatheExtendedModifiers.INTROVERTED == null) return false;

            if (PlayerPsychoComponent.KEY.get(player).getPsychoTicks() > 0) return false; // ignore when psycho

            GameTimeComponent time = GameTimeComponent.KEY.get(player.getWorld());
            if (time.resetTime - time.getTime() < GameConstants.TIME_TO_FIRST_TASK) return false;

            WorldModifierComponent modifier = WorldModifierComponent.KEY.get(player.getWorld());
            if (!modifier.isModifier(player, WatheExtendedModifiers.INTROVERTED)) return false;
            float range = WatheExtendedServerConfig.getIntrovertedCrowdRange() * WatheExtendedServerConfig.getIntrovertedCrowdRange();
            int count = 0;
            for (PlayerEntity other : player.getWorld().getPlayers()) {
                if (other == player) continue;
                if (!GameFunctions.isPlayerAliveAndSurvival(other)) continue;
                if (other.squaredDistanceTo(player) <= range) count++;
            }
            return count >= WatheExtendedServerConfig.getIntrovertedCrowdCount();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
