package cat.rezelyn.watheextended.mixin.client.game.roles.physician;

import cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public abstract class PhysicianDeathReasonTextMixin {

  @ModifyVariable(
      method =
          "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0)
  private Text watheextended$hidePhysicianDeathReason(Text text) {
    if (!ConfigHelper.getDisablePhysicianDeathReason()
        || !(text.getContent() instanceof TranslatableTextContent content)
        || !"hud.death_info".equals(content.getKey())) {
      return text;
    }

    return Text.empty();
  }
}
