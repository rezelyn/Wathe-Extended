package cat.rezelyn.watheextended.game.roles.cook;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class CookPan {
  private static final Identifier PAN = Identifier.of("kinswathe", "pan");

  private CookPan() {}

  public static boolean canBlockShot(PlayerEntity target) {
    ItemStack held = target.getMainHandStack();
    return PAN.equals(Registries.ITEM.getId(held.getItem()))
        && !target.getItemCooldownManager().isCoolingDown(held.getItem());
  }
}
