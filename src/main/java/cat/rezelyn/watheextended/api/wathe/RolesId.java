package cat.rezelyn.watheextended.api.wathe;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RolesId {

    private RolesId() {
    }

    public static List<String> get() {
        List<String> ids = new ArrayList<>();
        for (Role role : WatheRoles.ROLES) {
            if (role != null && role.identifier() != null) {
                ids.add(role.identifier().toString());
            }
        }
        Collections.sort(ids);
        return Collections.unmodifiableList(ids);
    }
}
