package cat.rezelyn.watheextended.client.network;

import cat.rezelyn.watheextended.client.render.hud.LastStandRenderer;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.utils.PronounsCache;
import cat.rezelyn.watheextended.game.LastStand;
import cat.rezelyn.watheextended.network.ClientConfig;
import cat.rezelyn.watheextended.network.PresetManager;
import cat.rezelyn.watheextended.network.PronounsManager;
import cat.rezelyn.watheextended.network.ServerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Registers the client receivers for every payload the server sends and forwards them */
public final class ClientNetworking {

  private ClientNetworking() {}

  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(
        LastStand.LastStandPayload.ID, ClientNetworking::handleLastStand);
    ClientPlayNetworking.registerGlobalReceiver(
        ServerConfig.SyncPayload.ID, ClientNetworking::handleConfigSync);
    ClientPlayNetworking.registerGlobalReceiver(
        PronounsManager.SyncPayload.ID, ClientNetworking::handlePronounsSync);
    ClientPlayNetworking.registerGlobalReceiver(
        PresetManager.ListPayload.ID, ClientNetworking::handlePresetList);
    ClientPlayNetworking.registerGlobalReceiver(
        PresetManager.ResultPayload.ID, ClientNetworking::handlePresetResult);
  }

  private static void handleLastStand(
      LastStand.LastStandPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> LastStandRenderer.start(payload.totalTicks()));
  }

  private static void handleConfigSync(
      ServerConfig.SyncPayload payload, ClientPlayNetworking.Context context) {
    ClientConfig.setRemoteServer(true);
    ClientConfig.update(payload.data());
    ConfigScreen.onCacheUpdated();
    GuidebookScreen.invalidateIfOpen();
  }

  private static void handlePronounsSync(
      PronounsManager.SyncPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> PronounsCache.set(payload.uuid(), payload.pronouns()));
  }

  private static void handlePresetList(
      PresetManager.ListPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ConfigScreen.onPresetList(payload));
  }

  private static void handlePresetResult(
      PresetManager.ResultPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ConfigScreen.onPresetResult(payload));
  }
}
