package cat.rezelyn.watheextended.compat;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;

/**
 * Reflective bridge to optional role mods (Noelle's Roles, KinsWathe). Everything is resolved once,
 * anything that isn't installed is {@code null} / {@code false}
 */
public final class AddonModCompat {

  // add-ons paths
  private static final String NOELLES_ROLES = "org.agmas.noellesroles.";
  private static final String KINS_ROLES = "org.BsXinQin.kinswathe.client.roles.";

  // widgets class
  public static final Class<?> MORPHLING_WIDGET = findClass(NOELLES_ROLES + "client.ui.MorphlingPlayerWidget");
  public static final Class<?> SWAPPER_WIDGET = findClass(NOELLES_ROLES + "client.ui.SwapperPlayerWidget");
  public static final Class<?> VOODOO_WIDGET = findClass(NOELLES_ROLES + "client.ui.VoodooPlayerWidget");
  public static final Class<?> GUESSER_WIDGET = findClass(NOELLES_ROLES + "client.ui.guesser.GuesserPlayerWidget");
  public static final Class<?> JUDGE_WIDGET = findClass(KINS_ROLES + "judge.JudgePlayerWidget");
  public static final Class<?> BODYMAKER_WIDGET = findClass(KINS_ROLES + "bodymaker.BodymakerPlayerWidget");
  public static final Class<?> BODYMAKER_REASON_WIDGET = findClass(KINS_ROLES + "bodymaker.BodymakerDeathReasonWidget");

  // cca components
  private static final ComponentField MORPH_TICKS = ComponentField.find(NOELLES_ROLES + "morphling.MorphlingPlayerComponent", "morphTicks");
  private static final ComponentField VOODOO_TARGET = ComponentField.find(NOELLES_ROLES + "voodoo.VoodooPlayerComponent", "target");
  private static final Field GUESSER_SELECTED_PLAYER = findField(GUESSER_WIDGET, "selectedPlayer");

  private AddonModCompat() {}

  public static boolean isMorphlingActive(PlayerEntity player) {
    return MORPH_TICKS != null && MORPH_TICKS.read(player) instanceof Integer ticks && ticks > 0;
  }

  public static boolean voodooHasTarget(PlayerEntity player) {
    return VOODOO_TARGET != null
        && VOODOO_TARGET.read(player) instanceof UUID target
        && !target.equals(player.getUuid());
  }

  public static boolean isGuesserPickerOpen() {
    if (GUESSER_SELECTED_PLAYER == null) return false;
    try {
      return GUESSER_SELECTED_PLAYER.get(null) != null;
    } catch (IllegalAccessException | RuntimeException e) {
      return false;
    }
  }

  private static Class<?> findClass(String name) {
    try {
      return Class.forName(name, false, AddonModCompat.class.getClassLoader());
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static Field findField(Class<?> owner, String fieldName) {
    if (owner == null) return null;
    try {
      Field field = owner.getField(fieldName);
      field.setAccessible(true);
      return field;
    } catch (ReflectiveOperationException | RuntimeException e) {
      return null;
    }
  }

  /** A field on a CCA player component, looked up reflectively */
  private record ComponentField(ComponentKey<?> key, Field field) {

    static ComponentField find(String className, String fieldName) {
      try {
        Class<?> cls = Class.forName(className);
        Field keyField = cls.getDeclaredField("KEY");
        keyField.setAccessible(true);
        Field valueField = cls.getDeclaredField(fieldName);
        valueField.setAccessible(true);
        return new ComponentField((ComponentKey<?>) keyField.get(null), valueField);
      } catch (ReflectiveOperationException | RuntimeException e) {
        return null;
      }
    }

    Object read(PlayerEntity player) {
      if (player == null) return null;
      try {
        return field.get(key.get(player));
      } catch (ReflectiveOperationException | RuntimeException e) {
        return null;
      }
    }
  }
}
