package cat.rezelyn.watheextended.api.hml;

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
        for (Modifier mod : HMLModifiers.MODIFIERS) {
            if (mod == null || mod.identifier == null) continue;
            String id = mod.identifier.toString();
            Text name = resolveName(mod, id);
            int color = resolveColor(mod);
            result.put(id, new ModifierDisplay(id, name, color));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Text resolveName(Modifier mod, String fallbackId) {
        try {
            return mod.getName();
        } catch (Throwable ignored) {
        }
        return Text.literal(localName(fallbackId));
    }

    private static int resolveColor(Modifier mod) {
        try {
            return mod.color();
        } catch (Throwable ignored) {
            return 0xFFFFFF;
        }
    }

    public static String localName(String id) {
        int colon = id.indexOf(':');
        String raw = colon >= 0 ? id.substring(colon + 1) : id;
        String[] parts = raw.split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.isEmpty() ? raw : sb.toString();
    }

    public record ModifierDisplay(String id, Text display, int color) {
    }
}
