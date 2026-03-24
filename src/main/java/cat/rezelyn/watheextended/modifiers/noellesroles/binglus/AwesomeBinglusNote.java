package cat.rezelyn.watheextended.modifiers.noellesroles.binglus;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public final class AwesomeBinglusNote {

    private AwesomeBinglusNote() {}

    public static void applyOnGameStart(World world, GameWorldComponent gameWorldComponent) {
        if (!(world instanceof ServerWorld sw)) return;
        try {
            for (UUID uuid : gameWorldComponent.getAllWithRole(org.agmas.noellesroles.Noellesroles.AWESOME_BINGLUS)) {
                PlayerEntity player = sw.getPlayerByUuid(uuid);
                if (!(player instanceof ServerPlayerEntity sp)) continue;

                for (int i = 0; i < sp.getInventory().size(); i++) {
                    if (sp.getInventory().getStack(i).isOf(WatheItems.NOTE)) {
                        sp.getInventory().setStack(i, ItemStack.EMPTY);
                    }
                }
                sp.getInventory().insertStack(new ItemStack(WatheItems.NOTE, 4));
            }
        } catch (Throwable ignored) {}
    }
}
