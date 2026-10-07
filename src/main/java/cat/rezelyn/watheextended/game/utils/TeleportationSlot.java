package cat.rezelyn.watheextended.game.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public final class TeleportationSlot {

  public final double x;
  public final double y;
  public final double z;
  public final float yaw;
  public final float pitch;

  public TeleportationSlot(double x, double y, double z, float yaw, float pitch) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public static TeleportationSlot fromNbt(NbtCompound tag) {
    return new TeleportationSlot(
        tag.getDouble("x"),
        tag.getDouble("y"),
        tag.getDouble("z"),
        tag.getFloat("yaw"),
        tag.getFloat("pitch"));
  }

  private static String rounded(double v) {
    String string =
        new BigDecimal(Double.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
    return string.replaceAll("0+$", "").replaceAll("\\.$", "");
  }

  private static String rounded(float v) {
    String string =
        new BigDecimal(Float.toString(v)).setScale(2, RoundingMode.DOWN).toPlainString();
    return string.replaceAll("0+$", "").replaceAll("\\.$", "");
  }

  public Vec3d toVec3d() {
    return new Vec3d(x, y, z);
  }

  public NbtCompound toNbt() {
    NbtCompound tag = new NbtCompound();
    tag.putDouble("x", x);
    tag.putDouble("y", y);
    tag.putDouble("z", z);
    tag.putFloat("yaw", yaw);
    tag.putFloat("pitch", pitch);
    return tag;
  }

  @Override
  public String toString() {
    return String.format(
        "%.4f %.4f %.4f (yaw=%.4f, pitch=%.4f)", x, y, z, (double) yaw, (double) pitch);
  }

  public String toCommandArgs() {
    return rounded(x)
        + " "
        + rounded(y)
        + " "
        + rounded(z)
        + " "
        + rounded(yaw)
        + " "
        + rounded(pitch);
  }
}
