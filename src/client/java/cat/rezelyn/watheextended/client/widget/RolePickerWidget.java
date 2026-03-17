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
    private static final int SEARCH_H = 13;
    private static final int SEP_H = 1;
    private static final int PAD = 2;
    private static final int COL_BORDER = 0x50252525;
    private static final int COL_BG = 0x10000000;
    private static final int COL_SEARCH = 0x75252525;
    private static final int COL_SEP = 0x75252525;
    private static final int COL_HOVER = 0x50505050;
    private static final int COL_HINT = 0xFF505050;

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
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        updateScrollSmooth(delta);
        int x0 = getX(), y0 = getY(), x1 = x0 + width, y1 = y0 + height;
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        ctx.fill(x0, y0, x1, y1, COL_BORDER);
        ctx.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, COL_BG);

        ctx.fill(x0 + 1, y0 + 1, x1 - 1, y0 + SEARCH_H, COL_SEARCH);

        Text displayText;
        int searchColor;

        if (searchFilter.isEmpty()) {
            displayText = Text.translatable("gui.watheextended.inventory.rolepicker.search");
            searchColor = COL_HINT;
        } else {
            displayText = Text.literal(isFocused() ? searchFilter + "|" : searchFilter);
            searchColor = 0xFFFFFFFF;
        }

        ctx.drawTextWithShadow(tr, displayText, x0 + PAD + 2, y0 + (SEARCH_H - tr.fontHeight) / 2 + 1, searchColor);

        ctx.fill(x0 + 1, y0 + SEARCH_H, x1 - 1, y0 + SEARCH_H + SEP_H, COL_SEP);
        ctx.enableScissor(x0 + 1, listTop(), x1 - 1, listBottom());

        List<RoleEntry> entries = filtered();
        int y = listTop() - (int) scrollSmooth;
        for (RoleEntry entry : entries) {
            if (y + ENTRY_H > listTop() && y < listBottom()) {
                boolean hovered = mouseX >= x0 && mouseX < x1 && mouseY >= y && mouseY < y + ENTRY_H;
                if (hovered) {
                    ctx.fill(x0 + 1, y, x1 - 1, y + ENTRY_H, COL_HOVER);
                }
                int textY = y + (ENTRY_H - tr.fontHeight) / 2;
                ctx.drawTextWithShadow(tr, entry.label(), x0 + PAD + 2, textY, entry.color());
            }
            y += ENTRY_H;
        }

        ctx.disableScissor();

        if (entries.isEmpty()) {
            Text hint = Text.translatable("gui.watheextended.inventory.rolepicker.no_matches");
            int hx = x0 + (width - tr.getWidth(hint)) / 2;
            int hy = listTop() + (listH() - tr.fontHeight) / 2;
            ctx.drawTextWithShadow(tr, hint, hx, hy, COL_HINT);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!visible || !isMouseOver(mx, my)) return false;
        setFocused(true);

        if (button == 1 && my >= listTop()) {
            isDragging = true;
            dragAnchorY = (int) my;
            dragAnchorScroll = scrollTarget;
            return true;
        }

        if (button == 0 && my >= listTop()) {
            List<RoleEntry> entries = filtered();
            int y = listTop() - (int) scrollSmooth;
            for (RoleEntry entry : entries) {
                if (my >= y && my < y + ENTRY_H) {
                    onSelect.accept(entry.sendValue());
                    return true;
                }
                y += ENTRY_H;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (!visible || !isDragging || button != 1) return false;
        int dragDelta = dragAnchorY - (int) my; // drag up → scroll down
        int max = Math.max(0, filtered().size() * ENTRY_H - listH());
        scrollTarget = Math.max(0, Math.min(dragAnchorScroll + dragDelta, max));
        return true;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 1) isDragging = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hDelta, double vDelta) {
        if (!visible || !isMouseOver(mx, my)) return false;
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
    public boolean charTyped(char chr, int modifiers) {
        if (!isFocused() || !visible) return false;
        if (chr >= 32) {
            searchFilter += chr;
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

