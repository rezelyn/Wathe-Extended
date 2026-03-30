package cat.rezelyn.watheextended.api;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class AssignedRole {

    public static Assigned getRole(PlayerEntity player) {
        if (player == null) return null;
        try {
            World world = player.getWorld();
            if (world == null) return null;

            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            if (component == null) return null;

            Role role = component.getRole(player);
            if (role == null || role.identifier() == null) return null;

            RolesDisplay.RoleDisplay display = RolesDisplay.get().get(role.identifier().toString());
            if (display == null) return null;

            return new Assigned(display.display(), display.color());
        } catch (Throwable t) {
            return null;
        }
    }

    public record Assigned(net.minecraft.text.Text text, int color) {
    }
}
