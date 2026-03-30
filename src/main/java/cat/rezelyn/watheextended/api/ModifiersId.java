package cat.rezelyn.watheextended.api;

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
        for (Modifier modifier : HMLModifiers.MODIFIERS) {
            if (modifier != null && modifier.identifier != null) {
                ids.add(modifier.identifier.toString());
            }
        }
        Collections.sort(ids);
        return Collections.unmodifiableList(ids);
    }
}
