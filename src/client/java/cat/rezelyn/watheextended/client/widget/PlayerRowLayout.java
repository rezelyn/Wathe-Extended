package cat.rezelyn.watheextended.client.widget;

import net.minecraft.client.gui.widget.ClickableWidget;

/** Shared geometry for the player picker rows and their pagination controls. */
public final class PlayerRowLayout {

  public static final int MAX_ROW_SIZE = 12;

  private static final int WIDGET_WIDTH = 18;
  private static final int WIDGET_HEIGHT = 18;
  private static final int COLUMN_STEP = 36;
  private static final int ROW_GROUP_GAP = 28;
  private static final int HORIZONTAL_SCREEN_MARGIN = 36;
  private static final int SCREEN_EDGE_MARGIN = 4;
  private static final int CONTROL_GAP = 10;
  private static final int ARROW_HEIGHT = 10;

  private PlayerRowLayout() {}

  public static int pageCapacity(int screenWidth) {
    return Math.clamp((screenWidth - HORIZONTAL_SCREEN_MARGIN) / COLUMN_STEP, 1, MAX_ROW_SIZE);
  }

  public static int firstRowY(
      int screenHeight, int preferredY, boolean reserveControlSpace, int extraBottomSpace) {
    int reservedControlHeight = reserveControlSpace ? CONTROL_GAP + ARROW_HEIGHT : 0;
    int maxFirstY =
        screenHeight
            - SCREEN_EDGE_MARGIN
            - reservedControlHeight
            - WIDGET_HEIGHT
            - Math.max(0, extraBottomSpace);
    return Math.max(SCREEN_EDGE_MARGIN, Math.min(preferredY, maxFirstY));
  }

  public static int rowGroupHeight(int entryCount, boolean reserveControlSpace) {
    if (entryCount <= 0) return 0;
    int controlsHeight = reserveControlSpace ? CONTROL_GAP + ARROW_HEIGHT : 0;
    return WIDGET_HEIGHT + controlsHeight;
  }

  public static int secondaryRowGap() {
    return ROW_GROUP_GAP;
  }

  public static int secondaryRowY(int primaryBottom, int fallbackY) {
    return primaryBottom < 0 ? fallbackY : primaryBottom + secondaryRowGap();
  }

  public static int controlsY(int rowY) {
    return rowY + WIDGET_HEIGHT + CONTROL_GAP;
  }

  public static void placeInRow(
      ClickableWidget widget, int index, int count, int screenWidth, int y) {
    int rowX = screenWidth / 2 - count * COLUMN_STEP / 2 + (COLUMN_STEP - WIDGET_WIDTH) / 2;
    widget.setX(rowX + index * COLUMN_STEP);
    widget.setY(y);
  }
}
