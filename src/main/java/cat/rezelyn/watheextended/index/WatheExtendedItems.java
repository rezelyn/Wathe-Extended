package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.item.CreateRtpSlotItem;
import cat.rezelyn.watheextended.item.GuidebookItem;
import cat.rezelyn.watheextended.item.TeleportToReadyAreaItem;
import cat.rezelyn.watheextended.item.TeleportToSceneryItem;
import dev.doctor4t.wathe.item.CocktailItem;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class WatheExtendedItems {

    public static final Item GUIDEBOOK = register("guidebook", new GuidebookItem(new Item.Settings().maxCount(1)));
    public static final Item TELEPORT_TO_READY_AREA = register("teleport_to_ready_area", new TeleportToReadyAreaItem(new Item.Settings().maxCount(1)));
    public static final Item TELEPORT_TO_SCENERY = register("teleport_to_scenery", new TeleportToSceneryItem(new Item.Settings().maxCount(1)));
    public static final Item CREATE_RTP_SLOT = register("create_rtp_slot", new CreateRtpSlotItem(new Item.Settings().maxCount(1)));

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registries.ITEM, WatheExtended.id(id), item);
    }

    public static void initialize() {}
}
