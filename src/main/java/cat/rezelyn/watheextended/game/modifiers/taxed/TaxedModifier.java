package cat.rezelyn.watheextended.game.modifiers.taxed;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TaxedModifier {

  private static final Map<UUID, List<Long>> killTimestamps = new ConcurrentHashMap<>();

  private TaxedModifier() {}

  public static void recordKill(UUID killerUuid) {
    killTimestamps
        .computeIfAbsent(killerUuid, kill -> new ArrayList<>())
        .add(System.currentTimeMillis());
  }

  private static int countKillsInWindow(UUID uuid) {
    List<Long> times = killTimestamps.get(uuid);
    if (times == null) return 0;
    long windowMs = WatheExtendedServerConfig.taxedKillWindowSeconds * 1000L;
    long cutoff = System.currentTimeMillis() - windowMs;
    Iterator<Long> it = times.iterator();
    while (it.hasNext()) {
      if (it.next() < cutoff) it.remove();
    }
    return times.size();
  }

  public static int applyTaxIfEligible(UUID killerUuid, int amount) {
    int killCount = countKillsInWindow(killerUuid);
    int threshold = WatheExtendedServerConfig.taxedKillThreshold;
    if (killCount <= threshold) return amount;
    float reduction = WatheExtendedServerConfig.taxedCoinReduction;
    return (int) Math.floor(amount * (1.0f - reduction));
  }

  public static void clearAll() {
    killTimestamps.clear();
  }
}
