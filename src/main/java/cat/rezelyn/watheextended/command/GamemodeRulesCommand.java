package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
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
        dispatcher.register(CommandManager.literal("watheextended:enableCollisions").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(GamemodeRulesCommand::setPlayerCollisions)));
        dispatcher.register(CommandManager.literal("watheextended:enableWorldProtection").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(GamemodeRulesCommand::setWorldProtection)));
        dispatcher.register(CommandManager.literal("watheextended:enableItemBoundsCheck").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes(GamemodeRulesCommand::setItemBoundsCheck)));
        dispatcher.register(CommandManager.literal("watheextended:jumpMode").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("DEFAULT").executes(ctx -> {
            WatheExtendedServerConfig.setJumpMode("DEFAULT");
            return sync(ctx);
        })).then(CommandManager.literal("LOBBY").executes(ctx -> {
            WatheExtendedServerConfig.setJumpMode("LOBBY");
            return sync(ctx);
        })).then(CommandManager.literal("EVERYWHERE").executes(ctx -> {
            WatheExtendedServerConfig.setJumpMode("EVERYWHERE");
            return sync(ctx);
        })));
    }

    private static int setPlayerCollisions(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ServerCommandSource source = context.getSource();
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(source.getWorld());
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
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(source.getWorld());
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
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(source.getWorld());
            component.setItemBoundsCheckEnabled(enabled);
        } catch (Throwable throwable) {
            return 0;
        }
        return 1;
    }
}
