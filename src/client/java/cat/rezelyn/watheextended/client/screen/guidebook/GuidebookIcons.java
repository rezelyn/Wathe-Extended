package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class GuidebookIcons {

    public static final Map<String, String> ICONS;
    private static final Style ICON_STYLE = Style.EMPTY
            .withFont(Identifier.of("minecraft", "default"))
            .withColor(Formatting.WHITE)
            .withBold(false)
            .withItalic(false)
            .withUnderline(false)
            .withStrikethrough(false)
            .withObfuscated(false);

    static {
        Map<String, String> map = new HashMap<>();
        map.put("enabled", "\uE400");
        map.put("disabled", "\uE401");

        map.put("knife", "\uE100");
        map.put("fake_knife", "\uE100");
        map.put("revolver", "\uE101");
        map.put("fake_revolver", "\uE101");
        map.put("lighter", "\uE102");
        map.put("lockpick", "\uE103");
        map.put("master_key", "\uE104");
        map.put("role_mine", "\uE105");
        map.put("tape", "\uE106");
        map.put("jerry_can", "\uE107");
        map.put("defense_vial", "\uE108");
        map.put("delusion_vial", "\uE109");
        map.put("psycho_mode", "\uE10A");
        map.put("bat", "\uE10B");
        map.put("blowgun", "\uE10C");
        map.put("body_bag", "\uE10D");
        map.put("crowbar", "\uE10E");
        map.put("dream_imprint", "\uE10F");
        map.put("firecracker", "\uE200");
        map.put("grenade", "\uE201");
        map.put("hunting_knife", "\uE202");
        map.put("knockout_drug", "\uE203");
        map.put("medical_kit", "\uE204");
        map.put("note", "\uE205");
        map.put("pan", "\uE206");
        map.put("pill", "\uE207");
        map.put("poison_injector", "\uE208");
        map.put("poison_vial", "\uE209");
        map.put("sulfuric_acid_barrel", "\uE210");
        map.put("coin", "\uE211");
        map.put("killer", "\uE212");
        map.put("civilian", "\uE213");
        map.put("neutral", "\uE214");
        map.put("scorpion", "\uE217");
        map.put("blackout", "\uE218");
        map.put("phone", "\uE219");
        map.put("icon_ability_cooldown_refresh", "\uE21A");
        map.put("icon_potion_effect_refresh", "\uE21B");
        map.put("icon_weapon_cooldown_refresh", "\uE21C");

        map.put("ability_adrenaline", "\uE300");
        map.put("ability_athletic", "\uE301");
        map.put("ability_autopsy", "\uE302");
        map.put("ability_avarice", "\uE303");
        map.put("ability_cannibal", "\uE304");
        map.put("ability_clean", "\uE305");
        map.put("ability_imprint", "\uE306");
        map.put("ability_instinct", "\uE307");
        map.put("ability_invisibility", "\uE308");
        map.put("ability_judgement", "\uE309");
        map.put("ability_last_words", "\uE310");
        map.put("ability_morph", "\uE311");
        map.put("ability_nemesis", "\uE312");
        map.put("ability_psychosis", "\uE313");
        map.put("ability_question", "\uE314");
        map.put("ability_recall", "\uE315");
        map.put("ability_revive", "\uE316");
        map.put("ability_sense", "\uE317");
        map.put("ability_starstruck", "\uE318");
        map.put("ability_swap", "\uE319");
        map.put("ability_time", "\uE321");
        map.put("ability_undercover", "\uE322");
        map.put("ability_voodoo", "\uE323");

        map.put("adrenaline", "\uE300");
        map.put("athletic", "\uE301");
        map.put("autopsy", "\uE302");
        map.put("avarice", "\uE303");
        map.put("cannibal", "\uE304");
        map.put("clean", "\uE305");
        map.put("imprint", "\uE306");
        map.put("instinct", "\uE307");
        map.put("invisibility", "\uE308");
        map.put("judgement", "\uE309");
        map.put("last_words", "\uE310");
        map.put("morph", "\uE311");
        map.put("nemesis", "\uE312");
        map.put("psychosis", "\uE313");
        map.put("question", "\uE314");
        map.put("recall", "\uE315");
        map.put("revive", "\uE316");
        map.put("sense", "\uE317");
        map.put("starstruck", "\uE318");
        map.put("swap", "\uE319");
        map.put("time", "\uE321");
        map.put("undercover", "\uE322");
        map.put("voodoo", "\uE323");

        ICONS = Map.copyOf(map);
    }

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
