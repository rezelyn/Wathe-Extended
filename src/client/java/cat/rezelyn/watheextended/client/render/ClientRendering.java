package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.client.render.entity.*;
import cat.rezelyn.watheextended.client.render.hud.*;
import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

/** Registers HUD layers, block entity renderers and block render layers */
public final class ClientRendering {

  private ClientRendering() {}

  public static void register() {
    BoxDebugRenderer.register();
    registerHud();
    registerBlockEntityRenderers();
    registerBlockRenderLayers();
  }

  private static void registerHud() {
    HudRenderCallback.EVENT.register((context, tickCounter) -> LastStandRenderer.render(context));
    HudRenderCallback.EVENT.register(InstinctHudRenderer::render);
  }

  private static void registerBlockEntityRenderers() {
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.ISH_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.REZELYN_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.OWNY_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.JOCHOIS_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.NAGANEKI_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.AAOKI_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.KAMTA_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.JEANBADA_PLUSH, PlushBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.ZORIVIEK_PLUSH, PlushBlockEntityRenderer::new);
  }

  private static void registerBlockRenderLayers() {
    BlockRenderLayerMap.INSTANCE.putBlocks(
        RenderLayer.getCutout(),
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
        WatheExtendedBlocks.SNOWY_FLOWERING_AZALEA_LEAVES);
  }
}
