package cat.rezelyn.watheextended.mixin.client.item;

import cat.rezelyn.watheextended.client.CopyPositionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class CopyPositionAttackMixin {

  @Shadow @Nullable public ClientPlayerEntity player;

  @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
  private void watheextended$toggleCopyPositionMode(CallbackInfoReturnable<Boolean> cir) {
    if (!CopyPositionHandler.canUse(player)) return;
    CopyPositionHandler.toggleMode(player);
    cir.setReturnValue(false);
  }

  @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
  private void watheextended$preventCopyPositionBreaking(boolean breaking, CallbackInfo ci) {
    if (CopyPositionHandler.canUse(player)) ci.cancel();
  }
}
