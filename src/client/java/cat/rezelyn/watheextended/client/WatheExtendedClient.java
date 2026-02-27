package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.TypedActionResult;

public class WatheExtendedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                WatheExtendedBlocks.ANTHRACITE_STEEL_ORNAMENT,
                WatheExtendedBlocks.KHAKI_STEEL_ORNAMENT,
                WatheExtendedBlocks.MAROON_STEEL_ORNAMENT,
                WatheExtendedBlocks.MUNTZ_STEEL_ORNAMENT,
                WatheExtendedBlocks.NAVY_STEEL_ORNAMENT,
                WatheExtendedBlocks.SNOWY_OAK_LEAVES,
                WatheExtendedBlocks.SNOWY_SPRUCE_LEAVES,
                WatheExtendedBlocks.SNOWY_BIRCH_LEAVES,
                WatheExtendedBlocks.SNOWY_JUNGLE_LEAVES,
                WatheExtendedBlocks.SNOWY_ACACIA_LEAVES,
                WatheExtendedBlocks.SNOWY_DARK_OAK_LEAVES,
                WatheExtendedBlocks.SNOWY_MANGROVE_LEAVES,
                WatheExtendedBlocks.SNOWY_CHERRY_LEAVES,
                WatheExtendedBlocks.SNOWY_AZALEA_LEAVES,
                WatheExtendedBlocks.SNOWY_FLOWERING_AZALEA_LEAVES,
                WatheExtendedBlocks.MUSIC_DISC_BOX
        );

        // BlockRenderLayerMap.INSTANCE.putBlock(WatheExtendedBlocks.MUSIC_DISC_BOX, RenderLayer.getTranslucent());

        // Open the Guidebook screen when the item is used on the client
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) return TypedActionResult.pass(player.getStackInHand(hand));
            var stack = player.getStackInHand(hand);
            if (stack.getItem() == WatheExtendedItems.GUIDEBOOK) {
                MinecraftClient.getInstance().setScreen(new GuidebookScreen());
                return TypedActionResult.success(stack);
            }
            return TypedActionResult.pass(stack);
        });
    }
}
