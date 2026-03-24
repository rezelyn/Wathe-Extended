package cat.rezelyn.watheextended.mixin.client.noellesroles.voodoo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "org.agmas.noellesroles.client.ui.VoodooPlayerWidget", remap = false)
public abstract class VoodooSlotTextureMixin {

    private static final Identifier VOODOO_SLOT = Identifier.of("watheextended", "voodoo_slot");

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), require = 0)
    private void watheextended$overrideVoodooSlotTexture(DrawContext context, Identifier texture, int x, int y, int width, int height) {
        context.drawGuiTexture(VOODOO_SLOT, x, y, width, height);
    }
}
