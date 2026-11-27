package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.game.ShooterPunishment;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.network.GunShootPayload;
import dev.doctor4t.wathe.util.Scheduler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GunShootPayload.Receiver.class)
public abstract class ShooterPunishmentMixin {

  private static final Identifier ALLOWED_ROLES = Identifier.of("kinswathe", "licensed_villain");

  private static boolean shouldBypass(ServerPlayerEntity player) {
    var role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
    return role != null && ALLOWED_ROLES.equals(role.identifier());
  }

  @Inject(method = "receive", at = @At("HEAD"), cancellable = true)
  private void watheextended$applyShooterPunishment(
      GunShootPayload payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
    if (!ShooterPunishment.shouldHandle(context.player(), payload)) {
      return;
    }

    ShooterPunishment.handle(context.player(), payload, context);
    ci.cancel();
  }

  // wathe checks for a revolver here to apply penalties before it calls killPlayer
  @ModifyExpressionValue(
      method = "receive",
      slice =
          @Slice(
              from =
                  @At(
                      value = "FIELD",
                      target = "Ldev/doctor4t/wathe/index/WatheItems;REVOLVER:Lnet/minecraft/item/Item;")),
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
              ordinal = 0))
  private boolean watheextended$penalizeRevolverHit(boolean isRevolver, @Local PlayerEntity target) {
    return isRevolver && !ShooterPunishment.shouldBlock(target);
  }

  @Redirect(
      method = "receive",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ldev/doctor4t/wathe/util/Scheduler;schedule(Ljava/lang/Runnable;I)Ldev/doctor4t/wathe/util/Scheduler$ScheduledTask;"))
  private Scheduler.ScheduledTask watheextended$bypassShooterPunishment(
      Runnable task, int delay, GunShootPayload payload, ServerPlayNetworking.Context context) {
    ServerPlayerEntity shooter = context.player();
    if (shouldBypass(shooter)) {
      return Scheduler.schedule(() -> PlayerMoodComponent.KEY.get(shooter).setMood(0), delay);
    }
    return Scheduler.schedule(task, delay);
  }
}
