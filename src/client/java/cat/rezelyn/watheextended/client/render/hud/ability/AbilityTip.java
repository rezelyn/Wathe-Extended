package cat.rezelyn.watheextended.client.render.hud.ability;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.doctor4t.wathe.game.GameFunctions;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Builds and draws the bottom-right ability tips. */
public final class AbilityTip {
    private static final String KEY = "gui.watheextended.hud.ability.";

    private static final int MARGIN_RIGHT = 5;
    private static final int MARGIN_BOTTOM = 12;
    private static final int SPACING = 2;

    private AbilityTip() {}

    public static boolean isHidden(MinecraftClient client) {
        return !GameFunctions.isPlayerAliveAndSurvival(client.player) || !WatheExtendedClientConfig.showWatheHud;
    }

    public static MutableText ready(String keybind, boolean canUse) {
        return symbol("✔", canUse ? Formatting.DARK_GREEN : Formatting.DARK_GRAY)
                .append(translated("ready", canUse ? Formatting.GREEN : Formatting.GRAY))
                .append(keybind(keybind));
    }

    public static MutableText notReady(String keybind) {
        return symbol("✘", Formatting.DARK_GRAY)
                .append(translated("not_ready", Formatting.GRAY))
                .append(keybind(keybind));
    }

    public static int drawNotReady(
            DrawContext context, TextRenderer renderer, Text detail, String keybind, int color) {
        draw(context, renderer, detail, color, 1);
        return draw(context, renderer, notReady(keybind), color);
    }

    public static MutableText disabled() {
        return symbol("⚠", Formatting.GOLD).append(translated("disabled", Formatting.YELLOW));
    }

    public static MutableText cooldown(String seconds) {
        return timer(seconds, Formatting.DARK_RED, Formatting.RED);
    }

    public static MutableText price(String price) {
        return Text.empty()
                .append(Text.literal(price).formatted(Formatting.RED))
                .append(ScreenUtils.icon("coin"))
                .formatted(Formatting.RESET);
    }

    public static MutableText value(String symbol, int value) {
        return Text.literal(symbol + " " + value);
    }

    public static MutableText progress(String symbol, Object current, Object required, Text label) {
        return Text.literal(symbol + " " + current + "/" + required + " ").append(label);
    }

    private static MutableText cancel(String keybind) {
        return symbol("✘", Formatting.DARK_RED)
                .append(translated("cancel", Formatting.RED))
                .append(keybind(keybind));
    }

    private static MutableText timer(String seconds, Formatting iconColor, Formatting valueColor) {
        return symbol("⏱", iconColor).append(Text.literal(seconds + "s").formatted(valueColor));
    }

    private static MutableText symbol(String symbol, Formatting color) {
        return Text.literal(symbol + " ").formatted(color);
    }

    private static MutableText translated(String key, Formatting color) {
        return Text.translatable(KEY + key).formatted(color);
    }

    private static MutableText keybind(String keybind) {
        return Text.literal(" [")
                .formatted(Formatting.DARK_GRAY)
                .append(Text.literal(keybind).formatted(Formatting.GRAY))
                .append(Text.literal("]").formatted(Formatting.DARK_GRAY));
    }

    public static void drawDuration(
            DrawContext context, TextRenderer renderer, int ticks, boolean canCancel, String keyName) {
        String seconds = String.valueOf(Math.ceilDiv(ticks, 20));
        MutableText duration = timer(seconds, Formatting.DARK_GRAY, Formatting.GRAY);

        if (!canCancel) {
            draw(context, renderer, duration, 0xFFFFFF);
            return;
        }
        draw(context, renderer, duration, 0xFFFFFF, 1);
        draw(context, renderer, cancel(boundKeyLabel(keyName)), 0xFFFFFF);
    }

    public static int draw(DrawContext context, TextRenderer renderer, Text text, int color) {
        return draw(context, renderer, text, color, 0);
    }

    /**
     * Draws {@code text} right-aligned in the bottom-right corner, {@code row} lines up from the
     * bottom.
     *
     * @return the value of the underlying {@code drawText} call, or 0 if nothing was drawn
     */
    public static int draw(
            DrawContext context, TextRenderer renderer, Text text, int color, int row) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (isHidden(client)) return 0;

        int x = context.getScaledWindowWidth() - renderer.getWidth(text) - MARGIN_RIGHT;
        int y = context.getScaledWindowHeight() - MARGIN_BOTTOM - row * (renderer.fontHeight + SPACING);
        return context.drawText(renderer, text, x, y, color, true);
    }

    private static String boundKeyLabel(String keyBindingName) {
        return Arrays.stream(MinecraftClient.getInstance().options.allKeys)
                .filter(key -> keyBindingName.equals(key.getTranslationKey()))
                .findFirst()
                .map(key -> key.getBoundKeyLocalizedText().getString())
                .orElse("?");
    }

    public static void draw(DrawContext context, TextRenderer renderer, Text text, Formatting formatting, int row) {
        Integer color = formatting.getColorValue();
        draw(context, renderer, text, color == null ? 0xFFFFFF : color, row);
    }

    public static void draw(DrawContext context, TextRenderer renderer, Text text, Formatting formatting) {
        draw(context, renderer, text, formatting, 0);
    }
}
