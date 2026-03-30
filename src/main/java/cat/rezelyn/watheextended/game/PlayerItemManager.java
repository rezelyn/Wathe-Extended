package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class PlayerItemManager {

    private PlayerItemManager() {}

    public static void giveGuidebook(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.GUIDEBOOK)) return;
        }
        player.getInventory().insertStack(new ItemStack(WatheExtendedItems.GUIDEBOOK));
    }

    public static void giveTeleportItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) return;
        }
        player.getInventory().insertStack(new ItemStack(WatheExtendedItems.TELEPORT_TO_READY_AREA));
    }

    public static void giveTeleportToSceneryItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TELEPORT_TO_SCENERY)) return;
        }
        player.getInventory().insertStack(new ItemStack(WatheExtendedItems.TELEPORT_TO_SCENERY));
    }

    public static void removeTeleportItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public static void removeTeleportToSceneryItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TELEPORT_TO_SCENERY)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public static void applyItemState(ServerPlayerEntity player, ServerWorld world) {
        boolean gameRunning = GameStatus.State(world);

        if (gameRunning) {
            removeTeleportItem(player);
            removeTeleportToSceneryItem(player);
        } else {
            boolean isCreativeOp = player.isCreative() && player.hasPermissionLevel(2);
            if (isCreativeOp) {
                giveTeleportToSceneryItem(player);
            } else {
                removeTeleportToSceneryItem(player);
            }

            Box readyArea = MapVariables.getReadyArea(world);
            if (readyArea != null && readyArea.contains(player.getPos())) {
                removeTeleportItem(player);
                giveGuidebook(player);
            } else {
                giveTeleportItem(player);
                giveGuidebook(player);
            }
        }
    }

    public static void tickAll(ServerWorld world) {
        for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                applyItemState(serverPlayer, world);
            }
        }
    }
}

