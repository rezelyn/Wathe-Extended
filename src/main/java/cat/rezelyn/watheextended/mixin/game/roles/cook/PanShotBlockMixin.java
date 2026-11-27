package cat.rezelyn.watheextended.mixin.game.roles.cook;

import cat.rezelyn.watheextended.game.roles.cook.CookPan;
import cat.rezelyn.watheextended.game.utils.SoundUtils;
import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWatheGameSettings;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KinsWatheGameSettings.class, remap = false)
public class PanShotBlockMixin {

  @Inject(
      method = "lambda$registerEvents$4",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/entity/player/PlayerEntity;isUsingItem()Z",
              remap = true),
      cancellable = true)
  private static void watheextended$blockIncomingShot(
      PlayerEntity victim,
      PlayerEntity killer,
      Identifier reason,
      CallbackInfoReturnable<Boolean> cir) {
    if (victim == killer
        || !GameConstants.DeathReasons.GUN.equals(reason)
        || !CookPan.canBlockShot(victim)) return;

    KinsWatheItems.setItemAfterUsing(victim, KinsWatheItems.PAN, Hand.MAIN_HAND);
    SoundUtils.playAtEntity(victim, SoundEvents.ENTITY_ITEM_BREAK, 1.5f, 1f, 5);
    cir.setReturnValue(false);
  }
}
