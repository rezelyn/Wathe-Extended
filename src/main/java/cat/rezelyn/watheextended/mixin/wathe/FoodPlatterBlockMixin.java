package cat.rezelyn.watheextended.mixin.wathe;

import cat.rezelyn.watheextended.mixin.ServerChunkLoadingManagerAccessor;
import dev.doctor4t.wathe.block.FoodPlatterBlock;
import dev.doctor4t.wathe.block_entity.BeveragePlateBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(FoodPlatterBlock.class)
public class FoodPlatterBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void watheextended$preventDuplicateTrayPickup(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) return;
        if (player.isCreative()) return;
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BeveragePlateBlockEntity plate)) return;
        if (plate.getStoredItems().isEmpty()) return;

        boolean isDrink = plate.isDrink();

        Set<Item> trayItems = new HashSet<>();
        Iterable<ChunkHolder> chunkHolders = ((ServerChunkLoadingManagerAccessor) serverWorld.getChunkManager().chunkLoadingManager).watheextended$invokeEntryIterator();
        for (ChunkHolder holder : chunkHolders) {
            if (!(holder.getLatest() instanceof WorldChunk chunk)) continue;
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (!(blockEntity instanceof BeveragePlateBlockEntity otherPlate)) continue;
                if (otherPlate.isDrink() != isDrink) continue;
                for (ItemStack stored : otherPlate.getStoredItems()) {
                    if (!stored.isEmpty()) trayItems.add(stored.getItem());
                }
            }
        }

        if (trayItems.isEmpty()) return;

        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack invStack = inventory.getStack(i);
            if (invStack.isEmpty()) continue;
            if (trayItems.contains(invStack.getItem())) {
                cir.setReturnValue(ActionResult.PASS);
                cir.cancel();
                return;
            }
        }
    }
}
