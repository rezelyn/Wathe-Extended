package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.command.PronounsCommand;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

/** Registers Wathe gameplay payloads and restores state when a player joins. */
public final class ServerNetworking {
  private ServerNetworking() {}

  public static void register() {
    ConfigSchema.register();
    PayloadTypeRegistry.playS2C().register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(PronounsManager.UpdatePayload.ID, PronounsManager.UpdatePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(PronounsManager.SyncPayload.ID, PronounsManager.SyncPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(LastStand.LastStandPayload.ID, LastStand.LastStandPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(PlayerMovement.SetPronePayload.ID, PlayerMovement.SetPronePayload.CODEC);

    ServerPlayNetworking.registerGlobalReceiver(PronounsManager.UpdatePayload.ID,
        (payload, context) -> setPronouns(context.player(), payload.pronouns()));
    ServerPlayNetworking.registerGlobalReceiver(PlayerMovement.SetPronePayload.ID, PlayerMovement::handleSetProne);
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.player, server));
  }

  static void setPronouns(ServerPlayerEntity player, String value) {
    PronounsManager.set(player.getUuid(), value.strip());
    PronounsCommand.broadcastAll(player.getCommandSource(), player.getUuid(), PronounsManager.get(player.getUuid()));
  }

  private static void onPlayerJoin(ServerPlayerEntity joining, MinecraftServer server) {
    ServerConfig.sendToPlayer(joining);
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
