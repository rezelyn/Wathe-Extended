package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.client.debug.BoxDebugRenderer;
import cat.rezelyn.watheextended.client.render.IshPlushBlockEntityRenderer;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.screen.WatheOptionsScreen;
import cat.rezelyn.watheextended.game.PronounsManager;
import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.TypedActionResult;

public class WatheExtendedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WatheExtendedClientConfig.load();
        BoxDebugRenderer.register();
        WatheOptionsScreen.registerTickHandler();

        BlockEntityRendererFactories.register(WatheExtendedBlockEntities.ISH_PLUSH, IshPlushBlockEntityRenderer::new);

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

        ClientPlayNetworking.registerGlobalReceiver(ServerConfig.SyncPayload.ID,
                (payload, context) -> {
                    ClientConfig.setRemoteServer(true);
                    ClientConfig.update(payload.data());
                    WatheOptionsScreen.onCacheUpdated();
                    GuidebookScreen.invalidateIfOpen();
                });

        ClientPlayNetworking.registerGlobalReceiver(
                PronounsManager.SyncPayload.ID,
                (payload, context) -> context.client().execute(() ->
                        cat.rezelyn.watheextended.client.pronouns.PronounsCache.set(
                                payload.uuid(), payload.pronouns())));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client2) -> {
            if (client2.isIntegratedServerRunning()) {
                ClientConfig.setRemoteServer(false);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client2) -> {
            WatheOptionsScreen.clearPendingState();
            ClientConfig.clear();
            cat.rezelyn.watheextended.client.pronouns.PronounsCache.clear();
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) return TypedActionResult.pass(player.getStackInHand(hand));
            var stack = player.getStackInHand(hand);
            if (stack.getItem() == WatheExtendedItems.GUIDEBOOK) {
                MinecraftClient.getInstance().setScreen(new GuidebookScreen());
                return TypedActionResult.pass(stack);
            }
            return TypedActionResult.pass(stack);
        });

        if (FabricLoader.getInstance().isModLoaded("starexpress")) {
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> fixStarExpressAbilityBind());
        }
    }

    private static void fixStarExpressAbilityBind() {
        if (!FabricLoader.getInstance().isModLoaded("starexpress")) return;
        try {
            if (org.aussiebox.starexpress.client.StarryExpressClient.abilityBind == null) {
                org.aussiebox.starexpress.client.StarryExpressClient.abilityBind = org.agmas.noellesroles.client.NoellesrolesClient.abilityBind;
            }
        } catch (Throwable ignored) {
        }
    }
}
