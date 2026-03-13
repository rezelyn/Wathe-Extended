package cat.rezelyn.watheextended.api.hml;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssignedModifier {

    public static List<Assigned> getModifiers(PlayerEntity player) {
        if (player == null) return Collections.emptyList();
        try {
            World world = player.getWorld();
            if (world == null) return Collections.emptyList();

            try {
                org.agmas.harpymodloader.component.WorldModifierComponent wmc = org.agmas.harpymodloader.component.WorldModifierComponent.KEY.get(world);
                if (wmc == null) return Collections.emptyList();

                List<?> modifiers = wmc.getModifiers(player);
                if (modifiers == null || modifiers.isEmpty()) return Collections.emptyList();

                List<Assigned> out = new ArrayList<>();

                for (Object m : modifiers) {
                    if (m == null) continue;
                    try {
                        org.agmas.harpymodloader.modifiers.Modifier mod = (org.agmas.harpymodloader.modifiers.Modifier) m;
                        Text name = mod.getName();
                        int color = 0xFFFFFF;

                        try {
                            color = mod.color();
                        } catch (Throwable t) {
                        }

                        if (name != null) {
                            out.add(new Assigned(name, color));
                        }
                    } catch (Throwable ignored) {
                    }
                }
                return out;
            } catch (NoClassDefFoundError e) {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    public record Assigned(Text text, int color) {
    }
}
