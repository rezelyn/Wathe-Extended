package cat.rezelyn.watheextended.client.screen;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;

public final class ScreenUtils {

    private ScreenUtils() {
    }

    public static final Map<String, String> ICONS;
    public static final Style ICON_STYLE = Style.EMPTY
            .withFont(Identifier.of("minecraft", "default"))
            .withColor(Formatting.WHITE)
            .withBold(false)
            .withItalic(false)
            .withUnderline(false)
            .withStrikethrough(false)
            .withObfuscated(false);

    static {
        Map<String, String> map = new HashMap<>();
        // misc
        map.put("enabled", "\uE400");
        map.put("disabled", "\uE401");

        // items
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

        // abilities
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

    public static Text icon(String name) {
        String icon = ICONS.get(name);
        if (icon == null) return Text.empty();
        return Text.literal(icon).setStyle(ICON_STYLE);
    }

    public static boolean hasIcon(String name) {
        return ICONS.containsKey(name);
    }

    public static boolean isBlacklisted(String id, Set<String> blacklist) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return blacklist.contains(local);
    }

    public static void stage(BiConsumer<String, Screen> command, Screen parent, String key, Object value) {
        command.accept(key + " " + value, parent);
    }

    public static FloatFieldControllerBuilder floatController(Option<Float> option) {
        return FloatFieldControllerBuilder.create(option).formatValue(value -> Text.literal(format(value)));
    }

    public static String format(double v) {
        String string = new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        string = string.replaceAll("0+$", "").replaceAll("\\.$", "");
        return string;
    }

    public static String format(float v) {
        String string = new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
        string = string.replaceAll("0+$", "").replaceAll("\\.$", "");
        return string;
    }

    public static String boxToArgs(@Nullable Box box) {
        if (box == null) return "0 0 0 0 0 0";
        return format(box.minX) + " " + format(box.minY) + " " + format(box.minZ) + " " + format(box.maxX) + " " + format(box.maxY) + " " + format(box.maxZ);
    }

    public static String vec3iToArgs(@Nullable Vec3i vec) {
        if (vec == null) return "0 0 0";
        return vec.getX() + " " + vec.getY() + " " + vec.getZ();
    }

    public static String posToArgs(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        return format(pos.pos.x) + " " + format(pos.pos.y) + " " + format(pos.pos.z) + " " + format(pos.yaw) + " " + format(pos.pitch);
    }
}
