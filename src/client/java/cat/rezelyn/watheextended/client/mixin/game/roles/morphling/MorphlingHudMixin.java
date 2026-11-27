package cat.rezelyn.watheextended.client.mixin.game.roles.morphling;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTip;
import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = RoleNameRenderer.class)
public class MorphlingHudMixin {

  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderMorphlingCancelHud(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    try {
      if (!GameWorldComponent.KEY.get(player.getWorld()).isRole(player, Noellesroles.MORPHLING))
        return;
      int morphTicks = MorphlingPlayerComponent.KEY.get(player).getMorphTicks();
      if (morphTicks < 0) {
        var inventoryKey = MinecraftClient.getInstance().options.inventoryKey;
        String seconds = String.valueOf(Math.ceilDiv(-(long) morphTicks, 20));
        AbilityTip.drawNotReady(
            context,
            renderer,
            AbilityTip.cooldown(seconds),
            inventoryKey.getBoundKeyLocalizedText().getString(),
            0xFFFFFF);
        return;
      }
      if (morphTicks == 0) return;

      boolean canCancel = ClientConfig.getBool("watheextended.morphling.canCancelAbility", true);

      AbilityTip.drawDuration(context, renderer, morphTicks, canCancel, "key.noellesroles.ability");
    } catch (Throwable ignored) {
    }
  }
}
