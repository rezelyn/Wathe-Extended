package cat.rezelyn.watheextended.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class MusicDiscBoxBlock extends HorizontalFacingBlock {

    public static final MapCodec<MusicDiscBoxBlock> CODEC = createCodec(MusicDiscBoxBlock::new);

    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(3, 0, 2, 13, 4, 14);
    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(2, 0, 3, 14, 4, 13);

    public MusicDiscBoxBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<MusicDiscBoxBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, net.minecraft.util.math.BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        return (facing == Direction.EAST || facing == Direction.WEST) ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }
}
