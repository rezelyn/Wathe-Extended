package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.network.PronounsManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class PronounsCommand {

  private PronounsCommand() {}

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    /// SET PROUNOUNS
    dispatcher.register(
        CommandManager.literal("pronouns")
            .then(
                CommandManager.literal("set")
                    .then(
                        CommandManager.argument("field", StringArgumentType.string())
                            .executes(
                                context -> {
                                  ServerPlayerEntity player =
                                      context.getSource().getPlayerOrThrow();
                                  return setPronouns(
                                      context.getSource(),
                                      player,
                                      StringArgumentType.getString(context, "field"));
                                })
                            .then(
                                CommandManager.argument("target", EntityArgumentType.player())
                                    .executes(
                                        context -> {
                                          ServerPlayerEntity sourcePlayer =
                                              context.getSource().getPlayerOrThrow();
                                          ServerPlayerEntity target =
                                              EntityArgumentType.getPlayer(context, "target");
                                          if (!sourcePlayer.getUuid().equals(target.getUuid())
                                              && !context.getSource().hasPermissionLevel(2)) {
                                            throw new SimpleCommandExceptionType(
                                                    Text.translatable("commands.generic.permission"))
                                                .create();
                                          }
                                          return setPronouns(
                                              context.getSource(),
                                              target,
                                              StringArgumentType.getString(context, "field"));
                                        }))))
            /// CLEAR PRONOUNS
            .then(
                CommandManager.literal("clear")
                    .executes(
                        context -> {
                          ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                          UUID uuid = player.getUuid();
                          PronounsManager.clear(uuid);
                          broadcastAll(context.getSource(), uuid, "");
                          context
                              .getSource()
                              .sendFeedback(
                                  () -> Text.translatable("command.watheextended.pronouns.cleared"),
                                  false);
                          return 1;
                        })
                    /// CLEAR TARGET
                    .then(
                        CommandManager.argument("targets", EntityArgumentType.entities())
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(
                                context -> {
                                  for (ServerPlayerEntity player :
                                      EntityArgumentType.getPlayers(context, "targets")) {
                                    UUID uuid = player.getUuid();
                                    PronounsManager.clear(uuid);
                                    broadcastAll(context.getSource(), uuid, "");
                                    context
                                        .getSource()
                                        .sendFeedback(
                                            () ->
                                                Text.translatable(
                                                    "command.watheextended.pronouns.cleared_other",
                                                    player.getName()),
                                            true);
                                  }
                                  return 1;
                                }))));
  }

  private static int setPronouns(
      ServerCommandSource source, ServerPlayerEntity target, String value) {
    String raw = value.trim();
    UUID uuid = target.getUuid();
    PronounsManager.set(uuid, raw);
    String pronouns = PronounsManager.get(uuid);
    broadcastAll(source, uuid, pronouns);
    if (source.getEntity() == target) {
      source.sendFeedback(
          () -> Text.translatable("command.watheextended.pronouns.set", pronouns), false);
    } else {
      source.sendFeedback(
          () ->
              Text.translatable(
                  "command.watheextended.pronouns.set_other", target.getName(), pronouns),
          true);
    }
    return 1;
  }

  public static void broadcastAll(ServerCommandSource source, UUID uuid, String pronouns) {
    if (source.getServer() == null) return;
    PronounsManager.SyncPayload payload = new PronounsManager.SyncPayload(uuid, pronouns);
    source
        .getServer()
        .execute(
            () -> {
              for (ServerPlayerEntity player :
                  source.getServer().getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, payload);
              }
            });
  }
}
