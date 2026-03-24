package cat.rezelyn.watheextended.api.wathe;

import dev.doctor4t.wathe.cca.AutoStartComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.TrainWorldComponent;
import net.minecraft.world.World;

public final class GameComponents {

    private GameComponents() {}

    public static boolean getBounds(World world) {
        if (world == null) return true;
        try {
            return GameWorldComponent.KEY.get(world).isBound();
        } catch (Throwable t) {
            return true;
        }
    }

    public static int getBackfire(World world) {
        if (world == null) return 0;
        try {
            return Math.round(GameWorldComponent.KEY.get(world).getBackfireChance() * 100);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static int getKillerDividend(World world) {
        if (world == null) return 5;
        try {
            return GameWorldComponent.KEY.get(world).getKillerDividend();
        } catch (Throwable t) {
            return 5;
        }
    }

    public static int getVigilanteDividend(World world) {
        if (world == null) return 5;
        try {
            return GameWorldComponent.KEY.get(world).getVigilanteDividend();
        } catch (Throwable t) {
            return 5;
        }
    }

    public static int getAutoStart(World world) {
        if (world == null) return 0;
        try {
            // startTime is stored in ticks; the command accepts seconds
            return AutoStartComponent.KEY.get(world).startTime / 20;
        } catch (Throwable t) {
            return 0;
        }
    }

    public static boolean getFog(World world) {
        if (world == null) return false;
        try {
            return TrainWorldComponent.KEY.get(world).isFoggy();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean getHud(World world) {
        if (world == null) return false;
        try {
            return TrainWorldComponent.KEY.get(world).hasHud();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean getSnow(World world) {
        if (world == null) return false;
        try {
            TrainWorldComponent comp = TrainWorldComponent.KEY.get(world);
            java.lang.reflect.Field snowField = comp.getClass().getDeclaredField("snow");
            snowField.setAccessible(true);
            return (boolean) snowField.get(comp);
        } catch (Throwable t) {
            return false;
        }
    }
}
