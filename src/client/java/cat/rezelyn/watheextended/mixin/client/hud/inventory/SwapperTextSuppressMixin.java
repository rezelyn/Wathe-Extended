package cat.rezelyn.watheextended.mixin.client.hud.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class SwapperTextSuppressMixin {

    @Inject(method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", at = @At("HEAD"), cancellable = true)
    private void watheextended$suppressSwapperHintText(TextRenderer tr, Text text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
        if (text.getContent() instanceof TranslatableTextContent content && content.getKey().equals("hud.swapper.first_player_selection")) {
            cir.setReturnValue(0);
        }
        if (text.getContent() instanceof TranslatableTextContent content && content.getKey().equals("hud.swapper.second_player_selection")) {
            cir.setReturnValue(0);
        }
    }
}
