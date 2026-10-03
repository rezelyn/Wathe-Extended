package cat.rezelyn.watheextended.client.compat;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;

/** Optional, reflection-only access to KinsWathe's components */
public final class KinsWatheAccess {
  private static final Identifier HACKER_ROLE = Identifier.of("kinswathe", "hacker");

  private KinsWatheAccess() {}

  public static HackerStatus getHackerStatus(
      GameWorldComponent game, PlayerEntity viewer, PlayerEntity target) {
    if (!isLoaded() || game == null || viewer == null || target == null) return null;
    try {
      Role hackerRole = game.getRole(viewer);
      if (hackerRole == null
          || !HACKER_ROLE.equals(hackerRole.identifier())
          || !GameFunctions.isPlayerAliveAndSurvival(viewer)
          || !GameFunctions.isPlayerAliveAndSurvival(target)) return null;

      Role targetRole = game.getRole(target);
      if (targetRole == null
          || game.canUseKillerFeatures(target)
          || isKillerNeutral(targetRole)
          || isKillerSidedNeutral(target)) return null;

      Object safeComponent =
          getComponent(
              "org.BsXinQin.kinswathe.component.GameSafeComponent", "KEY", viewer.getWorld());
      if ((boolean) safeComponent.getClass().getMethod("isSafe").invoke(safeComponent)) {
        return new HackerStatus(
            Text.translatable("hud.kinswathe.hacker.target_safe"),
            null,
            hackerRole.color(),
            0x00FF00);
      }

      Object hackerComponent =
          getComponent("org.BsXinQin.kinswathe.roles.hacker.HackerComponent", "KEY", target);
      int hackingTime = getField(hackerComponent.getClass(), "hackingTime").getInt(hackerComponent);
      Object config =
          getComponent(
              "org.BsXinQin.kinswathe.component.ConfigWorldComponent", "KEY", viewer.getWorld());
      int hackingDuration = getField(config.getClass(), "HackerHackingTime").getInt(config) * 20;
      if (hackingDuration > 0 && hackingTime < hackingDuration) {
        int percentage = (int) (hackingTime / (float) hackingDuration * 100f);
        return new HackerStatus(
            Text.translatable("hud.kinswathe.hacker.target"),
            Text.literal(" [ " + percentage + "% ]"),
            hackerRole.color(),
            0x00FF00);
      }
      return new HackerStatus(
          Text.translatable("hud.kinswathe.hacker.target_hacked"), null, 0x00FF00, 0x00FF00);
    } catch (Throwable ignored) {
      return null;
    }
  }

  public static boolean isHackerCohortTarget(
      GameWorldComponent game, PlayerEntity viewer, PlayerEntity target) {
    if (!isLoaded() || game == null || viewer == null || target == null) return false;
    try {
      Role viewerRole = game.getRole(viewer);
      Role targetRole = game.getRole(target);
      if (viewerRole == null
          || !HACKER_ROLE.equals(viewerRole.identifier())
          || targetRole == null
          || !GameFunctions.isPlayerAliveAndSurvival(viewer)
          || !GameFunctions.isPlayerAliveAndSurvival(target)) return false;
      return game.canUseKillerFeatures(target)
          || isKillerNeutral(targetRole)
          || isKillerSidedNeutral(target);
    } catch (Throwable ignored) {
      return false;
    }
  }

  private static boolean isLoaded() {
    return FabricLoader.getInstance().isModLoaded("kinswathe");
  }

  private static boolean isKillerNeutral(Role role) throws ReflectiveOperationException {
    Class<?> roles = Class.forName("org.BsXinQin.kinswathe.KinsWatheRoles");
    Object value = getField(roles, "KILLER_NEUTRAL_ROLES").get(null);
    return value instanceof Collection<?> collection && collection.contains(role);
  }

  private static boolean isKillerSidedNeutral(PlayerEntity target)
      throws ReflectiveOperationException {
    Class<?> roles = Class.forName("org.BsXinQin.kinswathe.KinsWatheRoles");
    Method method = roles.getMethod("isKillerSidedNeutral", PlayerEntity.class);
    return (boolean) method.invoke(null, target);
  }

  private static Object getComponent(
      String componentClassName, String keyFieldName, Object provider)
      throws ReflectiveOperationException {
    Class<?> componentClass = Class.forName(componentClassName);
    Object key = getField(componentClass, keyFieldName).get(null);
    return ((ComponentKey<?>) key).get(provider);
  }

  private static Field getField(Class<?> owner, String fieldName) throws NoSuchFieldException {
    Field field = owner.getField(fieldName);
    field.setAccessible(true);
    return field;
  }

  public record HackerStatus(Text primary, Text suffix, int primaryColor, int suffixColor) {}
}
