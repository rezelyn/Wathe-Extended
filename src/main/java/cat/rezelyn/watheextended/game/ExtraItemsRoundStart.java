package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ExtraItemsRoundStart {

    private static final String NAMESPACE = "watheextraitems";

    private ExtraItemsRoundStart() {
    }

    public static ItemStack pickItem(ServerPlayerEntity player) {
        List<Item> pool = WatheExtendedServerConfig.ROLEPLAY_ITEM_DEFAULTS.keySet().stream()
                .filter(WatheExtendedServerConfig::isRoleplayItemEnabled)
                .map(id -> Registries.ITEM.get(Identifier.of(NAMESPACE, id)))
                .toList();
        if (pool.isEmpty()) return ItemStack.EMPTY;
        return new ItemStack(pool.get(player.getRandom().nextInt(pool.size())));
    }
}
