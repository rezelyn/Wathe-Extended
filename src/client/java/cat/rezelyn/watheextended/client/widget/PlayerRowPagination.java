package cat.rezelyn.watheextended.client.widget;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/** Paging state and textured controls for inventory player-picker rows */
public final class PlayerRowPagination {

  /** Page controls (arrows + label) */
  private static final int ARROW_WIDTH = 8;
  private static final int ARROW_HEIGHT = 10;
  private static final int LABEL_WIDTH = 44;
  private static final int GAP = 10;
  private static final int WIDTH = ARROW_WIDTH * 2 + LABEL_WIDTH + 16;
  private static final int LEFT_OFFSET = -WIDTH / 2;
  private static final int RIGHT_OFFSET = WIDTH / 2 - ARROW_WIDTH;

  private static final ArrowTextures LEFT = ArrowTextures.of("left");
  private static final ArrowTextures RIGHT = ArrowTextures.of("right");

  private static final Map<ClickableWidget, PageState> PAGE_STATES = new WeakHashMap<>();

  private final PageArrowButton previousButton = new PageArrowButton(this, false);
  private final PageArrowButton nextButton = new PageArrowButton(this, true);

  private int page;
  private int entryCount;
  private int pageEntryLimit = PlayerRowLayout.MAX_ROW_SIZE;
  private int controlsY;
  private int labelX;
  private int labelColor = 0xFFFFFFFF;

  private boolean controlsRequested;

  public ClickableWidget previousButton() {
    return previousButton;
  }

  public ClickableWidget nextButton() {
    return nextButton;
  }

  public int getPage() {
    return page;
  }

  public int getPageCount() {
    return Math.max(1, Math.ceilDiv(entryCount, pageEntryLimit));
  }

  public boolean hasMultiplePages() {
    return entryCount > pageEntryLimit;
  }

  public void setEntryCount(int entryCount, int screenWidth) {
    this.entryCount = Math.max(0, entryCount);
    pageEntryLimit = PlayerRowLayout.pageCapacity(screenWidth);
    page = MathHelper.clamp(page, 0, getPageCount() - 1);
    updateButtons();
  }

  public void setControlsVisible(boolean visible) {
    controlsRequested = visible;
    updateButtons();
  }

  public void setLabelColor(int color) {
    labelColor = 0xFF000000 | color;
  }

  /** Places this page's entries in a single centered row. */
  public void arrangeAt(List<? extends ClickableWidget> widgets, int screenWidth, int firstY) {
    int first = page * pageEntryLimit;
    int end = Math.min(widgets.size(), first + pageEntryLimit);

    for (int i = 0; i < widgets.size(); i++) {
      ClickableWidget widget = widgets.get(i);
      boolean onCurrentPage = i >= first && i < end;
      syncVisibility(widget, onCurrentPage);
      if (onCurrentPage) {
        PlayerRowLayout.placeInRow(widget, i - first, end - first, screenWidth, firstY);
      }
    }
  }

  public void positionControls(int screenWidth, int firstY) {
    int centerX = screenWidth / 2;
    controlsY = PlayerRowLayout.controlsY(firstY);
    previousButton.setPosition(centerX + LEFT_OFFSET, controlsY);
    nextButton.setPosition(centerX + RIGHT_OFFSET, controlsY);
    labelX = centerX;
  }

  public void renderPageLabel(DrawContext context) {
    if (!controlsVisible() || !previousButton.visible || !nextButton.visible) return;
    var textRenderer = MinecraftClient.getInstance().textRenderer;
    context.drawCenteredTextWithShadow(
        textRenderer,
        Text.translatable("gui.watheextended.inventory.picker.page", page + 1, getPageCount()),
        labelX,
        controlsY + (ARROW_HEIGHT - textRenderer.fontHeight) / 2,
        labelColor);
  }

  public static void setPageVisibility(ClickableWidget widget, boolean visible) {
    PageState state = PAGE_STATES.get(widget);
    widget.visible = visible && (state == null || state.shouldBeVisible());
  }

  public static void resetWidgetVisibility(ClickableWidget widget, boolean visible) {
    PAGE_STATES.remove(widget);
    widget.visible = visible;
  }

  public static int pageEligibleCount(List<? extends ClickableWidget> widgets) {
    int count = 0;
    for (ClickableWidget widget : widgets) {
      PageState state = PAGE_STATES.get(widget);
      if (state == null ? widget.visible : state.visibleBeforePaging) count++;
    }
    return count;
  }

  public static boolean isPageButton(ClickableWidget widget) {
    return widget instanceof PageArrowButton;
  }

  public static boolean belongsTo(ClickableWidget widget, PlayerRowPagination pagination) {
    return widget instanceof PageArrowButton button && button.owner == pagination;
  }

  /**
   * Controls follow row visibility (inactive arrows remain visible with their disabled textures)
   */
  private boolean controlsVisible() {
    return controlsRequested;
  }

  private void updateButtons() {
    boolean visible = controlsVisible();
    previousButton.visible = visible;
    nextButton.visible = visible;
    previousButton.active = visible && page > 0;
    nextButton.active = visible && page < getPageCount() - 1;
  }

  private void turnPage(int delta) {
    if (!controlsVisible()) return;
    page = MathHelper.clamp(page + delta, 0, getPageCount() - 1);
    updateButtons();
  }

  /** Hides/restores a widget only when its on-page status actually changes */
  private static void syncVisibility(ClickableWidget widget, boolean onCurrentPage) {
    PageState state = PAGE_STATES.computeIfAbsent(widget, w -> new PageState(w.visible));
    if (state.onCurrentPage == onCurrentPage) return;
    state.onCurrentPage = onCurrentPage;
    widget.visible = state.shouldBeVisible();
  }

  private static final class PageState {
    final boolean visibleBeforePaging;
    boolean onCurrentPage = true;

    PageState(boolean visibleBeforePaging) {
      this.visibleBeforePaging = visibleBeforePaging;
    }

    boolean shouldBeVisible() {
      return onCurrentPage && visibleBeforePaging;
    }
  }

  private record ArrowTextures(Identifier normal, Identifier hover, Identifier disabled) {
    static ArrowTextures of(String side) {
      String base = "textures/gui/sprites/" + side + "_arrow";
      return new ArrowTextures(
          Identifier.of("watheextended", base + ".png"),
          Identifier.of("watheextended", base + "_hover.png"),
          Identifier.of("watheextended", base + "_disabled.png"));
    }

    Identifier pick(boolean active, boolean hovered) {
      return !active ? disabled : hovered ? hover : normal;
    }
  }

  private static final class PageArrowButton extends ClickableWidget {

    private final PlayerRowPagination owner;
    private final boolean next;
    private final ArrowTextures textures;

    private PageArrowButton(PlayerRowPagination owner, boolean next) {
      super(0, 0, ARROW_WIDTH, ARROW_HEIGHT, Text.empty());
      this.owner = owner;
      this.next = next;
      this.textures = next ? RIGHT : LEFT;
      visible = false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
      context.drawTexture(
          textures.pick(active, isHovered()),
          getX(),
          getY(),
          0,
          0,
          ARROW_WIDTH,
          ARROW_HEIGHT,
          ARROW_WIDTH,
          ARROW_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0 || !visible || !active || !isMouseOver(mouseX, mouseY)) return false;
      setFocused(true);
      owner.turnPage(next ? 1 : -1);
      MinecraftClient.getInstance()
          .getSoundManager()
          .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
      return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (!active || !isFocused() || keyCode != (next ? GLFW.GLFW_KEY_RIGHT : GLFW.GLFW_KEY_LEFT)) {
        return false;
      }
      owner.turnPage(next ? 1 : -1);
      return true;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {}
  }
}
