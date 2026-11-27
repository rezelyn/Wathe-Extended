package cat.rezelyn.watheextended.client.render.scenery;

import dev.doctor4t.wathe.cca.TrainWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkSectionPos;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class TrainScenery {
  private static final int TILE_LENGTH = 32 * 16;
  private static final int LOOP_LENGTH = 3 * TILE_LENGTH;
  private static final int TRACK_SPACING = 15 * 16;
  private static final float MODEL_MARGIN = 2.125f;

  private final FrustumIntersection frustum = new FrustumIntersection();
  private final Matrix4f clip = new Matrix4f();

  private double cameraX, cameraY, cameraZ;
  private int cameraZ_s, distance;
  private float scroll;
  private boolean moving;
  private float dx, dy, dz;

  public void begin(Matrix4fc view, Matrix4fc projection, double x, double y, double z) {
    moving = WatheClient.isTrainMoving();

    if (!moving) return;
    cameraX = x;
    cameraY = y;
    cameraZ = z;
    cameraZ_s = ChunkSectionPos.getSectionCoord(z);

    float ticks =
        WatheClient.trainComponent.getTime()
            + MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
    scroll = ticks / 73.8f * WatheClient.getTrainSpeed();

    distance =
        WatheClient.trainComponent.getTimeOfDay() == TrainWorldComponent.TimeOfDay.SUNDOWN
            ? 320
            : 160;

    frustum.set(clip.set(projection).mul(view));
  }

  public boolean moving() {
    return moving;
  }

  public float dx() {
    return dx;
  }

  public float dy() {
    return dy;
  }

  public float dz() {
    return dz;
  }

  public void locate(int x, int y, int z) {
    int relativeSection = (z >> 4) - cameraZ_s;
    int side = relativeSection <= -8 ? -1 : relativeSection >= 8 ? 1 : 0;

    if (side == 0 && y >= 64) {
      dx = dy = dz = 0;
      return;
    }

    float relativeX = (float) (x - cameraX);
    dx = (relativeX + side * TILE_LENGTH + scroll) % LOOP_LENGTH - LOOP_LENGTH / 2f - relativeX;
    dy = 116;
    dz = -side * TRACK_SPACING;
  }

  public boolean visible(int x, int y, int z, boolean shadowPass) {
    float left = (float) (x - cameraX) + dx;
    float bottom = (float) (y - cameraY) + dy;
    float near = (float) (z - cameraZ) + dz;

    return Math.abs(left) < distance
        && (shadowPass
            || frustum.testAab(
                left - MODEL_MARGIN,
                bottom - MODEL_MARGIN,
                near - MODEL_MARGIN,
                left + 16 + MODEL_MARGIN,
                bottom + 16 + MODEL_MARGIN,
                near + 16 + MODEL_MARGIN));
  }
}
