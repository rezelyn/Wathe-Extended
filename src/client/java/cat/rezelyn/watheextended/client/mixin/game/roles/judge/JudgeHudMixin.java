package cat.rezelyn.watheextended.client.mixin.game.roles.judge;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityConditions;
import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTip;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public class JudgeHudMixin {
  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderJudgementTip(
      TextRenderer renderer, ClientPlayerEntity player, DrawContext context,
      RenderTickCounter tickCounter, CallbackInfo ci) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (player != client.player || AbilityTip.isHidden(client)) return;
    GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
    if (game.getGameStatus() != GameWorldComponent.GameStatus.ACTIVE
        || !game.isRole(player, KinsWatheRoles.JUDGE)) return;

    if (AbilityConditions.isDisabledByMood(player, KinsWatheRoles.JUDGE)) {
      AbilityTip.draw(context, renderer, AbilityTip.disabled(), 0xFFFFFF);
      return;
    }

    String key = client.options.inventoryKey.getBoundKeyLocalizedText().getString();
    int cooldown = AbilityPlayerComponent.KEY.get(player).cooldown;
    if (cooldown > 0) {
      AbilityTip.drawNotReady(context, renderer,
          AbilityTip.cooldown(String.valueOf(Math.ceilDiv(cooldown, 20))), key, 0xFFFFFF);
      return;
    }
    int price = ConfigWorldComponent.KEY.get(player.getWorld()).JudgeAbilityPrice;
    if (PlayerShopComponent.KEY.get(player).balance < price) {
      AbilityTip.drawNotReady(context, renderer, AbilityTip.price(String.valueOf(price)), key, 0xFFFFFF);
      return;
    }
    AbilityTip.draw(context, renderer, AbilityTip.ready(key, true), 0xFFFFFF);
  }
}
