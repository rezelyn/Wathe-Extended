package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.api.AssignedRole;
import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.client.mixin.hud.DrawContextAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Draws the shared player slot sprite with the tint used by its row. */
public final class PlayerSlotRenderer {

  private static final Identifier SLOT =
      Identifier.of("watheextended", "textures/gui/sprites/shop_slot_greyscale.png");
  private static final String GUESSER_MODIFIER = "noellesroles:guesser";
  private static final int UNKNOWN_COLOR = Integer.MIN_VALUE;

  private static int cachedGuesserColor = UNKNOWN_COLOR;

  private PlayerSlotRenderer() {}

  public static void drawRoleSlot(DrawContext context, int x, int y, int width, int height) {
    var player = MinecraftClient.getInstance().player;
    var role = AssignedRole.getRole(player);
    draw(context, x, y, width, height, role == null ? 0xFFFFFF : role.color());
  }

  public static void drawGuesserSlot(DrawContext context, int x, int y, int width, int height) {
    draw(context, x, y, width, height, guesserColor());
  }

  private static void draw(DrawContext context, int x, int y, int width, int height, int color) {
    float red = ((color >> 16) & 0xFF) / 255.0F;
    float green = ((color >> 8) & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    ((DrawContextAccessor) (Object) context)
        .watheextended$drawTintedQuad(
            SLOT, x, x + width, y, y + height, 0, 0.0F, 1.0F, 0.0F, 1.0F, red, green, blue, 1.0F);
  }

  public static int guesserColor() {
    if (cachedGuesserColor == UNKNOWN_COLOR) {
      var display = ModifiersDisplay.get().get(GUESSER_MODIFIER);
      cachedGuesserColor = display == null ? 0xFFFFFF : display.color();
    }
    return cachedGuesserColor;
  }
}
