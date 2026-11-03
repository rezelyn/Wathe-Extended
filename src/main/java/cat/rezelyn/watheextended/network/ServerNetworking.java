package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

/**
 * Server-side networking <br>
 * <br>
 * <b>Responsibilities:</b>
 *
 * <ul>
 *   <li>Registering the codecs of every S2C and C2S payload
 *   <li>Handling config, pronouns, preset and prone requests from clients
 *   <li>Syncing state to players joining the server
 * </ul>
 */
public final class ServerNetworking {

  private static final int PERMISSION_LEVEL = 2;
  private static final int CONFIG_COMMAND_PERMISSION_LEVEL = 4;
  private static final String COMMAND_CHANGE_PREFIX = "cmd:";

  private ServerNetworking() {}

  public static void register() {
    registerPayloadTypes();
    registerReceivers();
    registerConnectionEvents();
  }

  /** Registers the codecs of every S2C and C2S payload */
  private static void registerPayloadTypes() {
    // Config
    PayloadTypeRegistry.playS2C()
        .register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
    PayloadTypeRegistry.playC2S()
        .register(ServerConfig.ChangePayload.ID, ServerConfig.ChangePayload.CODEC);
    // Pronouns
    PayloadTypeRegistry.playC2S()
        .register(PronounsManager.UpdatePayload.ID, PronounsManager.UpdatePayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PronounsManager.SyncPayload.ID, PronounsManager.SyncPayload.CODEC);
    // Last Stand
    PayloadTypeRegistry.playS2C()
        .register(LastStand.LastStandPayload.ID, LastStand.LastStandPayload.CODEC);
    // Presets
    PayloadTypeRegistry.playC2S()
        .register(PresetManager.ActionPayload.ID, PresetManager.ActionPayload.CODEC);
    PayloadTypeRegistry.playC2S()
        .register(PlayerMovement.SetPronePayload.ID, PlayerMovement.SetPronePayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PresetManager.ListPayload.ID, PresetManager.ListPayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PresetManager.ResultPayload.ID, PresetManager.ResultPayload.CODEC);
  }

  private static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(
        ServerConfig.ChangePayload.ID, ServerNetworking::handleConfigChange);
    ServerPlayNetworking.registerGlobalReceiver(
        PronounsManager.UpdatePayload.ID, ServerNetworking::handlePronounsUpdate);
    ServerPlayNetworking.registerGlobalReceiver(
        PresetManager.ActionPayload.ID, ServerNetworking::handlePresetAction);
    ServerPlayNetworking.registerGlobalReceiver(
        PlayerMovement.SetPronePayload.ID, PlayerMovement::handleSetProne);
  }

  /**
   * Applies config changes sent from the client config screen, then re-syncs everyone
   *
   * <p>Entries whose key starts with {@value #COMMAND_CHANGE_PREFIX} are executed as silent
   * commands; all others are applied to the {@link ServerConfig} registry in one batch. Requires
   * permission level {@value #PERMISSION_LEVEL}
   */
  private static void handleConfigChange(
      ServerConfig.ChangePayload payload, ServerPlayNetworking.Context context) {
    ServerPlayerEntity player = context.player();
    if (!player.hasPermissionLevel(PERMISSION_LEVEL)) return;

    MinecraftServer server = context.server();
    server.execute(
        () -> {
          Map<String, String> registryChanges = new LinkedHashMap<>();
          for (Map.Entry<String, String> entry : payload.changes().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(COMMAND_CHANGE_PREFIX)) {
              runConfigCommand(server, player, key.substring(COMMAND_CHANGE_PREFIX.length()));
            } else {
              registryChanges.put(key, entry.getValue());
            }
          }
          if (!registryChanges.isEmpty())
            ServerConfig.applyChanges(registryChanges, server.getOverworld());
          ServerConfig.broadcastToAll(server);
        });
  }

  /** Runs a command requested by the config screen; newline-separated commands run in order */
  private static void runConfigCommand(
      MinecraftServer server, ServerPlayerEntity player, String command) {
    for (String single : command.split("\n")) {
      try {
        server
            .getCommandManager()
            .getDispatcher()
            .execute(
                single,
                player.getCommandSource().withLevel(CONFIG_COMMAND_PERMISSION_LEVEL).withSilent());
      } catch (Throwable ignored) {
      }
    }
  }

  /** Stores player's pronouns and broadcasts the result to all players */
  private static void handlePronounsUpdate(
      PronounsManager.UpdatePayload payload, ServerPlayNetworking.Context context) {
    UUID uuid = context.player().getUuid();
    String pronouns = payload.pronouns().trim();
    MinecraftServer server = context.server();

    server.execute(
        () -> {
          PronounsManager.set(uuid, pronouns);
          PronounsManager.SyncPayload sync =
              new PronounsManager.SyncPayload(uuid, PronounsManager.get(uuid));
          for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(target, sync);
          }
        });
  }

  /**
   * Handles preset management requests (list, save, load, override, delete)
   *
   * <p>Requires permission level {@value #PERMISSION_LEVEL}. Exceptions are logged and reported
   * back to the requester through {@link PresetManager.ResultPayload}
   */
  private static void handlePresetAction(
      PresetManager.ActionPayload payload, ServerPlayNetworking.Context context) {
    ServerPlayerEntity player = context.player();
    if (!player.hasPermissionLevel(PERMISSION_LEVEL)) return;

    MinecraftServer server = context.server();
    server.execute(
        () -> {
          try {
            runPresetAction(payload, player, server);
          } catch (Exception exception) {
            WatheExtended.LOGGER.error("Preset action failed", exception);
            String message =
                exception.getMessage() == null ? "Preset action failed" : exception.getMessage();
            ServerPlayNetworking.send(
                player, new PresetManager.ResultPayload(false, payload.action(), message));
          }
        });
  }

  /** Executes a single preset action against the current server config snapshot */
  private static void runPresetAction(
      PresetManager.ActionPayload payload, ServerPlayerEntity player, MinecraftServer server)
      throws IOException {
    ServerWorld overworld = server.getOverworld();

    switch (payload.action()) {
      case "list" -> PresetManager.sendList(player);
      case "save" -> {
        PresetManager.save(
            payload.name(), payload.description(), player, ServerConfig.snapshot(overworld));
        PresetManager.sendList(player);
      }
      case "load" -> {
        PresetManager.apply(PresetManager.load(payload.id()), overworld);
        ServerConfig.broadcastToAll(server);
        ServerPlayNetworking.send(player, new PresetManager.ResultPayload(true, "load", ""));
      }
      case "override" -> {
        PresetManager.Preset preset = PresetManager.load(payload.id());
        PresetManager.save(
            preset.metadata().name(),
            preset.metadata().description(),
            player,
            ServerConfig.snapshot(overworld));
        PresetManager.sendList(player);
        ServerPlayNetworking.send(player, new PresetManager.ResultPayload(true, "override", ""));
      }
      case "delete" -> {
        PresetManager.delete(payload.id());
        PresetManager.sendList(player);
      }
      default -> throw new IllegalArgumentException("Unknown preset action");
    }
  }

  /** Registers the player join hook */
  private static void registerConnectionEvents() {
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> {
          ServerPlayerEntity joining = handler.player;
          server.execute(() -> onPlayerJoin(joining, server));
        });
  }

  /**
   * Syncs config, presets and all known pronouns to a joining player and restores their in-game
   * state: players killed during a running game rejoin as spectators, and item state is re-applied
   */
  private static void onPlayerJoin(ServerPlayerEntity joining, MinecraftServer server) {
    ServerConfig.sendToPlayer(joining);
    PresetManager.sendList(joining);
    PronounsManager.getAll()
        .forEach(
            (uuid, pronouns) ->
                ServerPlayNetworking.send(
                    joining, new PronounsManager.SyncPayload(uuid, pronouns)));

    try {
      ServerWorld overworld = server.getOverworld();
      GameWorldComponent game = GameWorldComponent.KEY.get(overworld);
      WatheExtendedWorldComponent extended = WatheExtendedWorldComponent.KEY.get(overworld);

      if (game.isRunning() && extended.isPlayerKilled(joining.getUuid())) {
        joining.changeGameMode(GameMode.SPECTATOR);
      }
      PlayerItem.applyItemState(joining, overworld);
    } catch (Throwable ignored) {
    }
  }
}
