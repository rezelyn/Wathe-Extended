package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.block.IshPlushBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class WatheExtendedBlockEntities {

    public static final BlockEntityType<IshPlushBlockEntity> ISH_PLUSH = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            WatheExtended.id("ish_plush"),
            BlockEntityType.Builder.create(IshPlushBlockEntity::new, WatheExtendedBlocks.ISH_PLUSH).build()
    );

    public static void initialize() {}
}
