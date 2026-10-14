package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class PlayerItem {

  private PlayerItem() {}

  public static void giveItem(Item item, ServerPlayerEntity player) {
    for (int i = 0; i < player.getInventory().size(); i++) {
      if (player.getInventory().getStack(i).isOf(item)) return;
    }
    player.getInventory().insertStack(new ItemStack(item));
  }

  public static void removeItem(Item item, ServerPlayerEntity player) {
    for (int i = 0; i < player.getInventory().size(); i++) {
      if (player.getInventory().getStack(i).isOf(item)) {
        player.getInventory().setStack(i, ItemStack.EMPTY);
      }
    }
  }

  public static void applyItemState(ServerPlayerEntity player, ServerWorld world) {
    boolean gameRunning = GameStatus.State(world);

    if (gameRunning) {
      removeItem(WatheExtendedItems.TELEPORT_TO_READY_AREA, player);
      removeItem(WatheExtendedItems.TELEPORT_TO_SCENERY, player);
      removeItem(WatheExtendedItems.CREATE_RTP_SLOT, player);
      removeItem(WatheExtendedItems.TRIGGER_RTP, player);
      removeItem(WatheExtendedItems.ADD_PLAYERS, player);
      removeItem(WatheExtendedItems.REMOVE_PLAYERS, player);
      removeItem(WatheExtendedItems.COPY_POSITION, player);
    } else {
      boolean isCreativeOp = player.isCreative() && player.hasPermissionLevel(2);
      if (isCreativeOp) {
        giveItem(WatheExtendedItems.TELEPORT_TO_SCENERY, player);
        giveItem(WatheExtendedItems.ADD_PLAYERS, player);
        giveItem(WatheExtendedItems.REMOVE_PLAYERS, player);
        giveItem(WatheExtendedItems.COPY_POSITION, player);
      } else {
        removeItem(WatheExtendedItems.COPY_POSITION, player);
        removeItem(WatheExtendedItems.TELEPORT_TO_SCENERY, player);
        removeItem(WatheExtendedItems.CREATE_RTP_SLOT, player);
        removeItem(WatheExtendedItems.TRIGGER_RTP, player);
        removeItem(WatheExtendedItems.ADD_PLAYERS, player);
        removeItem(WatheExtendedItems.REMOVE_PLAYERS, player);
      }

      Box readyArea = MapVariables.getReadyArea(world);
      if (readyArea != null && readyArea.contains(player.getPos())) {
        removeItem(WatheExtendedItems.TELEPORT_TO_READY_AREA, player);
      } else {
        giveItem(WatheExtendedItems.TELEPORT_TO_READY_AREA, player);
      }
      giveItem(WatheExtendedItems.GUIDEBOOK, player);
    }
  }

  public static void tickAll(ServerWorld world) {
    for (PlayerEntity player : world.getPlayers()) {
      if (player instanceof ServerPlayerEntity serverPlayer) {
        applyItemState(serverPlayer, world);
      }
    }
  }
}
