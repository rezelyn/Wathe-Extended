package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.agmas.harpymodloader.Harpymodloader;

public final class MapEffectCommand {
    private MapEffectCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("watheextended:mapEffect")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("gamemode")
                                .then(CommandManager.literal("MODDED_MURDER").executes(ctx -> setGameMode(ctx.getSource(), "MODDED_MURDER", Harpymodloader.MODDED_GAMEMODE, false)))
                                .then(CommandManager.literal("MODDED_SECRET_MURDER").executes(ctx -> setGameMode(ctx.getSource(), "MODDED_SECRET_MURDER", Harpymodloader.SECRET_MODDED_GAMEMODE, false)))
                                .then(CommandManager.literal("MURDER").executes(ctx -> setGameMode(ctx.getSource(), "MURDER", WatheGameModes.MURDER, true)))
                                .then(CommandManager.literal("LOOSE_ENDS").executes(ctx -> setGameMode(ctx.getSource(), "LOOSE_ENDS", WatheGameModes.LOOSE_ENDS, true)))
                                .then(CommandManager.literal("SECRET_MURDER").executes(ctx -> setGameMode(ctx.getSource(), "SECRET_MURDER", WatheGameModes.SECRET_MURDER, true)))
                                .then(CommandManager.literal("DISCOVERY").executes(ctx -> setGameMode(ctx.getSource(), "DISCOVERY", WatheGameModes.DISCOVERY, true))))
                        .then(CommandManager.literal("time")
                                .then(CommandManager.literal("DAY").executes(ctx -> setTime(ctx.getSource(), "DAY", false)))
                                .then(CommandManager.literal("NIGHT").executes(ctx -> setTime(ctx.getSource(), "NIGHT", false)))
                                .then(CommandManager.literal("SUNDOWN").executes(ctx -> setTime(ctx.getSource(), "SUNDOWN", false))))
                        .then(CommandManager.literal("lobbyTime")
                                .then(CommandManager.literal("DAY").executes(ctx -> setTime(ctx.getSource(), "DAY", true)))
                                .then(CommandManager.literal("NIGHT").executes(ctx -> setTime(ctx.getSource(), "NIGHT", true)))
                                .then(CommandManager.literal("SUNDOWN").executes(ctx -> setTime(ctx.getSource(), "SUNDOWN", true))))
                        .then(CommandManager.literal("duration")
                                .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 60))
                                        .executes(ctx -> setDuration(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "minutes")))))));
    }

    private static int setGameMode(ServerCommandSource source, String selection, GameMode mode, boolean vanilla) {
        GameWorldComponent game = GameWorldComponent.KEY.get(source.getWorld());
        if (game.isRunning()) return 0;
        WatheExtendedWorldComponent.KEY.get(source.getWorld()).setConfiguredGameMode(selection);
        Harpymodloader.wantsToStartVannila = vanilla;
        game.sync();
        sync(source);
        return 1;
    }

    private static int setTime(ServerCommandSource source, String value, boolean lobby) {
        WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        if (lobby) component.setLobbyTimeOfDay(value);
        else component.setGameTimeOfDay(value);
        sync(source);
        return 1;
    }

    private static int setDuration(ServerCommandSource source, int minutes) {
        WatheExtendedWorldComponent.KEY.get(source.getWorld()).setGameDurationMinutes(minutes);
        sync(source);
        return 1;
    }

    private static void sync(ServerCommandSource source) {
        try { ServerConfig.broadcastToAll(source.getServer()); } catch (Throwable ignored) {}
    }
}
