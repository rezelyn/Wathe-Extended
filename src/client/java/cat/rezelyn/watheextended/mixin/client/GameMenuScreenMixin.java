package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.client.widget.IconButtonWidget;
import cat.rezelyn.watheextended.client.screen.WatheOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    private static final Identifier watheIcon = Identifier.of("watheextended", "textures/gui/sprites/hud/wathe.png");

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void watheExtended$buttonsLayout(CallbackInfo ci) {
        List<ButtonWidget> buttons = new ArrayList<>();
        for (Element element : this.children()) {
            if (element instanceof ButtonWidget button) {
                buttons.add(button);
            }
        }

        ButtonWidget options = null;
        boolean isOperator = watheExtended$isOperator();

        for (ButtonWidget button : buttons) {
            String key = watheExtended$getTranslationKey(button.getMessage());
            if ("menu.options".equals(key)) {
                options = button;
                break;
            }
        }

        // Add Wathe Extended button anchored to the Options button if the player is OP
        if (isOperator && options != null) {
            ButtonWidget watheButton = new IconButtonWidget(0, 0, 20, 20, this::watheExtended$onPress, watheIcon, 16, 16, 16);
            watheButton.setTooltip(Tooltip.of(Text.translatable("gui.watheextended.config.title")));
            this.addDrawableChild(watheButton);

            int watheX = options.getX() - (20 + 4);
            int yPos = options.getY();

            watheButton.setPosition(watheX, yPos);
        }
    }

    private static boolean watheExtended$isOperator() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return false;
        }
        return client.player.hasPermissionLevel(2);
    }

    private void watheExtended$onPress(ButtonWidget b) {
        MinecraftClient.getInstance().setScreen(
                WatheOptionsScreen.create(this));
    }

    private static String watheExtended$getTranslationKey(Text text) {
        if (text.getContent() instanceof TranslatableTextContent translatable) {
            return translatable.getKey();
        }
        return null;
    }
}
