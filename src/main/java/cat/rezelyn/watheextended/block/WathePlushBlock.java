package cat.rezelyn.watheextended.block;

import dev.doctor4t.ratatouille.index.RatatouilleSounds;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WathePlushBlock extends dev.doctor4t.ratatouille.block.PlushBlock {

  private final Supplier<BlockEntityType<PlushBlockEntity>> blockEntityType;
  private final SoundEvent plushSound;

  public WathePlushBlock(
      Settings settings,
      Supplier<BlockEntityType<PlushBlockEntity>> blockEntityType,
      SoundEvent plushSound) {
    super(settings);
    this.blockEntityType = blockEntityType;
    this.plushSound = plushSound;
  }

  public BlockEntityType<PlushBlockEntity> getBlockEntityType() {
    return blockEntityType.get();
  }

  @Override
  protected ActionResult onUse(
      BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
    world.playSound(
        player,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        RatatouilleSounds.BLOCK_RAT_MAID_PLUSH_HONK,
        SoundCategory.BLOCKS,
        0.5f,
        1.0f);
    if (plushSound != null) {
      float pitch = (float) (Math.random() * 0.6f + 0.9f);
      world.playSound(
          player,
          pos.getX() + 0.5,
          pos.getY() + 0.5,
          pos.getZ() + 0.5,
          plushSound,
          SoundCategory.BLOCKS,
          2.0f,
          pitch);
    }
    if (!world.isClient()) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof PlushBlockEntity plushBlockEntity) {
        plushBlockEntity.squish(1);
      }
    }
    return ActionResult.SUCCESS;
  }

  @Override
  public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    if (!world.isClient()) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof PlushBlockEntity plushBlockEntity) {
        plushBlockEntity.squish(24);
      }
    }
  }

  @Override
  protected void spawnBreakParticles(
      World world, PlayerEntity player, BlockPos pos, BlockState state) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof PlushBlockEntity plushBlockEntity) {
      plushBlockEntity.squish(4);
    }
    super.spawnBreakParticles(world, player, pos, state);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new PlushBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      World world, BlockState state, BlockEntityType<T> type) {
    return validateTicker(type, getBlockEntityType(), PlushBlockEntity::tick);
  }
}
