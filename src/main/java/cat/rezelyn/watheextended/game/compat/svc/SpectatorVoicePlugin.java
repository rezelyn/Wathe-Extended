package cat.rezelyn.watheextended.game.compat.svc;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.JoinGroupEvent;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.compat.TrainVoicePlugin;
import net.minecraft.server.network.ServerPlayerEntity;

public final class SpectatorVoicePlugin implements VoicechatPlugin {
  public static void leaveSpectatorGroup(ServerPlayerEntity player) {
    if (TrainVoicePlugin.isVoiceChatMissing()) return;
    var connection = TrainVoicePlugin.SERVER_API.getConnectionOf(player.getUuid());
    if (connection == null) return;
    var group = connection.getGroup();
    if (group != null && TrainVoicePlugin.GROUP_ID.equals(group.getId())) {
      connection.setGroup(null);
    }
  }

  @Override
  public String getPluginId() {
    return "watheextended";
  }

  @Override
  public void registerEvents(EventRegistration registration) {
    registration.registerEvent(JoinGroupEvent.class, event -> {
      var group = event.getGroup();
      if (group == null || !TrainVoicePlugin.GROUP_ID.equals(group.getId())) return;
      if (!(event.getConnection().getPlayer().getPlayer() instanceof ServerPlayerEntity player)) return;

      var world = player.getServerWorld().getServer().getOverworld();
      if (!GameStatus.State(world)) return;

      var round = WatheExtendedWorldComponent.KEY.get(world);
      if (!player.isSpectator() || (round.hasRoundDeathRecord()
          && !round.isPlayerKilled(player.getUuid())
          && GameWorldComponent.KEY.get(world).getRole(player) != null)) {
        event.cancel();
      }
    });

    registration.registerEvent(PlayerConnectedEvent.class, event -> {
      if (!(event.getConnection().getPlayer().getPlayer() instanceof ServerPlayerEntity player)) return;
      var server = player.getServerWorld().getServer();
      server.execute(() -> {
        if (server.getPlayerManager().getPlayer(player.getUuid()) != player) return;
        if (!player.isSpectator()) {
          leaveSpectatorGroup(player);
        } else if (GameStatus.State(server.getOverworld())) {
          TrainVoicePlugin.addPlayer(player.getUuid());
        }
      });
    });
  }
}
