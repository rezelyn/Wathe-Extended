package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.cca.GameComponents;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.function.BiConsumer;

public final class OptionsCategory {

    private OptionsCategory() {
    }

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options"))
                .tooltip(Text.translatable("gui.watheextended.config.category.options.tooltip"));

        builder.group(buildGamerulesGroup(parent, sendCommand));
        builder.group(buildWatheOptionsGroup(parent, sendCommand));
        builder.group(buildRolesOptionsGroup(parent, sendCommand));
        builder.group(buildModifiersOptionsGroup(parent, sendCommand));

        return builder.build();
    }

    private static void stage(BiConsumer<String, Screen> cmd, Screen parent, String key, Object value) {
        cmd.accept(key + " " + value, parent);
    }

    private static OptionGroup buildGamerulesGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(ClientConfig.getBool("watheextended.playerCollisions", true),
                        () -> ClientConfig.getBool("watheextended.playerCollisions", true),
                        v -> stage(sendCommand, parent, "watheextended.playerCollisions", v))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            () -> cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.getInsanePlayersSeeMorphs(null),
                            v -> stage(sendCommand, parent, "noellesroles.insanePlayersSeeMorphs", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            final boolean startSafeTimeDefault = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStartSafeTime(world);

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(startSafeTimeDefault,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStartSafeTime(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.EnableStartSafeTime", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(30,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getStartingCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.StartingCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildWatheOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getBackfire(world),
                        () -> GameComponents.getBackfire(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:gameSettings set backfire " + (v / 100f), parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.isLoaded()) {
            final String[] punishmentModes = cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.getPunishmentModes();

            group.option(Option.<String>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.getCurrentPunishment(),
                            cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper::getCurrentPunishment,
                            v -> stage(sendCommand, parent, "shooterpunishments.currentMode", v))
                    .controller(opt -> CyclingListControllerBuilder.create(opt)
                            .values(Arrays.asList(punishmentModes))
                            .formatValue(v -> {
                                String sp = v.replaceAll("([A-Z])", " $1");
                                return Text.literal(Character.toUpperCase(sp.charAt(0)) + sp.substring(1));
                            }))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            final boolean watheTweaksEnabled = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableWatheModify(world);

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.wathetweaks"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.wathetweaks.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableWatheModify(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.EnableWatheModify", v))
                    .controller(TickBoxControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialcivilianincome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialcivilianincome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(0,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialCivilianIncome(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.InitialCivilianIncome", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialnnetralincome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialnnetralincome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(0,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialNeutralIncome(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.InitialNeutralIncome", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialkillerncome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialkillerncome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(100,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialKillerIncome(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.InitialKillerIncome", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.increasemoneywhenkilll"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.increasemoneywhenkilll.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(100,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getIncreaseMoneyWhenKill(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.IncreaseMoneyWhenKill", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.preventkillerdroprevolver"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.preventkillerdroprevolver.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getPreventKillerDropRevolver(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.PreventKillerDropRevolver", v))
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildRolesOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_killer"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_killer.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getKillerDividend(world),
                        () -> GameComponents.getKillerDividend(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:gameSettings set roleDividend killer " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_vigilante"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_vigilante.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getVigilanteDividend(world),
                        () -> GameComponents.getVigilanteDividend(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:gameSettings set roleDividend vigilante " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            final boolean noellesRolesTweaksEnabled = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(world);

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.EnableNoellesRolesModify", v))
                    .controller(TickBoxControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(noellesRolesTweaksEnabled)
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getConductorInstinctModify(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.ConductorInstinctModify", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.coronerinstinct"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.coronerinstinct.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(noellesRolesTweaksEnabled)
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCoronerInstinctModify(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.CoronerInstinctModify", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bellringer").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(200,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.BellringerAbilityPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(120,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.BellringerAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bodymaker").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.BodymakerAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityFakeRole(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.BodymakerAbilityFakeRole", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cleaner").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(200,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.CleanerAbilityPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(150,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.CleanerAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cook").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(250,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCookPanPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.CookPanPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.detective").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(200,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DetectiveAbilityPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DetectiveAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.dreamer").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDreamerInitialItemQuantity(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DreamerInitialItemQuantity", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.drugmaker").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(10,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPlayerLimit(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DrugmakerPlayerLimit", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(50,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerGetCoins(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DrugmakerGetCoins", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(125,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPoisonInjectorPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DrugmakerPoisonInjectorPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(175,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerBlowgunPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.DrugmakerBlowgunPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.hunter").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(125,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.HunterAbilityPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(5,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.HunterAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.judge").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(300,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.JudgeAbilityPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityGlowing(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.JudgeAbilityGlowing", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(180,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.JudgeAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.kidnapper").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(75,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getKidnapperKnockoutDrugPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.KidnapperKnockoutDrugPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.licensedvillain").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(10,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainPlayerLimit(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.LicensedVillainPlayerLimit", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(300,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainRevolverPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.LicensedVillainRevolverPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.physician").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(300,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getPhysicianPillPrice(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.PhysicianPillPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.robot").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(10,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityDuration(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.RobotAbilityDuration", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityCooldown(MinecraftClient.getInstance().world),
                            v -> stage(sendCommand, parent, "kinswathe.RobotAbilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.voodoo").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.nonkillerdeaths"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.nonkillerdeaths.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getVoodooNonKillerDeaths,
                            v -> stage(sendCommand, parent, "noellesroles.voodooNonKillerDeaths", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.shotlikeevil"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.shotlikeevil.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getVoodooShotLikeEvil,
                            v -> stage(sendCommand, parent, "noellesroles.voodooShotLikeEvil", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.conductor").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductor.keycountvisible"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductor.keycountvisible.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(10,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getPlayerCountToMakeConducterKeyVisible,
                            v -> stage(sendCommand, parent, "noellesroles.playerCountToMakeConducterKeyVisible", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bartender").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.maxdefensevials"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.maxdefensevials.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getMaximumDefenseVials,
                            v -> stage(sendCommand, parent, "noellesroles.maximumDefenseVials", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.defensevialsprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.defensevialsprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(100,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getDefenseVialPrice,
                            v -> stage(sendCommand, parent, "noellesroles.defenseVialPrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.trapper").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.trapper.rolemineprice"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.trapper.rolemineprice.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(100,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getRoleMinePrice,
                            v -> stage(sendCommand, parent, "noellesroles.roleMinePrice", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.necromancer").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.necromancer.hasshop"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.necromancer.hasshop.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getNecromancerHasShop,
                            v -> stage(sendCommand, parent, "stupidexpress.necromancerHasShop", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.arsonist").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.arsonist.keepsgamegoing"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.arsonist.keepsgamegoing.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getArsonistKeepsGameGoing,
                            v -> stage(sendCommand, parent, "stupidexpress.arsonistKeepsGameGoing", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.amnesiac").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.bodiesglow"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.bodiesglow.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getBodiesGlowToAmnesiac,
                            v -> stage(sendCommand, parent, "stupidexpress.bodiesGlowToAmnesiac", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.glowsdifferently"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.glowsdifferently.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getAmnesiacGlowsDifferently,
                            v -> stage(sendCommand, parent, "stupidexpress.amnesiacGlowsDifferently", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.starstruck").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskReducesCooldown,
                            v -> stage(sendCommand, parent, "starexpress.taskReducesCooldown", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(5,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskCooldownReduction,
                            v -> stage(sendCommand, parent, "starexpress.taskCooldownReduction", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityCooldown,
                            v -> stage(sendCommand, parent, "starexpress.abilityCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(15,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityDuration,
                            v -> stage(sendCommand, parent, "starexpress.abilityDuration", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityAffectsMovementSpeed,
                            v -> stage(sendCommand, parent, "starexpress.abilityAffectsMovementSpeed", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.12f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityWalkSpeed,
                            v -> stage(sendCommand, parent, "starexpress.abilityWalkSpeed", v))
                    .controller(FloatFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.15f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilitySprintSpeed,
                            v -> stage(sendCommand, parent, "starexpress.abilitySprintSpeed", v))
                    .controller(FloatFieldControllerBuilder::create)
                    .build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.muzzler").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(20,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeCooldown,
                            v -> stage(sendCommand, parent, "starexpress.tapeCooldown", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(60,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerSuffocationTime,
                            v -> stage(sendCommand, parent, "starexpress.suffocationTime", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(5,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearCheckCount,
                            v -> stage(sendCommand, parent, "starexpress.tapeTearCheckCount", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.1f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearMoodChange,
                            v -> stage(sendCommand, parent, "starexpress.tapeTearMoodChange", v))
                    .controller(FloatFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerKillIfCheckedAtZero,
                            v -> stage(sendCommand, parent, "starexpress.killIfCheckedAtZero", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(120,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerDisplaySilencedTipDelay,
                            v -> stage(sendCommand, parent, "starexpress.displaySilencedTipDelay", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildModifiersOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.tooltip")))
                .collapsed(false);

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1,
                        ConfigHelper::getModifierMaximum,
                        v -> stage(sendCommand, parent, "hml.modifierMaximum", v))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1,
                        ConfigHelper::getModifierMultiplier,
                        v -> stage(sendCommand, parent, "hml.modifierMultiplier", v))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.guesser").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.allowcivillian"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.allowcivillian.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getAllowCivillianGuessers,
                            v -> stage(sendCommand, parent, "noellesroles.allowCivillianGuessers", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<String>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.wrongguessmode"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.wrongguessmode.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding("none",
                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getGuesserDiesAfterIncorrectGuess,
                            v -> stage(sendCommand, parent, "noellesroles.guesserDiesAfterIncorrectGuess", v))
                    .controller(opt -> CyclingListControllerBuilder.create(opt)
                            .values(java.util.List.of("none", "death", "explode"))
                            .formatValue(v -> Text.literal(Character.toUpperCase(v.charAt(0)) + v.substring(1))))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.lovers").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.forbidden"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.forbidden.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            () -> ClientConfig.getBool("watheextended.forbiddenLovers", false),
                            v -> stage(sendCommand, parent, "watheextended.forbiddenLovers", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.knowimmediately"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.knowimmediately.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversKnowImmediately,
                            v -> stage(sendCommand, parent, "stupidexpress.loversKnowImmediately", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithkillers"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithkillers.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversWinWithKillers,
                            v -> stage(sendCommand, parent, "stupidexpress.loversWinWithKillers", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithcivilians"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithcivilians.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversWinWithCivilians,
                            v -> stage(sendCommand, parent, "stupidexpress.loversWinWithCivilians", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.glowtoeachother"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.glowtoeachother.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversGlowToEachother,
                            v -> stage(sendCommand, parent, "stupidexpress.loversGlowToEachother", v))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.allergic").styled(s -> s.withColor(0xAAAAAA))));

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(3,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicNothingChance,
                            v -> stage(sendCommand, parent, "starexpress.nothingChance", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctChance,
                            v -> stage(sendCommand, parent, "starexpress.instinctChance", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicArmorChance,
                            v -> stage(sendCommand, parent, "starexpress.armorChance", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicPoisonChance,
                            v -> stage(sendCommand, parent, "starexpress.poisonChance", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(3,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctDuration,
                            v -> stage(sendCommand, parent, "starexpress.instinctDuration", v))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.introverted").styled(s -> s.withColor(0xAAAAAA))));

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowdcount"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowdcount.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(3,
                        () -> ClientConfig.getInt("watheextended.introverted.crowdCount", 3),
                        v -> stage(sendCommand, parent, "watheextended.introverted.crowdCount", v))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowdrange"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowdrange.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(5.0f,
                        () -> ClientConfig.getFloat("watheextended.introverted.crowdRange", 5.0f),
                        v -> stage(sendCommand, parent, "watheextended.introverted.crowdRange", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowddrainmultiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.crowddrainmultiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(2.0f,
                        () -> ClientConfig.getFloat("watheextended.introverted.crowdDrainMultiplier", 2.0f),
                        v -> stage(sendCommand, parent, "watheextended.introverted.crowdDrainMultiplier", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.alonedrainmultiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.introverted.alonedrainmultiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(0.5f,
                        () -> ClientConfig.getFloat("watheextended.introverted.aloneDrainMultiplier", 0.5f),
                        v -> stage(sendCommand, parent, "watheextended.introverted.aloneDrainMultiplier", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.taxed").styled(s -> s.withColor(0xAAAAAA))));

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.taxed.coinreduction"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.taxed.coinreduction.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(0.25f,
                        () -> ClientConfig.getFloat("watheextended.taxed.coinReduction", 0.25f),
                        v -> stage(sendCommand, parent, "watheextended.taxed.coinReduction", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.adaptive").styled(s -> s.withColor(0xAAAAAA))));

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.adaptive.penaltyreduction"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.adaptive.penaltyreduction.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(0.25f,
                        () -> ClientConfig.getFloat("watheextended.adaptive.penaltyReduction", 0.25f),
                        v -> stage(sendCommand, parent, "watheextended.adaptive.penaltyReduction", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        group.option(Option.<Float>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.adaptive.bonusmultiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.adaptive.bonusmultiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(0.50f,
                        () -> ClientConfig.getFloat("watheextended.adaptive.bonusMultiplier", 0.50f),
                        v -> stage(sendCommand, parent, "watheextended.adaptive.bonusMultiplier", v))
                .controller(FloatFieldControllerBuilder::create)
                .build());

        return group.build();
    }
}
