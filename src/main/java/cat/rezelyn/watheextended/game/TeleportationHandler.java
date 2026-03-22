package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
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
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            if (!wec.isRtpEnabled()) return;

            List<TeleportationSlot> slots = new ArrayList<>(wec.getTeleportationSlots().values());
            if (slots.isEmpty()) return;

            List<ServerPlayerEntity> eligiblePlayers = new ArrayList<>();
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (player instanceof ServerPlayerEntity sp) eligiblePlayers.add(sp);
            }
            if (eligiblePlayers.isEmpty()) return;

            Collections.shuffle(eligiblePlayers);
            Collections.shuffle(slots);

            int count = Math.min(eligiblePlayers.size(), slots.size());
            for (int i = 0; i < count; i++) {
                ServerPlayerEntity player = eligiblePlayers.get(i);
                TeleportationSlot slot = slots.get(i);
                TeleportTarget target = new TeleportTarget(world, new Vec3d(slot.x, slot.y, slot.z), Vec3d.ZERO, slot.yaw, slot.pitch, TeleportTarget.NO_OP);
                player.teleportTo(target);
            }
        } catch (Throwable ignored) {
        }
    }
}
