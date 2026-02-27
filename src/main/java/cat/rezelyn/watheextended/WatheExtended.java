package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.api.cca.GameStatus;
import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.command.GamemodeRulesCommand;
import cat.rezelyn.watheextended.command.TeleportationSlotsCommand;
import cat.rezelyn.watheextended.command.WatheExtendedMapVariablesCommand;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WatheExtended implements ModInitializer {
    public static final String MOD_ID = "watheextended";
    private static final Logger log = LoggerFactory.getLogger(WatheExtended.class);

    private static final Map<RegistryKey<World>, Long> rtpSchedule = new ConcurrentHashMap<>();
    private static final Map<RegistryKey<World>, Boolean> prevStarting = new ConcurrentHashMap<>();
    private static final int RTP_FADE_TICK = 40;

    public static @NotNull Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }

    public static void giveTeleportItem(ServerPlayerEntity player) {
        boolean haveItem = false;
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                haveItem = true;
                break;
            }
        }
        if (!haveItem) {
            player.getInventory().insertStack(new ItemStack(WatheExtendedItems.TELEPORT_TO_READY_AREA));
        }
    }

    public static void removeTeleportItem(ServerPlayerEntity player) {
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.TELEPORT_TO_READY_AREA)) {
                player.getInventory().setStack(item, ItemStack.EMPTY);
            }
        }
    }

    public static void giveGuidebook(ServerPlayerEntity player) {
        boolean haveItem = false;
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.GUIDEBOOK)) {
                haveItem = true;
                break;
            }
        }
        if (!haveItem) {
            player.getInventory().insertStack(new ItemStack(WatheExtendedItems.GUIDEBOOK));
        }
    }

    public static void removeGuidebook(ServerPlayerEntity player) {
        for (int item = 0; item < player.getInventory().size(); item++) {
            if (player.getInventory().getStack(item).isOf(WatheExtendedItems.GUIDEBOOK)) {
                player.getInventory().setStack(item, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onInitialize() {
        WatheExtendedItems.initialize();
        WatheExtendedBlocks.initialize();
        WatheExtendedSounds.initialize();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WatheExtendedMapVariablesCommand.register(dispatcher);
            TeleportationSlotsCommand.register(dispatcher);
            GamemodeRulesCommand.register(dispatcher);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            boolean gameRunning = GameStatus.State(world);
            boolean isStarting = isStarting(world);
            boolean wasStarting = prevStarting.getOrDefault(world.getRegistryKey(), false);

            if (isStarting && !wasStarting) {
                rtpSchedule.put(world.getRegistryKey(), world.getTime() + RTP_FADE_TICK);
            }
            prevStarting.put(world.getRegistryKey(), isStarting);

            Long fireAt = rtpSchedule.get(world.getRegistryKey());
            if (fireAt != null && world.getTime() >= fireAt) {
                rtpSchedule.remove(world.getRegistryKey());
                performRtp(serverWorld);
            }

            Box readyArea = MapVariables.getReadyArea(world);
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity serverPlayer)) continue;

                if (gameRunning) {
                    removeTeleportItem(serverPlayer);
                    removeGuidebook(serverPlayer);
                } else if (readyArea != null && readyArea.contains(serverPlayer.getPos())) {
                    removeTeleportItem(serverPlayer);
                    giveGuidebook(serverPlayer);
                } else {
                    giveTeleportItem(serverPlayer);
                    giveGuidebook(serverPlayer);
                }
            }
        });

        log.info("Mod initialized");
    }

    private static boolean isStarting(World world) {
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            return gwc != null && gwc.getGameStatus() == GameWorldComponent.GameStatus.STARTING;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void performRtp(ServerWorld world) {
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            if (!wec.isRtpEnabled()) return;

            List<TeleportationSlot> slots = new ArrayList<>(wec.getTeleportationSlots());
            if (slots.isEmpty()) return;

            List<ServerPlayerEntity> eligiblePlayers = new ArrayList<>();
            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                eligiblePlayers.add(sp);
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
