package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.ModifiersId;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.function.BiConsumer;

public final class ModifiersCategory {

    private ModifiersCategory() {}

    public static ConfigCategory build(Screen parent, Map<String, Boolean> pendingState, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers"))
                .tooltip(Text.translatable("gui.watheextended.config.category.modifiers.tooltip"));

        try {
            if (ModifiersId.get().isEmpty()) {
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.modifiers.empty").styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, ModifiersDisplay.ModifierDisplay> modifierName = ModifiersDisplay.get();

                for (String id : ModifiersId.get()) {
                    ModifiersDisplay.ModifierDisplay display = modifierName.get(id);
                    Text label = display != null ? display.display().copy().styled(style -> style.withColor(display.color())) : Text.literal(ModifiersDisplay.prettyName(id));

                    boolean state = pendingState.containsKey(id) ? pendingState.get(id) : !ConfigHelper.getDisabledModifiers().contains(id);

                    OptionGroup.Builder group = OptionGroup.createBuilder().name(label);
                    group.option(Option.<Boolean>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.enabled"))
                            .description(OptionDescription.of(Text.literal(id).styled(style -> style.withColor(0x505050))))
                            .binding(state, () -> pendingState.containsKey(id) ? pendingState.get(id) : !ConfigHelper.getDisabledModifiers().contains(id), enabled -> {
                                pendingState.put(id, enabled);
                            })
                            .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                            .build());

                    modifierOptions(id, group, parent, sendCommand);
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.modifiers.error").styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    private static void modifierOptions(String id, OptionGroup.Builder builder, Screen parent, BiConsumer<String, Screen> sendCommand) {
        switch (id) {
            case "watheextended:introverted" -> {
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowdcount"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowdcount.desc")))
                        .binding(3, () -> ClientConfig.getInt("watheextended.introverted.crowdCount", 3), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.introverted.crowdCount", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowdrange"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowdrange.desc")))
                        .binding(5.0f, () -> ClientConfig.getFloat("watheextended.introverted.crowdRange", 5.0f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.introverted.crowdRange", value))
                        .controller(ScreenUtils::floatController).build());
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowddrainmultiplier"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.crowddrainmultiplier.desc")))
                        .binding(2.0f, () -> ClientConfig.getFloat("watheextended.introverted.crowdDrainMultiplier", 2.0f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.introverted.crowdDrainMultiplier", value))
                        .controller(ScreenUtils::floatController).build());
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.alonedrainmultiplier"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.introverted.alonedrainmultiplier.desc")))
                        .binding(0.5f, () -> ClientConfig.getFloat("watheextended.introverted.aloneDrainMultiplier", 0.5f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.introverted.aloneDrainMultiplier", value))
                        .controller(ScreenUtils::floatController).build());
            }
            case "watheextended:taxed" -> {
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.coinreduction"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.coinreduction.desc")))
                        .binding(0.50f, () -> ClientConfig.getFloat("watheextended.taxed.coinReduction", 0.50f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.taxed.coinReduction", value))
                        .controller(ScreenUtils::floatController).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.killthreshold"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.killthreshold.desc")))
                        .binding(1, () -> ClientConfig.getInt("watheextended.taxed.killThreshold", 1), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.taxed.killThreshold", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.killwindowseconds"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.taxed.killwindowseconds.desc")))
                        .binding(60, () -> ClientConfig.getInt("watheextended.taxed.killWindowSeconds", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.taxed.killWindowSeconds", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            case "watheextended:adaptive" -> {
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.adaptive.penaltyreduction"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.adaptive.penaltyreduction.desc")))
                        .binding(0.25f, () -> ClientConfig.getFloat("watheextended.adaptive.penaltyReduction", 0.25f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.adaptive.penaltyReduction", value))
                        .controller(ScreenUtils::floatController).build());
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.adaptive.bonusmultiplier"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.adaptive.bonusmultiplier.desc")))
                        .binding(0.50f, () -> ClientConfig.getFloat("watheextended.adaptive.bonusMultiplier", 0.50f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.adaptive.bonusMultiplier", value))
                        .controller(ScreenUtils::floatController).build());
            }
            case "noellesroles:guesser" -> {
                if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.guesser.allowcivillian"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.guesser.allowcivillian.desc")))
                        .binding(false, cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getAllowCivillianGuessers, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.allowCivillianGuessers", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                builder.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.guesser.wrongguessmode"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.guesser.wrongguessmode.desc")))
                        .binding("death", cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getGuesserDiesAfterIncorrectGuess, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.guesserDiesAfterIncorrectGuess", value))
                        .controller(option -> CyclingListControllerBuilder.create(option).values(java.util.List.of("none", "death", "explode")).formatValue(value -> Text.literal(value.toUpperCase()))).build());
            }
            case "stupid_express:lovers" -> {
                if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) return;
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.forbidden"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.forbidden.desc")))
                        .binding(false, () -> ClientConfig.getBool("watheextended.forbiddenLovers", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.forbiddenLovers", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.forbidden.chance"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.forbidden.chance.desc")))
                        .binding(0.25f, () -> ClientConfig.getFloat("watheextended.forbiddenLovers.chance", 0.25f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.forbiddenLovers.chance", value))
                        .controller(ScreenUtils::floatController).build());
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.knowimmediately"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.knowimmediately.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getLoversKnowImmediately, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.loversKnowImmediately", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.winwithkillers"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.winwithkillers.desc")))
                        .binding(false, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getLoversWinWithKillers, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.loversWinWithKillers", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.winwithcivilians"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.winwithcivilians.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getLoversWinWithCivilians, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.loversWinWithCivilians", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.glowtoeachother"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.lovers.glowtoeachother.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getLoversGlowToEachother, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.loversGlowToEachother", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
            }
            case "starexpress:allergic" -> {
                if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) return;
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.nothingchance"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.nothingchance.desc")))
                        .binding(3, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getAllergicNothingChance, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.nothingChance", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.instinctchance"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.instinctchance.desc")))
                        .binding(1, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getAllergicInstinctChance, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.instinctChance", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.armorchance"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.armorchance.desc")))
                        .binding(1, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getAllergicArmorChance, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.armorChance", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.poisonchance"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.poisonchance.desc")))
                        .binding(1, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getAllergicPoisonChance, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.poisonChance", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.instinctduration"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.modifiers.opt.allergic.instinctduration.desc")))
                        .binding(3, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getAllergicInstinctDuration, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.instinctDuration", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
        }
    }
}
