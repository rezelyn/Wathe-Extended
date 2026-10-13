package cat.rezelyn.watheextended.client.screen;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public final class ScreenUtils {

  public static final Map<String, String> ICONS;
  public static final Style ICON_STYLE =
      Style.EMPTY
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
    map.put("save", "\uE500");
    map.put("refresh", "\uE501");
    map.put("load", "\uE502");
    map.put("delete", "\uE503");
    map.put("mood", "\uE504");
    map.put("override", "\uE505");

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
    map.put("wrench", "\uE21D");
    map.put("capture_device", "\uE21E");
    map.put("icon_power_restoration", "\uE21F");

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
    map.put("infection", "\uE324");

    ICONS = Map.copyOf(map);
  }

  private ScreenUtils() {}

  public static Text icon(String name) {
    String icon = ICONS.get(name);
    if (icon == null) return Text.empty();
    return Text.literal(icon).setStyle(ICON_STYLE);
  }

  public static boolean hasIcon(String name) {
    return ICONS.containsKey(name);
  }

  public static boolean isDenied(String id, Set<String> denylist) {
    int colon = id.indexOf(':');
    String local = colon >= 0 ? id.substring(colon + 1) : id;
    return denylist.contains(local);
  }

  public static void stage(
      BiConsumer<String, Screen> command, Screen parent, String key, Object value) {
    command.accept(key + " " + value, parent);
  }

  public static Option<String> textField(Text name, Text description, Consumer<String> onValue) {
    return Option.<String>createBuilder()
        .name(name)
        .description(OptionDescription.of(description))
        .binding("", () -> "", onValue)
        .controller(StringControllerBuilder::create)
        .build();
  }

  public static ButtonOption deleteButton(
      Text name, Text description, Text confirmTitle, Text confirmMessage, String command) {
    return ButtonOption.createBuilder()
        .name(name)
        .text(icon("delete"))
        .description(OptionDescription.of(description))
        .action(
            (screen, button) ->
                ConfigScreen.confirmCommand(command, confirmTitle, confirmMessage, screen))
        .build();
  }

  public static String areaLabel(String id, @Nullable String displayName) {
    return displayName == null || displayName.isBlank() ? id : displayName;
  }

  public interface AreaSaveCommand {
    String build(String name, Box box);
  }

  // Collects the name and both corners from two text fields and stages the save command once both
  // are filled in and valid
  public static final class NewAreaDraft {
    private final Screen parent;
    private final BiConsumer<String, Screen> sendCommand;
    private final AreaSaveCommand command;
    private String name = "";
    private Box box;

    public NewAreaDraft(
        Screen parent, BiConsumer<String, Screen> sendCommand, AreaSaveCommand command) {
      this.parent = parent;
      this.sendCommand = sendCommand;
      this.command = command;
    }

    public Option<String> nameField(Text label, Text description) {
      return textField(
          label,
          description,
          value -> {
            name = value.trim();
            stageIfComplete();
          });
    }

    // Takes both corners at once as x1 y1 z1 x2 y2 z2
    public Option<String> boxField(Text label, Text description) {
      return textField(
          label,
          description,
          value -> {
            int[] c = parseInts(value, 6);
            box = c == null ? null : new Box(c[0], c[1], c[2], c[3], c[4], c[5]);
            stageIfComplete();
          });
    }

    private void stageIfComplete() {
      if (name.isEmpty() || box == null) return;
      sendCommand.accept(command.build(name, box), parent);
    }
  }

  public static FloatFieldControllerBuilder floatController(Option<Float> option) {
    return FloatFieldControllerBuilder.create(option)
        .formatValue(value -> Text.literal(format(value)));
  }

  public static String format(double v) {
    String string =
        new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
    string = string.replaceAll("0+$", "").replaceAll("\\.$", "");
    return string;
  }

  public static String format(float v) {
    String string =
        new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
    string = string.replaceAll("0+$", "").replaceAll("\\.$", "");
    return string;
  }

  public static String boxToArgs(@Nullable Box box) {
    if (box == null) return "0 0 0 0 0 0";
    return format(box.minX)
        + " "
        + format(box.minY)
        + " "
        + format(box.minZ)
        + " "
        + format(box.maxX)
        + " "
        + format(box.maxY)
        + " "
        + format(box.maxZ);
  }

  public static int @Nullable [] parseInts(String value, int count) {
    String[] parts = value.trim().split("[\\s,]+");
    if (parts.length != count) return null;
    int[] out = new int[count];
    try {
      for (int i = 0; i < count; i++) out[i] = Integer.parseInt(parts[i]);
    } catch (NumberFormatException e) {
      return null;
    }
    return out;
  }

  public static String vec3iToArgs(@Nullable Vec3i vec) {
    if (vec == null) return "0 0 0";
    return vec.getX() + " " + vec.getY() + " " + vec.getZ();
  }

  public static String posToArgs(@Nullable MapVariablesWorldComponent.PosWithOrientation pos) {
    if (pos == null) return "0 0 0 0 0";
    return format(pos.pos.x)
        + " "
        + format(pos.pos.y)
        + " "
        + format(pos.pos.z)
        + " "
        + format(pos.yaw)
        + " "
        + format(pos.pitch);
  }
}
