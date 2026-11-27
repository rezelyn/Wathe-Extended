package cat.rezelyn.watheextended.client.mixin.keybinds;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityConditions;
import net.minecraft.client.MinecraftClient;
import org.BsXinQin.kinswathe.client.KinsWatheInitializeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KinsWatheInitializeClient.class, remap = false)
public class AbilityKeyMixin {
  @Inject(method = "lambda$setRoleAbilityPackets$1", at = @At("HEAD"), cancellable = true)
  private static void watheextended$skipDetectiveAbilityKey(CallbackInfo ci) {
    if (AbilityConditions.isDetective(MinecraftClient.getInstance())) ci.cancel();
  }
}
