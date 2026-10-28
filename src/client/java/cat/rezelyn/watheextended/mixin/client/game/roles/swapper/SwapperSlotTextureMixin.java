package cat.rezelyn.watheextended.mixin.client.game.roles.swapper;

import cat.rezelyn.watheextended.client.render.PlayerSlotRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "org.agmas.noellesroles.client.ui.SwapperPlayerWidget", remap = false)
public abstract class SwapperSlotTextureMixin {

  @Redirect(
      method = "renderWidget",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"),
      require = 0)
  private void watheextended$overrideSwapperSlotTexture(
      DrawContext context, Identifier texture, int x, int y, int width, int height) {
    PlayerSlotRenderer.drawRoleSlot(context, x, y, width, height);
  }

  @Redirect(
      method = "lambda$new$0",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/entity/player/PlayerEntity;hasVehicle()Z",
              remap = true),
      remap = false)
  private static boolean watheextended$allowRidingPlayers(PlayerEntity player) {
    return false;
  }
}
