package cat.rezelyn.watheextended.api.wathe;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AssignedRole {

    public static Assigned getRole(PlayerEntity player) {
        if (player == null) return null;
        try {
            World world = player.getWorld();
            if (world == null) return null;

            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            if (gwc == null) return null;

            Role role = gwc.getRole(player);
            if (role == null) return null;

            Text roleName = null;
            int color = 0xFFFFFF;
            try {
                roleName = org.agmas.harpymodloader.Harpymodloader.getRoleName(role);
            } catch (Throwable ignored) {
            }
            try {
                color = role.color();
            } catch (Throwable t) {
            }
            return new Assigned(roleName, color);
        } catch (Throwable t) {
            return null;
        }
    }

    public record Assigned(Text text, int color) {
    }
}
