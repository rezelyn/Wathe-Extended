package cat.rezelyn.watheextended.mixin.client.game.roles.dreamer;

import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ClientPlayNetworkHandler.class)
public abstract class DreamImprintLabelMixin {

  @Inject(method = "onOverlayMessage", at = @At("HEAD"), cancellable = true)
  private void watheextended$suppressDisabledDreamImprintNotice(
      OverlayMessageS2CPacket packet, CallbackInfo ci) {
    if (ClientConfig.getBool("watheextended.dreamer.imprintNotificationEnabled", true)) return;
    if (!(packet.text().getContent() instanceof TranslatableTextContent content)
        || !"tip.kinswathe.dreamer.imprint".equals(content.getKey())) return;

    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null || client.world == null) return;

    try {
      Role role = GameWorldComponent.KEY.get(client.world).getRole(client.player);
      // The Dreamer receives a separate confirmation this option only controls the marked player's
      // notice
      if (role != null
          && role.identifier() != null
          && "kinswathe:dreamer".equals(role.identifier().toString())) return;
    } catch (Throwable ignored) {
      return;
    }

    ci.cancel();
  }
}
