package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public final class GuidebookIcons {

    public static final Map<String, String> ICONS = Map.ofEntries(
            Map.entry("knife", "\uE100"),
            Map.entry("revolver", "\uE101"),
            Map.entry("lighter", "\uE102"),
            Map.entry("lockpick", "\uE103"),
            Map.entry("master_key", "\uE104"),
            Map.entry("role_mine", "\uE105"),
            Map.entry("tape", "\uE106"),
            Map.entry("jerry_can", "\uE107"),
            Map.entry("defense_vial", "\uE108"),
            Map.entry("delusion_vial", "\uE109"),
            Map.entry("psycho_mode", "\uE10A"),
            Map.entry("bat", "\uE10B"),
            Map.entry("blowgun", "\uE10C"),
            Map.entry("body_bag", "\uE10D"),
            Map.entry("crowbar", "\uE10E"),
            Map.entry("dream_imprint", "\uE10F"),
            Map.entry("firecracker", "\uE200"),
            Map.entry("grenade", "\uE201"),
            Map.entry("hunting_knife", "\uE202"),
            Map.entry("knockout_drug", "\uE203"),
            Map.entry("medical_kit", "\uE204"),
            Map.entry("note", "\uE205"),
            Map.entry("pan", "\uE206"),
            Map.entry("pill", "\uE207"),
            Map.entry("poison_injector", "\uE208"),
            Map.entry("poison_vial", "\uE209"),
            Map.entry("sulfuric_acid_barrel", "\uE210"),
            Map.entry("coin", "\uE211"),
            Map.entry("killer", "\uE212"),
            Map.entry("civilian", "\uE213"),
            Map.entry("neutral", "\uE214")
    );
    private static final Style ICON_STYLE = Style.EMPTY
            .withColor(Formatting.WHITE)
            .withBold(false)
            .withItalic(false)
            .withUnderline(false)
            .withStrikethrough(false)
            .withObfuscated(false);

    private GuidebookIcons() {
    }

    public static Text icon(String name) {
        String ch = ICONS.get(name);
        if (ch == null) return Text.empty();
        return Text.literal(ch).setStyle(ICON_STYLE);
    }

    public static boolean has(String name) {
        return ICONS.containsKey(name);
    }
}
