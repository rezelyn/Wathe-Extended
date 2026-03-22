package cat.rezelyn.watheextended.client.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class IconButtonWidget extends ButtonWidget {
    private final Identifier icon;
    private final int iconSize;
    private final int textureWidth;
    private final int textureHeight;

    public IconButtonWidget(int x, int y, int width, int height, PressAction onPress, Identifier icon, int iconSize, int textureWidth, int textureHeight) {
        super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.icon = icon;
        this.iconSize = iconSize;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        int iconX = getX() + (getWidth() - iconSize) / 2;
        int iconY = getY() + (getHeight() - iconSize) / 2;
        context.drawTexture(icon, iconX, iconY, 0, 0, iconSize, iconSize, textureWidth, textureHeight);
    }
}
