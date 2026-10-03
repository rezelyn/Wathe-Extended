package cat.rezelyn.watheextended.game.mood;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.player.PlayerEntity;

public final class DepressedMoodState {
  private static final Map<PlayerEntity, Integer> DEPRESSED_TICKS = new WeakHashMap<>();

  private DepressedMoodState() {}

  public static synchronized int tick(PlayerEntity player) {
    if (!isDepressed(player)) {
      DEPRESSED_TICKS.remove(player);
      return 0;
    }
    int duration = DEPRESSED_TICKS.getOrDefault(player, 0) + 1;
    DEPRESSED_TICKS.put(player, duration);
    return duration;
  }

  public static synchronized int ticks(PlayerEntity player) {
    return isDepressed(player) ? DEPRESSED_TICKS.getOrDefault(player, 0) : 0;
  }

  public static boolean isDepressed(PlayerEntity player) {
    try {
      return player != null && PlayerMoodComponent.KEY.get(player).isLowerThanDepressed();
    } catch (Throwable ignored) {
      return false;
    }
  }
}
