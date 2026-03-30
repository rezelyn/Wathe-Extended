package cat.rezelyn.watheextended.api;

import net.minecraft.text.Text;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModifiersDisplay {

    private ModifiersDisplay() {
    }

    public static Map<String, ModifierDisplay> get() {
        Map<String, ModifierDisplay> result = new LinkedHashMap<>();
        for (Modifier modifier : HMLModifiers.MODIFIERS) {
            if (modifier == null || modifier.identifier == null) continue;
            String id = modifier.identifier.toString();
            Text name;
            try {
                name = modifier.getName();
            } catch (Throwable ignored) {
                name = Text.literal(prettyName(id));
            }
            result.put(id, new ModifierDisplay(id, name, modifier.color()));
        }
        return Collections.unmodifiableMap(result);
    }

    public static String prettyName(String id) {
        int colon = id.indexOf(':');
        String raw = colon >= 0 ? id.substring(colon + 1) : id;
        String[] parts = raw.split("[_\\-]");
        StringBuilder string = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!string.isEmpty()) string.append(' ');
            string.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return string.isEmpty() ? raw : string.toString();
    }

    public record ModifierDisplay(String id, Text display, int color) {
    }
}
