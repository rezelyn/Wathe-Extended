package cat.rezelyn.watheextended.client.render.hud.ability;

import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.agmas.noellesroles.Noellesroles;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

/**
 * Supported add-ons HUD translation keys we replace, with what it means and which roles own it
 *
 * <p>To support a new ability tip, add a new constant here with the matching key
 */
public enum AbilityKey {

  // Ready
  STARSTRUCK_READY("tip.starexpress.starstruck", Kind.READY, Set.of(AbilityRoles.STARSTRUCK)),
  PHANTOM_READY("tip.phantom", Kind.READY, Set.of(AbilityRoles.PHANTOM)),
  INFECTED_READY("tip.infected", Kind.READY, Set.of(AbilityRoles.INFECTED)),
  KINS_READY("tip.kinswathe.ability.can_use", Kind.READY, AbilityRoles.KINS_GENERIC),
  THIEF_READY("hud.stupid_express.thief.ready", Kind.READY, Set.of(AbilityRoles.THIEF)),
  RECALLER_PLACE("tip.recaller.place", Kind.READY, Set.of(AbilityRoles.RECALLER)),
  RECALLER_TELEPORT("tip.recaller.teleport", Kind.READY, Set.of(AbilityRoles.RECALLER)),

  // Cooldown
  STARSTRUCK_COOLDOWN("tip.starexpress.cooldown", Kind.COOLDOWN, Set.of(AbilityRoles.STARSTRUCK)),
  THIEF_COOLDOWN("hud.stupid_express.thief.cooldown", Kind.COOLDOWN, Set.of(AbilityRoles.THIEF)),
  NOELLES_COOLDOWN("tip.noellesroles.cooldown", Kind.COOLDOWN, AbilityRoles.NOELLES_COOLDOWN_ROLES),
  KINS_COOLDOWN("tip.kinswathe.cooldown", Kind.COOLDOWN, AbilityRoles.KINS_GENERIC),

  // Not enough money
  RECALLER_NOT_ENOUGH_MONEY("tip.recaller.not_enough_money", Kind.COST, Set.of(AbilityRoles.RECALLER)),
  KINS_NOT_ENOUGH_MONEY("tip.kinswathe.ability.not_enough_money", Kind.COST, AbilityRoles.KINS_GENERIC),

  // Progress counters
  VULTURE_PROGRESS("tip.vulture", ProgressStyle.VULTURE, Set.of(AbilityRoles.VULTURE)),
  DREAMER_PROGRESS("tip.kinswathe.dreamer.counts", ProgressStyle.DREAMER, Set.of(AbilityRoles.DREAMER));

  enum Kind {
    READY,
    COOLDOWN,
    COST,
    PROGRESS
  }

  /** {@code symbol}, {@code label} and {@code color} of a {@link Kind#PROGRESS} tip */
  record ProgressStyle(String symbol, String labelKey, IntSupplier roleColor) {
    static final ProgressStyle VULTURE =
        new ProgressStyle("☠", "gui.watheextended.hud.ability.vulture", () -> Noellesroles.VULTURE.color());
    static final ProgressStyle DREAMER =
        new ProgressStyle("✦", "gui.watheextended.hud.ability.dreamer", () -> KinsWatheRoles.DREAMER.color());

    int color() {
      return roleColor.getAsInt();
    }
  }

  private static final Map<String, AbilityKey> BY_TRANSLATION_KEY =
      Arrays.stream(values())
          .collect(
              Collectors.toUnmodifiableMap(AbilityKey::translationKey, Function.identity()));

  private final String translationKey;
  private final Kind kind;
  private final ProgressStyle progressStyle;
  private final Set<String> roles;

  AbilityKey(String translationKey, Kind kind, Set<String> roles) {
    this(translationKey, kind, null, roles);
  }

  AbilityKey(String translationKey, ProgressStyle progressStyle, Set<String> roles) {
    this(translationKey, Kind.PROGRESS, progressStyle, roles);
  }

  AbilityKey(
          String translationKey, Kind kind, ProgressStyle progressStyle, Set<String> roles) {
    this.translationKey = translationKey;
    this.kind = kind;
    this.progressStyle = progressStyle;
    this.roles = roles;
  }

  static Optional<AbilityKey> find(String translationKey) {
    return Optional.ofNullable(BY_TRANSLATION_KEY.get(translationKey));
  }

  String translationKey() {
    return translationKey;
  }

  Kind kind() {
    return kind;
  }

  ProgressStyle progressStyle() {
    return progressStyle;
  }

  public boolean appliesTo(String roleId) {
    return roles.contains(roleId);
  }
}
