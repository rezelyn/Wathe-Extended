package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.MapVariables;
import dev.doctor4t.wathe.index.WatheEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class ItemBoundsChecker {

    private static final List<Entity> TARGETS = new ArrayList<>();

    private ItemBoundsChecker() {}

    public static void tick(ServerWorld world) {
        try {
            Box playArea = MapVariables.getPlayArea(world);
            if (playArea == null) return;

            TARGETS.clear();
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (player instanceof ServerPlayerEntity p && p.isAlive() && !p.isSpectator() && !p.isCreative()) {
                    TARGETS.add(player);
                }
            }
            TARGETS.addAll(world.getEntitiesByType(WatheEntities.PLAYER_BODY, body -> true));

            if (TARGETS.isEmpty()) return;

            for (ItemEntity item : world.getEntitiesByType(EntityType.ITEM, e -> !playArea.contains(e.getPos()))) {
                Entity closest = findClosest(item.getPos(), TARGETS);
                if (closest == null) continue;
                Vec3d dest = closest.getPos();
                item.requestTeleport(dest.x, dest.y, dest.z);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Entity findClosest(Vec3d from, List<Entity> candidates) {
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            double distance = from.squaredDistanceTo(candidate.getPos());
            if (distance < bestDist) {
                bestDist = distance;
                best = candidate;
            }
        }
        return best;
    }
}

