package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.cca.GameComponents;
import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
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
        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            builder.group(buildRolesOptionsGroup(parent, sendCommand));
            builder.group(buildModifiersOptionsGroup(parent, sendCommand));
        }

        return builder.build();
    }

    private static OptionGroup buildGamerulesGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.gamerules.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        boolean collisionsDefault;
        try {
            WatheExtendedWorldComponent wec = world != null ? WatheExtendedWorldComponent.KEY.get(world) : null;
            collisionsDefault = wec == null || wec.isPlayerCollisionsEnabled();
        } catch (Throwable t) {
            collisionsDefault = true;
        }
        final boolean collisionsFinal = collisionsDefault;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(collisionsFinal,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isPlayerCollisionsEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand.accept("watheextended:enableCollisions " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
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
                            v -> sendCommand.accept("watheextended:config noellesroles enableMorphPsychosis " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
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
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStartSafeTime(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand.accept("watheextended:config kinswathe enableSafePrepTime " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(30,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getStartingCooldown(MinecraftClient.getInstance().world),
                            v -> sendCommand.accept("watheextended:config kinswathe setStartingCooldown " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildWatheOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.wathe_options.tooltip")))
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
                            v -> sendCommand.accept("watheextended:config shooterpunishments setMode " + v, parent))
                    .controller(opt -> CyclingListControllerBuilder.create(opt)
                            .values(Arrays.asList(punishmentModes))
                            .formatValue(v -> {
                                String spaced = v.replaceAll("([A-Z])", " $1");
                                String titled = Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
                                return Text.literal(titled);
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
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableWatheModify(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand.accept("watheextended:config kinswathe enableWatheTweaks " + v, parent))
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
                            v -> sendCommand.accept("watheextended:config kinswathe setInitialCivilianIncome " + v, parent))
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
                            v -> sendCommand.accept("watheextended:config kinswathe setInitialNeutralIncome " + v, parent))
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
                            v -> sendCommand.accept("watheextended:config kinswathe setInitialKillerIncome " + v, parent))
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
                            v -> sendCommand.accept("watheextended:config kinswathe setIncreaseMoneyWhenKill " + v, parent))
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
                            v -> sendCommand.accept("watheextended:config kinswathe setPreventKillerDropRevolver " + v, parent))
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildRolesOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.tooltip")))
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

        final boolean noellesRolesTweaksEnabled = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(world);

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("watheextended:config kinswathe enableNoellesRolesTweaks " + v, parent))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .available(noellesRolesTweaksEnabled)
                .binding(false,
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getConductorInstinctModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("watheextended:config kinswathe modifyConductorInstinct " + v, parent))
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
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCoronerInstinctModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("watheextended:config kinswathe modifyCoronerInstinct " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bellringer").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setBellringerAbilityPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(120, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setBellringerAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bodymaker").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setBodymakerAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityFakeRole(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setBodymakerAbilityFakeRole " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cleaner").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setCleanerAbilityPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(150, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setCleanerAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cook").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(250, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCookPanPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setCookPanPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.detective").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDetectiveAbilityPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDetectiveAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.dreamer").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(1, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDreamerInitialItemQuantity(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDreamerInitialItemQuantity " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.drugmaker").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPlayerLimit(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDrugmakerPlayerLimit " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(50, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerGetCoins(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDrugmakerGetCoins " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(125, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPoisonInjectorPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDrugmakerPoisonInjectorPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(175, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerBlowgunPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setDrugmakerBlowgunPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.hunter").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(125, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setHunterAbilityPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(5, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setHunterAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.judge").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setJudgeAbilityPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityGlowing(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setJudgeAbilityGlowing " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(180, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setJudgeAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.kidnapper").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(75, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getKidnapperKnockoutDrugPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setKidnapperKnockoutDrugPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.licensedvillain").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainPlayerLimit(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setLicensedVillainPlayerLimit " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainRevolverPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setLicensedVillainRevolverPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.physician").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getPhysicianPillPrice(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setPhysicianPillPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.robot").styled(s -> s.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityDuration(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setRobotAbilityDuration " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand.accept("watheextended:config kinswathe setRobotAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

        // NoellesRoles roles
        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.voodoo").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.nonkillerdeaths")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.nonkillerdeaths.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getVoodooNonKillerDeaths, v -> sendCommand.accept("watheextended:config noellesroles setVoodooNonKillerDeaths " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.shotlikeevil")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.voodoo.shotlikeevil.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getVoodooShotLikeEvil, v -> sendCommand.accept("watheextended:config noellesroles setVoodooShotLikeEvil " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.conductor").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductor.keycountvisible")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductor.keycountvisible.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(10, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getPlayerCountToMakeConducterKeyVisible, v -> sendCommand.accept("watheextended:config noellesroles setPlayerCountToMakeConducterKeyVisible " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bartender").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.maxdefensevials")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.maxdefensevials.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(0, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getMaximumDefenseVials, v -> sendCommand.accept("watheextended:config noellesroles setMaximumDefenseVials " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.defensevialsprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bartender.defensevialsprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(100, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getDefenseVialPrice, v -> sendCommand.accept("watheextended:config noellesroles setDefenseVialPrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.trapper").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.trapper.rolemineprice")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.trapper.rolemineprice.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(100, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getRoleMinePrice, v -> sendCommand.accept("watheextended:config noellesroles setRoleMinePrice " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        }

        if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.necromancer").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.necromancer.hasshop")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.necromancer.hasshop.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getNecromancerHasShop, v -> sendCommand.accept("watheextended:config stupid_express setNecromancerHasShop " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.arsonist").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.arsonist.keepsgamegoing")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.arsonist.keepsgamegoing.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getArsonistKeepsGameGoing, v -> sendCommand.accept("watheextended:config stupid_express setArsonistKeepsGameGoing " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.amnesiac").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.bodiesglow")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.bodiesglow.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getBodiesGlowToAmnesiac, v -> sendCommand.accept("watheextended:config stupid_express setBodiesGlowToAmnesiac " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.glowsdifferently")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.amnesiac.glowsdifferently.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getAmnesiacGlowsDifferently, v -> sendCommand.accept("watheextended:config stupid_express setAmnesiacGlowsDifferently " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
        }

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.starstruck").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskReducesCooldown, v -> sendCommand.accept("watheextended:config starexpress setStarstruckTaskReducesCooldown " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(5, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskCooldownReduction, v -> sendCommand.accept("watheextended:config starexpress setStarstruckTaskCooldownReduction " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(90, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityCooldown, v -> sendCommand.accept("watheextended:config starexpress setStarstruckAbilityCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(15, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityDuration, v -> sendCommand.accept("watheextended:config starexpress setStarstruckAbilityDuration " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityAffectsMovementSpeed, v -> sendCommand.accept("watheextended:config starexpress setStarstruckAbilityAffectsMovementSpeed " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Float>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(0.12f, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityWalkSpeed, v -> sendCommand.accept("watheextended:config starexpress setStarstruckAbilityWalkSpeed " + v, parent)).controller(FloatFieldControllerBuilder::create).build());
            group.option(Option.<Float>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(0.15f, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilitySprintSpeed, v -> sendCommand.accept("watheextended:config starexpress setStarstruckAbilitySprintSpeed " + v, parent)).controller(FloatFieldControllerBuilder::create).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.muzzler").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(20, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeCooldown, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerTapeCooldown " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(60, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerSuffocationTime, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerSuffocationTime " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(5, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearCheckCount, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerTapeTearCheckCount " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Float>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(0.1f, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearMoodChange, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerTapeTearMoodChange " + v, parent)).controller(FloatFieldControllerBuilder::create).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerKillIfCheckedAtZero, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerKillIfCheckedAtZero " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(120, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerDisplaySilencedTipDelay, v -> sendCommand.accept("watheextended:config starexpress setMuzzlerDisplaySilencedTipDelay " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        }

        return group.build();
    }

    private static OptionGroup buildModifiersOptionsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.tooltip")))
                .collapsed(false);

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1, ConfigHelper::getModifierMaximum, ConfigHelper::setModifierMaximum)
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1, ConfigHelper::getModifierMultiplier, ConfigHelper::setModifierMultiplier)
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.guesser").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.allowcivillian")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.allowcivillian.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getAllowCivillianGuessers, v -> sendCommand.accept("watheextended:config noellesroles setAllowCivillianGuessers " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<String>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.wrongguessmode")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.guesser.wrongguessmode.desc").styled(s -> s.withColor(0xFFFFFF)))).binding("none", cat.rezelyn.watheextended.api.noellesroles.ConfigHelper::getGuesserDiesAfterIncorrectGuess, v -> sendCommand.accept("watheextended:config noellesroles setGuesserDiesAfterIncorrectGuess " + v, parent)).controller(opt -> CyclingListControllerBuilder.create(opt).values(java.util.List.of("none", "death", "explode")).formatValue(v -> Text.literal(Character.toUpperCase(v.charAt(0)) + v.substring(1)))).build());
        }

        // StupidExpress modifiers
        if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.lovers").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.knowimmediately")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.knowimmediately.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversKnowImmediately, v -> sendCommand.accept("watheextended:config stupid_express setLoversKnowImmediately " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithkillers")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithkillers.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversWinWithKillers, v -> sendCommand.accept("watheextended:config stupid_express setLoversWinWithKillers " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithcivilians")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.winwithcivilians.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(true, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversWinWithCivilians, v -> sendCommand.accept("watheextended:config stupid_express setLoversWinWithCivilians " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
            group.option(Option.<Boolean>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.glowtoeachother")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.lovers.glowtoeachother.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(false, cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper::getLoversGlowToEachother, v -> sendCommand.accept("watheextended:config stupid_express setLoversGlowToEachother " + v, parent)).controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off"))).build());
        }

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.allergic").styled(s -> s.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(3, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicNothingChance, v -> sendCommand.accept("watheextended:config starexpress setAllergicNothingChance " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(1, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctChance, v -> sendCommand.accept("watheextended:config starexpress setAllergicInstinctChance " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(1, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicArmorChance, v -> sendCommand.accept("watheextended:config starexpress setAllergicArmorChance " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(1, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicPoisonChance, v -> sendCommand.accept("watheextended:config starexpress setAllergicPoisonChance " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder().name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration")).description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration.desc").styled(s -> s.withColor(0xFFFFFF)))).binding(3, cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctDuration, v -> sendCommand.accept("watheextended:config starexpress setAllergicInstinctDuration " + v, parent)).controller(IntegerFieldControllerBuilder::create).build());
        }

        return group.build();
    }
}

