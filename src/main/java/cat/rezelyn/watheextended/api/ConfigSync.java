package cat.rezelyn.watheextended.api;

import net.minecraft.server.world.ServerWorld;

import java.util.List;

public final class ConfigSync {

    private static int timer = 0;
    private static List<String> lastKnownDisabledRoles = List.of();
    private static List<String> lastKnownDisabledModifiers = List.of();

    private ConfigSync() {}

    public static void tick(ServerWorld world) {
        if (++timer < 20) return;
        timer = 0;

        List<String> currentRoles = cat.rezelyn.watheextended.api.hml.ConfigHelper.getDisabledRoles();
        List<String> currentModifiers = cat.rezelyn.watheextended.api.hml.ConfigHelper.getDisabledModifiers();

        if (!currentRoles.equals(lastKnownDisabledRoles) || !currentModifiers.equals(lastKnownDisabledModifiers)) {
            lastKnownDisabledRoles = List.copyOf(currentRoles);
            lastKnownDisabledModifiers = List.copyOf(currentModifiers);
            ServerConfig.broadcastToAll(world.getServer());
        }
    }
}

