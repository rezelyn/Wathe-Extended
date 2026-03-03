package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.kinswathe.ConfigHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AddonsConfigCommand {

    private static Text feedback(boolean enabled, String enabledKey, String disabledKey, Object... args) {
        return Text.translatable(enabled ? enabledKey : disabledKey, args)
                .styled(s -> s.withColor(enabled ? 0x55FF55 : 0xFF5555));
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
                                            return 1;
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
                                            return 1;
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
                                            return 1;
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
                                            return 1;
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
                                            return 1;
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
                                            return 1;
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
                                            return 1;
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
                    .then(CommandManager.literal("setInitialCivilianIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setInitialCivilianIncome(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setInitialNeutralIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setInitialNeutralIncome(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setInitialKillerIncome")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setInitialKillerIncome(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setIncreaseMoneyWhenKill")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setIncreaseMoneyWhenKill(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setPreventKillerDropRevolver")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setPreventKillerDropRevolver(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Bellringer
                    .then(CommandManager.literal("setBellringerAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setBellringerAbilityPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setBellringerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setBellringerAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Bodymaker
                    .then(CommandManager.literal("setBodymakerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setBodymakerAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setBodymakerAbilityFakeRole")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setBodymakerAbilityFakeRole(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Cleaner
                    .then(CommandManager.literal("setCleanerAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setCleanerAbilityPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setCleanerAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setCleanerAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Cook
                    .then(CommandManager.literal("setCookPanPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setCookPanPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Detective
                    .then(CommandManager.literal("setDetectiveAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDetectiveAbilityPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setDetectiveAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDetectiveAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Dreamer
                    .then(CommandManager.literal("setDreamerInitialItemQuantity")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDreamerInitialItemQuantity(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Drugmaker
                    .then(CommandManager.literal("setDrugmakerPlayerLimit")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDrugmakerPlayerLimit(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setDrugmakerGetCoins")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDrugmakerGetCoins(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setDrugmakerPoisonInjectorPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDrugmakerPoisonInjectorPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setDrugmakerBlowgunPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setDrugmakerBlowgunPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Hunter
                    .then(CommandManager.literal("setHunterAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setHunterAbilityPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setHunterAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setHunterAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Judge
                    .then(CommandManager.literal("setJudgeAbilityPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setJudgeAbilityPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setJudgeAbilityGlowing")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setJudgeAbilityGlowing(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setJudgeAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setJudgeAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Kidnapper
                    .then(CommandManager.literal("setKidnapperKnockoutDrugPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setKidnapperKnockoutDrugPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Licensed Villain
                    .then(CommandManager.literal("setLicensedVillainPlayerLimit")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setLicensedVillainPlayerLimit(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setLicensedVillainRevolverPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setLicensedVillainRevolverPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Physician
                    .then(CommandManager.literal("setPhysicianPillPrice")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setPhysicianPillPrice(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    // Robot
                    .then(CommandManager.literal("setRobotAbilityDuration")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setRobotAbilityDuration(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
                    .then(CommandManager.literal("setRobotAbilityCooldown")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        World world = ctx.getSource().getWorld();
                                        try { ConfigHelper.setRobotAbilityCooldown(world, v); return 1; } catch (Exception e) { return 0; }
                                    })))
            );
        }

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("noellesroles")
                    .then(CommandManager.literal("enableMorphPsychosis")
                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean v = BoolArgumentType.getBool(ctx, "enabled");
                                        World world = ctx.getSource().getWorld();
                                        try {
                                            cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.setInsanePlayersSeeMorphs(world, v);
                                            ctx.getSource().sendFeedback(() -> feedback(v,
                                                    "command.watheextended.noellesroles.morphpsychosis.enabled",
                                                    "command.watheextended.noellesroles.morphpsychosis.disabled"), true);
                                            return 1;
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    })))
            );
        }

        dispatcher.register(root);
    }
}
