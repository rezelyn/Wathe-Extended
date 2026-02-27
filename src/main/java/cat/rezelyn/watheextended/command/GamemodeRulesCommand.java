package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GamemodeRulesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("watheextended:enableCollisions")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(GamemodeRulesCommand::setPlayerCollisions)
                        )
        );

        dispatcher.register(
                CommandManager.literal("watheextended:enableWorldProtection")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(GamemodeRulesCommand::setWorldProtection)
                        )
        );
    }

    private static int setPlayerCollisions(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ServerCommandSource source = context.getSource();
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
            wec.setPlayerCollisionsEnabled(enabled);
            source.sendFeedback(() -> Text.translatable(
                            enabled
                                    ? "command.watheextended.gamerules.collisions.enabled"
                                    : "command.watheextended.gamerules.collisions.disabled"),
                    true);
        } catch (Throwable t) {
            return 0;
        }
        return 1;
    }

    private static int setWorldProtection(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ServerCommandSource source = context.getSource();
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
            wec.setBlockInteractionsProtected(enabled);
            source.sendFeedback(() -> Text.translatable(
                            enabled
                                    ? "command.watheextended.gamerules.worldprotection.enabled"
                                    : "command.watheextended.gamerules.worldprotection.disabled"),
                    true);
        } catch (Throwable t) {
            return 0;
        }
        return 1;
    }
}

