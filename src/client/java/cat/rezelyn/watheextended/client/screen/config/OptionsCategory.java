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

import java.util.Arrays;
import java.util.function.BiConsumer;

import static cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableNoellesRolesModify;

public final class OptionsCategory {

    private OptionsCategory() {}

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options"))
                .tooltip(Text.translatable("gui.watheextended.config.category.options.tooltip"));

        builder.group(gamerulesGroup(parent, sendCommand));
        builder.group(watheOptionsGroup(parent, sendCommand));
        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            builder.group(extraOptionsGroup(parent, sendCommand));
        }

        return builder.build();
    }

    private static OptionGroup gamerulesGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.tooltip")))
                .collapsed(false);

        World client = MinecraftClient.getInstance().world;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions.desc")))
                .binding(ClientConfig.getBool("watheextended.playerCollisions", true), () -> ClientConfig.getBool("watheextended.playerCollisions", true), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.playerCollisions", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.suppressabilityvfxsfx"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.suppressabilityvfxsfx.desc")))
                .binding(ClientConfig.getBool("watheextended.suppressAbilityVfxSfx", false), () -> ClientConfig.getBool("watheextended.suppressAbilityVfxSfx", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.suppressAbilityVfxSfx", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());

        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis.desc")))
                    .binding(false, () -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.getInsanePlayersSeeMorphs(null), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.insanePlayersSeeMorphs", value))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                    .build());
        }

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.laststand"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.laststand.desc")))
                .binding(ClientConfig.getBool("watheextended.lastStand.enabled", false), () -> ClientConfig.getBool("watheextended.lastStand.enabled", false), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lastStand.enabled", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.laststandcooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.laststandcooldown.desc")))
                .binding(30, () -> ClientConfig.getInt("watheextended.lastStand.cooldown", 30), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lastStand.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            final boolean startSafeTimeDefault = cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableStartSafeTime(client);

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime.desc")))
                    .binding(startSafeTimeDefault, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableStartSafeTime(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableStartSafeTime", value))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown.desc")))
                    .binding(30, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getStartingCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.StartingCooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    /**
     * TODO: Refactor lines <b>211-246</b> into {@link ItemsCategory}
     */
    private static OptionGroup watheOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        // Global
        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.label.global").styled(style -> style.withColor(0xAAAAAA))));
        /// KILL TIME INCREASE
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.killincreasetime"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.killincreasetime.desc")))
                .binding(60, () -> ClientConfig.getInt("watheextended.killIncreaseTime", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.killIncreaseTime", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// BACKFIRE CHANCE
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire.desc")))
                .binding(GameComponents.getBackfire(world), () -> GameComponents.getBackfire(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set backfire " + (value / 100f), parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.isLoaded()) {
            final String[] punishmentModes = cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.getPunishmentModes();
            /// SHOOTER PUNISHEMENT
            group.option(Option.<String>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment.desc")))
                    .binding(cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.getCurrentPunishment(), cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper::getCurrentPunishment, value -> ScreenUtils.stage(sendCommand, parent, "shooterpunishments.currentMode", value))
                    .controller(option -> CyclingListControllerBuilder.create(option).values(Arrays.asList(punishmentModes)).formatValue(value -> {
                        String string = value.replaceAll("([A-Z])", " $1");
                        return Text.literal(Character.toUpperCase(string.charAt(0)) + string.substring(1));
                    }))
                    .build());
        }

        // Roles
        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.roles").styled(style -> style.withColor(0xAAAAAA))));
        /// KILLER DIVIDEND
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.roledividend_killer"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.roledividend_killer.desc")))
                .binding(GameComponents.getKillerDividend(world), () -> GameComponents.getKillerDividend(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set roleDividend killer " + value, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// VIGILANTE DIVIDEND
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.roledividend_vigilante"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.roledividend_vigilante.desc")))
                .binding(GameComponents.getVigilanteDividend(world), () -> GameComponents.getVigilanteDividend(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set roleDividend vigilante " + value, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Modifiers
        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.modifiers").styled(style -> style.withColor(0xAAAAAA))));
        /// MODIFIER MAXIMUM
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.modifiers_maximum"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.modifiers_maximum.desc")))
                .binding(1, ConfigHelper::getModifierMaximum, value -> ScreenUtils.stage(sendCommand, parent, "hml.modifierMaximum", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// MODIFIER MULTIPLIER
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.modifiers_multiplier"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.modifiers_multiplier.desc")))
                .binding(1, ConfigHelper::getModifierMultiplier, value -> ScreenUtils.stage(sendCommand, parent, "hml.modifierMultiplier", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Money
        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.label.money").styled(style -> style.withColor(0xAAAAAA))));
        /// MONEY START - KILLERS
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_killers"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_killers.desc")))
                .binding(100, () -> ClientConfig.getInt("gamerules.moneyStartKillers", 100), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moneyStartKillers", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// MONEY START - CIVILIANS
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_civilians"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_civilians.desc")))
                .binding(0, () -> ClientConfig.getInt("gamerules.moneyStartCivilians", 0), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moneyStartCivilians", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// MONEY START - NEUTRALS
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_neutrals"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_start_neutrals.desc")))
                .binding(0, () -> ClientConfig.getInt("gamerules.moneyStartNeutrals", 0), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moneyStartNeutrals", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// MONEY PER KILL
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_per_kill"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.money_per_kill.desc")))
                .binding(100, () -> ClientConfig.getInt("gamerules.moneyPerKill", 100), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moneyPerKill", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Items
        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.label.timers").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.blackout_timer"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.blackout_timer.desc")))
                .binding(5, () -> ClientConfig.getInt("gamerules.blackoutMinDuration", 5), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.blackoutMinDuration", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.blackout_max"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.blackout_max.desc")))
                .binding(90, () -> ClientConfig.getInt("gamerules.blackoutMaxDuration", 90), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.blackoutMaxDuration", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psycho_armour"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psycho_armour.desc")))
                .binding(50, () -> ClientConfig.getInt("gamerules.psychoModeArmour", 50), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.psychoModeArmour", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psycho_timer"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psycho_timer.desc")))
                .binding(5, () -> ClientConfig.getInt("gamerules.psychoTimer", 5), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.psychoTimer", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.firecracker_timer"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.firecracker_timer.desc")))
                .binding(45, () -> ClientConfig.getInt("gamerules.firecrackerTimer", 45), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.firecrackerTimer", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.label.task").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.time_to_first_task"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.time_to_first_task.desc")))
                .binding(9, () -> ClientConfig.getInt("gamerules.timeToFirstTask", 9), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.timeToFirstTask", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.min_task_cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.min_task_cooldown.desc")))
                .binding(10, () -> ClientConfig.getInt("gamerules.minTaskCooldown", 10), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.minTaskCooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.max_task_cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.max_task_cooldown.desc")))
                .binding(60, () -> ClientConfig.getInt("gamerules.maxTaskCooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.maxTaskCooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.sleep_task_duration"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.sleep_task_duration.desc")))
                .binding(120, () -> ClientConfig.getInt("gamerules.sleepTaskDuration", 120), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.sleepTaskDuration", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.outside_task_duration"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.outside_task_duration.desc")))
                .binding(300, () -> ClientConfig.getInt("gamerules.outsideTaskDuration", 300), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.outsideTaskDuration", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.label.mood").styled(style -> style.withColor(0xAAAAAA))));

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.mood_gain"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.mood_gain.desc")))
                .binding(5, () -> ClientConfig.getInt("gamerules.moodGain", 5), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moodGain", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.mood_drain"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.mood_drain.desc")))
                .binding(3, () -> ClientConfig.getInt("gamerules.moodDrain", 3), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.moodDrain", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options opt.mid_mood_threshold"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.mid_mood_threshold.desc")))
                .binding(30, () -> ClientConfig.getInt("gamerules.midMoodThreshold", 30), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.midMoodThreshold", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.depressive_mood_threshold"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.depressive_mood_threshold.desc")))
                .binding(15, () -> ClientConfig.getInt("gamerules.depressiveMoodThreshold", 15), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.depressiveMoodThreshold", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psychosis_chance"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psychosis_chance.desc")))
                .binding(30, () -> ClientConfig.getInt("gamerules.itemPsychosisChance", 30), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.itemPsychosisChance", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psychosis_enabled"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.psychosis_enabled.desc")))
                .binding(true, () -> ClientConfig.getBool("gamerules.itemPsychosisChanceEnabled", true), value -> ScreenUtils.stage(sendCommand, parent, "gamerules.itemPsychosisChanceEnabled", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());

        return group.build();
    }

    private static OptionGroup extraOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;
        final boolean watheTweaksEnabled = cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableWatheModify(world);
        final boolean noellesRolesTweaksEnabled = getEnableNoellesRolesModify(world);

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.wathetweaks"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.wathetweaks.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableWatheModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableWatheModify", value))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialcivilianincome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialcivilianincome.desc")))
                .binding(0, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialCivilianIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialCivilianIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialnnetralincome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialnnetralincome.desc")))
                .binding(0, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialNeutralIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialNeutralIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialkillerncome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.initialkillerncome.desc")))
                .binding(100, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getInitialKillerIncome(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.InitialKillerIncome", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.increasemoneywhenkilll"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.increasemoneywhenkilll.desc")))
                .binding(100, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getIncreaseMoneyWhenKill(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.IncreaseMoneyWhenKill", value))
                .controller(IntegerFieldControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.preventkillerdroprevolver"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.preventkillerdroprevolver.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getPreventKillerDropRevolver(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.PreventKillerDropRevolver", value))
                .controller(TickBoxControllerBuilder::create)
                .available(watheTweaksEnabled)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.noellestweaks"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.noellestweaks.desc")))
                .binding(false, () -> getEnableNoellesRolesModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.EnableNoellesRolesModify", value))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.conductorinstinct"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.conductorinstinct.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getConductorInstinctModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.ConductorInstinctModify", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .available(noellesRolesTweaksEnabled)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.coronerinstinct"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.extra_options.opt.coronerinstinct.desc")))
                .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getCoronerInstinctModify(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.CoronerInstinctModify", value))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .available(noellesRolesTweaksEnabled)
                .build());

        return group.build();
    }
}
