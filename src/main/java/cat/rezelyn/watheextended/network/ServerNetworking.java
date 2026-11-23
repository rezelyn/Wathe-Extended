package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.command.PronounsCommand;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.game.roles.swapper.SwapperTeleportPayload;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;

/** Registers Wathe gameplay payloads and restores state when a player joins. */
public final class ServerNetworking {
  private ServerNetworking() {}

  public static void register() {
    ConfigSchema.register();
    PayloadTypeRegistry.playS2C().register(SwapperTeleportPayload.ID, SwapperTeleportPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(PronounsManager.UpdatePayload.ID, PronounsManager.UpdatePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(PronounsManager.SyncPayload.ID, PronounsManager.SyncPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(LastStand.LastStandPayload.ID, LastStand.LastStandPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(PlayerMovement.SetPronePayload.ID, PlayerMovement.SetPronePayload.CODEC);

    ServerPlayNetworking.registerGlobalReceiver(PronounsManager.UpdatePayload.ID,
        (payload, context) -> setPronouns(context.player(), payload.pronouns()));
    ServerPlayNetworking.registerGlobalReceiver(PlayerMovement.SetPronePayload.ID, PlayerMovement::handleSetProne);
    ServerPlayerEvents.JOIN.register(ServerNetworking::onPlayerJoin);
    ServerPlayerEvents.LEAVE.register(player -> {
      if (!player.isSpectator()) {
        var world = player.getServerWorld().getServer().getOverworld();
        WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayersUUID(player.getUuid());
      }
    });
  }

  static void setPronouns(ServerPlayerEntity player, String value) {
    PronounsManager.set(player.getUuid(), value.strip());
    PronounsCommand.broadcastAll(player.getCommandSource(), player.getUuid(), PronounsManager.get(player.getUuid()));
  }

  private static void onPlayerJoin(ServerPlayerEntity joining) {
    ServerConfig.sendToPlayer(joining);
    PronounsManager.getAll()
        .forEach(
            (uuid, pronouns) ->
                ServerPlayNetworking.send(
                    joining, new PronounsManager.SyncPayload(uuid, pronouns)));

    ServerWorld overworld = joining.getServerWorld().getServer().getOverworld();
    if (GameStatus.State(overworld)
        && (GameWorldComponent.KEY.get(overworld).getRole(joining) == null
            || joining.isSpectator()
            || WatheExtendedWorldComponent.KEY.get(overworld).isPlayerKilled(joining.getUuid()))) {
      var spawn = MapVariablesWorldComponent.KEY.get(overworld).getSpectatorSpawnPos();
      joining.changeGameMode(GameMode.SPECTATOR);
      joining.teleportTo(
          new TeleportTarget(
              overworld, spawn.pos, Vec3d.ZERO, spawn.yaw, spawn.pitch, TeleportTarget.NO_OP));
    }
    PlayerItem.applyItemState(joining, overworld);
  }
}
