package cat.rezelyn.watheextended.game.modifiers.adaptive;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.network.ServerPlayerEntity;

public final class AdaptiveModifier {

  public static final ThreadLocal<KillContext> CURRENT_KILL = new ThreadLocal<>();
  private static final Map<UUID, String> lastKillMethod = new ConcurrentHashMap<>();

  private AdaptiveModifier() {}

  public static int applyAdaptive(ServerPlayerEntity killer, int amount) {
    KillContext kill = CURRENT_KILL.get();
    if (kill == null || !kill.killerUuid().equals(killer.getUuid())) return amount;
    try {
      if (PlayerPsychoComponent.KEY.get(killer).getPsychoTicks() > 0)
        return amount; // exclude psycho mode
    } catch (Throwable t) {
      return amount;
    }

    String method = kill.deathReason();
    UUID uuid = kill.killerUuid();
    String last = lastKillMethod.get(uuid);
    lastKillMethod.put(uuid, method);

    if (last == null) return amount;
    if (last.equals(method)) {
      // same method as last kill: apply penalty
      float penalty = WatheExtendedServerConfig.adaptivePenaltyReduction;
      return (int) Math.floor(amount * (1.0f - penalty));
    } else {
      // different method from last kill: apply bonus
      float bonus = WatheExtendedServerConfig.adaptiveBonusMultiplier;
      return (int) Math.floor(amount * (1.0f + bonus));
    }
  }

  public static void clearAll() {
    lastKillMethod.clear();
  }

  public record KillContext(UUID killerUuid, String deathReason) {}
}
