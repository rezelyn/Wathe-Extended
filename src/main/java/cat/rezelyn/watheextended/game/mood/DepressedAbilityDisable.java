package cat.rezelyn.watheextended.game.mood;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Shared server-side policy for blocking role abilities during prolonged depression. Individual
 * add-on mixins call this from their authoritative ability entry points.
 */
public final class DepressedAbilityDisable {
  private static final Map<ServerPlayerEntity, Integer> LAST_NOTICE_TICK = new WeakHashMap<>();

  private DepressedAbilityDisable() {}

  public static boolean shouldDisable(PlayerEntity player) {
    if (!(player instanceof ServerPlayerEntity)) return false;
    if (!WatheExtendedServerConfig.moodDisableAbilityWhenDepressed) return false;

    try {
      Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
      if (role == null || role.getMoodType() != Role.MoodType.REAL) return false;

      int thresholdTicks =
          Math.max(0, WatheExtendedServerConfig.moodDisableAbilityWhenDepressedDelay) * 20;
      boolean disabled =
          DepressedMoodState.isDepressed(player)
              && DepressedMoodState.ticks(player) >= thresholdTicks;
      if (disabled) showDisabledTip((ServerPlayerEntity) player);
      return disabled;
    } catch (Throwable ignored) {
      return false;
    }
  }

  // not used
  public static boolean shouldDisableForRole(PlayerEntity player, String roleId) {
    if (!(player instanceof ServerPlayerEntity) || roleId == null) return false;
    try {
      Role role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
      return role != null
          && role.identifier() != null
          && role.identifier().toString().equals(roleId)
          && shouldDisable(player);
    } catch (Throwable ignored) {
      return false;
    }
  }

  private static synchronized void showDisabledTip(ServerPlayerEntity player) {
    int currentTick = player.age;
    int lastNoticeTick = LAST_NOTICE_TICK.getOrDefault(player, Integer.MIN_VALUE / 2);
    if (currentTick - lastNoticeTick < 40) return;

    LAST_NOTICE_TICK.put(player, currentTick);
    player.sendMessage(
        Text.translatable("gui.watheextended.hud.ability.disabled").formatted(Formatting.RED));
  }
}
