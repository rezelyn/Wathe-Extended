package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

    private static final Set<String> ABILITY_READY_KEYS = Set.of("tip.starexpress.starstruck", "tip.phantom", "tip.recaller.teleport", "tip.recaller.place", "tip.kinswathe.ability.can_use", "hud.stupid_express.thief.ready");
    private static final Set<String> ABILITY_COOLDOWN_KEYS = Set.of("tip.starexpress.cooldown", "tip.noellesroles.cooldown", "tip.kinswathe.cooldown", "hud.stupid_express.thief.cooldown");
    private static final Set<String> ABILITY_COST_KEYS = Set.of("tip.kinswathe.ability.not_enough_money", "tip.recaller.not_enough_money");
    private static final Set<String> ABILITY_VULTURE_KEYS = Set.of("tip.vulture");
    private static final Set<String> ABILITY_DREAMER_KEYS = Set.of("tip.kinswathe.dreamer.counts");
    private static final Set<String> CLEANER_ABILITY_KEYS = Set.of("tip.kinswathe.ability.can_use", "tip.kinswathe.cooldown", "tip.kinswathe.ability.not_enough_money");

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

        if (!(text.getContent() instanceof TranslatableTextContent content)) return;

        String key = content.getKey();
        Object[] args = content.getArgs();
        DrawContext context = (DrawContext) (Object) this;

        // cleaner player limit
        if (CLEANER_ABILITY_KEYS.contains(key) && watheextended$isCleanerAbilityDisabledByLimit()) {
            MutableText styled = Text.literal("§6⚠  ").append(Text.translatable("gui.watheextended.hud.ability.disabled").formatted(Formatting.YELLOW));
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, color));
            return;
        }

        // ready
        if (ABILITY_READY_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String keybind = args[0] instanceof Text t ? t.getString() : String.valueOf(args[0]);
            MutableText styled = Text.literal("§2✔ ").append(Text.translatable("gui.watheextended.hud.ability.ready").formatted(Formatting.GREEN)).append(Text.literal(" §8[§7" + keybind + "§8]"));
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, color));
        }

        // cooldown
        else if (ABILITY_COOLDOWN_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String seconds = String.valueOf(args[0]);
            MutableText styled = Text.literal("§4⏱ §c" + seconds + "s");
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, color));
        }

        // cost
        else if (ABILITY_COST_KEYS.contains(key)) {
            if (args == null || args.length == 0) return;
            String price = args[0] instanceof Text t ? t.getString() : String.valueOf(args[0]);
            MutableText styled = Text.literal("§4✘ §c" + price).append(ScreenUtils.icon("coin"));
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, color));
        }

        // progress (noellesroles:vulture)
        else if (ABILITY_VULTURE_KEYS.contains(key)) {
            if (args == null || args.length < 2) return;
            String eaten = String.valueOf(args[0]);
            String required = String.valueOf(args[1]);
            MutableText styled = Text.literal("☠ " + eaten + "/" + required);
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, 0xB56700));
        }

        // progress (kinswathe:dreamer)
        else if (ABILITY_DREAMER_KEYS.contains(key)) {
            if (args == null || args.length < 2) return;
            String counts = String.valueOf(args[0]);
            String required = String.valueOf(args[1]);
            MutableText styled = Text.literal("✦ " + counts + "/" + required);
            cir.setReturnValue(watheextended$drawAbilityHudText(context, renderer, styled, 0xE5CCFF));
        }
    }

    @Unique
    private boolean watheextended$isCleanerAbilityDisabledByLimit() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return false;
            int limit = ClientConfig.getInt("watheextended.cleaner.playerLimit", 0);
            if (limit <= 0) return false;
            Role role = GameWorldComponent.KEY.get(client.world).getRole(client.player);
            if (role == null || !role.identifier().toString().equals("kinswathe:cleaner")) return false;
            long aliveCount = client.world.getPlayers().stream().filter(p -> !p.isSpectator() && !p.isCreative()).count();
            return aliveCount < limit;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Unique
    private static int watheextended$drawAbilityHudText(DrawContext context, TextRenderer renderer, MutableText styled, int color) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                int fade = GameWorldComponent.KEY.get(client.world).getFade();
                if (fade > 0) {
                    int alpha = (int)(255.0f * Math.max(0.0f, 1.0f - fade / (float) GameConstants.FADE_TIME));
                    if (alpha <= 3) return 0;
                    color = (alpha << 24) | (color & 0xFFFFFF);
                }
            }
        } catch (Throwable ignored) {}

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int styledWidth = renderer.getWidth(styled);
        return context.drawText(renderer, styled, width - styledWidth - 5, height - 12, color, true);
    }
}
