package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.kinswathe.ConfigHelper;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AddonsConfigCommand {

    private static Text feedback(boolean enabled, String enabledKey, String disabledKey, Object... args) {
        return Text.translatable(enabled ? enabledKey : disabledKey, args)
                .styled(s -> s.withColor(enabled ? 0x55FF55 : 0xFF5555));
    }

    private static int syncAndReturn(CommandContext<ServerCommandSource> ctx) {
        try {
            ServerConfig.broadcastToAll(ctx.getSource().getServer());
        } catch (Throwable ignored) {
        }
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var root = CommandManager.literal("watheextended:config")
                .requires(source -> source.hasPermissionLevel(2));

        if (ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("kinswathe")
                    .then(CommandManager.literal("setStartingCooldown")
                            .then(CommandManager.argument("seconds", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setStartingCooldown(world, seconds);
                                            ctx.getSource().sendFeedback(() -> feedback(true,
                                                    "command.watheextended.kinswathe.startingcooldown.set",
                                                    "command.watheextended.kinswathe.startingcooldown.set", seconds), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("enableJumpInLobby")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setEnableJumpNotInGame(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.jumpinlobby.enabled",
                                                    "command.watheextended.kinswathe.jumpinlobby.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("enableSafePrepTime")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setEnableStartSafeTime(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.safepreptime.enabled",
                                                    "command.watheextended.kinswathe.safepreptime.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("enableNoellesRolesTweaks")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setEnableNoellesRolesModify(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.noellesrolestweaks.enabled",
                                                    "command.watheextended.kinswathe.noellesrolestweaks.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("enableWatheTweaks")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setEnableWatheModify(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.wathetweaks.enabled",
                                                    "command.watheextended.kinswathe.wathetweaks.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("modifyConductorInstinct")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setConductorInstinctModify(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.conductorinstinct.enabled",
                                                    "command.watheextended.kinswathe.conductorinstinct.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("modifyCoronerInstinct")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setCoronerInstinctModify(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.kinswathe.coronerinstinct.enabled",
                                                    "command.watheextended.kinswathe.coronerinstinct.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setInitialCivilianIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setInitialCivilianIncome(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setInitialNeutralIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setInitialNeutralIncome(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setInitialKillerIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setInitialKillerIncome(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setIncreaseMoneyWhenKill")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setIncreaseMoneyWhenKill(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setPreventKillerDropRevolver")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setPreventKillerDropRevolver(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Bellringer
                    .then(CommandManager.literal("setBellringerAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setBellringerAbilityPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setBellringerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setBellringerAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Bodymaker
                    .then(CommandManager.literal("setBodymakerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setBodymakerAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setBodymakerAbilityFakeRole")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setBodymakerAbilityFakeRole(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Cleaner
                    .then(CommandManager.literal("setCleanerAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setCleanerAbilityPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setCleanerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setCleanerAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Cook
                    .then(CommandManager.literal("setCookPanPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setCookPanPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Detective
                    .then(CommandManager.literal("setDetectiveAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDetectiveAbilityPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setDetectiveAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDetectiveAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Dreamer
                    .then(CommandManager.literal("setDreamerInitialItemQuantity")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDreamerInitialItemQuantity(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Drugmaker
                    .then(CommandManager.literal("setDrugmakerPlayerLimit")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDrugmakerPlayerLimit(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setDrugmakerGetCoins")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDrugmakerGetCoins(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setDrugmakerPoisonInjectorPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDrugmakerPoisonInjectorPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setDrugmakerBlowgunPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setDrugmakerBlowgunPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Hunter
                    .then(CommandManager.literal("setHunterAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setHunterAbilityPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setHunterAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setHunterAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Judge
                    .then(CommandManager.literal("setJudgeAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setJudgeAbilityPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setJudgeAbilityGlowing")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setJudgeAbilityGlowing(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setJudgeAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setJudgeAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Kidnapper
                    .then(CommandManager.literal("setKidnapperKnockoutDrugPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setKidnapperKnockoutDrugPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Licensed Villain
                    .then(CommandManager.literal("setLicensedVillainPlayerLimit")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setLicensedVillainPlayerLimit(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setLicensedVillainRevolverPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setLicensedVillainRevolverPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Physician
                    .then(CommandManager.literal("setPhysicianPillPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setPhysicianPillPrice(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Robot
                    .then(CommandManager.literal("setRobotAbilityDuration")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setRobotAbilityDuration(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setRobotAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            ConfigHelper.setRobotAbilityCooldown(world, v);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("noellesroles")
                    .then(CommandManager.literal("enableMorphPsychosis")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setInsanePlayersSeeMorphs(null, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.noellesroles.morphpsychosis.enabled",
                                                    "command.watheextended.noellesroles.morphpsychosis.disabled"), true);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Voodoo
                    .then(CommandManager.literal("setVoodooNonKillerDeaths")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setVoodooNonKillerDeaths(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setVoodooShotLikeEvil")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setVoodooShotLikeEvil(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Conductor
                    .then(CommandManager.literal("setPlayerCountToMakeConducterKeyVisible")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setPlayerCountToMakeConducterKeyVisible(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Bartender
                    .then(CommandManager.literal("setMaximumDefenseVials")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setMaximumDefenseVials(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setDefenseVialPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setDefenseVialPrice(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Trapper
                    .then(CommandManager.literal("setRoleMinePrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setRoleMinePrice(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Guesser
                    .then(CommandManager.literal("setAllowCivillianGuessers")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setAllowCivillianGuessers(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setGuesserDiesAfterIncorrectGuess")
                            .then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setGuesserDiesAfterIncorrectGuess(com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "mode"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        if (cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("shooterpunishments")
                    .then(CommandManager.literal("setMode")
                            .then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word())
                                    .executes(ctx -> {
                                        String mode = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "mode");
                                        try {
                                            ctx.getSource().getServer().getCommandManager().getDispatcher()
                                                    .execute("setShootInnocentPunishment " + mode, ctx.getSource());
                                            cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.setLastKnownMode(mode);
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("starexpress")
                    // Starstruck
                    .then(CommandManager.literal("setStarstruckTaskReducesCooldown")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckTaskReducesCooldown(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckTaskCooldownReduction")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckTaskCooldownReduction(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckAbilityCooldown(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckAbilityDuration")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckAbilityDuration(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckAbilityAffectsMovementSpeed")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckAbilityAffectsMovementSpeed(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckAbilityWalkSpeed")
                            .then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckAbilityWalkSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setStarstruckAbilitySprintSpeed")
                            .then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setStarstruckAbilitySprintSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Muzzler
                    .then(CommandManager.literal("setMuzzlerTapeCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerTapeCooldown(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setMuzzlerSuffocationTime")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerSuffocationTime(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setMuzzlerTapeTearCheckCount")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerTapeTearCheckCount(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setMuzzlerTapeTearMoodChange")
                            .then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f, 1f))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerTapeTearMoodChange(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setMuzzlerKillIfCheckedAtZero")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerKillIfCheckedAtZero(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setMuzzlerDisplaySilencedTipDelay")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setMuzzlerDisplaySilencedTipDelay(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Allergic
                    .then(CommandManager.literal("setAllergicNothingChance")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setAllergicNothingChance(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setAllergicInstinctChance")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setAllergicInstinctChance(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setAllergicArmorChance")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setAllergicArmorChance(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setAllergicPoisonChance")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setAllergicPoisonChance(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setAllergicInstinctDuration")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper.setAllergicInstinctDuration(IntegerArgumentType.getInteger(ctx, "value"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        if (cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("stupid_express")
                    // Necromancer
                    .then(CommandManager.literal("setNecromancerHasShop")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setNecromancerHasShop(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Arsonist
                    .then(CommandManager.literal("setArsonistKeepsGameGoing")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setArsonistKeepsGameGoing(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Amnesiac
                    .then(CommandManager.literal("setBodiesGlowToAmnesiac")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setBodiesGlowToAmnesiac(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setAmnesiacGlowsDifferently")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setAmnesiacGlowsDifferently(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    // Lovers
                    .then(CommandManager.literal("setLoversKnowImmediately")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setLoversKnowImmediately(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setLoversWinWithKillers")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setLoversWinWithKillers(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setLoversWinWithCivilians")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setLoversWinWithCivilians(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setLoversGlowToEachother")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        try {
                                            cat.rezelyn.watheextended.api.stupidexpress.ConfigHelper.setLoversGlowToEachother(BoolArgumentType.getBool(ctx, "enabled"));
                                            return syncAndReturn(ctx);
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        dispatcher.register(root);

        // hml modifier config commands
        dispatcher.register(CommandManager.literal("watheextended:config")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("hml")
                        .then(CommandManager.literal("setModifierMaximum")
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            cat.rezelyn.watheextended.api.hml.ConfigHelper.setModifierMaximum(v);
                                            return syncAndReturn(ctx);
                                        })))
                        .then(CommandManager.literal("setModifierMultiplier")
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            cat.rezelyn.watheextended.api.hml.ConfigHelper.setModifierMultiplier(v);
                                            return syncAndReturn(ctx);
                                        })))));
    }
}
