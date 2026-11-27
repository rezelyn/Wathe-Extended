package cat.rezelyn.watheextended.client.mixin.game.roles.judge;

import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.BsXinQin.kinswathe.client.roles.judge.JudgePlayerWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(JudgePlayerWidget.class)
public abstract class JudgePickerTooltipMixin {
  @ModifyArg(
      method = "renderWidget",
      at = @At(value = "INVOKE", target =
          "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V"),
      index = 1)
  private List<Text> watheextended$removeAbilityStatus(List<Text> lines) {
    return lines.stream()
        .filter(line -> !(line.getContent() instanceof TranslatableTextContent text
            && (text.getKey().equals("hud.kinswathe.judge.ready")
                || text.getKey().equals("tip.kinswathe.ability.not_enough_money"))))
        .toList();
  }
}
