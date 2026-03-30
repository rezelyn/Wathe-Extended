package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.block.IshPlushBlock;
import dev.doctor4t.wathe.block.OrnamentBlock;
import dev.doctor4t.wathe.block.PanelBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class WatheExtendedBlocks {
    // Panels
    public static final Block TARNISHED_GOLD_PANEL = register("tarnished_gold_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.NETHERITE)));
    public static final Block GOLD_PANEL = register("gold_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.NETHERITE)));
    public static final Block PRISTINE_GOLD_PANEL = register("pristine_gold_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.NETHERITE)));
    public static final Block BLACK_HULL_PANEL = register("black_hull_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.NETHERITE)));
    public static final Block BLACK_HULL_SHEETS_PANEL = register("black_hull_sheets_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.COPPER)));
    public static final Block METAL_SHEET_PANEL = register("metal_sheet_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.COPPER)));
    public static final Block STAINLESS_STEEL_PANEL = register("stainless_steel_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.COPPER)));
    public static final Block DARK_STEEL_PANEL = register("dark_steel_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.COPPER)));
    public static final Block MARBLE_PANEL = register("marble_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.CALCITE)));
    public static final Block DARK_MARBLE_PANEL = register("dark_marble_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.CALCITE)));
    public static final Block MARBLE_TILES_PANEL = register("marble_tiles_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.CALCITE)));
    public static final Block MAHOGANY_PLANKS_PANEL = register("mahogany_planks_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block MAHOGANY_HERRINGBONE_PANEL = register("mahogany_herringbone_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block MAHOGANY_BOOKSHELF_PANEL = register("mahogany_bookshelf_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block BUBINGA_PLANKS_PANEL = register("bubinga_planks_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block BUBINGA_HERRINGBONE_PANEL = register("bubinga_herringbone_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block BUBINGA_BOOKSHELF_PANEL = register("bubinga_bookshelf_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block EBONY_PLANKS_PANEL = register("ebony_planks_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block EBONY_HERRINGBONE_PANEL = register("ebony_herringbone_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));
    public static final Block EBONY_BOOKSHELF_PANEL = register("ebony_bookshelf_panel", new PanelBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.2f).sounds(BlockSoundGroup.WOOD)));

    // Ornaments
    public static final Block ANTHRACITE_STEEL_ORNAMENT = register("anthracite_steel_ornament", new OrnamentBlock(ornamentBlock()));
    public static final Block KHAKI_STEEL_ORNAMENT = register("khaki_steel_ornament", new OrnamentBlock(ornamentBlock()));
    public static final Block MAROON_STEEL_ORNAMENT = register("maroon_steel_ornament", new OrnamentBlock(ornamentBlock()));
    public static final Block MUNTZ_STEEL_ORNAMENT = register("muntz_steel_ornament", new OrnamentBlock(ornamentBlock()));
    public static final Block NAVY_STEEL_ORNAMENT = register("navy_steel_ornament", new OrnamentBlock(ornamentBlock()));

    // Moquettes
    public static final Block WHITE_MOQUETTE = register("white_moquette", new Block(moquetteBlock()));
    public static final Block LIGHT_GRAY_MOQUETTE = register("light_gray_moquette", new Block(moquetteBlock()));
    public static final Block GRAY_MOQUETTE = register("gray_moquette", new Block(moquetteBlock()));
    public static final Block BLACK_MOQUETTE = register("black_moquette", new Block(moquetteBlock()));
    public static final Block ORANGE_MOQUETTE = register("orange_moquette", new Block(moquetteBlock()));
    public static final Block YELLOW_MOQUETTE = register("yellow_moquette", new Block(moquetteBlock()));
    public static final Block LIME_MOQUETTE = register("lime_moquette", new Block(moquetteBlock()));
    public static final Block GREEN_MOQUETTE = register("green_moquette", new Block(moquetteBlock()));
    public static final Block CYAN_MOQUETTE = register("cyan_moquette", new Block(moquetteBlock()));
    public static final Block LIGHT_BLUE_MOQUETTE = register("light_blue_moquette", new Block(moquetteBlock()));
    public static final Block PURPLE_MOQUETTE = register("purple_moquette", new Block(moquetteBlock()));
    public static final Block MAGENTA_MOQUETTE = register("magenta_moquette", new Block(moquetteBlock()));
    public static final Block PINK_MOQUETTE = register("pink_moquette", new Block(moquetteBlock()));

    // Snowy Leaves
    public static final Block SNOWY_OAK_LEAVES = register("snowy_oak_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_SPRUCE_LEAVES = register("snowy_spruce_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_BIRCH_LEAVES = register("snowy_birch_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_JUNGLE_LEAVES = register("snowy_jungle_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_ACACIA_LEAVES = register("snowy_acacia_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_DARK_OAK_LEAVES = register("snowy_dark_oak_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_MANGROVE_LEAVES = register("snowy_mangrove_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_CHERRY_LEAVES = register("snowy_cherry_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_AZALEA_LEAVES = register("snowy_azalea_leaves", new LeavesBlock(leavesBlock()));
    public static final Block SNOWY_FLOWERING_AZALEA_LEAVES = register("snowy_flowering_azalea_leaves", new LeavesBlock(leavesBlock()));

    // Plushies
    public static final Block ISH_PLUSH = register("ish_plush", new IshPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(BlockSoundGroup.WOOL)));

    private static AbstractBlock.Settings ornamentBlock() {
        return AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.25f).sounds(BlockSoundGroup.COPPER);
    }

    private static AbstractBlock.Settings moquetteBlock() {
        return AbstractBlock.Settings.create().strength(0.5f).sounds(BlockSoundGroup.WOOL);
    }

    private static AbstractBlock.Settings leavesBlock() {
        return AbstractBlock.Settings.create().nonOpaque().ticksRandomly().strength(0.2f).sounds(BlockSoundGroup.GRASS);
    }

    private static <T extends Block> T register(String id, T block) {
        Registry.register(Registries.BLOCK, WatheExtended.id(id), block);
        Registry.register(Registries.ITEM, WatheExtended.id(id), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {}
}
