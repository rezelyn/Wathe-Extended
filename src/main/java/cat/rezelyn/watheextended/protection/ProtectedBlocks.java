package cat.rezelyn.watheextended.protection;

import dev.doctor4t.wathe.block.*;
import net.minecraft.block.*;

public final class ProtectedBlocks {

    private ProtectedBlocks() {
    }

    public static boolean isProtected(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ChestBlock) return true;
        if (block instanceof TrappedChestBlock) return true;
        if (block instanceof BarrelBlock) return true;
        if (block instanceof ShulkerBoxBlock) return true;
        if (block instanceof FurnaceBlock) return true;
        if (block instanceof BlastFurnaceBlock) return true;
        if (block instanceof SmokerBlock) return true;
        if (block instanceof DispenserBlock) return true;
        if (block instanceof HopperBlock) return true;
        if (block instanceof BrewingStandBlock) return true;
        if (block instanceof BeehiveBlock) return true;
        if (block instanceof ChiseledBookshelfBlock) return true;
        if (block instanceof CraftingTableBlock) return true;
        if (block instanceof AnvilBlock) return true;
        if (block instanceof CartographyTableBlock) return true;
        if (block instanceof GrindstoneBlock) return true;
        if (block instanceof LoomBlock) return true;
        if (block instanceof SmithingTableBlock) return true;
        if (block instanceof StonecutterBlock) return true;
        if (block instanceof EnchantingTableBlock) return true;
        if (block instanceof BeaconBlock) return true;
        if (block instanceof ButtonBlock) return true;
        if (block instanceof LeverBlock) return true;
        if (block instanceof TrapdoorBlock) return true;
        if (block instanceof DoorBlock) return true;
        if (block instanceof FenceGateBlock) return true;
        if (block instanceof NoteBlock) return true;
        if (block instanceof CakeBlock) return true;
        if (block instanceof CandleCakeBlock) return true;
        if (block instanceof CandleBlock) return true;
        if (block instanceof JukeboxBlock) return true;

        // WATHE
        if (block instanceof WatheButtonBlock) return true;
        if (block instanceof ToggleableFacingLightBlock) return true;
        if (block instanceof NeonTubeBlock) return true;
        if (block instanceof NeonPillarBlock) return true;
        if (block instanceof SmallDoorBlock) return true;
        if (block instanceof VentHatchBlock) return true;
        return block instanceof OrnamentBlock;
    }
}

