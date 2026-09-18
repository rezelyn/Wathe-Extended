package cat.rezelyn.watheextended.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class PresetSaveScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget nameField;
    private TextFieldWidget descriptionField;
    private boolean nameError;

    public PresetSaveScreen(Screen parent) {
        super(Text.translatable("gui.watheextended.config.category.presets.save.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, center - 150, 70, 300, 20, Text.translatable("gui.watheextended.config.category.presets.name"));
        this.nameField.setMaxLength(64);
        this.nameField.setPlaceholder(Text.translatable("gui.watheextended.config.category.presets.name.placeholder"));
        this.addDrawableChild(this.nameField);
        this.descriptionField = new TextFieldWidget(this.textRenderer, center - 150, 120, 300, 20, Text.translatable("gui.watheextended.config.category.presets.description"));
        this.descriptionField.setMaxLength(256);
        this.descriptionField.setPlaceholder(Text.translatable("gui.watheextended.config.category.presets.description.placeholder"));
        this.addDrawableChild(this.descriptionField);
        this.addDrawableChild(ButtonWidget.builder(ScreenUtils.icon("save"), button -> save()).dimensions(center - 155, 165, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), button -> close()).dimensions(center + 5, 165, 150, 20).build());
        this.setInitialFocus(this.nameField);
    }

    private void save() {
        String name = this.nameField.getText().trim();
        if (name.isEmpty()) {
            this.nameError = true;
            return;
        }
        this.nameError = false;
        ConfigScreen.requestPresetSave(name, this.descriptionField.getText());
        close();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xF0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 35, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("gui.watheextended.config.category.presets.name"), this.width / 2 - 150, 57, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("gui.watheextended.config.category.presets.description"), this.width / 2 - 150, 107, 0xFFFFFF);
        if (this.nameError) {
            context.drawTextWithShadow(this.textRenderer, Text.translatable("gui.watheextended.config.category.presets.name.required"), this.width / 2 - 150, 92, 0xFF5555);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

}
