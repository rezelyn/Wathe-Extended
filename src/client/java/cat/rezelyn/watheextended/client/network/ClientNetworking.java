package cat.rezelyn.watheextended.client.network;

import cat.rezelyn.watheextended.client.render.hud.LastStandRenderer;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.utils.PronounsCache;
import cat.rezelyn.watheextended.game.LastStand;
import cat.rezelyn.watheextended.network.ClientConfig;
import cat.rezelyn.watheextended.network.PronounsManager;
import cat.rezelyn.watheextended.network.ServerConfig;
import cat.rezelyn.watheextended.game.roles.swapper.SwapperTeleportPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Registers the client receivers for every payload the server sends and forwards them */
public final class ClientNetworking {

  private ClientNetworking() {}

  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(
        SwapperTeleportPayload.ID, ClientNetworking::handleSwapperTeleport);
    ClientPlayNetworking.registerGlobalReceiver(
        LastStand.LastStandPayload.ID, ClientNetworking::handleLastStand);
    ClientPlayNetworking.registerGlobalReceiver(
        ServerConfig.SyncPayload.ID, ClientNetworking::handleConfigSync);
    ClientPlayNetworking.registerGlobalReceiver(
        PronounsManager.SyncPayload.ID, ClientNetworking::handlePronounsSync);
  }

  private static void handleSwapperTeleport(
      SwapperTeleportPayload swap, ClientPlayNetworking.Context context) {
    var player = context.player();
    var partner = player.getWorld().getEntityById(swap.partnerId());
    if (partner != null) {
      var pos = swap.partnerDestination();
      partner.updateTrackedPositionAndAngles(
          pos.x, pos.y, pos.z, partner.getYaw(), partner.getPitch(), 0);
      partner.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
    }
    
    var pos = swap.destination();
    player.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
  }

  private static void handleLastStand(
      LastStand.LastStandPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> LastStandRenderer.start(payload.totalTicks()));
  }

  private static void handleConfigSync(
      ServerConfig.SyncPayload payload, ClientPlayNetworking.Context context) {
    context
        .client()
        .execute(() -> {
          ClientConfig.setRemoteServer(true);
          ClientConfig.update(payload.data());
          GuidebookScreen.invalidateIfOpen();
        });
  }

  private static void handlePronounsSync(
      PronounsManager.SyncPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> PronounsCache.set(payload.uuid(), payload.pronouns()));
  }
}
