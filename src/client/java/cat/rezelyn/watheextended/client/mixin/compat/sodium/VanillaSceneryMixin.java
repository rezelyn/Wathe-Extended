package cat.rezelyn.watheextended.client.mixin.compat.sodium;

import cat.rezelyn.watheextended.client.render.scenery.TrainScenery;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
abstract class VanillaSceneryMixin {
  @Unique private final TrainScenery watheextended$scenery = new TrainScenery();

  @ModifyExpressionValue(
      method = "setupTerrain",
      at =
          @At(
              value = "FIELD",
              target = "Lnet/minecraft/client/MinecraftClient;chunkCullingEnabled:Z"))
  private boolean watheextended$includeSourceSections(boolean enabled) {
    return enabled && !WatheClient.isTrainMoving();
  }

  @Inject(method = "renderLayer", at = @At("HEAD"))
  private void watheextended$beginLayer(
      RenderLayer layer,
      double x,
      double y,
      double z,
      Matrix4f view,
      Matrix4f projection,
      CallbackInfo ci) {
    watheextended$scenery.begin(view, projection, x, y, z);
  }

  @ModifyExpressionValue(
      method = "renderLayer",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;isEmpty(Lnet/minecraft/client/render/RenderLayer;)Z"))
  private boolean watheextended$skipHiddenSection(
      boolean empty, @Local ChunkBuilder.BuiltChunk chunk) {
    if (empty || !watheextended$scenery.moving()) return empty;

    BlockPos pos = chunk.getOrigin();
    watheextended$scenery.locate(pos.getX(), pos.getY(), pos.getZ());

    return !watheextended$scenery.visible(pos.getX(), pos.getY(), pos.getZ(), false);
  }

  @WrapOperation(
      method = "renderLayer",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/gl/GlUniform;set(FFF)V",
              ordinal = 0))
  private void watheextended$positionSection(
      GlUniform uniform, float x, float y, float z, Operation<Void> original) {
    if (watheextended$scenery.moving()) {
      x += watheextended$scenery.dx();
      y += watheextended$scenery.dy();
      z += watheextended$scenery.dz();
    }
    original.call(uniform, x, y, z);
  }
}
