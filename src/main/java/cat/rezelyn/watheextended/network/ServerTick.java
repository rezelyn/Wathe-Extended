package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.game.modifiers.introverted.IntrovertedModifier;
import cat.rezelyn.watheextended.game.utils.EffectCleaner;
import cat.rezelyn.watheextended.game.utils.TeleportationHandler;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/** Drives the per-world server tick loop */
public final class ServerTick {

  private static final int ITEM_TICK_INTERVAL = 20;
  private static final int ITEM_BOUNDS_CHECK_INTERVAL = 5;

  private ServerTick() {}

  /** Registers the end-of-world-tick hook */
  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(ServerTick::tickWorld);
  }

  /**
   * Per-world tick logic
   *
   * <ul>
   *   <li>While the game is {@code STOPPING}: strips lingering effects from all players
   *   <li>Every tick: Random Teleportation handling and Introverted modifier
   *   <li>Every {@value #ITEM_TICK_INTERVAL} ticks: per-player item updates
   *   <li>Every {@value #ITEM_BOUNDS_CHECK_INTERVAL} ticks during an {@code ACTIVE} game: Item
   *       Bounds Check (if enabled)
   *   <li>World only: config sync and Last Stand
   * </ul>
   */
  private static void tickWorld(ServerWorld world) {
    GameWorldComponent game;
    try {
      game = GameWorldComponent.KEY.get(world);
    } catch (Throwable throwable) {
      return;
    }
    if (game == null) return;

    GameWorldComponent.GameStatus status = game.getGameStatus();
    long worldTime = world.getTime();

    if (status == GameWorldComponent.GameStatus.STOPPING) {
      for (ServerPlayerEntity player : world.getPlayers()) {
        EffectCleaner.clear(world, player);
      }
    }

    TeleportationHandler.tick(world, status, worldTime);

    if (worldTime % ITEM_TICK_INTERVAL == 0) {
      PlayerItem.tickAll(world);
    }

    if (status == GameWorldComponent.GameStatus.ACTIVE
        && worldTime % ITEM_BOUNDS_CHECK_INTERVAL == 0) {
      tickItemBounds(world);
    }

    IntrovertedModifier.tick(world);

    if (world.getRegistryKey() == World.OVERWORLD) {
      ConfigSync.tick(world);
      LastStand.tick(world);
    }
  }

  /** Runs the Item Bounds Check if it is enabled */
  private static void tickItemBounds(ServerWorld world) {
    try {
      WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
      if (component != null && component.isItemBoundsCheckEnabled()) {
        ItemBoundsChecker.tick(world);
      }
    } catch (Throwable ignored) {
    }
  }
}
