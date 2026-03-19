package cat.rezelyn.watheextended.modifiers;

import cat.rezelyn.watheextended.WatheExtended;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.util.ArrayList;
import java.util.Set;

public final class WatheExtendedModifiers {

    private WatheExtendedModifiers() {
    }

    public static Modifier INTROVERTED;
    public static Modifier TAXED;
    public static Modifier ADAPTIVE;

    // roles that should never receive the Introverted modifier
    private static final Set<String> INTROVERTED_ROLE_BLACKLIST = Set.of(
            "kinswathe:robot",
            "kinswathe:dreamer",
            "stupid_express:thief"
    );

    public static void initialize() {
        INTROVERTED = HMLModifiers.registerModifier(new Modifier(
                WatheExtended.id("introverted"),
                0x9B7FD4,
                new ArrayList<>(),
                new ArrayList<>(),
                false,
                true
        ));
        TAXED = HMLModifiers.registerModifier(new Modifier(
                WatheExtended.id("taxed"),
                0xFC8E26,
                new ArrayList<>(),
                new ArrayList<>(),
                false,
                false
        ));
        ADAPTIVE = HMLModifiers.registerModifier(new Modifier(
                WatheExtended.id("adaptive"),
                0x4FC978,
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                false
        ));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (Role role : WatheRoles.ROLES) {
                if (role != null && role.identifier() != null
                        && INTROVERTED_ROLE_BLACKLIST.contains(role.identifier().toString())
                        && !INTROVERTED.cannotBeAppliedTo.contains(role)) {
                    INTROVERTED.cannotBeAppliedTo.add(role);
                }
            }
        });
    }
}
