package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.client.screen.PresetSaveScreen;
import cat.rezelyn.watheextended.game.PresetManager;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.RolesDisplay;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class PresetsCategory {

    private PresetsCategory() {
    }

    public static ConfigCategory build(Screen parent, List<PresetManager.PresetMetadata> presets) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.presets"))
                .tooltip(Text.translatable("gui.watheextended.config.category.presets.tooltip"));
        /// SAVE NEW PRESET
        builder.option(ButtonOption.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.presets.save"))
                .text(ScreenUtils.icon("save"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.presets.save.desc")))
                .action(screen -> MinecraftClient.getInstance().setScreen(new PresetSaveScreen(screen)))
                .build());
        /// REFRESH PRESET LIST
        builder.option(ButtonOption.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.presets.refresh"))
                .text(ScreenUtils.icon("refresh"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.presets.refresh.desc")))
                .action(screen -> ConfigScreen.requestPresetList())
                .build());

        if (presets.isEmpty()) {
            builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.presets.empty")
                    .styled(style -> style.withColor(0xAAAAAA))));
            return builder.build();
        }

        for (PresetManager.PresetMetadata preset : presets) {
            OptionGroup.Builder group = OptionGroup.createBuilder()
                    .name(Text.literal(preset.name()))
                    .description(OptionDescription.of(metadataText(preset)))
                    .collapsed(true);
            /// LOAD PRESET
            group.option(ButtonOption.createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.presets.load").styled(style -> style.withColor(Formatting.GREEN)))
                    .text(ScreenUtils.icon("load"))
                    .action(screen -> ConfigScreen.requestPresetLoad(preset.id()))
                    .build());
            /// DELETE PRESET
            group.option(ButtonOption.createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.presets.delete").styled(style -> style.withColor(Formatting.RED)))
                    .text(ScreenUtils.icon("delete"))
                    .action(screen -> ConfigScreen.confirmPresetDelete(preset.id(), preset.name(), screen))
                    .build());
            /// OVERRIDE PRESET
            group.option(ButtonOption.createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.presets.override").styled(style -> style.withColor(Formatting.YELLOW)))
                    .text(ScreenUtils.icon("override"))
                    .action(screen -> ConfigScreen.confirmPresetOverride(preset.id(), preset.name(), screen))
                    .build());
            builder.group(group.build());
        }

        return builder.build();
    }

    private static Text metadataText(PresetManager.PresetMetadata preset) {
        MutableText text = colored("ID: " + preset.id(), Formatting.DARK_GRAY);
        text.append(colored("\n\nCreated by: ", Formatting.GRAY)).append(colored(preset.authorName(), Formatting.WHITE))
                .append(colored("\nCreated at: ", Formatting.GRAY)).append(colored(preset.createdAt(), Formatting.WHITE))
                .append(colored("\nUpdated at: ", Formatting.GRAY)).append(colored(preset.updatedAt(), Formatting.WHITE))
                .append(Text.literal("\n\n" + (preset.description().isBlank() ? "No description" : preset.description().formatted(Formatting.WHITE))));

        if (preset.config().isEmpty()) {
            return text.append(colored("\nhow?", Formatting.DARK_RED).styled(style -> style.withBold(true)));
        }

        Map<String, String> options = new LinkedHashMap<>(preset.config());
        appendStateSection(text, "Roles", options.get("hml.disabled"), RolesDisplay.get().values(), RolesDisplay.RoleDisplay::id, RolesDisplay.RoleDisplay::display, RolesDisplay.RoleDisplay::color);
        appendStateSection(text, "Modifiers", options.get("hml.disabledModifiers"), ModifiersDisplay.get().values(), ModifiersDisplay.ModifierDisplay::id, ModifiersDisplay.ModifierDisplay::display, ModifiersDisplay.ModifierDisplay::color);
        options.remove("hml.disabled");
        options.remove("hml.disabledModifiers");

        return text;
    }

    private static final Set<String> ROLES_DENYLIST = Set.of(
            "killer",
            "civilian",
            "vigilante",
            "discovery_civilian",
            "loose_end",
            "secret_killer"
    );

    private static boolean localIdIn(String id, Set<String> denylist) {
        int colon = id.indexOf(':');
        return denylist.contains(colon >= 0 ? id.substring(colon + 1) : id);
    }

    private static boolean isDenied(String id) {
        return localIdIn(id, ROLES_DENYLIST);
    }

    private static <T> void appendStateSection(MutableText text, String title, String disabledValue, Collection<T> items, Function<T, String> idOf, Function<T, Text> displayOf, Function<T, Integer> colorOf) {
        Set<String> disabled = commaValues(disabledValue);
        text.append(colored("\n\n" + title, 0xFFAA00));
        items.stream()
            .filter(item -> !isDenied(idOf.apply(item)))
            .sorted(Comparator.<T, String>comparing(item -> displayOf.apply(item).getString(), String.CASE_INSENSITIVE_ORDER))
            .forEach(item -> appendStateLine(text, displayOf.apply(item), colorOf.apply(item), !disabled.contains(idOf.apply(item))));
    }

    private static void appendStateLine(MutableText text, Text display, int color, boolean enabled) {
        text.append(Text.literal("\n  "))
                .append(ScreenUtils.icon(enabled ? "enabled" : "disabled"))
                .append(Text.literal(" "))
                .append(display.copy().styled(style -> style.withColor(color)));
    }

    private static Set<String> commaValues(String value) {
        Set<String> result = new HashSet<>();
        if (value == null || value.isBlank()) return result;
        for (String item : value.split(",")) {
            if (!item.isBlank()) result.add(item.trim());
        }
        return result;
    }

    private static MutableText colored(String s, Formatting color) {
        return Text.literal(s).styled(style -> style.withColor(color));
    }

    private static MutableText colored(String s, int color) {
        return Text.literal(s).styled(style -> style.withColor(color));
    }
}
