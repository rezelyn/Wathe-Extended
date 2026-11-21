package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class GuidebookItemIcon {
  public static final char MARKER = '\uFFFC';
  private static final Identifier FONT = Identifier.of("watheextended", "guidebook_item");
  private static final int SIZE = 12;

  private GuidebookItemIcon() {}

  // A fixed-width spacer keeps the model attached to its position through text wrapping and bidi.
  public static Text text(ItemStack stack) {
    String id = Registries.ITEM.getId(stack.getItem()).toString();
    if (ScreenUtils.hasIcon(id)) return ScreenUtils.icon(id);
    HoverEvent item =
        new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack));
    return Text.literal(String.valueOf(MARKER)).styled(style -> style.withFont(FONT).withHoverEvent(item));
  }

  public static void draw(DrawContext context, TextRenderer renderer, OrderedText line, int x, int y) {
    float[] offset = {0};
    line.accept((index, style, codePoint) -> {
      if (FONT.equals(style.getFont()) && style.getHoverEvent() != null) {
        var item = style.getHoverEvent().getValue(HoverEvent.Action.SHOW_ITEM);
        if (item != null) {
          var matrices = context.getMatrices();
          matrices.push();
          matrices.translate(x + offset[0], y - 2, 0);
          matrices.scale(SIZE / 16f, SIZE / 16f, 1);
          context.drawItem(item.asStack(), 0, 0);
          matrices.pop();
        }
      }
      offset[0] += renderer.getTextHandler().getWidth(OrderedText.styled(codePoint, style));
      return true;
    });
  }
}
