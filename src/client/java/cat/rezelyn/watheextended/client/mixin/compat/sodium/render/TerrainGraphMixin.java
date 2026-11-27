package cat.rezelyn.watheextended.client.mixin.compat.sodium.render;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.compat.IrisHelper;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
abstract class TerrainGraphMixin {
  @Shadow private RenderSectionManager renderSectionManager;
  @Unique private boolean watheextended$moving;
  @Unique private boolean watheextended$limitUpdates;
  @Unique private long watheextended$section;

  @Inject(
      method = "setupTerrain",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;prepareFrame(Lorg/joml/Vector3dc;)V",
              shift = At.Shift.BEFORE))
  private void watheextended$checkSection(
      Camera camera, Viewport viewport, boolean spectator, boolean immediate, CallbackInfo ci) {
    boolean moving = WatheClient.isTrainMoving();
    boolean limit =
        moving
            && WatheExtendedClientConfig.limitTerrainGraphUpdates
            && !IrisHelper.isIrisShaderPackInUse();
    long section = ChunkSectionPos.toLong(camera.getBlockPos());

    if (moving != watheextended$moving
        || limit != watheextended$limitUpdates
        || (limit && section != watheextended$section)) {
      renderSectionManager.markGraphDirty();
    }

    watheextended$moving = moving;
    watheextended$limitUpdates = limit;
    watheextended$section = section;
  }

  // Sodium camera check
  @WrapWithCondition(
      method = "setupTerrain",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;markGraphDirty()V"))
  private boolean watheextended$allowCameraUpdate(RenderSectionManager manager) {
    return !watheextended$limitUpdates;
  }
}
