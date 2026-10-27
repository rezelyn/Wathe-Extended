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

  private static void giveItem(Item item, int hotbarSlot, ServerPlayerEntity player) {
    for (int i = 0; i < player.getInventory().size(); i++) {
      if (player.getInventory().getStack(i).isOf(item)) return;
    }

    ItemStack cursor = player.currentScreenHandler.getCursorStack();
    if (cursor.isOf(item)) return;

    ItemStack displaced = player.getInventory().getStack(hotbarSlot);
    if (!displaced.isEmpty() && !displaced.isOf(item)) {
      player.getInventory().setStack(hotbarSlot, ItemStack.EMPTY);
      boolean moved = false;
      for (int i = 0; i < player.getInventory().size(); i++) {
        if (i == hotbarSlot || !player.getInventory().getStack(i).isEmpty()) continue;
        player.getInventory().setStack(i, displaced);
        moved = true;
        break;
      }
      if (!moved) {
        player.dropItem(displaced, true);
      }
    }

    player.getInventory().setStack(hotbarSlot, new ItemStack(item));
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
    giveItem(WatheExtendedItems.GUIDEBOOK, 0, player);

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
        giveItem(WatheExtendedItems.ADD_PLAYERS, 5, player);
        giveItem(WatheExtendedItems.REMOVE_PLAYERS, 6, player);
        giveItem(WatheExtendedItems.COPY_POSITION, 7, player);
        giveItem(WatheExtendedItems.TELEPORT_TO_SCENERY, 8, player);
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
        giveItem(WatheExtendedItems.TELEPORT_TO_READY_AREA, 1, player);
      }
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
