package cat.rezelyn.watheextended.mixin.client.hud;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = RoleNameRenderer.class)
public class PhantomCancelHudMixin {

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void watheextended$renderPhantomCancelHud(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        try {
            Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
            if (role == null || !"noellesroles:phantom".equals(role.identifier().toString())) return;
            var effect = player.getStatusEffect(StatusEffects.INVISIBILITY);
            if (effect == null) return;

            boolean canCancel = ClientConfig.getBool("watheextended.phantom.canCancelAbility", true);

            int color = 0xFFFFFF;
            int fade = GameWorldComponent.KEY.get(player.getWorld()).getFade();
            if (fade > 0) {
                int alpha = (int) (255.0f * Math.max(0.0f, 1.0f - fade / (float) GameConstants.FADE_TIME));
                if (alpha <= 3) return;
                color = (alpha << 24) | (color & 0xFFFFFF);
            }

            int seconds = (effect.getDuration() + 19) / 20;
            MutableText timerText = Text.literal("§8⏱ §7" + seconds + "s");
            int timerWidth = renderer.getWidth(timerText);
            context.drawText(renderer, timerText, context.getScaledWindowWidth() - timerWidth - 5, context.getScaledWindowHeight() - (canCancel ? 22 : 12), color, true);

            if (canCancel) {
                String keybind = "?";
                for (KeyBinding kb : MinecraftClient.getInstance().options.allKeys) {
                    if ("key.noellesroles.ability".equals(kb.getTranslationKey())) {
                        keybind = kb.getBoundKeyLocalizedText().getString();
                        break;
                    }
                }
                MutableText text = Text.literal("§4✘ ")
                        .append(Text.translatable("gui.watheextended.hud.ability.cancel").formatted(Formatting.RED))
                        .append(Text.literal(" §8[§7" + keybind + "§8]"));
                int textWidth = renderer.getWidth(text);
                context.drawText(renderer, text, context.getScaledWindowWidth() - textWidth - 5, context.getScaledWindowHeight() - 12, color, true);
            }
        } catch (Throwable ignored) {}
    }
}
