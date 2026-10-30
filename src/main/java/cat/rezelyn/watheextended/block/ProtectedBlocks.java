package cat.rezelyn.watheextended.block;

import dev.doctor4t.wathe.block.*;
import net.minecraft.block.*;
import sir.nicholascooke.watheextraitems.block.RadioBlock;

public final class ProtectedBlocks {

  /** Categories used to query protected blocks and construct area-specific allowlists */
  public enum Category {
    CONTAINERS,
    WORKSTATIONS,
    LAMPS,
    BUTTONS,
    LEVERS,
    TRAPDOORS,
    DOORS,
    FENCE_GATES,
    PRIVACY_GLASS,
    DECORATIONS,
    MUSIC,
    FOOD,
    MISC,
    RADIO
  }

  private ProtectedBlocks() {}

  /**
   * Checks whether a block belongs to any protected category
   *
   * @param state the block state to check
   * @return {@code true} if the block is protected
   */
  public static boolean isProtected(BlockState state) {
    for (Category category : Category.values()) {
      if (isInCategory(state, category)) return true;
    }
    return false;
  }

  /**
   * Checks whether a block belongs to a specific category
   *
   * @param state the block state to check
   * @param category the category to match
   * @return {@code true} if the block belongs to the category
   */
  public static boolean isInCategory(BlockState state, Category category) {
    Block block = state.getBlock();
    return switch (category) {
      case CONTAINERS ->
          block instanceof ChestBlock
              || block instanceof TrappedChestBlock
              || block instanceof BarrelBlock
              || block instanceof ShulkerBoxBlock
              || block instanceof FurnaceBlock
              || block instanceof BlastFurnaceBlock
              || block instanceof SmokerBlock
              || block instanceof DispenserBlock
              || block instanceof HopperBlock
              || block instanceof BrewingStandBlock
              || block instanceof BeehiveBlock
              || block instanceof ChiseledBookshelfBlock;
      case WORKSTATIONS ->
          block instanceof CraftingTableBlock
              || block instanceof AnvilBlock
              || block instanceof CartographyTableBlock
              || block instanceof GrindstoneBlock
              || block instanceof LoomBlock
              || block instanceof SmithingTableBlock
              || block instanceof StonecutterBlock
              || block instanceof EnchantingTableBlock;
      case LAMPS ->
          block instanceof ToggleableFacingLightBlock
              || block instanceof NeonTubeBlock
              || block instanceof NeonPillarBlock
              || optionalCheck(block, "net.mokus.wathextras.block.custom.CandelabreBlock")
              || optionalCheck(block, "net.mokus.wathextras.block.custom.StackLightBlock");
      case BUTTONS -> block instanceof ButtonBlock || block instanceof WatheButtonBlock;
      case LEVERS -> block instanceof LeverBlock;
      case TRAPDOORS ->
          block instanceof TrapdoorBlock
              || block instanceof VentHatchBlock
              || optionalCheck(block, "net.mokus.wathextras.block.custom.ButterflyDoorBlock");
      case DOORS -> block instanceof DoorBlock || block instanceof SmallDoorBlock;
      case FENCE_GATES -> block instanceof FenceGateBlock;
      case PRIVACY_GLASS ->
          block instanceof PrivacyGlassBlock || block instanceof PrivacyGlassPanelBlock;
      case DECORATIONS -> block instanceof OrnamentBlock;
      case MUSIC -> block instanceof NoteBlock || block instanceof JukeboxBlock;
      case FOOD ->
          block instanceof CakeBlock
              || block instanceof CandleCakeBlock
              || block instanceof CandleBlock;
      case MISC -> block instanceof BeaconBlock;
      case RADIO -> block instanceof RadioBlock;
    };
  }

  /**
   * Checks whether a block belongs to any of the supplied categories
   *
   * @param state the block state to check
   * @param categories the categories to match
   * @return {@code true} if the block belongs to at least one category
   */
  public static boolean isInAnyCategory(BlockState state, Category... categories) {
    for (Category category : categories) {
      if (isInCategory(state, category)) return true;
    }
    return false;
  }

  /** Checks for an optional add-on block type without linking to its classes */
  private static boolean optionalCheck(Block block, String className) {
    for (Class<?> type = block.getClass(); type != null; type = type.getSuperclass()) {
      if (type.getName().equals(className)) return true;
    }
    return false;
  }
}
