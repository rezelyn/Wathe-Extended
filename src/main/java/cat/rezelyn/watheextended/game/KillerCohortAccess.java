package cat.rezelyn.watheextended.game;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

// shared killer-sided visibility rules used by the cohort overlay
public final class KillerCohortAccess {
    // mirrors noellesroles KILLER_SIDED_NEUTRALS
    private static final Set<Identifier> KILLER_SIDED_ROLES = Set.of(
            Identifier.of("kinswathe", "hacker"),
            Identifier.of("noellesroles", "executioner"),
            Identifier.of("noellesroles", "jester"),
            Identifier.of("noellesroles", "mimic"),
            Identifier.of("noellesroles", "vulture")
    );

    private KillerCohortAccess() {}

    public static boolean isKillerCohort(GameWorldComponent game, PlayerEntity player) {
        return player != null && game != null && (game.canUseKillerFeatures(player)
                || (game.getRole(player) != null && KILLER_SIDED_ROLES.contains(game.getRole(player).identifier())));
    }

    public static boolean canSeeCohorts(GameWorldComponent game, PlayerEntity player) {
        return isKillerCohort(game, player) || InstinctAccess.canUse(player);
    }
}
