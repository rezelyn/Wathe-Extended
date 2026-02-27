package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.watheextended.api.cca.GameStatus;
import cat.rezelyn.watheextended.client.screen.guidebook.GuidebookEntry;
import cat.rezelyn.watheextended.client.screen.guidebook.GuidebookEntryBuilder;
import cat.rezelyn.watheextended.client.screen.guidebook.GuidebookPageContent;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class GuidebookScreen extends Screen {

    static final int BOOK_WIDTH = 295;
    static final int BOOK_HEIGHT = 180;
    static final int LINE_HEIGHT = 11;
    static final int CONTENT_PADDING = 5;
    private static final Identifier BOOK_TEXTURE = Identifier.of("watheextended", "textures/gui/guidebook/book.png");
    private static final float TITLE_SCALE = 1.3f;

    // colours (hex + alpha)
    private static final int COLOR_SELECTED_BG = 0x33000000;
    private static final int COLOR_HOVER_BG = 0x1A000000;
    private static final int COLOR_SCROLL_BAR = 0xFF8B7355;
    private static final int COLOR_SCROLL_TRACK = 0x33000000;
    private static final int COLOR_RIGHT_TEXT = 0xFF3B2A1A;
    private static final int COLOR_SELECT_HINT = 0xFF9B8B6B;

    // navigation bar
    private static final int PAGE_COUNT = GuidebookPageContent.PAGE_LABELS.length;
    private static final int PAGE_BTN_W = 40;
    private static final int PAGE_BTN_H = 12;
    private static final int PAGE_BTN_MARGIN = 4;

    // runtime
    private Tab activeTab = Tab.ROLES;
    private boolean isOpened = true;

    private int bookX, bookY;
    private int leftPageX, leftPageY, leftPageWidth, leftPageHeight;
    private int rightPageX, rightPageY, rightPageWidth, rightPageHeight;

    // lpage scroll
    private int leftScrollTarget = 0;
    private float leftScrollSmooth = 0f;
    private int leftTotalHeight = 0;
    private boolean isDraggingScroll = false;
    private int dragStartY = 0, dragStartScroll = 0;

    // rpage scroll
    private int rightScrollTarget = 0;
    private float rightScrollSmooth = 0f;
    private int rightTotalHeight = 0;
    private boolean isDraggingRightScroll = false;
    private int dragStartRightY = 0, dragStartRightScroll = 0;

    // cached entry lists
    private List<GuidebookEntry> rolesEntries = null;
    private List<GuidebookEntry> modifiersEntries = null;
    private List<GuidebookEntry> itemsEntries = null;

    // selected entry
    private String selectedId = null;
    private Text selectedTitle = null;
    private int selectedColor = 0xFF3B2A1A;
    private String selectedDescKey = null;
    private String selectedEntryId = null;

    // rpage paging (0 = description, 1 = abilities, 2 = items)
    private int currentPage = 0;
    private List<Text> selectedDescLines = null;
    private boolean selectedDescNoContent = false;

    public GuidebookScreen() {
        super(Text.translatable("gui.watheextended.guidebook.title"));
    }

    private static int opaque(int color) {
        return 0xFF000000 | (color & 0x00FFFFFF);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    protected void init() {
        super.init();
        bookX = (width - BOOK_WIDTH) / 2;
        bookY = (height - BOOK_HEIGHT) / 2;

        int pageMarginX = 14, pageMarginTop = 16, pageMarginBottom = 12, spineW = 7;
        int halfBook = BOOK_WIDTH / 2;

        leftPageX = bookX + pageMarginX;
        leftPageY = bookY + pageMarginTop;
        leftPageWidth = halfBook - pageMarginX - spineW / 2 - 4;
        leftPageHeight = BOOK_HEIGHT - pageMarginTop - pageMarginBottom;

        rightPageX = bookX + halfBook + spineW / 2 + 4;
        rightPageY = bookY + pageMarginTop;
        rightPageWidth = halfBook - pageMarginX - spineW / 2 - 4;
        rightPageHeight = BOOK_HEIGHT - pageMarginTop - pageMarginBottom;

        int tabCount = Tab.values().length;
        int tabWidth = 60;
        int totalTabW = tabCount * tabWidth + (tabCount - 1) * 2;
        int tabStartX = bookX + (BOOK_WIDTH - totalTabW) / 2;
        int tabY = bookY - 22;

        clearChildren();
        for (int i = 0; i < tabCount; i++) {
            final Tab tab = Tab.values()[i];
            int tx = tabStartX + i * (tabWidth + 2);
            addDrawableChild(ButtonWidget.builder(tab.label, btn -> selectTab(tab))
                    .dimensions(tx, tabY, tabWidth, 18).build());
        }

        refreshEntries();
        if (isOpened) {
            isOpened = false;
            autoSelectPlayerRole();
            playSound(WatheExtendedSounds.GUIDEBOOK_OPEN);
        }
    }

    @Override
    public void removed() {
        playSound(WatheExtendedSounds.GUIDEBOOK_CLOSE);
        super.removed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float speed = 1f - (float) Math.pow(0.08, delta);
        leftScrollSmooth += (leftScrollTarget - leftScrollSmooth) * speed;
        rightScrollSmooth += (rightScrollTarget - rightScrollSmooth) * speed;

        context.fill(0, 0, width, height, 0xB0000000);
        context.drawTexture(BOOK_TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);
        renderLeftPage(context, mouseX, mouseY);
        renderRightPage(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLeftPage(DrawContext context, int mouseX, int mouseY) {
        int scrollbarW = 3;
        int usableW = leftPageWidth - CONTENT_PADDING * 2 - scrollbarW - 2;
        context.enableScissor(leftPageX, leftPageY, leftPageX + leftPageWidth, leftPageY + leftPageHeight);

        int y = leftPageY + CONTENT_PADDING - (int) leftScrollSmooth;

        for (GuidebookEntry entry : currentEntries()) {
            if (entry.text().getString().isEmpty()) {
                y += LINE_HEIGHT / 2;
                continue;
            }

            if (entry.isHeader()) {
                if (y + LINE_HEIGHT >= leftPageY && y <= leftPageY + leftPageHeight) {
                    int color = opaque(entry.color());
                    context.drawText(textRenderer, entry.text(), leftPageX + CONTENT_PADDING, y, color, true);
                }
                y += LINE_HEIGHT + 3;
                continue;
            }

            List<OrderedText> wrapped = textRenderer.wrapLines(entry.text(), usableW);
            int blockH = wrapped.size() * LINE_HEIGHT;

            if (y + blockH >= leftPageY && y <= leftPageY + leftPageHeight) {
                boolean isSelected = entry.id() != null && entry.id().equals(selectedId);
                boolean isHovered = entry.id() != null
                        && mouseX >= leftPageX && mouseX <= leftPageX + leftPageWidth
                        && mouseY >= y && mouseY < y + blockH;

                if (isSelected)
                    context.fill(leftPageX, y, leftPageX + leftPageWidth - scrollbarW - 2, y + blockH, COLOR_SELECTED_BG);
                else if (isHovered)
                    context.fill(leftPageX, y, leftPageX + leftPageWidth - scrollbarW - 2, y + blockH, COLOR_HOVER_BG);

                int color = opaque(entry.color());
                for (OrderedText line : wrapped) {
                    context.drawText(textRenderer, line, leftPageX + CONTENT_PADDING, y, color, false);
                    y += LINE_HEIGHT;
                }
            } else {
                y += blockH;
            }
        }

        context.disableScissor();
        renderScrollbar(context, leftPageX, leftPageY, leftPageWidth, leftPageHeight,
                leftTotalHeight, leftScrollSmooth);
    }

    private boolean isPaged() {
        return activeTab == Tab.ROLES;
    }

    private void renderRightPage(DrawContext context) {
        boolean paged = isPaged();
        int navBarH = paged ? PAGE_BTN_H + PAGE_BTN_MARGIN * 2 : 0;
        int scrollAreaH = rightPageHeight - navBarH;

        if (selectedId == null) {
            Text hint = Text.translatable("gui.watheextended.guidebook.hint.select");
            int hx = rightPageX + (rightPageWidth - textRenderer.getWidth(hint)) / 2;
            int hy = rightPageY + rightPageHeight / 2 - LINE_HEIGHT / 2;
            context.drawText(textRenderer, hint.copy().styled(s -> s.withItalic(true)), hx, hy, COLOR_SELECT_HINT, false);
            return;
        }

        int scrollbarW = 3;
        int usableW = rightPageWidth - CONTENT_PADDING * 2 - scrollbarW - 2;
        context.enableScissor(rightPageX, rightPageY, rightPageX + rightPageWidth, rightPageY + scrollAreaH);

        int y = rightPageY + CONTENT_PADDING - (int) rightScrollSmooth;

        int titleColor = opaque(selectedColor);
        int scaledTitleW = (int) (usableW / TITLE_SCALE);
        int scaledLineH = (int) Math.ceil(LINE_HEIGHT * TITLE_SCALE);
        for (OrderedText tl : textRenderer.wrapLines(selectedTitle.copy().styled(s -> s.withBold(true)), scaledTitleW)) {
            if (y >= rightPageY && y <= rightPageY + scrollAreaH) {
                int lineW = (int) (textRenderer.getWidth(tl) * TITLE_SCALE);
                int centeredX = rightPageX + CONTENT_PADDING + (usableW - lineW) / 2;
                context.getMatrices().push();
                context.getMatrices().translate(centeredX, y, 0);
                context.getMatrices().scale(TITLE_SCALE, TITLE_SCALE, 1f);
                context.drawText(textRenderer, tl, 0, 0, titleColor, true);
                context.getMatrices().pop();
            }
            y += scaledLineH;
        }
        y += 4;

        if (paged) {
            Text pageLabel = Text.translatable("gui.watheextended.guidebook.subtitle.separator",
                    Text.translatable(GuidebookPageContent.PAGE_LABELS[currentPage]));
            int labelX = rightPageX + CONTENT_PADDING + (usableW - textRenderer.getWidth(pageLabel)) / 2;
            if (y >= rightPageY && y <= rightPageY + scrollAreaH) {
                context.drawText(textRenderer,
                        pageLabel.copy().styled(s -> s.withItalic(true)), labelX, y, COLOR_SELECT_HINT, false);
            }
            y += LINE_HEIGHT + 2;
        }

        if (selectedDescLines != null) {
            if (selectedDescNoContent) {
                Text noContent = selectedDescLines.get(0);
                int nx = rightPageX + (rightPageWidth - textRenderer.getWidth(noContent)) / 2;
                int ny = rightPageY + scrollAreaH / 2 - LINE_HEIGHT / 2;
                context.drawText(textRenderer, noContent.copy().styled(s -> s.withItalic(true)), nx, ny, COLOR_SELECT_HINT, false);
            } else {
                for (Text line : selectedDescLines) {
                    if (line.getString().isEmpty()) {
                        y += LINE_HEIGHT / 2;
                        continue;
                    }
                    for (OrderedText wl : textRenderer.wrapLines(line, usableW)) {
                        if (y >= rightPageY && y <= rightPageY + scrollAreaH) {
                            context.drawText(textRenderer, wl, rightPageX + CONTENT_PADDING, y, COLOR_RIGHT_TEXT, false);
                        }
                        y += LINE_HEIGHT;
                    }
                }
            }
        }

        context.disableScissor();
        renderScrollbar(context, rightPageX, rightPageY, rightPageWidth, scrollAreaH,
                rightTotalHeight, rightScrollSmooth);
        if (paged) renderPageNavButtons(context, scrollAreaH);
    }

    private void renderScrollbar(DrawContext context,
                                 int pageX, int pageY, int pageW, int pageH,
                                 int totalH, float scrollSmooth) {
        if (totalH <= pageH) return;
        int sx = pageX + pageW - 4;
        context.fill(sx, pageY, sx + 3, pageY + pageH, COLOR_SCROLL_TRACK);
        int thumbH = Math.max(12, pageH * pageH / totalH);
        int maxScroll = Math.max(1, totalH - pageH);
        int thumbY = pageY + (int) (scrollSmooth * (pageH - thumbH) / maxScroll);
        context.fill(sx, thumbY, sx + 3, thumbY + thumbH, COLOR_SCROLL_BAR);
    }

    private void renderPageNavButtons(DrawContext context, int scrollAreaH) {
        int btnY = navBtnY(scrollAreaH);
        drawNavButton(context, navPrevX(), btnY, "gui.watheextended.guidebook.button.prev", currentPage > 0);
        drawNavButton(context, navNextX(), btnY, "gui.watheextended.guidebook.button.next", currentPage < PAGE_COUNT - 1);

        String indicator = (currentPage + 1) + " / " + PAGE_COUNT;
        int cx = rightPageX + (rightPageWidth - textRenderer.getWidth(indicator)) / 2;
        context.drawText(textRenderer, indicator, cx, btnY + (PAGE_BTN_H - 8) / 2, COLOR_SELECT_HINT, false);
    }

    private void drawNavButton(DrawContext context, int x, int y, String translationKey, boolean active) {
        context.fill(x, y, x + PAGE_BTN_W, y + PAGE_BTN_H, active ? 0xCC6B5A40 : 0x55443322);
        Text label = Text.translatable(translationKey);
        int lw = textRenderer.getWidth(label);
        context.drawText(textRenderer, label,
                x + (PAGE_BTN_W - lw) / 2, y + (PAGE_BTN_H - 8) / 2,
                active ? 0xFFE8D9B8 : 0x77998870, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        boolean inBook = mouseX >= bookX && mouseX <= bookX + BOOK_WIDTH
                && mouseY >= bookY && mouseY <= bookY + BOOK_HEIGHT;
        if (!inBook) return super.mouseClicked(mouseX, mouseY, button);

        if (isOnLeftScrollbar(mouseX, mouseY)) {
            isDraggingScroll = true;
            dragStartY = (int) mouseY;
            dragStartScroll = leftScrollTarget;
            return true;
        }
        if (isOnRightScrollbar(mouseX, mouseY)) {
            isDraggingRightScroll = true;
            dragStartRightY = (int) mouseY;
            dragStartRightScroll = rightScrollTarget;
            return true;
        }
        if (selectedId != null && isPaged()) {
            int scrollAreaH = rightPageHeight - (PAGE_BTN_H + PAGE_BTN_MARGIN * 2);
            int btnY = navBtnY(scrollAreaH);
            if (mouseY >= btnY && mouseY <= btnY + PAGE_BTN_H) {
                if (mouseX >= navPrevX() && mouseX <= navPrevX() + PAGE_BTN_W && currentPage > 0) {
                    changePage(currentPage - 1);
                    return true;
                }
                if (mouseX >= navNextX() && mouseX <= navNextX() + PAGE_BTN_W && currentPage < PAGE_COUNT - 1) {
                    changePage(currentPage + 1);
                    return true;
                }
            }
        }
        GuidebookEntry clicked = entryAt((int) mouseX, (int) mouseY);
        if (clicked != null && clicked.id() != null) {
            selectEntry(clicked);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (isDraggingScroll && button == 0) {
            int delta = (int) mouseY - dragStartY;
            int maxS = Math.max(0, leftTotalHeight - leftPageHeight);
            int thumbH = Math.max(12, leftPageHeight * leftPageHeight / Math.max(1, leftTotalHeight));
            int range = leftPageHeight - thumbH;
            if (range > 0) {
                leftScrollTarget = clamp(dragStartScroll + delta * maxS / range, 0, maxS);
                leftScrollSmooth = leftScrollTarget;
            }
            return true;
        }
        if (isDraggingRightScroll && button == 0) {
            int sah = rightPageHeight - (isPaged() ? PAGE_BTN_H + PAGE_BTN_MARGIN * 2 : 0);
            int delta = (int) mouseY - dragStartRightY;
            int maxS = Math.max(0, rightTotalHeight - sah);
            int thumbH = Math.max(12, sah * sah / Math.max(1, rightTotalHeight));
            int range = sah - thumbH;
            if (range > 0) {
                rightScrollTarget = clamp(dragStartRightScroll + delta * maxS / range, 0, maxS);
                rightScrollSmooth = rightScrollTarget;
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingScroll = false;
            isDraggingRightScroll = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double ha, double va) {
        boolean inBook = mouseX >= bookX && mouseX <= bookX + BOOK_WIDTH
                && mouseY >= bookY && mouseY <= bookY + BOOK_HEIGHT;
        if (!inBook) return super.mouseScrolled(mouseX, mouseY, ha, va);
        if (mouseX >= rightPageX && mouseX <= rightPageX + rightPageWidth
                && mouseY >= rightPageY && mouseY <= rightPageY + rightPageHeight) {
            scrollRight((int) (-va * 10));
        } else {
            scrollLeft((int) (-va * 10));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return switch (keyCode) {
            case 256 -> {
                close();
                yield true;
            }
            case 264 -> {
                scrollLeft(10);
                yield true;
            }
            case 265 -> {
                scrollLeft(-10);
                yield true;
            }
            case 266 -> {
                scrollLeft(-leftPageHeight);
                yield true;
            }
            case 267 -> {
                scrollLeft(leftPageHeight);
                yield true;
            }
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    private void selectTab(Tab tab) {
        activeTab = tab;
        leftScrollTarget = 0;
        leftScrollSmooth = 0f;
        rightScrollTarget = 0;
        rightScrollSmooth = 0f;
        selectedId = null;
        selectedTitle = null;
        selectedDescLines = null;
        selectedDescNoContent = false;
        selectedDescKey = null;
        selectedEntryId = null;
        currentPage = 0;
        refreshEntries();
    }

    private void selectEntry(GuidebookEntry entry) {
        playSound(WatheExtendedSounds.GUIDEBOOK_PAGE);
        selectedId = entry.id();
        selectedTitle = entry.displayTitle();
        selectedColor = entry.color();
        selectedDescKey = entry.descriptionKey();
        selectedEntryId = entry.id();
        currentPage = 0;
        rightScrollTarget = 0;
        rightScrollSmooth = 0f;
        loadPageContent();
    }

    private void changePage(int page) {
        currentPage = page;
        rightScrollTarget = 0;
        rightScrollSmooth = 0f;
        loadPageContent();
        playSound(WatheExtendedSounds.GUIDEBOOK_PAGE);
    }

    private void loadPageContent() {
        GuidebookPageContent.PageResult result = GuidebookPageContent.resolve(selectedDescKey, selectedEntryId, currentPage);
        selectedDescLines = result.lines();
        selectedDescNoContent = result.noContent();
        recalcRightHeight();
    }

    private void refreshEntries() {
        switch (activeTab) {
            case ROLES -> {
                if (rolesEntries == null) rolesEntries = GuidebookEntryBuilder.roles().build();
            }
            case MODIFIERS -> {
                if (modifiersEntries == null) modifiersEntries = GuidebookEntryBuilder.modifiers().build();
            }
            case ITEMS -> {
                if (itemsEntries == null) itemsEntries = GuidebookEntryBuilder.items().build();
            }
        }
        recalcLeftHeight();
    }

    private List<GuidebookEntry> currentEntries() {
        return switch (activeTab) {
            case ROLES -> rolesEntries != null ? rolesEntries : List.of();
            case MODIFIERS -> modifiersEntries != null ? modifiersEntries : List.of();
            case ITEMS -> itemsEntries != null ? itemsEntries : List.of();
        };
    }

    private void recalcLeftHeight() {
        int h = 0;
        for (GuidebookEntry e : currentEntries()) {
            if (e.isHeader()) {
                h += LINE_HEIGHT + 3;
            } else if (e.text().getString().isEmpty()) {
                h += LINE_HEIGHT / 2;
            } else {
                List<OrderedText> wrapped = textRenderer != null
                        ? textRenderer.wrapLines(e.text(), leftPageWidth - CONTENT_PADDING * 2 - 6)
                        : List.of(e.text().asOrderedText());
                h += wrapped.size() * LINE_HEIGHT;
            }
        }
        leftTotalHeight = h + CONTENT_PADDING * 2;
    }

    private void recalcRightHeight() {
        if (selectedDescLines == null) {
            rightTotalHeight = 0;
            return;
        }
        int scrollbarW = 3;
        int usableW = rightPageWidth - CONTENT_PADDING * 2 - scrollbarW - 2;
        int h = (int) Math.ceil(LINE_HEIGHT * TITLE_SCALE) + 4; // title
        if (isPaged()) h += LINE_HEIGHT + 2; // page-label
        for (Text line : selectedDescLines) {
            List<OrderedText> wrapped = textRenderer != null
                    ? textRenderer.wrapLines(line, usableW)
                    : List.of(line.asOrderedText());
            h += Math.max(1, wrapped.size()) * LINE_HEIGHT;
        }
        rightTotalHeight = h + CONTENT_PADDING * 2;
    }

    private void autoSelectPlayerRole() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (!GameStatus.State(player.getWorld())) return;

            GameWorldComponent gwc = GameWorldComponent.KEY.get(player.getWorld());
            if (gwc == null) return;
            Role role = gwc.getRole(player);
            if (role == null || role.identifier() == null) return;

            String roleId = role.identifier().toString();
            if (activeTab != Tab.ROLES) {
                activeTab = Tab.ROLES;
                if (rolesEntries == null) rolesEntries = GuidebookEntryBuilder.roles().build();
                recalcLeftHeight();
            }
            for (GuidebookEntry entry : currentEntries()) {
                if (roleId.equals(entry.id())) {
                    selectEntry(entry);
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private GuidebookEntry entryAt(int mouseX, int mouseY) {
        if (mouseX < leftPageX || mouseX > leftPageX + leftPageWidth) return null;
        if (mouseY < leftPageY || mouseY > leftPageY + leftPageHeight) return null;
        int usableW = leftPageWidth - CONTENT_PADDING * 2 - 3 - 2;
        int y = leftPageY + CONTENT_PADDING - (int) leftScrollSmooth;
        for (GuidebookEntry entry : currentEntries()) {
            if (entry.text().getString().isEmpty()) {
                y += LINE_HEIGHT / 2;
                continue;
            }
            if (entry.isHeader()) {
                y += LINE_HEIGHT + 3;
                continue;
            }
            int blockH = textRenderer.wrapLines(entry.text(), usableW).size() * LINE_HEIGHT;
            if (mouseY >= y && mouseY < y + blockH) return entry;
            y += blockH;
        }
        return null;
    }

    private void scrollLeft(int amount) {
        int max = Math.max(0, leftTotalHeight - leftPageHeight);
        leftScrollTarget = clamp(leftScrollTarget + amount, 0, max);
    }

    private void scrollRight(int amount) {
        int sah = rightPageHeight - (isPaged() ? PAGE_BTN_H + PAGE_BTN_MARGIN * 2 : 0);
        int max = Math.max(0, rightTotalHeight - sah);
        rightScrollTarget = clamp(rightScrollTarget + amount, 0, max);
    }

    private boolean isOnLeftScrollbar(double mx, double my) {
        int sx = leftPageX + leftPageWidth - 4;
        return mx >= sx && mx <= sx + 3 && my >= leftPageY && my <= leftPageY + leftPageHeight;
    }

    private boolean isOnRightScrollbar(double mx, double my) {
        int sx = rightPageX + rightPageWidth - 4;
        int scrollAreaH = rightPageHeight - (isPaged() ? PAGE_BTN_H + PAGE_BTN_MARGIN * 2 : 0);
        return mx >= sx && mx <= sx + 3 && my >= rightPageY && my <= rightPageY + scrollAreaH;
    }

    private int navPrevX() {
        return rightPageX + CONTENT_PADDING;
    }

    private int navNextX() {
        return rightPageX + rightPageWidth - CONTENT_PADDING - PAGE_BTN_W;
    }

    private int navBtnY(int saH) {
        return rightPageY + saH + PAGE_BTN_MARGIN;
    }

    private void playSound(net.minecraft.sound.SoundEvent sound) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) client.player.playSound(sound, 1f, 1f);
    }

    private enum Tab {
        ROLES(Text.translatable("gui.watheextended.guidebook.tab.roles")),
        MODIFIERS(Text.translatable("gui.watheextended.guidebook.tab.modifiers")),
        ITEMS(Text.translatable("gui.watheextended.guidebook.tab.items"));

        final Text label;

        Tab(Text label) {
            this.label = label;
        }
    }
}

