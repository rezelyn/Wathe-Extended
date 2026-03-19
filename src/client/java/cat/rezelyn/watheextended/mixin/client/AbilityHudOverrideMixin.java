package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.client.screen.guidebook.GuidebookIcons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class AbilityHudOverrideMixin {

    @Unique
    private static final Set<String> ABILITY_READY_KEYS = Set.of("tip.starexpress.starstruck", "tip.phantom", "tip.recaller.teleport", "tip.recaller.place", "tip.kinswathe.ability.can_use", "hud.stupid_express.thief.ready");

    @Unique
    private static final Set<String> ABILITY_COOLDOWN_KEYS = Set.of("tip.starexpress.cooldown", "tip.noellesroles.cooldown", "tip.kinswathe.cooldown", "hud.stupid_express.thief.cooldown");

    @Unique
    private static final Set<String> ABILITY_COST_KEYS = Set.of("tip.kinswathe.ability.not_enough_money", "tip.recaller.not_enough_money");

    @Unique
    private static final Set<String> ABILITY_VULTURE_KEYS = Set.of("tip.vulture");

    @Unique
    private static final Set<String> ABILITY_DREAMER_KEYS = Set.of("tip.kinswathe.dreamer.counts");

    @Inject(method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", at = @At("HEAD"), cancellable = true)
    private void watheextended$overrideAbilityHudTextShadow(TextRenderer renderer, Text text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
        watheextended$handleAbilityHud(renderer, text, color, cir);
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void watheextended$overrideAbilityHudTextDirect(TextRenderer renderer, Text text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        watheextended$handleAbilityHud(renderer, text, color, cir);
    }

    @Unique
    private void watheextended$handleAbilityHud(TextRenderer renderer, Text text, int color, CallbackInfoReturnable<Integer> cir) {

        if (!(text.getContent() instanceof TranslatableTextContent ttc)) return;

        String key = ttc.getKey();
        Object[] args = ttc.getArgs();
        DrawContext ctx = (DrawContext) (Object) this;

        // ready
        if (ABILITY_READY_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String keybind = args[0] instanceof Text t ? t.getString() : String.valueOf(args[0]);
            MutableText styled = Text.literal("§2✔ ").append(Text.translatable("gui.watheextended.ability.ready").formatted(Formatting.GREEN)).append(Text.literal(" §8[§7" + keybind + "§8]"));
            cir.setReturnValue(watheextended$drawAbilityHudText(ctx, renderer, styled, color));
        }

        // cooldown
        else if (ABILITY_COOLDOWN_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String seconds = String.valueOf(args[0]);
            MutableText styled = Text.literal("§4⏱ §c" + seconds + "s");
            cir.setReturnValue(watheextended$drawAbilityHudText(ctx, renderer, styled, color));
        }

        // cost
        else if (ABILITY_COST_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String price = args[0] instanceof Text t ? t.getString() : String.valueOf(args[0]);
            MutableText styled = Text.literal("§4✘ §c" + price).append(GuidebookIcons.icon("coin"));
            cir.setReturnValue(watheextended$drawAbilityHudText(ctx, renderer, styled, color));
        }

        // progress (noellesroles:vulture)
        else if (ABILITY_VULTURE_KEYS.contains(key)) {
            if (args == null || args.length < 2) return;
            String eaten = String.valueOf(args[0]);
            String required = String.valueOf(args[1]);
            MutableText styled = Text.literal("☠ " + eaten + "/" + required);
            cir.setReturnValue(watheextended$drawAbilityHudText(ctx, renderer, styled, 0xB56700));
        }

        // progress (kinswathe:dreamer)
        else if (ABILITY_DREAMER_KEYS.contains(key)) {
            if (args == null || args.length < 2) return;
            String counts = String.valueOf(args[0]);
            String required = String.valueOf(args[1]);
            MutableText styled = Text.literal("✦ " + counts + "/" + required);
            cir.setReturnValue(watheextended$drawAbilityHudText(ctx, renderer, styled, 0xE5CCFF));
        }
    }

    @Unique
    private static int watheextended$drawAbilityHudText(DrawContext ctx, TextRenderer renderer, MutableText styled, int color) {
        int width = ctx.getScaledWindowWidth();
        int height = ctx.getScaledWindowHeight();
        int sw = renderer.getWidth(styled);
        return ctx.drawText(renderer, styled, width - sw - 5, height - 12, color, true);
    }
}
