package cat.rezelyn.watheextended.compat.sodium;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.type.MethodListAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.MethodNode;

public final class IrisMixinAdjuster implements MixinAnnotationAdjuster {
  @Override
  public AdjustableAnnotationNode adjust(
      List<String> targets, String mixin, MethodNode handler, AdjustableAnnotationNode annotation) {
    if (!FabricLoader.getInstance().isDevelopmentEnvironment()
        || !(annotation instanceof MethodListAnnotationNode injector)) {
      return annotation;
    }

    if (mixin.equals("net.irisshaders.iris.mixin.MixinLevelRenderer")
        && injector
            .getMethod()
            .equals(List.of("Lnet/minecraft/client/render/WorldRenderer;renderWeather"))) {
      injector.setMethod(List.of("renderWeather"));

    } else if (mixin.equals(
            "net.irisshaders.iris.mixin.entity_render_context.MixinHumanoidArmorLayer")
        && injector
            .getMethod()
            .equals(
                List.of(
                    "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor"))) {
      injector.setMethod(List.of("renderArmor"));
    }

    return annotation;
  }
}
