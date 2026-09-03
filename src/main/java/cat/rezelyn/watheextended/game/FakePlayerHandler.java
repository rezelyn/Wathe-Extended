package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.api.MapVariables;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Spawns and despawns the Carpet mod fake players used to test the map with a full lobby.
 *
 * <p>This is the mod-side port of the datapack's {@code wathe:debug/fp/add} and
 * {@code wathe:debug/fp/remove} functions. Carpet exposes fake players only through its
 * {@code /player} command, so there is no API to call: the commands are dispatched through the
 * server's own command manager using a silent operator source, which is why nothing happens (beyond
 * a log line) when Carpet is not installed.
 */
public final class FakePlayerHandler {

    /** Matches the datapack's wathe_fp_1..wathe_fp_11 roster. */
    public static final int FAKE_PLAYER_COUNT = 11;
    private static final String NAME_PREFIX = "wathe_fp_";

    /** Used when the map has no ready area spawn configured; the datapack's hardcoded position. */
    private static final Vec3d DEFAULT_POS = new Vec3d(-999.5, 1.0, -360.5);
    private static final float DEFAULT_YAW = -90.0F;
    private static final float DEFAULT_PITCH = 0.0F;

    private FakePlayerHandler() {}

    /**
     * Spawns the full roster of fake players at the ready area spawn, in adventure mode.
     *
     * @return the number of spawn commands that succeeded; 0 means Carpet is missing
     */
    public static int spawnAll(ServerWorld world) {
        if (!isCarpetAvailable(world)) return 0;

        Vec3d pos = DEFAULT_POS;
        float yaw = DEFAULT_YAW;
        float pitch = DEFAULT_PITCH;

        MapVariablesWorldComponent.PosWithOrientation spawn = MapVariables.getReadyAreaSpawnPosition(world);
        if (spawn == null) {
            spawn = MapVariables.getSpawnPosition(world);
        }
        if (spawn != null && spawn.pos != null) {
            pos = spawn.pos;
            yaw = spawn.yaw;
            pitch = spawn.pitch;
        }

        String dimension = world.getRegistryKey().getValue().toString();
        int spawned = 0;
        for (int i = 1; i <= FAKE_PLAYER_COUNT; i++) {
            String command = String.format(
                    "player %s%d spawn at %.2f %.2f %.2f facing %.2f %.2f in %s in adventure",
                    NAME_PREFIX, i, pos.x, pos.y, pos.z, yaw, pitch, dimension);
            if (run(world.getServer(), command)) spawned++;
        }
        return spawned;
    }

    /**
     * Removes every fake player of the roster.
     *
     * @return the number of kill commands that succeeded; 0 means Carpet is missing or none existed
     */
    public static int removeAll(ServerWorld world) {
        if (!isCarpetAvailable(world)) return 0;

        int removed = 0;
        for (int i = 1; i <= FAKE_PLAYER_COUNT; i++) {
            if (run(world.getServer(), "player " + NAME_PREFIX + i + " kill")) removed++;
        }
        return removed;
    }

    /**
     * Carpet registers its {@code /player} command at server start, so the presence of that node in
     * the dispatcher is what tells us whether fake players are possible at all. The command manager
     * reports an unknown command to the source instead of throwing, so this check is the only way to
     * distinguish "Carpet is missing" from "the commands ran".
     */
    public static boolean isCarpetAvailable(ServerWorld world) {
        MinecraftServer server = world == null ? null : world.getServer();
        if (server == null) return false;
        try {
            return server.getCommandManager().getDispatcher().getRoot().getChild("player") != null;
        } catch (Throwable throwable) {
            return false;
        }
    }

    private static boolean run(MinecraftServer server, String command) {
        if (server == null) return false;
        try {
            ServerCommandSource source = server.getCommandSource().withSilent().withLevel(4);
            server.getCommandManager().executeWithPrefix(source, command);
            return true;
        } catch (Throwable throwable) {
            WatheExtended.LOGGER.warn("Failed to run fake player command '{}'", command, throwable);
            return false;
        }
    }
}
