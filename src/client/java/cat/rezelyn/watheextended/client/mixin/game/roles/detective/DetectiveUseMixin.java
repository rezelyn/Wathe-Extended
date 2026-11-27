package cat.rezelyn.watheextended.client.mixin.game.roles.detective;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityConditions;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import org.BsXinQin.kinswathe.packet.host.AbilityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class DetectiveUseMixin {
  @Shadow private int itemUseCooldown;

  @WrapWithCondition(
      method = "handleInputEvents",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V",
              ordinal = 0))
  private boolean watheextended$investigateOnUse(MinecraftClient client) {
    if (client.currentScreen != null || !AbilityConditions.canInvestigate(client)) return true;
    ClientPlayNetworking.send(new AbilityC2SPacket());
    itemUseCooldown = 4;
    return false;
  }
}
