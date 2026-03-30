package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.RolesDisplay;
import cat.rezelyn.watheextended.api.RolesId;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.*;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;

public final class RolesCategory {

    private RolesCategory() {
    }

    public static ConfigCategory build(Screen parent, Set<String> blacklist, Map<String, Boolean> pendingState, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.roles"))
                .tooltip(Text.translatable("gui.watheextended.config.category.roles.tooltip"));

        try {
            Set<String> roleId = new LinkedHashSet<>();
            for (String ID : RolesId.get()) {
                if (!ScreenUtils.isBlacklisted(ID, blacklist)) roleId.add(ID);
            }

            if (roleId.isEmpty()) {
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.roles.empty").styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, RolesDisplay.RoleDisplay> roleName = RolesDisplay.get();

                List<String> roles = new ArrayList<>(roleId);
                roles.sort(Comparator.comparingInt(id -> {
                    RolesDisplay.RoleDisplay display = roleName.get(id);
                    return display != null ? display.side().ordinal() : RolesDisplay.Side.NEUTRAL.ordinal();
                }));

                for (String id : roles) {
                    RolesDisplay.RoleDisplay display = roleName.get(id);
                    Text label = display != null ? display.display().copy().styled(style -> style.withColor(display.color())) : Text.literal(RolesDisplay.prettyName(id));

                    boolean state = pendingState.containsKey(id) ? pendingState.get(id) : !ConfigHelper.getDisabledRoles().contains(id);

                    OptionGroup.Builder group = OptionGroup.createBuilder().name(label);
                    group.option(Option.<Boolean>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.enabled"))
                            .description(OptionDescription.of(Text.literal(id).styled(style -> style.withColor(0x505050))))
                            .binding(state, () -> pendingState.containsKey(id) ? pendingState.get(id) : !ConfigHelper.getDisabledRoles().contains(id), enabled -> {
                                pendingState.put(id, enabled);
                                sendCommand.accept("setEnabledModifier " + id + " " + enabled, parent);
                            })
                            .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                            .build());

                    roleOptions(id, group, parent, sendCommand);
                    builder.group(group.build());
                }
            }
        } catch (Throwable throwable) {
            builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.roles.error").styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    private static void roleOptions(String id, OptionGroup.Builder builder, Screen parent, BiConsumer<String, Screen> sendCommand) {
        switch (id) {
            // Bellringer
            case "kinswathe:bellringer" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - PRICE
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.bellringer.price.desc")))
                        .binding(200, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getBellringerAbilityPrice(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.BellringerAbilityPrice", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.bellringer.cooldown.desc")))
                        .binding(120, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getBellringerAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.BellringerAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Bodymaker
            case "kinswathe:bodymaker" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - COOLDOWN
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.bodymaker.cooldown.desc")))
                        .binding(90, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getBodymakerAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.BodymakerAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - ALLOW FAKE ROLE
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.bodymaker.fakerole"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.bodymaker.fakerole.desc")))
                        .binding(true, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getBodymakerAbilityFakeRole(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.BodymakerAbilityFakeRole", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                        .build());
            }
            // Cleaner
            case "kinswathe:cleaner" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - PRICE
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.cleaner.price.desc")))
                        .binding(200, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getCleanerAbilityPrice(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.CleanerAbilityPrice", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.cleaner.cooldown.desc")))
                        .binding(150, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getCleanerAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.CleanerAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - PLAYER LIMIT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.playerlimit"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.cleaner.playerlimit.desc")))
                        .binding(10, () -> ClientConfig.getInt("watheextended.cleaner.playerLimit", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.cleaner.playerLimit", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Detective
            case "kinswathe:detective" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                /// ABILITY - PRICE
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.detective.price.desc")))
                        .binding(200, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getDetectiveAbilityPrice(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.DetectiveAbilityPrice", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.detective.cooldown.desc")))
                        .binding(90, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getDetectiveAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.DetectiveAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Dreamer
            case "kinswathe:dreamer" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// INITIAL DREAM IMPRINT COUNT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.dreamer.initialquantity"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.dreamer.initialquantity.desc")))
                        .binding(1, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getDreamerInitialItemQuantity(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.DreamerInitialItemQuantity", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Drugmaker
            case "kinswathe:drugmaker" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// PLAYER LIMIT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.playerlimit"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.drugmaker.playerlimit.desc")))
                        .binding(10, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getDrugmakerPlayerLimit(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.DrugmakerPlayerLimit", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// POISON KILL INCOME
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.drugmaker.killincome"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.drugmaker.killincome.desc")))
                        .binding(50, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getDrugmakerGetCoins(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.DrugmakerGetCoins", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Hacker
            case "kinswathe:hacker" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// PLAYER LIMIT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.playerlimit"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.playerlimit.desc")))
                        .binding(10, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHackerPlayerLimit(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HackerPlayerLimit", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// GENERATE WITH MIMIC
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.generatewithmimic"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.generatewithmimic.desc")))
                        .binding(false, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHackerGenerateWithMimic(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HackerGenerateWithMimic", value))
                        .controller(TickBoxControllerBuilder::create).build());
                /// HACKING TIME
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.hackingtime"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.hackingtime.desc")))
                        .binding(30, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHackerHackingTime(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HackerHackingTime", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// HAS SHOP
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.hasshop"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hacker.hasshop.desc")))
                        .binding(true, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHackerHasShop(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HackerHasShop", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.enabled"))).build());
            }
            // Hunter
            case "kinswathe:hunter" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - PRICE
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hunter.price.desc")))
                        .binding(125, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHunterAbilityPrice(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HunterAbilityPrice", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.hunter.cooldown.desc")))
                        .binding(5, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getHunterAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.HunterAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Judge
            case "kinswathe:judge" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - PRICE
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.judge.price.desc")))
                        .binding(300, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getJudgeAbilityPrice(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.JudgeAbilityPrice", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.judge.cooldown.desc")))
                        .binding(180, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getJudgeAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.JudgeAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - DURATION
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.duration"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.judge.duration.desc")))
                        .binding(90, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getJudgeAbilityGlowing(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.JudgeAbilityGlowing", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Licensed Villian
            case "kinswathe:licensed_villain" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// PLAYER LIMIT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.playerlimit"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.licensedvillain.playerlimit.desc")))
                        .binding(10, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getLicensedVillainPlayerLimit(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.LicensedVillainPlayerLimit", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Robot
            case "kinswathe:robot" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
                /// ABILITY - COOLDOWN
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.robot.cooldown.desc")))
                        .binding(90, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getRobotAbilityCooldown(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.RobotAbilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - DURATION
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.duration"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.robot.duration.desc")))
                        .binding(10, () -> cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getRobotAbilityDuration(MinecraftClient.getInstance().world), value -> ScreenUtils.stage(sendCommand, parent, "kinswathe.RobotAbilityDuration", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Voodoo
            case "noellesroles:voodoo" -> {
                if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
                /// SHOT LIKE EVIL
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.voodoo.shotlikeevil"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.voodoo.shotlikeevil.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getVoodooShotLikeEvil, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.voodooShotLikeEvil", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                /// ABILITY - VOODOO ON NATURAL DEATHS
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.voodoo.nonkillerdeaths"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.voodoo.nonkillerdeaths.desc")))
                        .binding(false, cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getVoodooNonKillerDeaths, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.voodooNonKillerDeaths", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
            }
            // Conductor
            case "noellesroles:conductor" -> {
                if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
                /// KEY VISIBLE PLAYER COUNT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.conductor.keycountvisible"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.conductor.keycountvisible.desc")))
                        .binding(10, cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getPlayerCountToMakeConducterKeyVisible, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.playerCountToMakeConducterKeyVisible", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Bartender
            case "noellesroles:bartender" -> {
                if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
                /// MAX DEFENSE VIALS
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.bartender.maxdefensevials"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.bartender.maxdefensevials.desc")))
                        .binding(1, cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper::getMaximumDefenseVials, value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.maximumDefenseVials", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
            // Necromancer
            case "stupid_express:necromancer" -> {
                if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) return;
                /// HAS SHOP
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.hasshop"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.necromancer.hasshop.desc")))
                        .binding(false, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getNecromancerHasShop, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.necromancerHasShop", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
            }
            // Arsonist
            case "stupid_express:arsonist" -> {
                if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) return;
                /// KEEPS GAME GOING
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.arsonist.keepsgamegoing"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.arsonist.keepsgamegoing.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getArsonistKeepsGameGoing, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.arsonistKeepsGameGoing", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
            }
            // Amnesiac
            case "stupid_express:amnesiac" -> {
                if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) return;
                /// BODIES GLOW
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.amnesiac.bodiesglow"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.amnesiac.bodiesglow.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getBodiesGlowToAmnesiac, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.bodiesGlowToAmnesiac", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                /// GLOWS DIFFERENTLY
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.amnesiac.glowsdifferently"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.amnesiac.glowsdifferently.desc")))
                        .binding(false, cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper::getAmnesiacGlowsDifferently, value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.amnesiacGlowsDifferently", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
            }
            // Starstruck
            case "starexpress:starstruck" -> {
                if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) return;
                /// ABILITY - COOLDOWN
                builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.text.ability").styled(style -> style.withColor(0xFF5555))));
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.cooldown.desc")))
                        .binding(90, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckAbilityCooldown, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.abilityCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - DURATION
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.duration"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.duration.desc")))
                        .binding(15, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckAbilityDuration, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.abilityDuration", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - TASK REDUCE COOLDOWN
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.taskreducescooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.taskreducescooldown.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckTaskReducesCooldown, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.taskReducesCooldown", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                /// ABILITY - TASK COOLDOWN REDUCTION
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.taskcooldownreduction"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.taskcooldownreduction.desc")))
                        .binding(5, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckTaskCooldownReduction, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.taskCooldownReduction", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// ABILITY - AFFECTS MOVEMENT SPEED
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.affectsmovementspeed"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.affectsmovementspeed.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckAbilityAffectsMovementSpeed, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.abilityAffectsMovementSpeed", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                /// ABILITY - WALK SPEED
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.walkspeed"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.walkspeed.desc")))
                        .binding(0.12f, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckAbilityWalkSpeed, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.abilityWalkSpeed", value))
                        .controller(ScreenUtils::floatController).build());
                /// ABILITY - SPRINT SPEED
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.sprintspeed"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.starstruck.sprintspeed.desc")))
                        .binding(0.15f, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getStarstruckAbilitySprintSpeed, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.abilitySprintSpeed", value))
                        .controller(ScreenUtils::floatController).build());
            }
            // Muzzler
            case "starexpress:muzzler" -> {
                if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) return;
                /// TAPE COOLDOWN
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapecooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapecooldown.desc")))
                        .binding(20, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerTapeCooldown, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeCooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// TAPE SUFFOCATION TIME
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.suffocationtime"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.suffocationtime.desc")))
                        .binding(60, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerSuffocationTime, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.suffocationTime", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// TAPE TEAR CHECK COUNT
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapetearcheckcount"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapetearcheckcount.desc")))
                        .binding(5, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerTapeTearCheckCount, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeTearCheckCount", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
                /// TAPE TEAR MOOD CHANGE
                builder.option(Option.<Float>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapetearmoodchange"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.tapetearmoodchange.desc")))
                        .binding(0.1f, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerTapeTearMoodChange, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeTearMoodChange", value))
                        .controller(ScreenUtils::floatController).build());
                /// TAPE KILL IF CHECK AT ZERO
                builder.option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.killifcheckedatzero"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.killifcheckedatzero.desc")))
                        .binding(true, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerKillIfCheckedAtZero, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.killIfCheckedAtZero", value))
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build());
                /// TAPE DISPLAY SILENCED TIP DELAY
                builder.option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.displaysilencedtipdelay"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.roles.opt.muzzler.displaysilencedtipdelay.desc")))
                        .binding(120, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerDisplaySilencedTipDelay, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.displaySilencedTipDelay", value))
                        .controller(IntegerFieldControllerBuilder::create).build());
            }
        }
    }
}
