package cat.rezelyn.watheextended.mixin.game.roles.necromancer;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pro.fazeclan.river.stupid_express.role.necromancer.RevivalSelectionHandler;

@Mixin(value = RevivalSelectionHandler.class, remap = false)
public class RevivalCooldownMixin {
  @ModifyArg(
      method = "lambda$init$0",
      at = @At(value = "INVOKE", target =
          "Lpro/fazeclan/river/stupid_express/cca/AbilityCooldownComponent;setCooldown(I)V"),
      index = 0)
  private static int watheextended$revivalCooldownTicks(int originalTicks) {
    int seconds = Math.clamp(WatheExtendedServerConfig.necromancerAbilityCooldown, 0, Integer.MAX_VALUE / 20);
    return seconds * 20;
  }
}
