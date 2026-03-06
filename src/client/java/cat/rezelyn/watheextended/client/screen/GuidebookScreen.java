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
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class GuidebookScreen extends Screen {

    // layout
    static final int BOOK_WIDTH = 380;
    static final int BOOK_HEIGHT = 240;
    static final int LINE_HEIGHT = 12;
    static final int CONTENT_PAD = 7;
    private static final int PAGE_MARGIN_X = 18;
    private static final int PAGE_MARGIN_TOP = 20;
    private static final int PAGE_MARGIN_BOTTOM = 16;
    private static final int SPINE_WIDTH = 9;
    private static final int TAB_WIDTH = 76;
    private static final int TAB_HEIGHT = 20;
    private static final int TAB_GAP = 3;
    private static final int CLOSE_BTN_WIDTH = 76;
    private static final int CLOSE_BTN_HEIGHT = 16;

    // nav-bar
    private static final int PAGE_COUNT = GuidebookPageContent.PAGE_LABELS.length;
    private static final int NAV_BTN_W = 52;
    private static final int NAV_BTN_H = 14;
    private static final int NAV_BTN_MARGIN = 5;

    // rendering
    private static final float TITLE_SCALE = 1.6f;
    private static final int TITLE_EXTRA_H = 4; // gap after title block

    private static final int COLOR_SELECTED_BG = 0x33000000;
    private static final int COLOR_HOVER_BG    = 0x1A000000;
    private static final int COLOR_RIGHT_TEXT  = 0xFF3B2A1A;
    private static final int COLOR_HINT        = 0xFF9B8B6B;

    // nav-button sprites
    private static final Identifier NAV_PREV          = Identifier.of("watheextended", "textures/gui/guidebook/sprites/previous.png");
    private static final Identifier NAV_PREV_DISABLED = Identifier.of("watheextended", "textures/gui/guidebook/sprites/previous_disabled.png");
    private static final Identifier NAV_PREV_HOVERED  = Identifier.of("watheextended", "textures/gui/guidebook/sprites/previous_hovered.png");
    private static final Identifier NAV_NEXT          = Identifier.of("watheextended", "textures/gui/guidebook/sprites/next.png");
    private static final Identifier NAV_NEXT_DISABLED = Identifier.of("watheextended", "textures/gui/guidebook/sprites/next_disabled.png");
    private static final Identifier NAV_NEXT_HOVERED  = Identifier.of("watheextended", "textures/gui/guidebook/sprites/next_hovered.png");

    private static final Identifier BOOK_TEXTURE =
            Identifier.of("watheextended", "textures/gui/guidebook/book.png");

    // regions
    private int bookX, bookY;
    private int leftPageX, leftPageY, leftPageW, leftPageH;
    private int rightPageX, rightPageY, rightPageW, rightPageH;

    // scroll
    private int leftScrollTarget = 0;
    private float leftScrollSmooth = 0f;
    private int leftContentHeight = 0;

    private int rightScrollTarget = 0;
    private float rightScrollSmooth = 0f;
    private int rightContentHeight = 0;

    private boolean isDraggingLeft = false;
    private boolean isDraggingRight = false;
    private int dragAnchorY = 0;
    private int dragAnchorScrollLeft = 0;
    private int dragAnchorScrollRight = 0;

    // tracked mouse position (updated each render frame for hover checks)
    private int lastMouseX = 0;
    private int lastMouseY = 0;

    // tabs and entries
    private Tab activeTab = Tab.ROLES;
    private boolean firstOpen = true;

    private List<GuidebookEntry> rolesEntries = null;
    private List<GuidebookEntry> modifierEntries = null;

    private String selectedId = null;
    private Text selectedTitle = null;
    private int selectedColor = 0xFF3B2A1A;
    private String selectedDescKey = null;
    private String selectedEntryId = null;
    private boolean selectedKillerSided = false;

    private int currentPage = 0;
    private List<Text> rightPageLines = null;
    private boolean rightPageNoContent = false;

    public GuidebookScreen() {
        super(Text.translatable("gui.watheextended.guidebook.title"));
    }

    private static boolean isInsidePage(int mx, int my, int px, int py, int pw, int ph) {
        return mx >= px && mx <= px + pw && my >= py && my <= py + ph;
    }

    private static boolean isYVisible(int y, int regionY, int regionH) {
        return y >= regionY && y <= regionY + regionH;
    }

    private static boolean isBlockVisible(int y, int blockH, int regionY, int regionH) {
        return y + blockH >= regionY && y <= regionY + regionH;
    }

    private static int opaque(int color) {
        return 0xFF000000 | (color & 0x00FFFFFF);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    protected void init() {
        super.init();
        computeLayout();
        addTabButtons();
        addCloseButton();
        refreshEntries();

        if (firstOpen) {
            firstOpen = false;
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

    private void computeLayout() {
        bookX = (width - BOOK_WIDTH) / 2;
        bookY = (height - BOOK_HEIGHT) / 2;

        int halfBook = BOOK_WIDTH / 2;
        int spineHalf = SPINE_WIDTH / 2;

        leftPageX = bookX + PAGE_MARGIN_X;
        leftPageY = bookY + PAGE_MARGIN_TOP;
        leftPageW = halfBook - PAGE_MARGIN_X - spineHalf - 4;
        leftPageH = BOOK_HEIGHT - PAGE_MARGIN_TOP - PAGE_MARGIN_BOTTOM;

        rightPageX = bookX + halfBook + spineHalf + 4;
        rightPageY = bookY + PAGE_MARGIN_TOP;
        rightPageW = halfBook - PAGE_MARGIN_X - spineHalf - 4;
        rightPageH = BOOK_HEIGHT - PAGE_MARGIN_TOP - PAGE_MARGIN_BOTTOM;
    }

    private void addTabButtons() {
        Tab[] tabs = Tab.values();
        int totalW = tabs.length * TAB_WIDTH + (tabs.length - 1) * TAB_GAP;
        int startX = bookX + (BOOK_WIDTH - totalW) / 2;
        int tabY = bookY - 22;

        clearChildren();
        for (int i = 0; i < tabs.length; i++) {
            final Tab tab = tabs[i];
            int tx = startX + i * (TAB_WIDTH + TAB_GAP);
            addDrawableChild(
                    ButtonWidget.builder(tab.label, btn -> selectTab(tab))
                            .dimensions(tx, tabY, TAB_WIDTH, TAB_HEIGHT)
                            .build()
            );
        }
    }

    private void addCloseButton() {
        int x = bookX + (BOOK_WIDTH - CLOSE_BTN_WIDTH) / 2;
        int y = bookY + BOOK_HEIGHT + 4;
        addDrawableChild(
                ButtonWidget.builder(
                        Text.translatable("gui.watheextended.guidebook.button.close"),
                        btn -> close()
                ).dimensions(x, y, CLOSE_BTN_WIDTH, CLOSE_BTN_HEIGHT).build()
        );
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        updateScrollSmooth(delta);

        context.fill(0, 0, width, height, 0xB0000000);
        context.drawTexture(BOOK_TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);
        renderLeftPage(context, mouseX, mouseY);
        renderRightPage(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void updateScrollSmooth(float delta) {
        float speed = 1f - (float) Math.pow(0.08, delta);
        leftScrollSmooth += (leftScrollTarget - leftScrollSmooth) * speed;
        rightScrollSmooth += (rightScrollTarget - rightScrollSmooth) * speed;
    }

    // left page
    private void renderLeftPage(DrawContext context, int mouseX, int mouseY) {
        int usableW = leftPageW - CONTENT_PAD * 2;
        context.enableScissor(leftPageX, leftPageY, leftPageX + leftPageW, leftPageY + leftPageH);

        int y = leftPageY + CONTENT_PAD - (int) leftScrollSmooth;

        for (GuidebookEntry entry : currentEntries()) {
            if (entry.text().getString().isEmpty()) {
                y += LINE_HEIGHT / 2;
                continue;
            }

            if (entry.isHeader()) {
                if (isYVisible(y, leftPageY, leftPageH)) {
                    context.drawText(textRenderer, entry.text(),
                            leftPageX + CONTENT_PAD, y, opaque(entry.color()), true);
                }
                y += LINE_HEIGHT + 3;
                continue;
            }

            List<OrderedText> wrapped = textRenderer.wrapLines(entry.text(), usableW);
            // highlight box fits the actual font height (fontHeight = 9) + 2px padding each side
            int fontH  = textRenderer.fontHeight;
            int rowH   = fontH + 4;
            int blockH = wrapped.size() * rowH;

            if (isBlockVisible(y, blockH, leftPageY, leftPageH)) {
                boolean isSelected = entry.id() != null && entry.id().equals(selectedId);
                boolean isHovered  = entry.id() != null && isMouseOverEntry(mouseX, mouseY, y, blockH);

                if (isSelected)     context.fill(leftPageX, y, leftPageX + leftPageW, y + blockH, COLOR_SELECTED_BG);
                else if (isHovered) context.fill(leftPageX, y, leftPageX + leftPageW, y + blockH, COLOR_HOVER_BG);

                int color   = opaque(entry.color());
                int offsetY = (rowH - fontH) / 2; // 2px — centers text vertically in the box
                int lineY   = y + offsetY;
                for (OrderedText line : wrapped) {
                    context.drawText(textRenderer, line, leftPageX + CONTENT_PAD, lineY, color, false);
                    lineY += rowH;
                }
            }
            y += blockH;
        }

        context.disableScissor();
    }

    private boolean isMouseOverEntry(int mouseX, int mouseY, int entryY, int blockH) {
        return mouseX >= leftPageX && mouseX <= leftPageX + leftPageW
                && mouseY >= entryY && mouseY < entryY + blockH;
    }

    // right page
    private boolean isPaged() {
        return activeTab == Tab.ROLES;
    }

    private int navBarHeight() {
        return isPaged() ? NAV_BTN_H + NAV_BTN_MARGIN * 2 : 0;
    }

    private void renderRightPage(DrawContext context) {
        if (selectedId == null) {
            renderNoSelectionHint(context);
            return;
        }

        int scrollAreaH = rightPageH - navBarHeight();
        renderRightContent(context, scrollAreaH);
        if (isPaged()) renderPageNavBar(context, scrollAreaH);
    }

    private void renderNoSelectionHint(DrawContext context) {
        Text hint = Text.translatable("gui.watheextended.guidebook.right_page.hint.select");
        int x = rightPageX + (rightPageW - textRenderer.getWidth(hint)) / 2;
        int y = rightPageY + rightPageH / 2 - LINE_HEIGHT / 2;
        context.drawText(textRenderer, hint.copy().styled(s -> s.withItalic(true)), x, y, COLOR_HINT, false);
    }

    private void renderRightContent(DrawContext context, int scrollAreaH) {
        int usableW = rightPageW - CONTENT_PAD * 2;
        context.enableScissor(rightPageX, rightPageY, rightPageX + rightPageW, rightPageY + scrollAreaH);

        int y = rightPageY + CONTENT_PAD - (int) rightScrollSmooth;
        y = renderRightTitle(context, y, usableW, scrollAreaH);
        y = renderPageSubtitle(context, y, usableW, scrollAreaH);
        renderRightLines(context, y, usableW, scrollAreaH);

        context.disableScissor();
    }

    private int renderRightTitle(DrawContext context, int y, int usableW, int scrollAreaH) {
        int titleColor = opaque(selectedColor);
        int scaledTitleW = (int) (usableW / TITLE_SCALE);
        int scaledLineH = (int) Math.ceil(LINE_HEIGHT * TITLE_SCALE);

        for (OrderedText line : textRenderer.wrapLines(selectedTitle.copy().styled(s -> s.withBold(true)), scaledTitleW)) {
            if (isYVisible(y, rightPageY, scrollAreaH)) {
                int lineW = (int) (textRenderer.getWidth(line) * TITLE_SCALE);
                int centeredX = rightPageX + CONTENT_PAD + (usableW - lineW) / 2;
                context.getMatrices().push();
                context.getMatrices().translate(centeredX, y, 0);
                context.getMatrices().scale(TITLE_SCALE, TITLE_SCALE, 1f);
                context.drawText(textRenderer, line, 0, 0, titleColor, true);
                context.getMatrices().pop();
            }
            y += scaledLineH;
        }
        return y + TITLE_EXTRA_H;
    }

    private int renderPageSubtitle(DrawContext context, int y, int usableW, int scrollAreaH) {
        if (!isPaged()) return y;

        Text label = Text.translatable(
                "gui.watheextended.guidebook.right_page.roles.subtitle.separator",
                Text.translatable(GuidebookPageContent.PAGE_LABELS[currentPage])
        );
        int x = rightPageX + CONTENT_PAD + (usableW - textRenderer.getWidth(label)) / 2;
        if (isYVisible(y, rightPageY, scrollAreaH)) {
            context.drawText(textRenderer, label.copy().styled(s -> s.withItalic(true)), x, y, COLOR_HINT, false);
        }
        return y + LINE_HEIGHT + 2;
    }

    private void renderRightLines(DrawContext context, int y, int usableW, int scrollAreaH) {
        if (rightPageLines == null) return;

        if (rightPageNoContent) {
            Text msg = rightPageLines.getFirst();
            int x = rightPageX + (rightPageW - textRenderer.getWidth(msg)) / 2;
            int cy = rightPageY + scrollAreaH / 2 - LINE_HEIGHT / 2;
            context.drawText(textRenderer, msg.copy().styled(s -> s.withItalic(true)), x, cy, COLOR_HINT, false);
            return;
        }

        for (Text line : rightPageLines) {
            if (line.getString().isEmpty()) {
                y += LINE_HEIGHT / 2;
                continue;
            }
            for (OrderedText wrapped : textRenderer.wrapLines(line, usableW)) {
                if (isYVisible(y, rightPageY, scrollAreaH)) {
                    context.drawText(textRenderer, wrapped, rightPageX + CONTENT_PAD, y, COLOR_RIGHT_TEXT, false);
                }
                y += LINE_HEIGHT;
            }
        }
    }

    // nav-bar
    private static final int NAV_SPRITE_W = 15;
    private static final int NAV_SPRITE_H = 10;

    private void renderPageNavBar(DrawContext context, int scrollAreaH) {
        int btnY = navBtnY(scrollAreaH);

        boolean prevActive  = currentPage > 0;
        boolean nextActive  = currentPage < PAGE_COUNT - 1;
        boolean prevHovered = prevActive && isInsideNavBtn(lastMouseX, lastMouseY, navPrevX(), btnY);
        boolean nextHovered = nextActive && isInsideNavBtn(lastMouseX, lastMouseY, navNextX(), btnY);

        Identifier prevTex = prevActive ? (prevHovered ? NAV_PREV_HOVERED : NAV_PREV) : NAV_PREV_DISABLED;
        Identifier nextTex = nextActive ? (nextHovered ? NAV_NEXT_HOVERED : NAV_NEXT) : NAV_NEXT_DISABLED;

        // Center the native-size sprite inside the hit-box
        int prevDrawX = navPrevX() + (NAV_BTN_W - NAV_SPRITE_W) / 2;
        int nextDrawX = navNextX() + (NAV_BTN_W - NAV_SPRITE_W) / 2;
        int drawY     = btnY       + (NAV_BTN_H - NAV_SPRITE_H) / 2;

        context.drawTexture(prevTex, prevDrawX, drawY, 0, 0, NAV_SPRITE_W, NAV_SPRITE_H, NAV_SPRITE_W, NAV_SPRITE_H);
        context.drawTexture(nextTex, nextDrawX, drawY, 0, 0, NAV_SPRITE_W, NAV_SPRITE_H, NAV_SPRITE_W, NAV_SPRITE_H);

        String indicator = (currentPage + 1) + " / " + PAGE_COUNT;
        int cx = rightPageX + (rightPageW - textRenderer.getWidth(indicator)) / 2;
        context.drawText(textRenderer, indicator, cx, btnY + (NAV_BTN_H - 8) / 2, COLOR_HINT, false);
    }

    private boolean isInsideNavBtn(int mx, int my, int btnX, int btnY) {
        return mx >= btnX && mx <= btnX + NAV_BTN_W
            && my >= btnY && my <= btnY + NAV_BTN_H;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (!isInsideBook(mouseX, mouseY)) return super.mouseClicked(mouseX, mouseY, button);
        if (selectedId != null && isPaged() && handleNavBarClick(mouseX, mouseY)) return true;

        startDragIfNeeded((int) mouseX, (int) mouseY);

        GuidebookEntry clicked = entryAt((int) mouseX, (int) mouseY);
        if (clicked != null && clicked.id() != null) {
            selectEntry(clicked);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleNavBarClick(double mouseX, double mouseY) {
        int scrollAreaH = rightPageH - navBarHeight();
        int btnY = navBtnY(scrollAreaH);
        if (mouseY < btnY || mouseY > btnY + NAV_BTN_H) return false;

        if (mouseX >= navPrevX() && mouseX <= navPrevX() + NAV_BTN_W && currentPage > 0) {
            changePage(currentPage - 1);
            return true;
        }
        if (mouseX >= navNextX() && mouseX <= navNextX() + NAV_BTN_W && currentPage < PAGE_COUNT - 1) {
            changePage(currentPage + 1);
            return true;
        }
        return false;
    }

    private void startDragIfNeeded(int mouseX, int mouseY) {
        boolean onLeft = isInsidePage(mouseX, mouseY, leftPageX, leftPageY, leftPageW, leftPageH);
        boolean onRight = isInsidePage(mouseX, mouseY, rightPageX, rightPageY, rightPageW, rightPageH);
        if (!onLeft && !onRight) return;

        isDraggingLeft = onLeft;
        isDraggingRight = onRight;
        dragAnchorY = mouseY;
        dragAnchorScrollLeft = leftScrollTarget;
        dragAnchorScrollRight = rightScrollTarget;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (button == 0 && (isDraggingLeft || isDraggingRight)) {
            int dragDelta = dragAnchorY - (int) mouseY; // drag up → scroll down
            if (isDraggingLeft) {
                int max = Math.max(0, leftContentHeight - leftPageH);
                leftScrollTarget = clamp(dragAnchorScrollLeft + dragDelta, 0, max);
            }
            if (isDraggingRight) {
                int scrollAreaH = rightPageH - navBarHeight();
                int max = Math.max(0, rightContentHeight - scrollAreaH);
                rightScrollTarget = clamp(dragAnchorScrollRight + dragDelta, 0, max);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingLeft = false;
            isDraggingRight = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isInsideBook(mouseX, mouseY)) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        int scrollDelta = (int) (-verticalAmount * 10);
        if (isInsidePage((int) mouseX, (int) mouseY, rightPageX, rightPageY, rightPageW, rightPageH)) {
            scrollRight(scrollDelta);
        } else {
            scrollLeft(scrollDelta);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return switch (keyCode) {
            case 256 -> {
                close();
                yield true;
            } // Escape
            case 264 -> {
                scrollLeft(10);
                yield true;
            } // Arrow Down
            case 265 -> {
                scrollLeft(-10);
                yield true;
            } // Arrow Up
            case 266 -> {
                scrollLeft(-leftPageH);
                yield true;
            } // Page Up
            case 267 -> {
                scrollLeft(leftPageH);
                yield true;
            } // Page Down
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    private void selectTab(Tab tab) {
        activeTab = tab;
        resetScrollBoth();
        clearSelection();
        refreshEntries();
    }

    private void selectEntry(GuidebookEntry entry) {
        playSound(WatheExtendedSounds.GUIDEBOOK_PAGE);
        selectedId = entry.id();
        selectedTitle = entry.displayTitle();
        selectedColor = entry.color();
        selectedDescKey = entry.descriptionKey();
        selectedEntryId = entry.id();
        selectedKillerSided = entry.killerSided();
        currentPage = 0;
        resetScrollRight();
        loadPageContent();
    }

    private void changePage(int page) {
        currentPage = page;
        resetScrollRight();
        loadPageContent();
        playSound(WatheExtendedSounds.GUIDEBOOK_PAGE);
    }

    private void clearSelection() {
        selectedId = null;
        selectedTitle = null;
        selectedDescKey = null;
        selectedEntryId = null;
        selectedKillerSided = false;
        currentPage = 0;
        rightPageLines = null;
        rightPageNoContent = false;
    }

    private void loadPageContent() {
        GuidebookPageContent.PageResult result =
                GuidebookPageContent.resolve(selectedDescKey, selectedEntryId, currentPage, selectedKillerSided);
        rightPageLines = result.lines();
        rightPageNoContent = result.noContent();
        recalcRightHeight();
    }

    private void refreshEntries() {
        switch (activeTab) {
            case ROLES -> {
                if (rolesEntries == null) rolesEntries = GuidebookEntryBuilder.roles().build();
            }
            case MODIFIERS -> {
                if (modifierEntries == null) modifierEntries = GuidebookEntryBuilder.modifiers().build();
            }
        }
        recalcLeftHeight();
    }

    private List<GuidebookEntry> currentEntries() {
        return switch (activeTab) {
            case ROLES -> rolesEntries != null ? rolesEntries : List.of();
            case MODIFIERS -> modifierEntries != null ? modifierEntries : List.of();
        };
    }

    private void recalcLeftHeight() {
        int h = CONTENT_PAD * 2;
        int usableW = leftPageW - CONTENT_PAD * 2;
        int fontH = textRenderer != null ? textRenderer.fontHeight : 9;
        int rowH  = fontH + 4;
        for (GuidebookEntry e : currentEntries()) {
            if (e.isHeader()) {
                h += LINE_HEIGHT + 3;
            } else if (e.text().getString().isEmpty()) {
                h += LINE_HEIGHT / 2;
            } else {
                List<OrderedText> wrapped = textRenderer != null
                        ? textRenderer.wrapLines(e.text(), usableW)
                        : List.of(e.text().asOrderedText());
                h += wrapped.size() * rowH;
            }
        }
        leftContentHeight = h;
    }

    private void recalcRightHeight() {
        if (rightPageLines == null) {
            rightContentHeight = 0;
            return;
        }
        int usableW = rightPageW - CONTENT_PAD * 2;
        int h = (int) Math.ceil(LINE_HEIGHT * TITLE_SCALE) + TITLE_EXTRA_H; // title block
        if (isPaged()) h += LINE_HEIGHT + 2; // page subtitle label
        for (Text line : rightPageLines) {
            List<OrderedText> wrapped = textRenderer != null
                    ? textRenderer.wrapLines(line, usableW)
                    : List.of(line.asOrderedText());
            h += Math.max(1, wrapped.size()) * LINE_HEIGHT;
        }
        rightContentHeight = h + CONTENT_PAD * 2;
    }

    // try to auto-select the player's role on open
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
        if (!isInsidePage(mouseX, mouseY, leftPageX, leftPageY, leftPageW, leftPageH)) return null;

        int usableW = leftPageW - CONTENT_PAD * 2;
        int y = leftPageY + CONTENT_PAD - (int) leftScrollSmooth;

        for (GuidebookEntry entry : currentEntries()) {
            if (entry.text().getString().isEmpty()) {
                y += LINE_HEIGHT / 2;
                continue;
            }
            if (entry.isHeader()) {
                y += LINE_HEIGHT + 3;
                continue;
            }
            int rowH   = textRenderer.fontHeight + 4;
            int blockH = textRenderer.wrapLines(entry.text(), usableW).size() * rowH;
            if (mouseY >= y && mouseY < y + blockH) return entry;
            y += blockH;
        }
        return null;
    }

    private boolean isInsideBook(double mouseX, double mouseY) {
        return mouseX >= bookX && mouseX <= bookX + BOOK_WIDTH
                && mouseY >= bookY && mouseY <= bookY + BOOK_HEIGHT;
    }

    private void scrollLeft(int amount) {
        int max = Math.max(0, leftContentHeight - leftPageH);
        leftScrollTarget = clamp(leftScrollTarget + amount, 0, max);
    }

    private void scrollRight(int amount) {
        int scrollAreaH = rightPageH - navBarHeight();
        int max = Math.max(0, rightContentHeight - scrollAreaH);
        rightScrollTarget = clamp(rightScrollTarget + amount, 0, max);
    }

    private void resetScrollBoth() {
        leftScrollTarget = 0;
        leftScrollSmooth = 0f;
        rightScrollTarget = 0;
        rightScrollSmooth = 0f;
    }

    private void resetScrollRight() {
        rightScrollTarget = 0;
        rightScrollSmooth = 0f;
    }

    private int navPrevX() {
        return rightPageX + CONTENT_PAD;
    }

    private int navNextX() {
        return rightPageX + rightPageW - CONTENT_PAD - NAV_BTN_W;
    }

    private int navBtnY(int scrollAreaH) {
        return rightPageY + scrollAreaH + NAV_BTN_MARGIN;
    }

    private void playSound(SoundEvent sound) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) client.player.playSound(sound, 1f, 1f);
    }

    private enum Tab {
        ROLES(Text.translatable("gui.watheextended.guidebook.tab.roles")),
        MODIFIERS(Text.translatable("gui.watheextended.guidebook.tab.modifiers"));

        final Text label;

        Tab(Text label) {
            this.label = label;
        }
    }
}
