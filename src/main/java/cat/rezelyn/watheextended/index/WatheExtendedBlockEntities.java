package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.block.PlushBlockEntity;
import cat.rezelyn.watheextended.block.WathePlushBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class WatheExtendedBlockEntities {

  public static final BlockEntityType<PlushBlockEntity> ISH_PLUSH =
      register("ish_plush", (WathePlushBlock) WatheExtendedBlocks.ISH_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> REZELYN_PLUSH =
      register("rezelyn_plush", (WathePlushBlock) WatheExtendedBlocks.REZELYN_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> OWNY_PLUSH =
      register("owny_plush", (WathePlushBlock) WatheExtendedBlocks.OWNY_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> JOCHOIS_PLUSH =
      register("jochois_plush", (WathePlushBlock) WatheExtendedBlocks.JOCHOIS_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> NAGANEKI_PLUSH =
      register("naganeki_plush", (WathePlushBlock) WatheExtendedBlocks.NAGANEKI_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> AAOKI_PLUSH =
      register("aaoki_plush", (WathePlushBlock) WatheExtendedBlocks.AAOKI_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> KAMTA_PLUSH =
      register("kamta_plush", (WathePlushBlock) WatheExtendedBlocks.KAMTA_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> ZORIVIEK_PLUSH =
      register("zoriviek_plush", (WathePlushBlock) WatheExtendedBlocks.ZORIVIEK_PLUSH);
  public static final BlockEntityType<PlushBlockEntity> JEANBADA_PLUSH =
      register("jeanbada_plush", (WathePlushBlock) WatheExtendedBlocks.JEANBADA_PLUSH);

  private static BlockEntityType<PlushBlockEntity> register(String id, Block block) {
    return Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        WatheExtended.id(id),
        BlockEntityType.Builder.create(PlushBlockEntity::new, block).build());
  }

  public static void initialize() {}
}
