package cat.rezelyn.watheextended.cca;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WatheExtendedWorldComponent implements AutoSyncedComponent {

    public static final ComponentKey<WatheExtendedWorldComponent> KEY = ComponentRegistry.getOrCreate(WatheExtended.id("mapvariables"), WatheExtendedWorldComponent.class);
    private static final MapVariablesWorldComponent.PosWithOrientation DEFAULT_READY_AREA_SPAWN_POS = new MapVariablesWorldComponent.PosWithOrientation(new Vec3d(-999.5, 1.0, -360.5), -90f, 0f);
    public static final Box DEFAULT_LOBBY_AREA = new Box(-1424, -50, -512, -753, 50, -225);

    private final World world;
    private final Map<Integer, TeleportationSlot> teleportationSlots = new LinkedHashMap<>();
    private final Set<UUID> killedPlayers = new HashSet<>();
    private int nextSlotId = 1;
    private MapVariablesWorldComponent.PosWithOrientation readyAreaSpawnPos = DEFAULT_READY_AREA_SPAWN_POS;
    private Box lobbyArea = DEFAULT_LOBBY_AREA;

    private boolean randomTeleportationEnabled = true;
    private boolean playerCollisionsEnabled = true;
    private boolean blockInteractionsProtected = true;
    private boolean itemBoundsCheckEnabled = true;
    private boolean forbiddenLoversEnabled = false;

    public WatheExtendedWorldComponent(World world) {
        this.world = world;
    }
    public void markPlayerKilled(UUID uuid) {
        killedPlayers.add(uuid);
    }
    public boolean isPlayerKilled(UUID uuid) {
        return killedPlayers.contains(uuid);
    }
    public void clearKilledPlayers() {
        killedPlayers.clear();
    }

    private static MapVariablesWorldComponent.PosWithOrientation getPosWithOrientationFromNbt(NbtCompound tag, String name) {
        Vec3d pos = new Vec3d(tag.getDouble(name + "X"), tag.getDouble(name + "Y"), tag.getDouble(name + "Z"));
        return new MapVariablesWorldComponent.PosWithOrientation(pos, tag.getFloat(name + "Yaw"), tag.getFloat(name + "Pitch"));
    }

    private static void writePosWithOrientationToNbt(NbtCompound tag, MapVariablesWorldComponent.PosWithOrientation pos, String name) {
        tag.putDouble(name + "X", pos.pos.getX());
        tag.putDouble(name + "Y", pos.pos.getY());
        tag.putDouble(name + "Z", pos.pos.getZ());
        tag.putFloat(name + "Yaw", pos.yaw);
        tag.putFloat(name + "Pitch", pos.pitch);
    }

    public void sync() {
        KEY.sync(this.world);
    }

    public MapVariablesWorldComponent.PosWithOrientation getReadyAreaSpawnPos() {
        return readyAreaSpawnPos;
    }

    public void setReadyAreaSpawnPos(MapVariablesWorldComponent.PosWithOrientation pos) {
        this.readyAreaSpawnPos = pos;
        this.sync();
    }

    public boolean isRtpEnabled() {
        return randomTeleportationEnabled;
    }

    public void setRtpEnabled(boolean enabled) {
        this.randomTeleportationEnabled = enabled;
        cat.rezelyn.watheextended.WatheExtendedServerConfig.setRtpEnabled(enabled);
        this.sync();
    }

    public boolean isPlayerCollisionsEnabled() {
        return playerCollisionsEnabled;
    }

    public void setPlayerCollisionsEnabled(boolean enabled) {
        this.playerCollisionsEnabled = enabled;
        cat.rezelyn.watheextended.WatheExtendedServerConfig.setPlayerCollisionsEnabled(enabled);
        this.sync();
    }

    public boolean isBlockInteractionsProtected() {
        return blockInteractionsProtected;
    }

    public void setBlockInteractionsProtected(boolean enabled) {
        this.blockInteractionsProtected = enabled;
        cat.rezelyn.watheextended.WatheExtendedServerConfig.setBlockProtectionEnabled(enabled);
        this.sync();
    }

    public boolean isItemBoundsCheckEnabled() {
        return itemBoundsCheckEnabled;
    }

    public void setItemBoundsCheckEnabled(boolean enabled) {
        this.itemBoundsCheckEnabled = enabled;
        cat.rezelyn.watheextended.WatheExtendedServerConfig.setItemBoundsCheckEnabled(enabled);
        this.sync();
    }

    public boolean isForbiddenLoversEnabled() {
        return forbiddenLoversEnabled;
    }

    public void setForbiddenLoversEnabled(boolean enabled) {
        this.forbiddenLoversEnabled = enabled;
        cat.rezelyn.watheextended.WatheExtendedServerConfig.setForbiddenLoversEnabled(enabled);
        this.sync();
    }

    @NotNull
    public Box getLobbyArea() {
        return lobbyArea;
    }

    public void setLobbyArea(@NotNull Box area) {
        this.lobbyArea = area;
        this.sync();
    }

    public Map<Integer, TeleportationSlot> getTeleportationSlots() {
        return Collections.unmodifiableMap(teleportationSlots);
    }

    public void setTeleportationSlots(Map<Integer, TeleportationSlot> slots) {
        this.teleportationSlots.clear();
        this.teleportationSlots.putAll(slots);
        this.nextSlotId = slots.isEmpty() ? 1 : Collections.max(slots.keySet()) + 1;
        this.sync();
    }

    public int addTeleportationSlot(TeleportationSlot slot) {
        int id = nextSlotId++;
        this.teleportationSlots.put(id, slot);
        this.sync();
        return id;
    }

    public boolean removeTeleportationSlot(int id) {
        if (!teleportationSlots.containsKey(id)) return false;
        teleportationSlots.remove(id);
        this.sync();
        return true;
    }

    public boolean editTeleportationSlot(int id, TeleportationSlot slot) {
        if (!teleportationSlots.containsKey(id)) return false;
        teleportationSlots.put(id, slot);
        this.sync();
        return true;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        if (tag.contains("readyAreaSpawnPosX")) {
            this.readyAreaSpawnPos = getPosWithOrientationFromNbt(tag, "readyAreaSpawnPos");
        } else {
            this.readyAreaSpawnPos = DEFAULT_READY_AREA_SPAWN_POS;
        }

        this.randomTeleportationEnabled = tag.contains("randomTeleportationEnabled") ? tag.getBoolean("randomTeleportationEnabled") : cat.rezelyn.watheextended.WatheExtendedServerConfig.isRtpEnabled();
        this.playerCollisionsEnabled = tag.contains("playerCollisionsEnabled") ? tag.getBoolean("playerCollisionsEnabled") : cat.rezelyn.watheextended.WatheExtendedServerConfig.isPlayerCollisionsEnabled();
        this.blockInteractionsProtected = tag.contains("blockInteractionsProtected") ? tag.getBoolean("blockInteractionsProtected") : cat.rezelyn.watheextended.WatheExtendedServerConfig.isBlockProtectionEnabled();
        this.itemBoundsCheckEnabled = tag.contains("itemBoundsCheckEnabled") ? tag.getBoolean("itemBoundsCheckEnabled") : cat.rezelyn.watheextended.WatheExtendedServerConfig.isItemBoundsCheckEnabled();
        this.forbiddenLoversEnabled = tag.contains("forbiddenLoversEnabled") ? tag.getBoolean("forbiddenLoversEnabled") : cat.rezelyn.watheextended.WatheExtendedServerConfig.isForbiddenLoversEnabled();

        if (tag.contains("lobbyAreaMinX")) {
            this.lobbyArea = new Box(tag.getDouble("lobbyAreaMinX"), tag.getDouble("lobbyAreaMinY"), tag.getDouble("lobbyAreaMinZ"), tag.getDouble("lobbyAreaMaxX"), tag.getDouble("lobbyAreaMaxY"), tag.getDouble("lobbyAreaMaxZ"));
        } else {
            this.lobbyArea = DEFAULT_LOBBY_AREA;
        }

        this.teleportationSlots.clear();
        if (tag.contains("teleportationSlots")) {
            NbtList list = tag.getList("teleportationSlots", NbtCompound.COMPOUND_TYPE);
            int autoId = 1;
            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                if (entry.contains("id")) {
                    // new format: use stored stable ID
                    int id = entry.getInt("id");
                    this.teleportationSlots.put(id, TeleportationSlot.fromNbt(entry));
                    if (id >= autoId) autoId = id + 1;
                } else {
                    // legacy format: assign sequential IDs starting from 1
                    this.teleportationSlots.put(autoId++, TeleportationSlot.fromNbt(entry));
                }
            }
        }
        this.nextSlotId = teleportationSlots.isEmpty() ? 1 : Collections.max(teleportationSlots.keySet()) + 1;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        writePosWithOrientationToNbt(tag, this.readyAreaSpawnPos, "readyAreaSpawnPos");

        tag.putBoolean("randomTeleportationEnabled", this.randomTeleportationEnabled);
        tag.putBoolean("playerCollisionsEnabled", this.playerCollisionsEnabled);
        tag.putBoolean("blockInteractionsProtected", this.blockInteractionsProtected);
        tag.putBoolean("itemBoundsCheckEnabled", this.itemBoundsCheckEnabled);
        tag.putBoolean("forbiddenLoversEnabled", this.forbiddenLoversEnabled);

        tag.putDouble("lobbyAreaMinX", this.lobbyArea.minX);
        tag.putDouble("lobbyAreaMinY", this.lobbyArea.minY);
        tag.putDouble("lobbyAreaMinZ", this.lobbyArea.minZ);
        tag.putDouble("lobbyAreaMaxX", this.lobbyArea.maxX);
        tag.putDouble("lobbyAreaMaxY", this.lobbyArea.maxY);
        tag.putDouble("lobbyAreaMaxZ", this.lobbyArea.maxZ);

        NbtList list = new NbtList();
        for (Map.Entry<Integer, TeleportationSlot> entry : this.teleportationSlots.entrySet()) {
            NbtCompound slotTag = entry.getValue().toNbt();
            slotTag.putInt("id", entry.getKey());
            list.add(slotTag);
        }
        tag.put("teleportationSlots", list);
    }
}
