package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.network.ServerConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GamemodeRulesCommand {

  private static int sync(CommandContext<ServerCommandSource> context) {
    try {
      ServerConfig.broadcastToAll(context.getSource().getServer());
    } catch (Throwable ignored) {
    }
    return 1;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    /// PLAYER COLLISIONS
    dispatcher.register(
        CommandManager.literal("watheextended:enableCollisions")
            .requires(source -> source.hasPermissionLevel(2))
            .then(
                CommandManager.argument("enabled", BoolArgumentType.bool())
                    .executes(GamemodeRulesCommand::setPlayerCollisions)));
    /// WORLD PROTECTION
    dispatcher.register(
        CommandManager.literal("watheextended:enableWorldProtection")
            .requires(source -> source.hasPermissionLevel(2))
            .then(
                CommandManager.argument("enabled", BoolArgumentType.bool())
                    .executes(GamemodeRulesCommand::setWorldProtection)));
    /// ITEM BOUNDS CHECK
    dispatcher.register(
        CommandManager.literal("watheextended:enableItemBoundsCheck")
            .requires(source -> source.hasPermissionLevel(2))
            .then(
                CommandManager.argument("enabled", BoolArgumentType.bool())
                    .executes(GamemodeRulesCommand::setItemBoundsCheck)));
    /// JUMP MODE
    dispatcher.register(
        CommandManager.literal("watheextended:jumpMode")
            .requires(source -> source.hasPermissionLevel(2))
            .then(
                CommandManager.literal("DEFAULT")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set("jumpMode", "DEFAULT");
                          return sync(ctx);
                        }))
            .then(
                CommandManager.literal("LOBBY")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set("jumpMode", "LOBBY");
                          return sync(ctx);
                        }))
            .then(
                CommandManager.literal("EVERYWHERE")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set("jumpMode", "EVERYWHERE");
                          return sync(ctx);
                        })));
    /// SHOOTER PUNISHMENTS
    dispatcher.register(
        CommandManager.literal("watheextended:shootInnocentPunishmentMode")
            .requires(source -> source.hasPermissionLevel(2))
            .then(
                CommandManager.literal("DEFAULT")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set("shootInnocentPunishmentMode", "DEFAULT");
                          return sync(ctx);
                        }))
            .then(
                CommandManager.literal("PREVENT_PICKUP")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set(
                              "shootInnocentPunishmentMode", "PREVENT_PICKUP");
                          return sync(ctx);
                        }))
            .then(
                CommandManager.literal("KILL_SHOOTER")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set(
                              "shootInnocentPunishmentMode", "KILL_SHOOTER");
                          return sync(ctx);
                        }))
            .then(
                CommandManager.literal("KILL_BOTH")
                    .executes(
                        ctx -> {
                          WatheExtendedServerConfig.set("shootInnocentPunishmentMode", "KILL_BOTH");
                          return sync(ctx);
                        })));
  }

  private static int setPlayerCollisions(CommandContext<ServerCommandSource> context) {
    boolean enabled = BoolArgumentType.getBool(context, "enabled");
    ServerCommandSource source = context.getSource();
    try {
      WatheExtendedWorldComponent component =
          WatheExtendedWorldComponent.KEY.get(source.getWorld());
      component.setPlayerCollisionsEnabled(enabled);
    } catch (Throwable throwable) {
      return 0;
    }
    return 1;
  }

  private static int setWorldProtection(CommandContext<ServerCommandSource> context) {
    boolean enabled = BoolArgumentType.getBool(context, "enabled");
    ServerCommandSource source = context.getSource();
    try {
      WatheExtendedWorldComponent component =
          WatheExtendedWorldComponent.KEY.get(source.getWorld());
      component.setBlockInteractionsProtected(enabled);
    } catch (Throwable throwable) {
      return 0;
    }
    return 1;
  }

  private static int setItemBoundsCheck(CommandContext<ServerCommandSource> context) {
    boolean enabled = BoolArgumentType.getBool(context, "enabled");
    ServerCommandSource source = context.getSource();
    try {
      WatheExtendedWorldComponent component =
          WatheExtendedWorldComponent.KEY.get(source.getWorld());
      component.setItemBoundsCheckEnabled(enabled);
    } catch (Throwable throwable) {
      return 0;
    }
    return 1;
  }
}
