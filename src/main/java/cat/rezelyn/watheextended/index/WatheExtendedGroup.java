package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class WatheExtendedGroup {

    public static final RegistryKey<ItemGroup> WATHE_EXTENDED_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, WatheExtended.id("main"));

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, WATHE_EXTENDED_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(WatheExtendedItems.GUIDEBOOK))
                .displayName(Text.translatable("itemGroup.watheextended.main"))
                .entries((context, entries) -> {
                    // Panels
                    entries.add(WatheExtendedBlocks.TARNISHED_GOLD_PANEL);
                    entries.add(WatheExtendedBlocks.GOLD_PANEL);
                    entries.add(WatheExtendedBlocks.PRISTINE_GOLD_PANEL);
                    entries.add(WatheExtendedBlocks.BLACK_HULL_PANEL);
                    entries.add(WatheExtendedBlocks.BLACK_HULL_SHEETS_PANEL);
                    entries.add(WatheExtendedBlocks.METAL_SHEET_PANEL);
                    entries.add(WatheExtendedBlocks.STAINLESS_STEEL_PANEL);
                    entries.add(WatheExtendedBlocks.DARK_STEEL_PANEL);
                    entries.add(WatheExtendedBlocks.MARBLE_PANEL);
                    entries.add(WatheExtendedBlocks.DARK_MARBLE_PANEL);
                    entries.add(WatheExtendedBlocks.MARBLE_TILES_PANEL);
                    entries.add(WatheExtendedBlocks.MAHOGANY_PLANKS_PANEL);
                    entries.add(WatheExtendedBlocks.MAHOGANY_HERRINGBONE_PANEL);
                    entries.add(WatheExtendedBlocks.MAHOGANY_BOOKSHELF_PANEL);
                    entries.add(WatheExtendedBlocks.BUBINGA_PLANKS_PANEL);
                    entries.add(WatheExtendedBlocks.BUBINGA_HERRINGBONE_PANEL);
                    entries.add(WatheExtendedBlocks.BUBINGA_BOOKSHELF_PANEL);
                    entries.add(WatheExtendedBlocks.EBONY_PLANKS_PANEL);
                    entries.add(WatheExtendedBlocks.EBONY_HERRINGBONE_PANEL);
                    entries.add(WatheExtendedBlocks.EBONY_BOOKSHELF_PANEL);

                    // Ornaments
                    entries.add(WatheExtendedBlocks.ANTHRACITE_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.KHAKI_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.MAROON_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.MUNTZ_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.NAVY_STEEL_ORNAMENT);

                    // Moquettes
                    entries.add(WatheExtendedBlocks.WHITE_MOQUETTE);
                    entries.add(WatheExtendedBlocks.LIGHT_GRAY_MOQUETTE);
                    entries.add(WatheExtendedBlocks.GRAY_MOQUETTE);
                    entries.add(WatheExtendedBlocks.BLACK_MOQUETTE);
                    entries.add(WatheExtendedBlocks.ORANGE_MOQUETTE);
                    entries.add(WatheExtendedBlocks.YELLOW_MOQUETTE);
                    entries.add(WatheExtendedBlocks.LIME_MOQUETTE);
                    entries.add(WatheExtendedBlocks.GREEN_MOQUETTE);
                    entries.add(WatheExtendedBlocks.CYAN_MOQUETTE);
                    entries.add(WatheExtendedBlocks.LIGHT_BLUE_MOQUETTE);
                    entries.add(WatheExtendedBlocks.PURPLE_MOQUETTE);
                    entries.add(WatheExtendedBlocks.MAGENTA_MOQUETTE);
                    entries.add(WatheExtendedBlocks.PINK_MOQUETTE);

                    // Snowy Leaves
                    entries.add(WatheExtendedBlocks.SNOWY_OAK_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_SPRUCE_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_BIRCH_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_JUNGLE_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_ACACIA_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_DARK_OAK_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_MANGROVE_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_CHERRY_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_AZALEA_LEAVES);
                    entries.add(WatheExtendedBlocks.SNOWY_FLOWERING_AZALEA_LEAVES);

                    // Items
                    entries.add(WatheExtendedItems.GUIDEBOOK);

                    // Plushies
                    entries.add(WatheExtendedBlocks.ISH_PLUSH);
                })
                .build());
    }
}

