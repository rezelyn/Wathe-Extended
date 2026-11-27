package cat.rezelyn.watheextended.client.mixin.game.roles.phantom;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityRoles;
import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTip;
import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = RoleNameRenderer.class)
public class PhantomHudMixin {

  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderPhantomCancelHud(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    try {
      Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
      if (role == null || !AbilityRoles.PHANTOM.equals(role.identifier().toString())) return;
      var effect = player.getStatusEffect(StatusEffects.INVISIBILITY);
      if (effect == null) return;

      boolean canCancel = ClientConfig.getBool("watheextended.phantom.canCancelAbility", true);

      AbilityTip.drawDuration(
          context, renderer, effect.getDuration(), canCancel, "key.noellesroles.ability");
    } catch (Throwable ignored) {
    }
  }
}
