package cat.rezelyn.watheextended.api;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class AssignedRole {

  public static Assigned getRole(PlayerEntity player) {
    try {
      Role role = findRole(player).orElse(null);
      if (role == null) return null;

      RolesDisplay.RoleDisplay display = RolesDisplay.get().get(role.identifier().toString());
      if (display == null) return null;

      return new Assigned(display.display(), display.color());
    } catch (Throwable t) {
      return null;
    }
  }

  public static Optional<Role> findRole(PlayerEntity player) {
    if (player == null) return Optional.empty();
    World world = player.getWorld();
    if (world == null) return Optional.empty();
    return GameWorldComponent.KEY
        .maybeGet(world)
        .map(game -> game.getRole(player))
        .filter(role -> role.identifier() != null);
  }

  public static boolean hasRole(PlayerEntity player, String roleId) {
    return findRole(player).filter(role -> role.identifier().toString().equals(roleId)).isPresent();
  }

  public record Assigned(net.minecraft.text.Text text, int color) {}
}
