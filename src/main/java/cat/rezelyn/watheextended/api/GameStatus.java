package cat.rezelyn.watheextended.api;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.world.World;

public class GameStatus {
    public static boolean State(World world) {
        if (world == null) return false;
        try {
            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            GameWorldComponent.GameStatus status = component.getGameStatus();
            return status == GameWorldComponent.GameStatus.ACTIVE || status == GameWorldComponent.GameStatus.STOPPING || status == GameWorldComponent.GameStatus.STARTING;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isActive(World world) {
        if (world == null) return false;
        try {
            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            return component != null && component.getGameStatus() == GameWorldComponent.GameStatus.ACTIVE;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isStarting(World world) {
        if (world == null) return false;
        try {
            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            return component != null && component.getGameStatus() == GameWorldComponent.GameStatus.STARTING;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isStopping(World world) {
        if (world == null) return false;
        try {
            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            return component != null && component.getGameStatus() == GameWorldComponent.GameStatus.STOPPING;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isInactive(World world) {
        if (world == null) return true;
        try {
            GameWorldComponent component = GameWorldComponent.KEY.get(world);
            return component == null || component.getGameStatus() == GameWorldComponent.GameStatus.INACTIVE;
        } catch (Throwable t) {
            return true;
        }
    }
}
