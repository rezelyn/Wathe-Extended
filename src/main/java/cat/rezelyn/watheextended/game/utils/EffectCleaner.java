package cat.rezelyn.watheextended.game.utils;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import java.util.ArrayList;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;

/** Clears lingering effects from players once a game is stopping */
public final class EffectCleaner {

  private EffectCleaner() {}

  /**
   * Clears effects from a player once the game is stopping: all vanilla status effects, Wathe
   * poison and Noelle's Roles infection <br>
   * Does nothing until the game is in {@code STOPPING} state
   *
   * @param world the world the player is in
   * @param player the player to clean up
   */
  public static void clear(World world, ServerPlayerEntity player) {
    if (world == null || player == null) return;
    try {
      GameWorldComponent game = GameWorldComponent.KEY.get(world);
      if (game.getGameStatus() != GameWorldComponent.GameStatus.STOPPING) return;
      for (StatusEffectInstance effect : new ArrayList<>(player.getStatusEffects())) {
        player.removeStatusEffect(effect.getEffectType());
      }
      clearPoison(player);
      clearInfection(player);
    } catch (Throwable ignored) {
    }
  }

  private static void clearPoison(ServerPlayerEntity player) {
    try {
      PlayerPoisonComponent poison = PlayerPoisonComponent.KEY.get(player);
      poison.poisoner = null;
      poison.reset();
    } catch (Throwable ignored) {
    }
  }

  private static void clearInfection(ServerPlayerEntity player) {
    try {
      InfectedPlayerComponent infected = InfectedPlayerComponent.KEY.get(player);
      infected.reset();
    } catch (Throwable ignored) {
    }
  }
}
