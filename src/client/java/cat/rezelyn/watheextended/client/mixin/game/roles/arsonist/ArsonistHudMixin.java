package cat.rezelyn.watheextended.client.mixin.game.roles.arsonist;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTip;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.constants.SERoles;
import pro.fazeclan.river.stupid_express.role.arsonist.cca.DousedPlayerComponent;

@Environment(EnvType.CLIENT)
@Mixin(value = RoleNameRenderer.class)
public class ArsonistHudMixin {

  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderDousedCount(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    try {
      if (!ConfigHelper.isLoaded()) return;
      if (!GameStatus.isActive(player.getWorld())) return;
      if (GameFunctions.isPlayerSpectatingOrCreative(player)) return;
      if (!GameWorldComponent.KEY.get(player.getWorld()).isRole(player, SERoles.ARSONIST)) return;

      int aliveCount = 0;
      int dousedCount = 0;

      for (PlayerEntity p : player.getWorld().getPlayers()) {
        if (!GameFunctions.isPlayerAliveAndSurvival(p)) continue;
        aliveCount++;
        if (DousedPlayerComponent.KEY.get(p).isDoused()) dousedCount++;
      }

      int required = (int) (aliveCount * 0.3);
      Text progress = AbilityTip.progress("🔥", dousedCount, required, Text.literal("doused players"));
      AbilityTip.draw(context, renderer, progress, SERoles.ARSONIST.color());
    } catch (Throwable ignored) {
    }
  }
}
