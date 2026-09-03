package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class PlayerItem {

    private PlayerItem() {}

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

    public static void giveCreateRtpSlotItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.CREATE_RTP_SLOT)) return;
        }
        player.getInventory().insertStack(new ItemStack(WatheExtendedItems.CREATE_RTP_SLOT));
    }

    public static void removeCreateRtpSlotItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.CREATE_RTP_SLOT)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public static void giveTriggerRtpItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TRIGGER_RTP)) return;
        }
        player.getInventory().insertStack(new ItemStack(WatheExtendedItems.TRIGGER_RTP));
    }

    public static void removeTriggerRtpItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TRIGGER_RTP)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public static void giveFakePlayerItems(ServerPlayerEntity player) {
        boolean hasAdd = false;
        boolean hasRemove = false;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(WatheExtendedItems.ADD_PLAYERS)) hasAdd = true;
            if (stack.isOf(WatheExtendedItems.REMOVE_PLAYERS)) hasRemove = true;
        }
        if (!hasAdd) player.getInventory().insertStack(new ItemStack(WatheExtendedItems.ADD_PLAYERS));
        if (!hasRemove) player.getInventory().insertStack(new ItemStack(WatheExtendedItems.REMOVE_PLAYERS));
    }

    public static void removeFakePlayerItems(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(WatheExtendedItems.ADD_PLAYERS) || stack.isOf(WatheExtendedItems.REMOVE_PLAYERS)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
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
            removeCreateRtpSlotItem(player);
            removeTriggerRtpItem(player);
            removeFakePlayerItems(player);
        } else {
            boolean isCreativeOp = player.isCreative() && player.hasPermissionLevel(2);
            if (isCreativeOp) {
                giveTeleportToSceneryItem(player);
                giveCreateRtpSlotItem(player);
                giveTriggerRtpItem(player);
                giveFakePlayerItems(player);
            } else {
                removeTeleportToSceneryItem(player);
                removeCreateRtpSlotItem(player);
                removeTriggerRtpItem(player);
                removeFakePlayerItems(player);
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

