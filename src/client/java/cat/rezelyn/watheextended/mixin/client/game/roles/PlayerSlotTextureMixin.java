package cat.rezelyn.watheextended.mixin.client.game.roles;

import cat.rezelyn.watheextended.client.render.PlayerSlotRenderer;
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
@Mixin(
    targets = {
      "org.agmas.noellesroles.client.ui.MorphlingPlayerWidget",
      "org.agmas.noellesroles.client.ui.VoodooPlayerWidget",
      "org.BsXinQin.kinswathe.client.roles.judge.JudgePlayerWidget",
      "org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget",
      "org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget"
    },
    remap = false)
public abstract class PlayerSlotTextureMixin {

  @Redirect(
      method = "renderWidget",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"),
      require = 0)
  private void watheextended$drawTintedRoleSlot(
      DrawContext context, Identifier texture, int x, int y, int width, int height) {
    PlayerSlotRenderer.drawRoleSlot(context, x, y, width, height);
  }
}
