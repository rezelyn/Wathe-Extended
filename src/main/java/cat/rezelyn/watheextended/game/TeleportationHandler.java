package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportationHandler {

    private static final Map<RegistryKey<World>, Long> schedule = new ConcurrentHashMap<>();
    private static final Map<RegistryKey<World>, Boolean> prevStarting = new ConcurrentHashMap<>();

    private TeleportationHandler() {}

    public static void tick(ServerWorld world, GameWorldComponent.GameStatus status, long worldTime) {
        boolean isStarting = status == GameWorldComponent.GameStatus.STARTING;
        boolean wasStarting = prevStarting.getOrDefault(world.getRegistryKey(), false);

        if (isStarting && !wasStarting) {
            schedule.put(world.getRegistryKey(), worldTime + 40); // 2s (after screen fades black)
        }
        prevStarting.put(world.getRegistryKey(), isStarting);

        Long fireAt = schedule.get(world.getRegistryKey());
        if (fireAt != null && worldTime >= fireAt) {
            schedule.remove(world.getRegistryKey());
            perform(world);
        }
    }

    private static void perform(ServerWorld world) {
        try {
            if (!WatheExtendedWorldComponent.KEY.get(world).isRtpEnabled()) return;
            teleportAll(world);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Assigns each player in the world a distinct teleportation slot and teleports them there.
     * Both lists are shuffled so the pairing is random and no slot is used twice; when there are
     * more players than slots, the surplus players stay where they are.
     *
     * <p>This ignores the RTP map variable, so callers that trigger it manually (such as the
     * Trigger RTP item) work even when automatic RTP at game start is disabled.
     *
     * @return the number of players teleported
     */
    public static int teleportAll(ServerWorld world) {
        WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);

        List<TeleportationSlot> slots = new ArrayList<>(component.getTeleportationSlots().values());
        if (slots.isEmpty()) return 0;

        List<ServerPlayerEntity> eligible = new ArrayList<>();
        for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity sp) eligible.add(sp);
        }
        if (eligible.isEmpty()) return 0;

        Collections.shuffle(eligible);
        Collections.shuffle(slots);

        int count = Math.min(eligible.size(), slots.size());
        for (int i = 0; i < count; i++) {
            ServerPlayerEntity player = eligible.get(i);
            TeleportationSlot slot = slots.get(i);
            TeleportTarget target = new TeleportTarget(world, new Vec3d(slot.x, slot.y, slot.z), Vec3d.ZERO, slot.yaw, slot.pitch, TeleportTarget.NO_OP);

            if (player.hasVehicle()) {
                player.stopRiding();
            }

            player.teleportTo(target);
        }
        return count;
    }
}
