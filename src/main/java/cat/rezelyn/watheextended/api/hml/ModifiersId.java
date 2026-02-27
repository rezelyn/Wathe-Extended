package cat.rezelyn.watheextended.api.hml;

import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModifiersId {

    private ModifiersId() {
    }

    public static List<String> get() {
        List<String> ids = new ArrayList<>();
        for (Modifier mod : HMLModifiers.MODIFIERS) {
            if (mod != null && mod.identifier != null) {
                ids.add(mod.identifier.toString());
            }
        }
        Collections.sort(ids);
        return Collections.unmodifiableList(ids);
    }
}
