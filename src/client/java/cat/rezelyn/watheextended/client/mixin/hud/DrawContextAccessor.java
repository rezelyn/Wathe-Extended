package cat.rezelyn.watheextended.client.mixin.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public interface DrawContextAccessor {

  @Invoker("drawTexturedQuad")
  void watheextended$drawTintedQuad(
      Identifier texture,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      float u1,
      float u2,
      float v1,
      float v2,
      float red,
      float green,
      float blue,
      float alpha);
}
