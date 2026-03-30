package cat.rezelyn.watheextended.api;

import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssignedModifier {

    public static List<Assigned> getModifiers(PlayerEntity player) {
        if (player == null) return Collections.emptyList();
        try {
            World world = player.getWorld();
            if (world == null) return Collections.emptyList();

            WorldModifierComponent component = WorldModifierComponent.KEY.get(world);
            if (component == null) return Collections.emptyList();

            List<?> modifiers = component.getModifiers(player);
            if (modifiers == null || modifiers.isEmpty()) return Collections.emptyList();

            Map<String, ModifiersDisplay.ModifierDisplay> display = ModifiersDisplay.get();
            List<Assigned> out = new ArrayList<>();

            for (Object object : modifiers) {
                if (object == null) continue;
                try {
                    Modifier modifier = (Modifier) object;
                    if (modifier.identifier == null) continue;
                    ModifiersDisplay.ModifierDisplay d = display.get(modifier.identifier.toString());
                    if (d != null) out.add(new Assigned(d.display(), d.color()));
                } catch (Throwable ignored) {
                }
            }
            return out;
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    public record Assigned(Text text, int color) {
    }
}
