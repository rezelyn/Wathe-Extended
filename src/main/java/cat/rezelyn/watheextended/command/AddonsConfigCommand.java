package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AddonsConfigCommand {

    @FunctionalInterface
    private interface ConfigUpdate {
        void apply(CommandContext<ServerCommandSource> context) throws Exception;
    }

    private static int sync(CommandContext<ServerCommandSource> context) {
        try {
            ServerConfig.broadcastToAll(context.getSource().getServer());
        } catch (Throwable ignored) {
        }
        return 1;
    }

    /// Wraps a config change as a command: swallows failures and syncs the result to all clients.
    private static Command<ServerCommandSource> update(ConfigUpdate update) {
        return context -> {
            try {
                update.apply(context);
            } catch (Exception exception) {
                return 0;
            }
            return sync(context);
        };
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var root = CommandManager.literal("watheextended:config").requires(source -> source.hasPermissionLevel(2));
        // Kin's Wathe
        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            root.then(
                CommandManager.literal("kinswathe")
                        .then(CommandManager.literal("setStartingCooldown").then(CommandManager.argument("seconds", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setStartingCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "seconds"))
                        ))))

                        .then(CommandManager.literal("enableSafePrepTime").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setEnableStartSafeTime(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("enableNoellesRolesTweaks").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setEnableNoellesRolesModify(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("enableWatheTweaks").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setEnableWatheModify(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("modifyConductorInstinct").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setConductorInstinctModify(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("modifyCoronerInstinct").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setCoronerInstinctModify(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setInitialCivilianIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setInitialCivilianIncome(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setInitialNeutralIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setInitialNeutralIncome(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setInitialKillerIncome").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setInitialKillerIncome(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setIncreaseMoneyWhenKill").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setIncreaseMoneyWhenKill(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setPreventKillerDropRevolver").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setPreventKillerDropRevolver(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// BELLRINGER
                        .then(CommandManager.literal("setBellringerAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setBellringerAbilityPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setBellringerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setBellringerAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// BODYMAKER
                        .then(CommandManager.literal("setBodymakerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setBodymakerAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setBodymakerAbilityFakeRole").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> ConfigHelper.setBodymakerAbilityFakeRole(context.getSource().getWorld(), BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// CLEANER
                        .then(CommandManager.literal("setCleanerAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setCleanerAbilityPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setCleanerAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setCleanerAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// COOK
                        .then(CommandManager.literal("setCookPanPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setCookPanPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// DETECTIVE
                        .then(CommandManager.literal("setDetectiveAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDetectiveAbilityPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDetectiveAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDetectiveAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// DREAMER
                        .then(CommandManager.literal("setDreamerInitialItemQuantity").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDreamerInitialItemQuantity(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// DRUGMAKER
                        .then(CommandManager.literal("setDrugmakerPlayerLimit").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDrugmakerPlayerLimit(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDrugmakerGetCoins").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDrugmakerGetCoins(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDrugmakerPoisonInjectorPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDrugmakerPoisonInjectorPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDrugmakerBlowgunPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setDrugmakerBlowgunPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// HUNTER
                        .then(CommandManager.literal("setHunterAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setHunterAbilityPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setHunterAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setHunterAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// JUDGE
                        .then(CommandManager.literal("setJudgeAbilityPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setJudgeAbilityPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setJudgeAbilityGlowing").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setJudgeAbilityGlowing(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setJudgeAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setJudgeAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// KIDNAPPER
                        .then(CommandManager.literal("setKidnapperKnockoutDrugPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setKidnapperKnockoutDrugPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// LICENSED VILLAIN
                        .then(CommandManager.literal("setLicensedVillainPlayerLimit").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setLicensedVillainPlayerLimit(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setLicensedVillainRevolverPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setLicensedVillainRevolverPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// PHYSICIAN
                        .then(CommandManager.literal("setPhysicianPillPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setPhysicianPillPrice(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// ROBOT
                        .then(CommandManager.literal("setRobotAbilityDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setRobotAbilityDuration(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setRobotAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> ConfigHelper.setRobotAbilityCooldown(context.getSource().getWorld(), IntegerArgumentType.getInteger(context, "value"))
                        ))))
            );
        }

        // Noelle's Roles
        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            root.then(
                CommandManager.literal("noellesroles")
                        .then(CommandManager.literal("enableMorphPsychosis").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setInsanePlayersSeeMorphs(null, BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// GENERAL
                        .then(CommandManager.literal("setShitpostRoles").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setShitpostRoles(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setGeneralCooldownTicks").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setGeneralCooldownTicks(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// VOODOO
                        .then(CommandManager.literal("setVoodooNonKillerDeaths").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setVoodooNonKillerDeaths(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setVoodooShotLikeEvil").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setVoodooShotLikeEvil(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// CONDUCTOR
                        .then(CommandManager.literal("setPlayerCountToMakeConducterKeyVisible").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setPlayerCountToMakeConducterKeyVisible(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// BARTENDER
                        .then(CommandManager.literal("setMaximumDefenseVials").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setMaximumDefenseVials(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDefenseVialPrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setDefenseVialPrice(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setDefenseMaximumTime").then(CommandManager.argument("value", IntegerArgumentType.integer(-1))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setDefenseMaximumTime(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// TRAPPER
                        .then(CommandManager.literal("setRoleMinePrice").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setRoleMinePrice(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setTrapperSeesNames").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setTrapperSeesNames(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// GUESSER
                        .then(CommandManager.literal("setAllowCivillianGuessers").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setAllowCivillianGuessers(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setGuesserDiesAfterIncorrectGuess").then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setGuesserDiesAfterIncorrectGuess(com.mojang.brigadier.arguments.StringArgumentType.getString(context, "mode"))
                        ))))

                        .then(CommandManager.literal("setGuesserCanUseInstinct").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setGuesserCanUseInstinct(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// INTROVERT
                        .then(CommandManager.literal("setIntrovertDisableRange").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setIntrovertDisableRange(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// INFECTED
                        .then(CommandManager.literal("setInfectedKillTime").then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setInfectedKillTime(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setInfectedCoughChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setInfectedCoughChance(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// RECON
                        .then(CommandManager.literal("setReconsSeeNames").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setReconsSeeNames(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// EXECUTION
                        .then(CommandManager.literal("setExecutionCanPickUpGun").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.setExecutionCanPickUpGun(BoolArgumentType.getBool(context, "enabled"))
                        ))))
            );
        }

        // More Shooter Punishements
        if (cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.isLoaded()) {
            root.then(
                CommandManager.literal("shooterpunishments")
                        .then(CommandManager.literal("setMode").then(CommandManager.argument("mode", com.mojang.brigadier.arguments.StringArgumentType.word())
                        .executes(update(
                                context -> {
                                    String mode = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "mode");
                                    context.getSource().getServer().getCommandManager().getDispatcher().execute("setShootInnocentPunishment " + mode, context.getSource());
                                    cat.rezelyn.watheextended.api.config.shooterpunishments.ConfigHelper.setLastKnownMode(mode);
                                }
                        ))))
            );
        }

        // Starry Express
        if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
            root.then(
                CommandManager.literal("starexpress")
                        /// STARSTUCK
                        .then(CommandManager.literal("setStarstruckTaskReducesCooldown").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckTaskReducesCooldown(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setStarstruckTaskCooldownReduction").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckTaskCooldownReduction(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setStarstruckAbilityCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityCooldown(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setStarstruckAbilityDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityDuration(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setStarstruckAbilityAffectsMovementSpeed").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityAffectsMovementSpeed(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setStarstruckAbilityWalkSpeed").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilityWalkSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"))
                        ))))

                        .then(CommandManager.literal("setStarstruckAbilitySprintSpeed").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setStarstruckAbilitySprintSpeed(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"))
                        ))))

                        /// MUZZLER
                        .then(CommandManager.literal("setMuzzlerTapeCooldown").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeCooldown(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setMuzzlerSuffocationTime").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerSuffocationTime(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setMuzzlerTapeTearCheckCount").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeTearCheckCount(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setMuzzlerTapeTearMoodChange").then(CommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0f, 1f))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerTapeTearMoodChange(com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "value"))
                        ))))

                        .then(CommandManager.literal("setMuzzlerKillIfCheckedAtZero").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerKillIfCheckedAtZero(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setMuzzlerDisplaySilencedTipDelay").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setMuzzlerDisplaySilencedTipDelay(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        /// ALLERGIC
                        .then(CommandManager.literal("setAllergicNothingChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicNothingChance(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setAllergicInstinctChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicInstinctChance(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setAllergicArmorChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicArmorChance(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setAllergicPoisonChance").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicPoisonChance(IntegerArgumentType.getInteger(context, "value"))
                        ))))

                        .then(CommandManager.literal("setAllergicInstinctDuration").then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.setAllergicInstinctDuration(IntegerArgumentType.getInteger(context, "value"))
                        ))))
            );
        }

        // Stupid Express
        if (cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) {
            root.then(
                CommandManager.literal("stupid_express")
                        /// NECROMANCER
                        .then(CommandManager.literal("setNecromancerHasShop").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setNecromancerHasShop(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// ARSONIST
                        .then(CommandManager.literal("setArsonistKeepsGameGoing").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setArsonistKeepsGameGoing(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// AMNESIAC
                        .then(CommandManager.literal("setBodiesGlowToAmnesiac").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setBodiesGlowToAmnesiac(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setAmnesiacGlowsDifferently").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setAmnesiacGlowsDifferently(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        /// LOVERS
                        .then(CommandManager.literal("setLoversKnowImmediately").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversKnowImmediately(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setLoversWinWithKillers").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversWinWithKillers(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setLoversWinWithCivilians").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversWinWithCivilians(BoolArgumentType.getBool(context, "enabled"))
                        ))))

                        .then(CommandManager.literal("setLoversGlowToEachother").then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(update(
                                context -> cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.setLoversGlowToEachother(BoolArgumentType.getBool(context, "enabled"))
                        ))))
            );
        }

        // HML
        root.then(
            CommandManager.literal("hml")
                    .then(CommandManager.literal("setModifierMaximum").then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                    .executes(update(
                            context -> cat.rezelyn.watheextended.api.config.hml.ConfigHelper.setModifierMaximum(IntegerArgumentType.getInteger(context, "value"))
                    ))))

                    .then(CommandManager.literal("setModifierMultiplier").then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                    .executes(update(
                            context -> cat.rezelyn.watheextended.api.config.hml.ConfigHelper.setModifierMultiplier(IntegerArgumentType.getInteger(context, "value"))
                    ))))
        );

        dispatcher.register(root);
    }
}
