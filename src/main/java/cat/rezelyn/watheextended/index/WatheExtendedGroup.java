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
                    // Snowy leaves
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

                    // Ornament variants
                    entries.add(WatheExtendedBlocks.ANTHRACITE_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.KHAKI_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.MAROON_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.MUNTZ_STEEL_ORNAMENT);
                    entries.add(WatheExtendedBlocks.NAVY_STEEL_ORNAMENT);

                    // Panels
                    entries.add(WatheExtendedBlocks.MARBLE_PANEL);
                    entries.add(WatheExtendedBlocks.DARK_MARBLE_PANEL);

                    // Custom blocks
                    entries.add(WatheExtendedBlocks.MUSIC_DISC_BOX);
                    entries.add(WatheExtendedBlocks.PIZZA);
                    entries.add(WatheExtendedBlocks.ISH_PLUSH);

                    // Items
                    entries.add(WatheExtendedItems.PRIDE_PUNCH);
                    entries.add(WatheExtendedItems.SUNSET_PRISM);
                    entries.add(WatheExtendedItems.MINT_OCEAN);
                    entries.add(WatheExtendedItems.GALAXY_FIZZ);
                    entries.add(WatheExtendedItems.COTTON_CANDY_SHAKE);
                    entries.add(WatheExtendedItems.HONEY_LEMONADE);
                    entries.add(WatheExtendedItems.NEON_SPLASH);
                    entries.add(WatheExtendedItems.THE_AMETHYST);
                    entries.add(WatheExtendedItems.VELVET_ACE);
                })
                .build());
    }
}

