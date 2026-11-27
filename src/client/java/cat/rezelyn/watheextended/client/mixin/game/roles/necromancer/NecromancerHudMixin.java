package cat.rezelyn.watheextended.client.mixin.game.roles.necromancer;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTargets;
import cat.rezelyn.watheextended.client.render.hud.ability.AbilityTip;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.cca.AbilityCooldownComponent;
import pro.fazeclan.river.stupid_express.constants.SERoles;
import pro.fazeclan.river.stupid_express.role.necromancer.cca.NecromancerComponent;

@Environment(EnvType.CLIENT)
@Mixin(RoleNameRenderer.class)
public class NecromancerHudMixin {
  @Inject(method = "renderHud", at = @At("TAIL"))
  private static void watheextended$renderReviveTip(
      TextRenderer renderer,
      ClientPlayerEntity player,
      DrawContext context,
      RenderTickCounter tickCounter,
      CallbackInfo ci) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (player != client.player || player.isSpectator() || player.isCreative()) return;

    GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
    if (game.getGameStatus() != GameWorldComponent.GameStatus.ACTIVE
        || !game.isRole(player, SERoles.NECROMANCER)) return;

    int revives = NecromancerComponent.KEY.get(player.getWorld()).getAvailableRevives();
    AbilityCooldownComponent cooldown = AbilityCooldownComponent.KEY.get(player);
    boolean onCooldown = cooldown.hasCooldown();
    if (revives > 0) {
      Text counter = AbilityTip.value("☠", revives);
      AbilityTip.draw(context, renderer, counter, SERoles.NECROMANCER.color(), onCooldown ? 2 : 1);
    }

    String useKey = client.options.useKey.getBoundKeyLocalizedText().getString();
    Text status;
    if (onCooldown) {
      Text timer = AbilityTip.cooldown(String.valueOf(cooldown.getCooldown() / 20));
      AbilityTip.drawNotReady(context, renderer, timer, useKey, 0xFFFFFF);
      return;
    } else if (revives <= 0) {
      Text counter = AbilityTip.value("☠", 0);
      AbilityTip.draw(context, renderer, counter, Formatting.RED, 1);
      status = AbilityTip.notReady(useKey);
    } else {
      status = AbilityTip.ready(useKey, watheextended$isLookingAtRevivableBody(client, player));
    }
    AbilityTip.draw(context, renderer, status, Formatting.WHITE);
  }

  @Unique
  private static boolean watheextended$isLookingAtRevivableBody(
      MinecraftClient client, ClientPlayerEntity player) {
    return AbilityTargets.underCrosshair(client, PlayerBodyEntity.class)
        .map(PlayerBodyEntity::getPlayerUuid)
        .map(player.networkHandler::getPlayerListEntry)
        .isPresent();
  }
}
