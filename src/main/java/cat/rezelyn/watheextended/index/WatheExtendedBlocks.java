package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.block.IshPlushBlock;
import cat.rezelyn.watheextended.block.MusicDiscBoxBlock;
import cat.rezelyn.watheextended.block.PizzaBlock;
import dev.doctor4t.wathe.block.OrnamentBlock;
import dev.doctor4t.wathe.block.PanelBlock;
import dev.doctor4t.wathe.index.WatheBlocks;
import dev.doctor4t.ratatouille.index.RatatouilleBlocks;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class WatheExtendedBlocks {

    public static final Block MARBLE_PANEL = register("marble_panel",
            new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.CALCITE)));
    public static final Block DARK_MARBLE_PANEL = register("dark_marble_panel",
            new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.CALCITE)));
    public static final Block ANTHRACITE_STEEL_ORNAMENT = register("anthracite_steel_ornament",
            new OrnamentBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER)));
    public static final Block KHAKI_STEEL_ORNAMENT = register("khaki_steel_ornament",
            new OrnamentBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER)));
    public static final Block MAROON_STEEL_ORNAMENT = register("maroon_steel_ornament",
            new OrnamentBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER)));
    public static final Block MUNTZ_STEEL_ORNAMENT = register("muntz_steel_ornament",
            new OrnamentBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER)));
    public static final Block NAVY_STEEL_ORNAMENT = register("navy_steel_ornament",
            new OrnamentBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER)));

    public static final Block MUSIC_DISC_BOX = register("music_disc_box",
            new MusicDiscBoxBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(BlockSoundGroup.WOOD)));

    public static final Block SNOWY_OAK_LEAVES = register("snowy_oak_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_SPRUCE_LEAVES = register("snowy_spruce_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_BIRCH_LEAVES = register("snowy_birch_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_JUNGLE_LEAVES = register("snowy_jungle_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_ACACIA_LEAVES = register("snowy_acacia_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_DARK_OAK_LEAVES = register("snowy_dark_oak_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_MANGROVE_LEAVES = register("snowy_mangrove_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_CHERRY_LEAVES = register("snowy_cherry_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_AZALEA_LEAVES = register("snowy_azalea_leaves", new LeavesBlock(leavesSettings()));
    public static final Block SNOWY_FLOWERING_AZALEA_LEAVES = register("snowy_flowering_azalea_leaves", new LeavesBlock(leavesSettings()));

    public static final Block PIZZA = register("pizza",
            new PizzaBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(BlockSoundGroup.MUD)));

    public static final Block ISH_PLUSH = register("ish_plush",
            new IshPlushBlock(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOL)));

    private static <T extends Block> T register(String id, T block) {
        Registry.register(Registries.BLOCK, WatheExtended.id(id), block);
        Registry.register(Registries.ITEM, WatheExtended.id(id), new BlockItem(block, new Item.Settings()));
        return block;
    }

    private static AbstractBlock.Settings leavesSettings() {
        return AbstractBlock.Settings.create().nonOpaque().ticksRandomly().strength(0.2f).sounds(BlockSoundGroup.GRASS);
    }

    public static void initialize() {
        // WATHE Decoration Group - Ornaments
        ItemGroupEvents.modifyEntriesEvent(WatheItems.DECORATION_GROUP).register(entries ->
                entries.addAfter(WatheBlocks.GOLD_ORNAMENT,
                        ANTHRACITE_STEEL_ORNAMENT,
                        KHAKI_STEEL_ORNAMENT,
                        MAROON_STEEL_ORNAMENT,
                        MUNTZ_STEEL_ORNAMENT,
                        NAVY_STEEL_ORNAMENT
                )
        );

        ItemGroupEvents.modifyEntriesEvent(WatheItems.DECORATION_GROUP).register(entries ->
                entries.addAfter(WatheBlocks.MAHOGANY_CABINET,
                        MUSIC_DISC_BOX
                )
        );

        // WATHE Building Group
        ItemGroupEvents.modifyEntriesEvent(WatheItems.BUILDING_GROUP).register(entries ->
                entries.addAfter(WatheBlocks.MARBLE_SLAB, MARBLE_PANEL)
        );

        ItemGroupEvents.modifyEntriesEvent(WatheItems.BUILDING_GROUP).register(entries ->
                entries.addAfter(WatheBlocks.DARK_MARBLE_SLAB, DARK_MARBLE_PANEL)
        );

        // Vanilla Natural Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries ->
                entries.addAfter(Items.FLOWERING_AZALEA_LEAVES,
                        SNOWY_OAK_LEAVES,
                        SNOWY_SPRUCE_LEAVES,
                        SNOWY_BIRCH_LEAVES,
                        SNOWY_JUNGLE_LEAVES,
                        SNOWY_ACACIA_LEAVES,
                        SNOWY_DARK_OAK_LEAVES,
                        SNOWY_MANGROVE_LEAVES,
                        SNOWY_CHERRY_LEAVES,
                        SNOWY_AZALEA_LEAVES,
                        SNOWY_FLOWERING_AZALEA_LEAVES
                )
        );

        // Vanilla Food & Drink Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
                entries.addAfter(Items.CAKE, PIZZA)
        );

        // Vanilla Functional Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
                entries.addAfter(RatatouilleBlocks.MAUVE_PLUSH, ISH_PLUSH)
        );
    }
}
