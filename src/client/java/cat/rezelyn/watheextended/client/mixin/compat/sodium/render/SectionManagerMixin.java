package cat.rezelyn.watheextended.client.mixin.compat.sodium.render;

import cat.rezelyn.watheextended.client.render.scenery.ScenerySource;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.client.WatheClient;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RenderSectionManager.class, remap = false)
abstract class SectionManagerMixin {
  @ModifyVariable(method = "createTerrainRenderList", at = @At("HEAD"), argsOnly = true)
  private Viewport watheextended$collectSourceSections(Viewport viewport) {
    if (!WatheClient.isTrainMoving()) return viewport;
    var pos = viewport.getTransform();
    return new Viewport(ScenerySource.INSTANCE, new Vector3d(pos.x, pos.y, pos.z));
  }

  @ModifyReturnValue(method = "shouldUseOcclusionCulling", at = @At("RETURN"))
  private boolean watheextended$ignoreSourceOcclusion(boolean enabled) {
    return enabled && !WatheClient.isTrainMoving();
  }

  @ModifyExpressionValue(
      method = "getSearchDistance",
      at =
          @At(
              value = "FIELD",
              target =
                  "Lnet/caffeinemc/mods/sodium/client/gui/SodiumOptions$PerformanceSettings;useFogOcclusion:Z"))
  private boolean watheextended$ignoreSourceFog(boolean enabled) {
    return enabled && !WatheClient.isTrainMoving();
  }
}
