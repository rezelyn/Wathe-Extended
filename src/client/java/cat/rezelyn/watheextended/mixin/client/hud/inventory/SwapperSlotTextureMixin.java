package cat.rezelyn.watheextended.mixin.client.hud.inventory;

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
@Mixin(targets = "org.agmas.noellesroles.client.ui.SwapperPlayerWidget", remap = false)
public abstract class SwapperSlotTextureMixin {

    private static final Identifier SWAPPER_SLOT = Identifier.of("watheextended", "swapper_slot");

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), require = 0)
    private void watheextended$overrideSwapperSlotTexture(DrawContext context, Identifier texture, int x, int y, int width, int height) {
        context.drawGuiTexture(SWAPPER_SLOT, x, y, width, height);
    }
}
