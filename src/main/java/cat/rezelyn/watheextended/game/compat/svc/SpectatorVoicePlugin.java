package cat.rezelyn.watheextended.game.compat.svc;

import cat.rezelyn.watheextended.api.GameStatus;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import dev.doctor4t.wathe.compat.TrainVoicePlugin;
import net.minecraft.server.network.ServerPlayerEntity;

public final class SpectatorVoicePlugin implements VoicechatPlugin {
  @Override
  public String getPluginId() {
    return "watheextended";
  }

  @Override
  public void registerEvents(EventRegistration registration) {
    registration.registerEvent(PlayerConnectedEvent.class, event -> {
      if (!(event.getConnection().getPlayer().getPlayer() instanceof ServerPlayerEntity player)) return;
      var server = player.getServerWorld().getServer();
      server.execute(() -> {
        if (server.getPlayerManager().getPlayer(player.getUuid()) == player
            && player.isSpectator() && GameStatus.State(server.getOverworld())) {
          TrainVoicePlugin.addPlayer(player.getUuid());
        }
      });
    });
  }
}
