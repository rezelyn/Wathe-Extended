package cat.rezelyn.watheextended.network;

import cat.rezelyn.orelib.api.network.ServerConfigs;
import java.util.List;
import java.util.Map;
import net.minecraft.server.world.ServerWorld;

/** Propagates gameplay changes and tracks edits made outside the configuration screen. */
public final class ConfigSync {
  private static List<String> disabledRoles = List.of();
  private static List<String> disabledModifiers = List.of();
  private static Map<String, String> commands = Map.of();
  private static boolean gameplayDirty;

  private ConfigSync() {}

  static void gameplayChanged() {
    gameplayDirty = true;
  }

  public static void tick(ServerWorld world) {
    if (world.getTime() % 20 == 0) {
      var roles = ConfigSchema.disabledRoles(world);
      var modifiers = ConfigSchema.disabledModifiers(world);
      if (!disabledRoles.equals(roles) || !disabledModifiers.equals(modifiers)) {
        disabledRoles = List.copyOf(roles);
        disabledModifiers = List.copyOf(modifiers);
        gameplayDirty = true;
      }
      var latest = CommandSettings.snapshot(world);
      if (!commands.equals(latest)) {
        commands = latest;
        ConfigSchema.refreshCommands(latest.keySet());
        if (!gameplayDirty) ServerConfigs.invalidate(ServerConfig.CONFIG_ID);
      }
    }
    if (gameplayDirty) {
      gameplayDirty = false;
      ServerConfig.broadcastToAll(world.getServer());
    }
  }
}
