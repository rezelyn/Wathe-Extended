package cat.rezelyn.watheextended.api.cca;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.world.World;

public class GameStatus {
    public static boolean State(World world) {
        if (world == null) return false;
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            GameWorldComponent.GameStatus status = gwc.getGameStatus();
            return status == GameWorldComponent.GameStatus.ACTIVE
                    || status == GameWorldComponent.GameStatus.STOPPING
                    || status == GameWorldComponent.GameStatus.STARTING;
        } catch (Throwable t) {
            return false;
        }
    }
}