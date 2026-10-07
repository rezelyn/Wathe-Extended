package cat.rezelyn.watheextended.client.compat;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import java.lang.reflect.Field;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;

/** Optional, reflection-only access to Stupid Express's components */
public final class StupidExpressAccess {
  private static final Identifier ARSONIST_ROLE = Identifier.of("stupid_express", "arsonist");
  private static final String DOUSED_COMPONENT =
      "pro.fazeclan.river.stupid_express.role.arsonist.cca.DousedPlayerComponent";

  private StupidExpressAccess() {}

  public static DousedStatus getArsonistDousedStatus(
      GameWorldComponent game, PlayerEntity viewer, PlayerEntity target) {
    if (!FabricLoader.getInstance().isModLoaded("stupid_express")
        || game == null
        || viewer == null
        || target == null
        || GameFunctions.isPlayerSpectatingOrCreative(viewer)) return null;

    var role = game.getRole(viewer);
    if (role == null || !ARSONIST_ROLE.equals(role.identifier())) return null;

    try {
      Class<?> componentClass = Class.forName(DOUSED_COMPONENT);
      Field keyField = componentClass.getField("KEY");
      Object component = ((ComponentKey<?>) keyField.get(null)).get(target);
      boolean doused = (boolean) componentClass.getMethod("isDoused").invoke(component);
      return new DousedStatus(
          Text.translatable("hud.stupid_express.arsonist.doused." + doused), doused);
    } catch (Throwable ignored) {
      return null;
    }
  }

  public record DousedStatus(Text text, boolean doused) {}
}
