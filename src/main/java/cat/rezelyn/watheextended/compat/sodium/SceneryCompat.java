package cat.rezelyn.watheextended.compat.sodium;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;

public final class SceneryCompat implements MixinCanceller {

  public static final boolean WATHE =
      FabricLoader.getInstance()
          .getModContainer("wathe")
          .filter(mod ->
              Set.of("1.4-1.21.1", "1.4.1-1.21.1")
                  .contains(mod.getMetadata().getVersion().getFriendlyString()))
          .filter(
              mod -> mod.findPath("dev/doctor4t/wathe/client/render/WatheShake.class").isEmpty())
          .isPresent();

  public static final boolean SODIUM =
      FabricLoader.getInstance()
          .getModContainer("sodium")
          .map(mod -> mod.getMetadata().getVersion())
          .filter(version ->
              version instanceof SemanticVersion v
                  && v.getVersionComponent(0) == 0
                  && v.getVersionComponent(1) == 8)
          .isPresent();

  @Override
  public boolean shouldCancel(List<String> targets, String mixin) {
    if (!WATHE) return false;

    return switch (mixin) {
      case "dev.doctor4t.wathe.mixin.client.scenery.CameraMixin" -> true;

      case "dev.doctor4t.wathe.mixin.client.scenery.SceneryWorldRendererMixin" ->
          !FabricLoader.getInstance().isModLoaded("sodium");

      case "dev.doctor4t.wathe.mixin.compat.sodium.DefaultChunkRendererMixin",
          "dev.doctor4t.wathe.mixin.compat.sodium.OcclusionCullerMixin",
          "dev.doctor4t.wathe.mixin.compat.sodium.RenderSectionManagerMixin" ->
          SODIUM;

      default -> false;
    };
  }
}
