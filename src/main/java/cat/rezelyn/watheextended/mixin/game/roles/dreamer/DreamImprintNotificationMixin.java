package cat.rezelyn.watheextended.mixin.game.roles.dreamer;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.items.DreamImprintItem")
public abstract class DreamImprintNotificationMixin {

  // DreamImprintItem sends the Dreamer's confirmation first, then the imprinted player's notice.
  @WrapOperation(
      method = "useOnEntity",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/player/PlayerEntity;sendMessage(Lnet/minecraft/text/Text;Z)V",
              ordinal = 1),
      require = 1,
      remap = true)
  private void watheextended$optionallyNotifyImprintedPlayer(
      PlayerEntity target, Text message, boolean overlay, Operation<Void> original) {
    if (WatheExtendedServerConfig.dreamerImprintNotificationEnabled)
      original.call(target, message, overlay);
  }
}
