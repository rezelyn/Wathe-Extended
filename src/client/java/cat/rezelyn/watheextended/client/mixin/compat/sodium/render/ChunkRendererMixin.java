package cat.rezelyn.watheextended.client.mixin.compat.sodium.render;

import cat.rezelyn.watheextended.client.render.scenery.TrainScenery;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.compat.IrisHelper;
import dev.doctor4t.wathe.compat.SodiumShaderInterface;
import java.nio.ByteBuffer;
import net.caffeinemc.mods.sodium.client.gl.buffer.GlBufferUsage;
import net.caffeinemc.mods.sodium.client.gl.buffer.GlMutableBuffer;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.gl.device.MultiDrawBatch;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.LocalSectionIndex;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderListIterable;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import net.irisshaders.iris.api.v0.IrisApi;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
abstract class ChunkRendererMixin {
  @Unique private static final TrainScenery watheextended$scenery = new TrainScenery();

  @Unique
  private static final ByteBuffer watheextended$offsets =
      BufferUtils.createByteBuffer(RenderRegion.REGION_SIZE * 16);

  @Unique private static boolean watheextended$shadowPass;
  @Unique private static boolean watheextended$visible;
  @Unique private MultiDrawBatch watheextended$movingBatch;
  @Unique private GlMutableBuffer watheextended$uniform;
  @Unique private boolean watheextended$zeroUploaded;

  @ModifyArg(
      method = "fillCommandBuffer",
      index = 0,
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/data/SectionRenderDataStorage;getDataPointer(I)J"))
  private static int watheextended$locateSection(
      int index, @Local(argsOnly = true) RenderRegion region) {

    if (!watheextended$scenery.moving()) return index;

    int x = region.getOriginX() + 16 * LocalSectionIndex.unpackX(index);
    int y = region.getOriginY() + 16 * LocalSectionIndex.unpackY(index);
    int z = region.getOriginZ() + 16 * LocalSectionIndex.unpackZ(index);

    watheextended$scenery.locate(x, y, z);
    watheextended$visible = watheextended$scenery.visible(x, y, z, watheextended$shadowPass);

    int base = index * 16;

    watheextended$offsets.putFloat(base, watheextended$scenery.dx());
    watheextended$offsets.putFloat(base + 4, watheextended$scenery.dy());
    watheextended$offsets.putFloat(base + 8, watheextended$scenery.dz());

    return index;
  }

  @ModifyExpressionValue(
      method = "fillCommandBuffer",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/data/SectionRenderDataUnsafe;getSliceMask(J)I"))
  private static int watheextended$cullSection(int mask) {
    return watheextended$scenery.moving() && !watheextended$visible ? 0 : mask;
  }

  @WrapOperation(
      method = "fillCommandBuffer",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/DefaultChunkRenderer;getVisibleFaces(IIIIII)I"))
  private static int watheextended$cullFaces(
      int x, int y, int z, int cx, int cy, int cz, Operation<Integer> original) {

    if (watheextended$scenery.moving()) {
      x -= Math.round(watheextended$scenery.dx());
      y -= Math.round(watheextended$scenery.dy());
      z -= Math.round(watheextended$scenery.dz());
    }

    return original.call(x, y, z, cx, cy, cz);
  }

  @Inject(method = "render", at = @At("HEAD"))
  private void watheextended$beginPass(
      ChunkRenderMatrices matrices,
      CommandList commands,
      ChunkRenderListIterable lists,
      TerrainRenderPass pass,
      CameraTransform camera,
      boolean indexed,
      CallbackInfo ci) {

    watheextended$scenery.begin(
        matrices.modelView(), matrices.projection(), camera.x, camera.y, camera.z);
    watheextended$shadowPass =
        watheextended$scenery.moving()
            && IrisHelper.isIrisShaderPackInUse()
            && IrisApi.getInstance().isRenderingShadowPass();
  }

  @WrapOperation(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;getCachedBatch(Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;)Lnet/caffeinemc/mods/sodium/client/gl/device/MultiDrawBatch;"))
  private MultiDrawBatch watheextended$selectBatch(
      RenderRegion region, TerrainRenderPass pass, Operation<MultiDrawBatch> original) {

    if (!watheextended$scenery.moving()) return original.call(region, pass);
    if (watheextended$movingBatch == null) {
      watheextended$movingBatch =
          new MultiDrawBatch(ModelQuadFacing.COUNT * RenderRegion.REGION_SIZE + 1);
    }

    watheextended$movingBatch.clear();

    return watheextended$movingBatch;
  }

  @Inject(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/DefaultChunkRenderer;executeDrawBatch(Lnet/caffeinemc/mods/sodium/client/gl/device/CommandList;Lnet/caffeinemc/mods/sodium/client/gl/tessellation/GlTessellation;Lnet/caffeinemc/mods/sodium/client/gl/device/MultiDrawBatch;)V"))
  private void watheextended$bindOffsets(
      CallbackInfo ci,
      @Local ChunkShaderInterface shader,
      @Local(argsOnly = true) CommandList commands) {

    if (watheextended$uniform == null) watheextended$uniform = commands.createMutableBuffer();
    if (watheextended$scenery.moving() || !watheextended$zeroUploaded) {
      watheextended$zeroUploaded = !watheextended$scenery.moving();

      if (watheextended$zeroUploaded) MemoryUtil.memSet(watheextended$offsets, 0);
      commands.uploadData(watheextended$uniform, watheextended$offsets, GlBufferUsage.STREAM_DRAW);
    }
    ((SodiumShaderInterface) shader).wathe$set(watheextended$uniform);
  }

  @Inject(method = "delete", at = @At("HEAD"))
  private void watheextended$releaseBuffers(CommandList commands, CallbackInfo ci) {
    if (watheextended$uniform != null) commands.deleteBuffer(watheextended$uniform);
    if (watheextended$movingBatch != null) watheextended$movingBatch.delete();
  }
}
