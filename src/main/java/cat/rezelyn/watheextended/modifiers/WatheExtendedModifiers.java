package cat.rezelyn.watheextended.modifiers;

import cat.rezelyn.watheextended.WatheExtended;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.util.ArrayList;

public final class WatheExtendedModifiers {

    private WatheExtendedModifiers() {
    }

    public static Modifier INTROVERTED;
    public static Modifier TAXED;
    public static Modifier ADAPTIVE;

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
                false,
                false
        ));
    }
}
