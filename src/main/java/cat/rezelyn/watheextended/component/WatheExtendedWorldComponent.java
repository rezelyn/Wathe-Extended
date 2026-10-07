package cat.rezelyn.watheextended.component;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.game.utils.TeleportationSlot;
import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.api.WatheMapEffects;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.doctor4t.wathe.cca.TrainWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import java.util.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.agmas.harpymodloader.Harpymodloader;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class WatheExtendedWorldComponent implements AutoSyncedComponent {

  public static final ComponentKey<WatheExtendedWorldComponent> KEY =
      ComponentRegistry.getOrCreate(
          WatheExtended.id("mapvariables"), WatheExtendedWorldComponent.class);
  public static final Box DEFAULT_LOBBY_AREA = new Box(-1424, -50, -512, -753, 50, -225);
  private static final MapVariablesWorldComponent.PosWithOrientation DEFAULT_READY_AREA_SPAWN_POS =
      new MapVariablesWorldComponent.PosWithOrientation(new Vec3d(-999.5, 1.0, -360.5), -90f, 0f);
  private final World world;
  private final Map<Integer, TeleportationSlot> teleportationSlots = new LinkedHashMap<>();
  private final Set<UUID> killedPlayers = new HashSet<>();
  private final Set<UUID> revolverPickupBlockedPlayers = new HashSet<>();
  private int nextSlotId = 1;
  private MapVariablesWorldComponent.PosWithOrientation readyAreaSpawnPos =
      DEFAULT_READY_AREA_SPAWN_POS;
  private Box lobbyArea = DEFAULT_LOBBY_AREA;

  private boolean randomTeleportationEnabled = true;
  private boolean playerCollisionsEnabled = true;
  private boolean blockInteractionsProtected = true;
  private boolean itemBoundsCheckEnabled = true;
  private boolean forbiddenLoversEnabled = false;
  private long gameStartWorldTime = -1L;
  private String gameTimeOfDay = "NIGHT";
  private String lobbyTimeOfDay = "DAY";
  private boolean genericMapEffectEnabled = false;
  private String gameModeSelection = "MODDED_MURDER";
  private int gameDurationMinutes = 10;

  public WatheExtendedWorldComponent(World world) {
    this.world = world;
  }

  private static MapVariablesWorldComponent.PosWithOrientation getPosWithOrientationFromNbt(
      NbtCompound tag, String name) {
    Vec3d pos =
        new Vec3d(tag.getDouble(name + "X"), tag.getDouble(name + "Y"), tag.getDouble(name + "Z"));
    return new MapVariablesWorldComponent.PosWithOrientation(
        pos, tag.getFloat(name + "Yaw"), tag.getFloat(name + "Pitch"));
  }

  private static void writePosWithOrientationToNbt(
      NbtCompound tag, MapVariablesWorldComponent.PosWithOrientation pos, String name) {
    tag.putDouble(name + "X", pos.pos.getX());
    tag.putDouble(name + "Y", pos.pos.getY());
    tag.putDouble(name + "Z", pos.pos.getZ());
    tag.putFloat(name + "Yaw", pos.yaw);
    tag.putFloat(name + "Pitch", pos.pitch);
  }

  public static void setTrainTime(ServerWorld world, String value) {
    try {
      TrainWorldComponent.TimeOfDay time =
          TrainWorldComponent.TimeOfDay.valueOf(normalizeTime(value, "DAY"));
      TrainWorldComponent.KEY.get(world).setTimeOfDay(time);
    } catch (Throwable ignored) {
    }
  }

  private static String normalizeTime(String value, String fallback) {
    try {
      return TrainWorldComponent.TimeOfDay.valueOf(value.toUpperCase(Locale.ROOT)).name();
    } catch (Throwable ignored) {
      return fallback;
    }
  }

  private static MapEffect mapEffectFor(String value) {
    return switch (normalizeTime(value, "NIGHT")) {
      case "DAY" -> WatheMapEffects.HARPY_EXPRESS_DAY;
      case "SUNDOWN" -> WatheMapEffects.HARPY_EXPRESS_SUNDOWN;
      default -> WatheMapEffects.HARPY_EXPRESS_NIGHT;
    };
  }

  private static GameMode gameModeForSelection(String selection) {
    return switch (selection) {
      case "MODDED_MURDER" -> Harpymodloader.MODDED_GAMEMODE;
      case "MODDED_SECRET_MURDER" -> Harpymodloader.SECRET_MODDED_GAMEMODE;
      case "LOOSE_ENDS" -> WatheGameModes.LOOSE_ENDS;
      case "SECRET_MURDER" -> WatheGameModes.SECRET_MURDER;
      case "DISCOVERY" -> WatheGameModes.DISCOVERY;
      default -> WatheGameModes.MURDER;
    };
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

  public void blockRevolverPickup(UUID uuid) {
    revolverPickupBlockedPlayers.add(uuid);
  }

  public void unblockRevolverPickup(UUID uuid) {
    revolverPickupBlockedPlayers.remove(uuid);
  }

  public boolean isRevolverPickupBlocked(UUID uuid) {
    return revolverPickupBlockedPlayers.contains(uuid);
  }

  public void clearRevolverPickupBlocks() {
    revolverPickupBlockedPlayers.clear();
  }

  public void sync() {
    KEY.sync(this.world);
  }

  public String getGameTimeOfDay() {
    return gameTimeOfDay;
  }

  public void setGameTimeOfDay(String value) {
    gameTimeOfDay = normalizeTime(value, "NIGHT");
    sync();
  }

  public String getLobbyTimeOfDay() {
    return lobbyTimeOfDay;
  }

  public void setLobbyTimeOfDay(String value) {
    lobbyTimeOfDay = normalizeTime(value, "DAY");
    if (world instanceof ServerWorld serverWorld
        && !GameWorldComponent.KEY.get(world).isRunning()) {
      setTrainTime(serverWorld, lobbyTimeOfDay);
    }
    sync();
  }

  public boolean isGenericMapEffectEnabled() {
    return genericMapEffectEnabled;
  }

  public void setGenericMapEffectEnabled(boolean enabled) {
    genericMapEffectEnabled = enabled;
    sync();
  }

  public MapEffect getConfiguredGameMapEffect() {
    return genericMapEffectEnabled ? WatheMapEffects.GENERIC : mapEffectFor(gameTimeOfDay);
  }

  public String getGameModeSelection() {
    return gameModeSelection;
  }

  public void setGameModeSelection(String value) {
    gameModeSelection = value == null ? "MODDED_MURDER" : value.toUpperCase(Locale.ROOT);
    sync();
  }

  public int getGameDurationMinutes() {
    return gameDurationMinutes;
  }

  public void setGameDurationMinutes(int value) {
    gameDurationMinutes = Math.clamp(value, 1, 60);
    sync();
  }

  public int getConfiguredGameDurationTicks() {
    return GameConstants.getInTicks(gameDurationMinutes, 0);
  }

  public void setConfiguredGameMode(String value) {
    String selection = value == null ? "MODDED_MURDER" : value.toUpperCase(Locale.ROOT);
    GameMode mode = gameModeForSelection(selection);
    if (!GameWorldComponent.KEY.get(world).isRunning()) {
      GameWorldComponent.KEY.get(world).setGameMode(mode);
      Harpymodloader.wantsToStartVannila = "MURDER".equals(selection);
    }
    gameModeSelection = selection;
    sync();
  }

  /**
   * Restores the persistent default after a secret round, which Wathe otherwise persists as the
   * current mode
   */
  public void restoreModdedMurderDefault() {
    gameModeSelection = "MODDED_MURDER";
    GameWorldComponent game = GameWorldComponent.KEY.get(world);
    game.setGameMode(Harpymodloader.MODDED_GAMEMODE);
    game.sync();
    sync();
  }

  /** Repairs saves where Wathe persisted the rolled secret mode as the inactive world's mode */
  public void restoreSecretMurderMode() {
    GameWorldComponent game = GameWorldComponent.KEY.get(world);
    if (!game.isRunning()
        && "MODDED_MURDER".equals(gameModeSelection)
        && (game.getGameMode() == WatheGameModes.SECRET_MURDER
            || game.getGameMode() == Harpymodloader.SECRET_MODDED_GAMEMODE)) {
      game.setGameMode(Harpymodloader.MODDED_GAMEMODE);
      game.sync();
    }
  }

  public GameMode getGameModeForStart() {
    boolean isModdedMurder = "MODDED_MURDER".equals(gameModeSelection);
    float chance =
        Math.clamp(GameWorldComponent.KEY.get(world).getSecretMurderRoundChance(), 0.0f, 1.0f);
    if (isModdedMurder && world.getRandom().nextFloat() < chance) {
      return Harpymodloader.SECRET_MODDED_GAMEMODE;
    }
    // Wathe's own startGame handles its standard MURDER roll from this same component value.
    return gameModeForSelection(gameModeSelection);
  }

  public boolean usesVanillaGameModeAtStart() {
    return !gameModeSelection.startsWith("MODDED_");
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
    WatheExtendedServerConfig.set("rtpEnabled", enabled);
    this.sync();
  }

  public boolean isPlayerCollisionsEnabled() {
    return playerCollisionsEnabled;
  }

  public void setPlayerCollisionsEnabled(boolean enabled) {
    this.playerCollisionsEnabled = enabled;
    WatheExtendedServerConfig.set("playerCollisionsEnabled", enabled);
    this.sync();
  }

  public boolean isBlockInteractionsProtected() {
    return blockInteractionsProtected;
  }

  public void setBlockInteractionsProtected(boolean enabled) {
    this.blockInteractionsProtected = enabled;
    WatheExtendedServerConfig.set("blockProtectionEnabled", enabled);
    this.sync();
  }

  public boolean isItemBoundsCheckEnabled() {
    return itemBoundsCheckEnabled;
  }

  public void setItemBoundsCheckEnabled(boolean enabled) {
    this.itemBoundsCheckEnabled = enabled;
    WatheExtendedServerConfig.set("itemBoundsCheckEnabled", enabled);
    this.sync();
  }

  public boolean isForbiddenLoversEnabled() {
    return forbiddenLoversEnabled;
  }

  public void setForbiddenLoversEnabled(boolean enabled) {
    this.forbiddenLoversEnabled = enabled;
    WatheExtendedServerConfig.set("forbiddenLoversEnabled", enabled);
    this.sync();
  }

  public long getGameStartWorldTime() {
    return gameStartWorldTime;
  }

  public void setGameStartWorldTime(long time) {
    this.gameStartWorldTime = time;
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
  public void readFromNbt(
      @NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
    if (tag.contains("readyAreaSpawnPosX")) {
      this.readyAreaSpawnPos = getPosWithOrientationFromNbt(tag, "readyAreaSpawnPos");
    } else {
      this.readyAreaSpawnPos = DEFAULT_READY_AREA_SPAWN_POS;
    }

    this.randomTeleportationEnabled =
        tag.contains("randomTeleportationEnabled")
            ? tag.getBoolean("randomTeleportationEnabled")
            : WatheExtendedServerConfig.rtpEnabled;
    this.playerCollisionsEnabled =
        tag.contains("playerCollisionsEnabled")
            ? tag.getBoolean("playerCollisionsEnabled")
            : WatheExtendedServerConfig.playerCollisionsEnabled;
    this.blockInteractionsProtected =
        tag.contains("blockInteractionsProtected")
            ? tag.getBoolean("blockInteractionsProtected")
            : WatheExtendedServerConfig.blockProtectionEnabled;
    this.itemBoundsCheckEnabled =
        tag.contains("itemBoundsCheckEnabled")
            ? tag.getBoolean("itemBoundsCheckEnabled")
            : WatheExtendedServerConfig.itemBoundsCheckEnabled;
    this.forbiddenLoversEnabled =
        tag.contains("forbiddenLoversEnabled")
            ? tag.getBoolean("forbiddenLoversEnabled")
            : WatheExtendedServerConfig.forbiddenLoversEnabled;
    this.gameTimeOfDay = tag.contains("gameTimeOfDay") ? tag.getString("gameTimeOfDay") : "NIGHT";
    this.lobbyTimeOfDay = tag.contains("lobbyTimeOfDay") ? tag.getString("lobbyTimeOfDay") : "DAY";
    this.genericMapEffectEnabled =
        tag.contains("genericMapEffectEnabled") && tag.getBoolean("genericMapEffectEnabled");
    this.gameModeSelection =
        tag.contains("gameModeSelection") ? tag.getString("gameModeSelection") : "MODDED_MURDER";
    this.gameDurationMinutes =
        Math.clamp(
            tag.contains("gameDurationMinutes") ? tag.getInt("gameDurationMinutes") : 10, 1, 60);

    if (tag.contains("lobbyAreaMinX")) {
      this.lobbyArea =
          new Box(
              tag.getDouble("lobbyAreaMinX"),
              tag.getDouble("lobbyAreaMinY"),
              tag.getDouble("lobbyAreaMinZ"),
              tag.getDouble("lobbyAreaMaxX"),
              tag.getDouble("lobbyAreaMaxY"),
              tag.getDouble("lobbyAreaMaxZ"));
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
    this.nextSlotId =
        teleportationSlots.isEmpty() ? 1 : Collections.max(teleportationSlots.keySet()) + 1;

    this.revolverPickupBlockedPlayers.clear();
    if (tag.contains("revolverPickupBlockedPlayers")) {
      NbtList list = tag.getList("revolverPickupBlockedPlayers", NbtElement.STRING_TYPE);
      for (int i = 0; i < list.size(); i++) {
        try {
          this.revolverPickupBlockedPlayers.add(UUID.fromString(list.getString(i)));
        } catch (Throwable ignored) {
        }
      }
    }
  }

  @Override
  public void writeToNbt(
      @NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
    writePosWithOrientationToNbt(tag, this.readyAreaSpawnPos, "readyAreaSpawnPos");

    tag.putBoolean("randomTeleportationEnabled", this.randomTeleportationEnabled);
    tag.putBoolean("playerCollisionsEnabled", this.playerCollisionsEnabled);
    tag.putBoolean("blockInteractionsProtected", this.blockInteractionsProtected);
    tag.putBoolean("itemBoundsCheckEnabled", this.itemBoundsCheckEnabled);
    tag.putBoolean("forbiddenLoversEnabled", this.forbiddenLoversEnabled);
    tag.putString("gameTimeOfDay", this.gameTimeOfDay);
    tag.putString("lobbyTimeOfDay", this.lobbyTimeOfDay);
    tag.putBoolean("genericMapEffectEnabled", this.genericMapEffectEnabled);
    tag.putString("gameModeSelection", this.gameModeSelection);
    tag.putInt("gameDurationMinutes", this.gameDurationMinutes);

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

    NbtList blockedPlayers = new NbtList();
    for (UUID uuid : this.revolverPickupBlockedPlayers) {
      blockedPlayers.add(NbtString.of(uuid.toString()));
    }
    tag.put("revolverPickupBlockedPlayers", blockedPlayers);
  }
}
