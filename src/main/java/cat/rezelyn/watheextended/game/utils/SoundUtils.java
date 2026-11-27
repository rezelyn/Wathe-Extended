package cat.rezelyn.watheextended.game.utils;

import dev.doctor4t.wathe.util.Scheduler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public final class SoundUtils {
  private SoundUtils() {}

  public static void playAtPosition(
      ServerWorld world,
      Vec3d pos,
      SoundEvent sound,
      SoundCategory category,
      float volume,
      float pitch) {

    world.playSound(null, pos.x, pos.y, pos.z, sound, category, volume, pitch);
  }

  public static void playAtEntity(
      Entity source,
      SoundEvent sound,
      float volume,
      float pitch,
      int delayTicks) {

    if (!(source.getWorld() instanceof ServerWorld world)) return;

    Vec3d pos = source.getPos();
    SoundCategory category = source.getSoundCategory();
    Scheduler.schedule(() -> playAtPosition(world, pos, sound, category, volume, pitch), delayTicks);
  }
}
