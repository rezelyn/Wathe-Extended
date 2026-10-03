package cat.rezelyn.watheextended.game.roles.arsonist;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pro.fazeclan.river.stupid_express.role.arsonist.cca.DousedPlayerComponent;

public final class ArsonistDousedNotification {
  private static final Map<UUID, Long> PENDING = new HashMap<>();
  private static long ticks;

  private ArsonistDousedNotification() {}

  public static void register() {
    ServerTickEvents.END_SERVER_TICK.register(ArsonistDousedNotification::tick);
  }

  public static void schedule(ServerPlayerEntity player) {
    if (!WatheExtendedServerConfig.arsonistDousedNotificationEnabled) return;
    long delay = (long) WatheExtendedServerConfig.arsonistDousedNotificationDelay * 20L;
    PENDING.put(player.getUuid(), ticks + delay);
  }

  private static void tick(MinecraftServer server) {
    ticks++;
    PENDING
        .entrySet()
        .removeIf(
            entry -> {
              if (ticks < entry.getValue()) return false;
              ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
              if (WatheExtendedServerConfig.arsonistDousedNotificationEnabled
                  && player != null
                  && DousedPlayerComponent.KEY.get(player).isDoused()) {
                player.sendMessage(
                    Text.translatable("tip.watheextended.arsonist.doused")
                        .formatted(Formatting.GOLD),
                    true);
              }
              return true;
            });
  }
}
