package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.pronouns.PronounsManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public final class PronounsCommand {

    private PronounsCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("pronouns")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("pronouns", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                            String raw = StringArgumentType.getString(ctx, "pronouns").trim();
                                            UUID uuid = player.getUuid();
                                            PronounsManager.set(uuid, raw);
                                            String stored = PronounsManager.get(uuid);
                                            broadcastAll(ctx.getSource(), uuid, stored);
                                            ctx.getSource().sendFeedback(
                                                    () -> Text.translatable("command.watheextended.pronouns.set", stored),
                                                    false);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("clear")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                    UUID uuid = player.getUuid();
                                    PronounsManager.clear(uuid);
                                    broadcastAll(ctx.getSource(), uuid, "");
                                    ctx.getSource().sendFeedback(
                                            () -> Text.translatable("command.watheextended.pronouns.cleared"),
                                            false);
                                    return 1;
                                }))
        );
    }

    public static void broadcastAll(ServerCommandSource source, UUID uuid, String pronouns) {
        if (source.getServer() == null) return;
        PronounsManager.SyncPayload payload = new PronounsManager.SyncPayload(uuid, pronouns);
        source.getServer().execute(() -> {
            for (ServerPlayerEntity p : source.getServer().getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(p, payload);
            }
        });
    }
}
