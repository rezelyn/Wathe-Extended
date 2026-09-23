package cat.rezelyn.watheextended.game;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// central compatibility check for every role that can use the new Instinct mechanic
public final class InstinctAccess {
    private static final Set<Identifier> NON_KILLER_ROLES = ConcurrentHashMap.newKeySet();
    private static final Identifier DREAMER_ROLE = Identifier.of("kinswathe", "dreamer");
    static {
        register(Identifier.of("kinswathe", "hacker"));
        register(Identifier.of("noellesroles", "awesome_binglus"));
        register(Identifier.of("noellesroles", "jester"));
        register(Identifier.of("stupid_express", "arsonist"));
        register(Identifier.of("stupid_express", "thief"));
    }

    private InstinctAccess() {}

    public static void register(Identifier roleId) {
        if (roleId != null) NON_KILLER_ROLES.add(roleId);
    }

    public static boolean canUse(PlayerEntity player) {
        if (player == null) return false;
        if (GameFunctions.isPlayerSpectatingOrCreative(player)) return true;
        if (!GameFunctions.isPlayerAliveAndSurvival(player)) return false;

        Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
        if (role != null && DREAMER_ROLE.equals(role.identifier())) return false;
        return role != null && (role.canUseKiller() || NON_KILLER_ROLES.contains(role.identifier()));
    }
}
