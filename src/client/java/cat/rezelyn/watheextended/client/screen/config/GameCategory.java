package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.GameComponents;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

import static cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableNoellesRolesModify;

public final class GameCategory {

    private GameCategory() {}

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game"))
                .tooltip(Text.translatable("gui.watheextended.config.category.game.tooltip"));

        builder.group(gamerulesGroup(parent, sendCommand));
        builder.group(watheOptionsGroup(parent, sendCommand));
        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            builder.group(extraOptionsGroup(parent, sendCommand));
        }

        return builder.build();
    }

    private static OptionGroup gamerulesGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.tooltip")))
                .collapsed(false);

        World client = MinecraftClient.getInstance().world;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.collisions"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.collisions.desc")))
                .binding(ClientConfig.getBool("watheextended.playerCollisions", true), () -> ClientConfig.getBool("watheextended.playerCollisions", true), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.playerCollisions", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.suppressabilityvfxsfx"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.suppressabilityvfxsfx.desc")))
                .binding(ClientConfig.getBool("watheextended.suppressAbilityVfxSfx", false), () -> ClientConfig.getBool("watheextended.suppressAbilityVfxSfx", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.suppressAbilityVfxSfx", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());

        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.morphpsychosis"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.morphpsychosis.desc")))
                    .binding(false, () -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.getInsanePlayersSeeMorphs(null), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.insanePlayersSeeMorphs", value))
                    .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                    .build());
        }

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.game.group.gamerules.label.laststand").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.enabled"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.laststand.desc")))
                .binding(ClientConfig.getBool("watheextended.lastStand.enabled", false), () -> ClientConfig.getBool("watheextended.lastStand.enabled", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lastStand.enabled", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.laststandcooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.laststandcooldown.desc")))
                .binding(30, () -> ClientConfig.getInt("watheextended.lastStand.cooldown", 30), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lastStand.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.instinct").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctcapacity"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctcapacity.desc")))
                .binding(100.0f, () -> ClientConfig.getFloat("watheextended.instinct.capacity", 100.0f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.instinct.capacity", value))
                .controller(option -> FloatSliderControllerBuilder.create(option)
                        .range(0.0f, 100.0f)
                        .step(1.0f)
                        .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%.0f%%", value))))
                .build());
        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctdrainrate"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctdrainrate.desc")))
                .binding(30.0f, () -> ClientConfig.getFloat("watheextended.instinct.drainRate", 30.0f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.instinct.drainRate", value))
                .controller(option -> FloatSliderControllerBuilder.create(option)
                        .range(0.0f, 100.0f)
                        .step(1.0f)
                        .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%.0f%%", value))))
                .build());
        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctreloadrate"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.instinctreloadrate.desc")))
                .binding(10.0f, () -> ClientConfig.getFloat("watheextended.instinct.reloadRate", 10.0f), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.instinct.reloadRate", value))
                .controller(option -> FloatSliderControllerBuilder.create(option)
                        .range(0.0f, 100.0f)
                        .step(1.0f)
                        .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%.0f%%", value))))
                .build());

        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            final boolean startSafeTimeDefault = cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableStartSafeTime(client);
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.game.group.gamerules.label.safepreptime").styled(style -> style.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.enabled"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.safepreptime.desc")))
                    .binding(startSafeTimeDefault, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableStartSafeTime(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableStartSafeTime", value))
                    .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.safeprepcooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.safeprepcooldown.desc")))
                    .binding(30, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getStartingCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.StartingCooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup watheOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.label.global").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.killincreasetime"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.gamerules.opt.killincreasetime.desc")))
                .binding(60, () -> ClientConfig.getInt("watheextended.killIncreaseTime", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.killIncreaseTime", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.shooterpunishment"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.shooterpunishment.desc")))
                .binding("DEFAULT", () -> ClientConfig.getString("watheextended.shootInnocentPunishmentMode", "DEFAULT"), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.shootInnocentPunishmentMode", value))
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(java.util.List.of("DEFAULT", "PREVENT_PICKUP", "KILL_SHOOTER", "KILL_BOTH"))
                        .formatValue(value -> Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.shooterpunishment." + value.toLowerCase(java.util.Locale.ROOT))))
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.backfire"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.backfire.desc")))
                .binding(GameComponents.getBackfire(world), () -> GameComponents.getBackfire(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set backfireChancePerInnocentKill " + (value / 100f), parent))
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 100)
                        .step(1)
                        .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%d%%", value))))
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.roles").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.roledividend_killer"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.roledividend_killer.desc")))
                .binding(GameComponents.getKillerDividend(world), () -> GameComponents.getKillerDividend(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set roleDividend killer " + value, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.roledividend_vigilante"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.roledividend_vigilante.desc")))
                .binding(GameComponents.getVigilanteDividend(world), () -> GameComponents.getVigilanteDividend(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set roleDividend vigilante " + value, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.modifiers").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.modifiers_maximum"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.modifiers_maximum.desc")))
                .binding(1, ConfigHelper::getModifierMaximum, value -> ScreenUtils.stage(sendCommand, parent, "hml.modifierMaximum", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.modifiers_multiplier"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.modifiers_multiplier.desc")))
                .binding(1, ConfigHelper::getModifierMultiplier, value -> ScreenUtils.stage(sendCommand, parent, "hml.modifierMultiplier", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.game.group.gamerules.label.economy").styled(style -> style.withColor(0xAAAAAA))));
        final boolean adjustPassiveIncomeEnabled = ClientConfig.getBool("watheextended.balance.adjustPassiveIncome", false);
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.base_passive_income"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.base_passive_income.desc")))
                .binding(5, () -> ClientConfig.getInt("watheextended.balance.basePassiveIncome", 5), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.balance.basePassiveIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.adjust_passive_income"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.adjust_passive_income.desc")))
                .binding(false, () -> ClientConfig.getBool("watheextended.balance.adjustPassiveIncome", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.balance.adjustPassiveIncome", value))
                .controller(option -> BooleanControllerBuilder.create(option).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.max_passive_income_distance"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.max_passive_income_distance.desc")))
                .binding(10, () -> ClientConfig.getInt("watheextended.balance.maxPassiveIncomeDistance", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.balance.maxPassiveIncomeDistance", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(adjustPassiveIncomeEnabled)
                .build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.min_passive_income"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.wathe_options.opt.min_passive_income.desc")))
                .binding(0, () -> ClientConfig.getInt("watheextended.balance.minPassiveIncome", 0), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.balance.minPassiveIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(adjustPassiveIncomeEnabled)
                .build());

         return group.build();
    }

    private static OptionGroup extraOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;
        final boolean watheTweaksEnabled = cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableWatheModify(world);
        final boolean noellesRolesTweaksEnabled = getEnableNoellesRolesModify(world);

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.wathetweaks"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.wathetweaks.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableWatheModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableWatheModify", value))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialcivilianincome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialcivilianincome.desc")))
                .binding(0, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialCivilianIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialCivilianIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialnnetralincome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialnnetralincome.desc")))
                .binding(0, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialNeutralIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialNeutralIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialkillerncome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.initialkillerncome.desc")))
                .binding(100, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialKillerIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialKillerIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.increasemoneywhenkilll"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.increasemoneywhenkilll.desc")))
                .binding(100, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getIncreaseMoneyWhenKill(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.IncreaseMoneyWhenKill", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.noellestweaks"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.noellestweaks.desc")))
                .binding(false, () -> getEnableNoellesRolesModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableNoellesRolesModify", value))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.conductorinstinct"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.conductorinstinct.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getConductorInstinctModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.ConductorInstinctModify", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .available(noellesRolesTweaksEnabled)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.coronerinstinct"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.game.group.extra_options.opt.coronerinstinct.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getCoronerInstinctModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.CoronerInstinctModify", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .available(noellesRolesTweaksEnabled)
                .build());

        return group.build();
    }
}
