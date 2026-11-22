package cat.rezelyn.watheextended.mixin.game.roles.jester;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameFunctions.class)
public abstract class JesterGunshotMixin {

  @Unique
  private static final Identifier LICENSED_VILLAIN = Identifier.of("kinswathe", "licensed_villain");

  @Unique
  private static boolean watheextended$isLicensedVillainGunshot(
      PlayerEntity killer, Identifier deathReason) {
    if (killer == null || !GameConstants.DeathReasons.GUN.equals(deathReason)) return false;
    var role = GameWorldComponent.KEY.get(killer.getWorld()).getRole(killer);
    return role != null && LICENSED_VILLAIN.equals(role.identifier());
  }
}
