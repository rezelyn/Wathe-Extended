package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import dev.doctor4t.wathe.cca.GameWorldComponent;
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

    public static void removeTeleportItem(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    public static void applyItemState(ServerPlayerEntity player, ServerWorld world, GameWorldComponent.GameStatus status) {
        boolean gameRunning = status == GameWorldComponent.GameStatus.ACTIVE || status == GameWorldComponent.GameStatus.STOPPING || status == GameWorldComponent.GameStatus.STARTING;

        if (gameRunning) {
            removeTeleportItem(player);
        } else {
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

    public static void tickAll(ServerWorld world, GameWorldComponent.GameStatus status) {
        for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                applyItemState(serverPlayer, world, status);
            }
        }
    }
}

