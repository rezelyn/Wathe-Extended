package cat.rezelyn.watheextended.teleport;

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

    public static TeleportationSlot fromNbt(NbtCompound tag) {
        return new TeleportationSlot(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z"),
                tag.getFloat("yaw"),
                tag.getFloat("pitch")
        );
    }

    @Override
    public String toString() {
        return String.format("%.4f %.4f %.4f (yaw=%.4f, pitch=%.4f)", x, y, z, (double) yaw, (double) pitch);
    }

    public String toCommandArgs() {
        return rounded(x) + " " + rounded(y) + " " + rounded(z) + " " + rounded(yaw) + " " + rounded(pitch);
    }

    private static String rounded(double v) {
        // round double to 4 decimal places and trim zeros
        String round = String.format("%.4f", v);
        round = round.replaceAll("0+$", "").replaceAll("\\.$", "");
        return round;
    }
}

