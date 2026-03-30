package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

public class AddonsConfigCommand {

    private static int sync(CommandContext<ServerCommandSource> context) {
        try {
            ServerConfig.broadcastToAll(context.getSource().getServer());
        } catch (Throwable ignored) {
        }
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var root = CommandManager.literal("watheextended:config").requires(source -> source.hasPermissionLevel(2));
        // Kin's Wathe
        if (ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("kinswathe").then(CommandManager.literal("setStartingCooldown").then(CommandManager.argument("seconds", IntegerArgumentType.integer(0)).executes(context -> {
                        int seconds = IntegerArgumentType.getInteger(context, "seconds");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setStartingCooldown(world, seconds);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("enableSafePrepTime").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setEnableStartSafeTime(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("enableNoellesRolesTweaks").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setEnableNoellesRolesModify(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("enableWatheTweaks").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setEnableWatheModify(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("modifyConductorInstinct").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setConductorInstinctModify(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("modifyCoronerInstinct").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setCoronerInstinctModify(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setInitialCivilianIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setInitialCivilianIncome(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setInitialNeutralIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setInitialNeutralIncome(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setInitialKillerIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setInitialKillerIncome(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setIncreaseMoneyWhenKill").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setIncreaseMoneyWhenKill(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setPreventKillerDropRevolver").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setPreventKillerDropRevolver(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// BELLRINGER
                    .then(CommandManager.literal("setBellringerAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setBellringerAbilityPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setBellringerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setBellringerAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// BODYMAKER
                    .then(CommandManager.literal("setBodymakerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setBodymakerAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setBodymakerAbilityFakeRole").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setBodymakerAbilityFakeRole(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// CLEANER
                    .then(CommandManager.literal("setCleanerAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setCleanerAbilityPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setCleanerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setCleanerAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// COOK
                    .then(CommandManager.literal("setCookPanPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setCookPanPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// DETECTIVE
                    .then(CommandManager.literal("setDetectiveAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDetectiveAbilityPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setDetectiveAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDetectiveAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// DREAMER
                    .then(CommandManager.literal("setDreamerInitialItemQuantity").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDreamerInitialItemQuantity(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// DRUGMAKER
                    .then(CommandManager.literal("setDrugmakerPlayerLimit").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDrugmakerPlayerLimit(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setDrugmakerGetCoins").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDrugmakerGetCoins(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setDrugmakerPoisonInjectorPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDrugmakerPoisonInjectorPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setDrugmakerBlowgunPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setDrugmakerBlowgunPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// HUNTER
                    .then(CommandManager.literal("setHunterAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setHunterAbilityPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setHunterAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setHunterAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// JUDGE
                    .then(CommandManager.literal("setJudgeAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setJudgeAbilityPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setJudgeAbilityGlowing").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setJudgeAbilityGlowing(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setJudgeAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setJudgeAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// KIDNAPPER
                    .then(CommandManager.literal("setKidnapperKnockoutDrugPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setKidnapperKnockoutDrugPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// LICENSED VILLAIN
                    .then(CommandManager.literal("setLicensedVillainPlayerLimit").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setLicensedVillainPlayerLimit(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setLicensedVillainRevolverPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setLicensedVillainRevolverPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// PHYSICIAN
                    .then(CommandManager.literal("setPhysicianPillPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setPhysicianPillPrice(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// ROBOT
                    .then(CommandManager.literal("setRobotAbilityDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setRobotAbilityDuration(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setRobotAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        int value = IntegerArgumentType.getInteger(context, "value");
                        World world = context.getSource().getWorld();
                        try {
                            ConfigHelper.setRobotAbilityCooldown(world, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))));
        }

        // Noelle's Roles
        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("noellesroles").then(CommandManager.literal("enableMorphPsychosis").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        boolean value = BoolArgumentType.getBool(context, "enabled");
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setInsanePlayersSeeMorphs(null, value);
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// VOODOO
                    .then(CommandManager.literal("setVoodooNonKillerDeaths").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setVoodooNonKillerDeaths(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setVoodooShotLikeEvil").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setVoodooShotLikeEvil(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// CONDUCTOR
                    .then(CommandManager.literal("setPlayerCountToMakeConducterKeyVisible").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setPlayerCountToMakeConducterKeyVisible(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// BARTENDER
                    .then(CommandManager.literal("setMaximumDefenseVials").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setMaximumDefenseVials(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setDefenseVialPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setDefenseVialPrice(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// TRAPPER
                    .then(CommandManager.literal("setRoleMinePrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setRoleMinePrice(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// GUESSER
                    .then(CommandManager.literal("setAllowCivillianGuessers").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setAllowCivillianGuessers(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setGuesserDiesAfterIncorrectGuess").then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setGuesserDiesAfterIncorrectGuess(com.mojang.brigadier.arguments.StringArgumentType.getString(context, "mode"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))));
        }

        // More Shooter Punishements
        if (cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("shooterpunishments").then(CommandManager.literal("setMode").then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word()).executes(context -> {
                String mode = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "mode");
                try {
                    context.getSource().getServer().getCommandManager().getDispatcher().execute("setShootInnocentPunishment " + mode, context.getSource());
                    cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.setLastKnownMode(mode);
                    return sync(context);
                } catch (Exception exception) {
                    return 0;
                }
            }))));
        }

        // Starry Express
        if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("starexpress")
                    /// STARSTUCK
                    .then(CommandManager.literal("setStarstruckTaskReducesCooldown").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckTaskReducesCooldown(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckTaskCooldownReduction").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckTaskCooldownReduction(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityCooldown(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckAbilityDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityDuration(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckAbilityAffectsMovementSpeed").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityAffectsMovementSpeed(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckAbilityWalkSpeed").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityWalkSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setStarstruckAbilitySprintSpeed").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilitySprintSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// MUZZLER
                    .then(CommandManager.literal("setMuzzlerTapeCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeCooldown(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setMuzzlerSuffocationTime").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerSuffocationTime(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setMuzzlerTapeTearCheckCount").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeTearCheckCount(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setMuzzlerTapeTearMoodChange").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f, 1f)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeTearMoodChange(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setMuzzlerKillIfCheckedAtZero").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerKillIfCheckedAtZero(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setMuzzlerDisplaySilencedTipDelay").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerDisplaySilencedTipDelay(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// ALLERGIC
                    .then(CommandManager.literal("setAllergicNothingChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicNothingChance(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setAllergicInstinctChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicInstinctChance(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setAllergicArmorChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicArmorChance(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setAllergicPoisonChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicPoisonChance(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setAllergicInstinctDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicInstinctDuration(IntegerArgumentType.getInteger(context, "value"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))));
        }

        // Stupid Express
        if (cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) {
            root.then(CommandManager.literal("stupid_express")
                    /// NECROMANCER
                    .then(CommandManager.literal("setNecromancerHasShop").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setNecromancerHasShop(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// ARSONIST
                    .then(CommandManager.literal("setArsonistKeepsGameGoing").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setArsonistKeepsGameGoing(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// AMNESIAC
                    .then(CommandManager.literal("setBodiesGlowToAmnesiac").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setBodiesGlowToAmnesiac(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setAmnesiacGlowsDifferently").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setAmnesiacGlowsDifferently(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    })))
                    /// LOVERS
                    .then(CommandManager.literal("setLoversKnowImmediately").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversKnowImmediately(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setLoversWinWithKillers").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversWinWithKillers(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setLoversWinWithCivilians").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversWinWithCivilians(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))).then(CommandManager.literal("setLoversGlowToEachother").then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                        try {
                            cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversGlowToEachother(BoolArgumentType.getBool(context, "enabled"));
                            return sync(context);
                        } catch (Exception exception) {
                            return 0;
                        }
                    }))));
        }

        dispatcher.register(root);

        dispatcher.register(CommandManager.literal("watheextended:config").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("hml").then(CommandManager.literal("setModifierMaximum").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> {
            int value = IntegerArgumentType.getInteger(context, "value");
            cat.rezelyn.watheextended.api.config.hml.ConfigHelper.setModifierMaximum(value);
            return sync(context);
        }))).then(CommandManager.literal("setModifierMultiplier").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> {
            int value = IntegerArgumentType.getInteger(context, "value");
            cat.rezelyn.watheextended.api.config.hml.ConfigHelper.setModifierMultiplier(value);
            return sync(context);
        })))));
    }
}
