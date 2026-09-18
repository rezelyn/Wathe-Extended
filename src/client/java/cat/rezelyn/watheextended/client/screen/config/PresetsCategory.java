package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.client.screen.PresetSaveScreen;
import cat.rezelyn.watheextended.game.PresetManager;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

public final class PresetsCategory {

    private PresetsCategory() {
    }

    public static ConfigCategory build(Screen parent, List<PresetManager.PresetMetadata> presets) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.presets"))
                .tooltip(Text.translatable("gui.watheextended.config.category.presets.tooltip"));

        builder.option(ButtonOption.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.presets.save"))
                .text(ScreenUtils.icon("save"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.presets.save.desc")))
                .action(screen -> MinecraftClient.getInstance().setScreen(new PresetSaveScreen(screen)))
                .build());
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
                    .description(OptionDescription.of(Text.literal(metadataText(preset))))
                    .collapsed(true);
            group.option(LabelOption.create(Text.literal(preset.description().isBlank()
                    ? "No description"
                    : preset.description())));
            group.option(LabelOption.create(Text.translatable(
                    "gui.watheextended.config.category.presets.author", preset.authorName())));
            group.option(ButtonOption.createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.presets.load"))
                    .text(ScreenUtils.icon("load"))
                    .action(screen -> ConfigScreen.requestPresetLoad(preset.id()))
                    .build());
            group.option(ButtonOption.createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.presets.delete"))
                    .text(ScreenUtils.icon("delete"))
                    .action(screen -> ConfigScreen.confirmPresetDelete(preset.id(), preset.name(), screen))
                    .build());
            builder.group(group.build());
        }

        return builder.build();
    }

    private static String metadataText(PresetManager.PresetMetadata preset) {
        return "Author: " + preset.authorName() + "\nUpdated: " + preset.updatedAt();
    }
}
