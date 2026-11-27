package cat.rezelyn.watheextended.compat;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.type.MethodListAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.MethodNode;

public final class AbilityHudLayerAdjuster implements MixinAnnotationAdjuster {
  private static final Set<String> TARGETS = Set.of(
      "org.BsXinQin.kinswathe.client.mixin.roles.bellringer.BellringerHudMixin",
      "org.BsXinQin.kinswathe.client.mixin.roles.cleaner.CleanerHudMixin",
      "org.BsXinQin.kinswathe.client.mixin.roles.detective.DetectiveHudMixin",
      "org.BsXinQin.kinswathe.client.mixin.roles.dreamer.DreamerHudMixin",
      "org.BsXinQin.kinswathe.client.mixin.roles.hunter.HunterHudMixin",
      "org.BsXinQin.kinswathe.client.mixin.roles.robot.RobotHudMixin",
      "org.agmas.noellesroles.client.mixin.infected.InfectedHudMixin",
      "org.agmas.noellesroles.client.mixin.phantom.PhantomHudMixin",
      "org.agmas.noellesroles.client.mixin.recaller.RecallerHudMixin",
      "org.agmas.noellesroles.client.mixin.vulture.VultureHudMixin",
      "org.aussiebox.starexpress.client.mixin.starstruck.StarstruckHudMixin",
      "pro.fazeclan.river.stupid_express.client.mixin.role.thief.ThiefCooldownHudMixin");

  @Override
  public AdjustableAnnotationNode adjust(
      List<String> targets, String mixin, MethodNode handler, AdjustableAnnotationNode annotation) {
    if (TARGETS.contains(mixin) && annotation instanceof MethodListAnnotationNode injector
        && (handler.name.equals("getAbilityHud") || handler.name.equals("getCountsHud")
            || handler.name.equals("phantomHud") || handler.name.equals("starstruckHud")
            || handler.name.equals("onRenderHud"))) {
      //InGameHud.renderMainHud(DrawContext, RenderTickCounter)
      String mainHud = FabricLoader.getInstance().getMappingResolver().mapMethodName(
          "intermediary", "net.minecraft.class_329", "method_55805",
          "(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V");
      injector.setMethod(List.of(mainHud));
    }
    return annotation;
  }
}
