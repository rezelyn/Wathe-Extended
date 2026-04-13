package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.game.PronounsManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public final class PronounsCommand {

    private PronounsCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pronouns").then(CommandManager.literal("set").then(CommandManager.argument("pronouns", StringArgumentType.greedyString()).executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            String raw = StringArgumentType.getString(context, "pronouns").trim();
            UUID uuid = player.getUuid();
            PronounsManager.set(uuid, raw);
            String pronouns = PronounsManager.get(uuid);
            broadcastAll(context.getSource(), uuid, pronouns);
            context.getSource().sendFeedback(() -> Text.translatable("command.watheextended.pronouns.set", pronouns), false);
            return 1;
        }))).then(CommandManager.literal("clear").executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            UUID uuid = player.getUuid();
            PronounsManager.clear(uuid);
            broadcastAll(context.getSource(), uuid, "");
            context.getSource().sendFeedback(() -> Text.translatable("command.watheextended.pronouns.cleared"), false);
            return 1;
        }).then(CommandManager.argument("targets", EntityArgumentType.entities()).requires(source -> source.hasPermissionLevel(2)).executes(context -> {
            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                UUID uuid = player.getUuid();
                PronounsManager.clear(uuid);
                broadcastAll(context.getSource(), uuid, "");
                context.getSource().sendFeedback(() -> Text.translatable("command.watheextended.pronouns.cleared_other", player.getName()), true);
            }
            return 1;
        }))));
    }

    public static void broadcastAll(ServerCommandSource source, UUID uuid, String pronouns) {
        if (source.getServer() == null) return;
        PronounsManager.SyncPayload payload = new PronounsManager.SyncPayload(uuid, pronouns);
        source.getServer().execute(() -> {
            for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, payload);
            }
        });
    }
}
