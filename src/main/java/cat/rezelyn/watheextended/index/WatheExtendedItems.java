package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.item.GuidebookItem;
import cat.rezelyn.watheextended.item.TeleportToReadyAreaItem;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.item.CocktailItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class WatheExtendedItems {

    private static final Item.Settings COCKTAIL = new Item.Settings().maxCount(1).food(FoodComponents.HONEY_BOTTLE);

    public static final Item GUIDEBOOK = register("guidebook", new GuidebookItem(new Item.Settings().maxCount(1)));
    public static final Item TELEPORT_TO_READY_AREA = register("teleport_to_ready_area", new TeleportToReadyAreaItem(new Item.Settings().maxCount(1)));
    public static final Item PRIDE_PUNCH = register("pride_punch", new CocktailItem(COCKTAIL)); // Rainbow Flag
    public static final Item SUNSET_PRISM = register("sunset_prism", new CocktailItem(COCKTAIL)); // Lesbian Flag
    public static final Item MINT_OCEAN = register("mint_ocean", new CocktailItem(COCKTAIL)); // Gay Flag
    public static final Item GALAXY_FIZZ = register("galaxy_fizz", new CocktailItem(COCKTAIL)); // Bisexual Flag
    public static final Item COTTON_CANDY_SHAKE = register("cotton_candy_shake", new CocktailItem(COCKTAIL)); // Transgender Flag
    public static final Item HONEY_LEMONADE = register("honey_lemonade", new CocktailItem(COCKTAIL)); // Non-binary Flag
    public static final Item NEON_SPLASH = register("neon_splash", new CocktailItem(COCKTAIL)); // Pansexual Flag
    public static final Item THE_AMETHYST = register("the_amethyst", new CocktailItem(COCKTAIL)); // Intersex Flag
    public static final Item VELVET_ACE = register("velvet_ace", new CocktailItem(COCKTAIL)); // Asexual Flag

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registries.ITEM, WatheExtended.id(id), item);
    }

    public static void initialize() {

        ItemGroupEvents.modifyEntriesEvent(WatheItems.EQUIPMENT_GROUP).register(entries ->
                entries.addAfter(WatheItems.CHAMPAGNE,
                        PRIDE_PUNCH,
                        SUNSET_PRISM,
                        MINT_OCEAN,
                        GALAXY_FIZZ,
                        COTTON_CANDY_SHAKE,
                        HONEY_LEMONADE,
                        NEON_SPLASH,
                        THE_AMETHYST,
                        VELVET_ACE
                )
        );
    }
}
