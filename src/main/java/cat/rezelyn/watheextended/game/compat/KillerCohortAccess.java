package cat.rezelyn.watheextended.game.compat;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

// shared killer-sided visibility rules used by the cohort overlay
public final class KillerCohortAccess {

  // mirrors noellesroles KILLER_SIDED_NEUTRALS
  private static final Set<Identifier> KILLER_SIDED_ROLES =
      Set.of(
          Identifier.of("kinswathe", "hacker"),
          Identifier.of("noellesroles", "executioner"),
          Identifier.of("noellesroles", "jester"),
          Identifier.of("noellesroles", "mimic"),
          Identifier.of("noellesroles", "vulture"));

  // Roles that should not see killer cohort labels, across all client overlays
  private static final Set<Identifier> COHORT_VISIBILITY_DENYLIST =
      Set.of(
          Identifier.of("stupid_express", "arsonist"),
          Identifier.of("stupid_express", "thief"),
          Identifier.of("noellesroles", "executioner"),
          Identifier.of("noellesroles", "mimic"),
          Identifier.of("noellesroles", "awesome_binglus"),
          Identifier.of("noellesroles", "jester"));

  // list of roles that shouldn't be able to see killer cohorts, for compatibility purposes
  private static final Identifier EXCLUDED_ROLES =
      Identifier.of("noellesroles", "awesome_binglus");

  private KillerCohortAccess() {}

  public static boolean isKillerCohort(GameWorldComponent game, PlayerEntity player) {
    if (player == null || game == null || isExcluded(game, player)) return false;
    var role = game.getRole(player);
    return game.canUseKillerFeatures(player)
        || (role != null && KILLER_SIDED_ROLES.contains(role.identifier()));
  }

  public static boolean canSeeCohorts(GameWorldComponent game, PlayerEntity player) {
    if (game == null
        || player == null
        || isCohortVisibilityDenied(game, player)) return false;
    return isKillerCohort(game, player) || InstinctAccess.canUse(player);
  }

  private static boolean isCohortVisibilityDenied(GameWorldComponent game, PlayerEntity player) {
    var role = game.getRole(player);
    return role != null && COHORT_VISIBILITY_DENYLIST.contains(role.identifier());
  }

  private static boolean isExcluded(GameWorldComponent game, PlayerEntity player) {
    if (!GameFunctions.isPlayerAliveAndSurvival(player)) return false;
    var role = game.getRole(player);
    return role != null && EXCLUDED_ROLES.equals(role.identifier());
  }

}
