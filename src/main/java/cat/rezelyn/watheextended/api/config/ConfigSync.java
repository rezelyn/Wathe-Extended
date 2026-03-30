package cat.rezelyn.watheextended.api.config;

import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
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

        List<String> currentRoles = ConfigHelper.getDisabledRoles();
        List<String> currentModifiers = ConfigHelper.getDisabledModifiers();

        if (!currentRoles.equals(lastKnownDisabledRoles) || !currentModifiers.equals(lastKnownDisabledModifiers)) {
            lastKnownDisabledRoles = List.copyOf(currentRoles);
            lastKnownDisabledModifiers = List.copyOf(currentModifiers);
            ServerConfig.broadcastToAll(world.getServer());
        }
    }
}

