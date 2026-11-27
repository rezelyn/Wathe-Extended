package cat.rezelyn.watheextended.client.render.hud.ability;

import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;

/** Typed client-side crosshair lookups for ability input and HUD elements */
@Environment(EnvType.CLIENT)
public final class AbilityTargets {
  private AbilityTargets() {}

  /** Returns the entity under the crosshair when it matches the requested type */
  public static <T extends Entity> Optional<T> underCrosshair(
      MinecraftClient client, Class<T> type) {

    if (client.player == null
        || !(client.crosshairTarget instanceof EntityHitResult hit)
        || !type.isInstance(hit.getEntity())) return Optional.empty();

    return Optional.of(type.cast(hit.getEntity()));
  }

  /** Also requires the raycast at the given reach to hit the eligible entity. */
  public static <T extends Entity> Optional<T> underCrosshair(
      MinecraftClient client, Class<T> type, double reach, Predicate<? super T> eligible) {

    if (!Double.isFinite(reach) || reach <= 0) return Optional.empty();

    return underCrosshair(client, type)
        .filter(target ->
            ProjectileUtil.getCollision(
                        client.player,
                        entity -> type.isInstance(entity) && eligible.test(type.cast(entity)),
                        reach)
                    instanceof EntityHitResult hit
                && hit.getEntity() == target);
  }
}
