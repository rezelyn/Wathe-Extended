package cat.rezelyn.watheextended.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

public class RolePickerWidget extends ClickableWidget {

    private static final int ENTRY_H = 12;
    private static final int SEARCH_H = 14;
    private static final int SEP_H = 1;
    private static final int PAD = 2;
    private static final int COLOR_BORDER_TOP = 0xFFC5A244;
    private static final int COLOR_BORDER_BOTTOM = 0xFF815A15;
    private static final int COLOR_BACKGROUND = 0xFF160902;
    private static final int COLOR_HOVER = 0xFF9A702A;
    private static final int COLOR_HINT = 0xFF808080;

    private final List<RoleEntry> allEntries;
    private final Consumer<String> onSelect;
    private String searchFilter = "";
    private int scrollTarget = 0;
    private float scrollSmooth = 0f;

    private boolean isDragging = false;
    private int dragAnchorY = 0;
    private int dragAnchorScroll = 0;

    public RolePickerWidget(int x, int y, int width, int height, List<RoleEntry> allEntries, Consumer<String> onSelect) {
        super(x, y, width, height, Text.empty());
        this.allEntries = allEntries;
        this.onSelect = onSelect;
    }

    private List<RoleEntry> filtered() {
        if (searchFilter.isEmpty()) return allEntries;
        String lf = searchFilter.toLowerCase();
        return allEntries.stream().filter(e -> e.label().getString().toLowerCase().contains(lf)).toList();
    }

    private int listTop() {
        return getY() + SEARCH_H + SEP_H;
    }

    private int listBottom() {
        return getY() + height;
    }

    private int listH() {
        return listBottom() - listTop();
    }

    private void clampScroll() {
        int max = Math.max(0, filtered().size() * ENTRY_H - listH());
        scrollTarget = Math.max(0, Math.min(scrollTarget, max));
    }

    private void updateScrollSmooth(float delta) {
        float speed = 1f - (float) Math.pow(0.16, delta);
        scrollSmooth += (scrollTarget - scrollSmooth) * speed;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        updateScrollSmooth(delta);
        int x0 = getX(), y0 = getY(), x1 = x0 + width, y1 = y0 + height;
        TextRenderer render = MinecraftClient.getInstance().textRenderer;

        context.fillGradient(x0, y0, x1, y1, COLOR_BORDER_TOP, COLOR_BORDER_BOTTOM);
        context.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, COLOR_BACKGROUND);
        context.fill(x0 + 1, y0 + 1, x1 - 1, y0 + SEARCH_H, COLOR_BACKGROUND);

        Text text;
        int searchColor;

        if (searchFilter.isEmpty()) {
            text = Text.translatable("gui.watheextended.inventory.rolepicker.search");
            searchColor = COLOR_HINT;
        } else {
            text = Text.literal(isFocused() ? searchFilter + "|" : searchFilter);
            searchColor = 0xFFFFFFFF;
        }

        context.drawTextWithShadow(render, text, x0 + PAD + 2, y0 + (SEARCH_H - render.fontHeight) / 2 + 1, searchColor);
        context.fill(x0 + 1, y0 + SEARCH_H, x1 - 1, y0 + SEARCH_H + SEP_H, COLOR_BORDER_TOP);
        context.enableScissor(x0 + 1, listTop(), x1 - 1, listBottom());

        List<RoleEntry> entries = filtered();
        int y = listTop() - (int) scrollSmooth;
        for (RoleEntry entry : entries) {
            if (y + ENTRY_H > listTop() && y < listBottom()) {
                boolean hovered = mouseX >= x0 && mouseX < x1 && mouseY >= y && mouseY < y + ENTRY_H;
                if (hovered) {
                    context.fill(x0 + 1, y, x1 - 1, y + ENTRY_H, COLOR_HOVER);
                }
                int textY = y + (ENTRY_H - render.fontHeight) / 2;
                context.drawTextWithShadow(render, entry.label(), x0 + PAD + 2, textY, entry.color());
            }
            y += ENTRY_H;
        }

        context.disableScissor();

        if (entries.isEmpty()) {
            Text hint = Text.translatable("gui.watheextended.inventory.rolepicker.no_matches");
            int hintX = x0 + (width - render.getWidth(hint)) / 2;
            int hintY = listTop() + (listH() - render.fontHeight) / 2;
            context.drawTextWithShadow(render, hint, hintX, hintY, COLOR_HINT);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        setFocused(true);

        if (button == 1 && mouseY >= listTop()) {
            isDragging = true;
            dragAnchorY = (int) mouseY;
            dragAnchorScroll = scrollTarget;
            return true;
        }

        if (button == 0 && mouseY >= listTop()) {
            List<RoleEntry> entries = filtered();
            int y = listTop() - (int) scrollSmooth;
            for (RoleEntry entry : entries) {
                if (mouseY >= y && mouseY < y + ENTRY_H) {
                    onSelect.accept(entry.sendValue());
                    return true;
                }
                y += ENTRY_H;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!visible || !isDragging || button != 1) return false;
        int delta = dragAnchorY - (int) mouseY; // drag up → scroll down
        int max = Math.max(0, filtered().size() * ENTRY_H - listH());
        scrollTarget = Math.max(0, Math.min(dragAnchorScroll + delta, max));
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hDelta, double vDelta) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        scrollTarget -= (int) (vDelta * ENTRY_H * 2);
        clampScroll();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused() || !visible) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!searchFilter.isEmpty()) {
                searchFilter = "";
                scrollTarget = 0;
                clampScroll();
                return true;
            }
            return false;
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!searchFilter.isEmpty()) {
                searchFilter = searchFilter.substring(0, searchFilter.length() - 1);
                scrollTarget = 0;
                clampScroll();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean charTyped(char chars, int modifiers) {
        if (!isFocused() || !visible) return false;
        if (chars >= 32) {
            searchFilter += chars;
            scrollTarget = 0;
            clampScroll();
            return true;
        }
        return false;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public Selectable.SelectionType getType() {
        return isFocused() ? Selectable.SelectionType.FOCUSED : Selectable.SelectionType.NONE;
    }

    public record RoleEntry(Text label, int color, String sendValue) {
    }
}

