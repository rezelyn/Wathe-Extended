package cat.rezelyn.watheextended.client.render.hud.ability;

import java.util.Set;

/** {@code namespace:path} of the addons roles ability HUD we replace */
public final class AbilityRoles {
  private AbilityRoles() {}

  // noellesroles
  public static final String PHANTOM = "noellesroles:phantom";
  public static final String INFECTED = "noellesroles:infected";
  public static final String RECALLER = "noellesroles:recaller";
  public static final String VULTURE = "noellesroles:vulture";
  public static final Set<String> NOELLES_COOLDOWN_ROLES =
      Set.of(PHANTOM, INFECTED, RECALLER, VULTURE);

  // starexpress
  public static final String STARSTRUCK = "starexpress:starstruck";

  // stupid_express
  public static final String THIEF = "stupid_express:thief";

  // kinswathe
  public static final String BELLRINGER = "kinswathe:bellringer";
  public static final String CLEANER = "kinswathe:cleaner";
  public static final String DETECTIVE = "kinswathe:detective";
  public static final String HUNTER = "kinswathe:hunter";
  public static final String PHYSICIAN = "kinswathe:physician";
  public static final String ROBOT = "kinswathe:robot";
  public static final String TECHNICIAN = "kinswathe:technician";
  public static final String DREAMER = "kinswathe:dreamer";
  public static final Set<String> KINS_GENERIC =
      Set.of(BELLRINGER, CLEANER, DETECTIVE, HUNTER, PHYSICIAN, ROBOT, TECHNICIAN);
}
