package cat.rezelyn.watheextended.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;

public class AssignedModifier {

  public static List<Assigned> getModifiers(PlayerEntity player) {
    try {
      List<Modifier> modifiers = findModifiers(player);
      if (modifiers.isEmpty()) return Collections.emptyList();

      Map<String, ModifiersDisplay.ModifierDisplay> display = ModifiersDisplay.get();
      List<Assigned> out = new ArrayList<>();

      for (Modifier modifier : modifiers) {
        ModifiersDisplay.ModifierDisplay d = display.get(modifier.identifier.toString());
        if (d != null) out.add(new Assigned(d.display(), d.color()));
      }
      return out;
    } catch (Throwable ignored) {
      return Collections.emptyList();
    }
  }

  public static List<Modifier> findModifiers(PlayerEntity player) {
    return findModifiers(player, false);
  }

  public static List<Modifier> findModifiers(PlayerEntity player, boolean includeUnidentified) {
    if (player == null) return List.of();
    World world = player.getWorld();
    if (world == null) return List.of();

    WorldModifierComponent component = WorldModifierComponent.KEY.maybeGet(world).orElse(null);
    if (component == null) return List.of();
    List<?> modifiers = component.getModifiers(player);
    if (modifiers == null || modifiers.isEmpty()) return List.of();

    List<Modifier> assigned = new ArrayList<>(modifiers.size());
    for (Object entry : modifiers) {
      if (entry instanceof Modifier modifier
          && (includeUnidentified || modifier.identifier != null)) {
        assigned.add(modifier);
      }
    }
    return Collections.unmodifiableList(assigned);
  }

  public static boolean hasModifier(PlayerEntity player, String modifierId) {
    return findModifiers(player).stream()
        .anyMatch(modifier -> modifier.identifier.toString().equals(modifierId));
  }

  public static boolean hasModifier(PlayerEntity player, Modifier modifier) {
    if (player == null || modifier == null) return false;
    WorldModifierComponent component =
        WorldModifierComponent.KEY.maybeGet(player.getWorld()).orElse(null);
    return component != null && component.isModifier(player, modifier);
  }

  public record Assigned(Text text, int color) {}
}
