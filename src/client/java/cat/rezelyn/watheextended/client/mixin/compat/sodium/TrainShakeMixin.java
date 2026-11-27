package cat.rezelyn.watheextended.client.mixin.compat.sodium;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.WatheConfig;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
abstract class TrainShakeMixin {
  @Unique
  private final PerlinNoiseSampler watheextended$wind = new PerlinNoiseSampler(Random.create());

  @ModifyArg(
      method = "renderWorld",
      index = 5,
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
  private Matrix4f watheextended$shakeWorld(Matrix4f view) {
    var client = MinecraftClient.getInstance();
    var player = client.player;

    if (WatheConfig.disableScreenShake
        || !WatheClient.isTrainMoving()
        || player == null
        || WatheClient.moodComponent == null) return view;

    boolean outside = Wathe.isSkyVisibleAdjacent(player);
    float delta = client.getRenderTickCounter().getTickDelta(false);
    float amplitude =
        (outside ? 0.01f : 0.0025f) * (2 - WatheClient.moodComponent.getMood()) * 2.5f;
    float phase = (player.age + delta) * (outside ? 1f : 0.5f);

    Matrix4f shaken = new Matrix4f();
    if (outside && Wathe.isExposedToWind(player)) {
      float t = (player.getWorld().getTime() % 24000 + delta) / 0.2f;
      float pitch = (float) Math.toRadians(client.gameRenderer.getCamera().getPitch());
      float pitchOffset = (float) Math.toRadians(0.9 * watheextended$wind.sample(t, -10, 0));
      float yawOffset = (float) Math.toRadians(0.9 * watheextended$wind.sample(t, 10, 0));
      shaken.rotateX(pitch + pitchOffset).rotateY(yawOffset).rotateX(-pitch);
    }

    return shaken
        .mul(view)
        .translate(
            0, (float) -Math.sin(phase) * amplitude / 2, (float) -Math.cos(phase) * amplitude);
  }
}
