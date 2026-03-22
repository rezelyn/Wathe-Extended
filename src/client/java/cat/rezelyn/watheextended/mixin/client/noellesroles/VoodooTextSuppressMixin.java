package cat.rezelyn.watheextended.mixin.client.noellesroles;

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
public class VoodooTextSuppressMixin {

    @Inject(method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", at = @At("HEAD"), cancellable = true)
    private void watheextended$suppressVoodooHintText(TextRenderer tr, Text text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
        if (text.getContent() instanceof TranslatableTextContent tc && tc.getKey().equals("hud.voodoo.player_deaths_only")) {
            cir.setReturnValue(0);
        }
    }
}
