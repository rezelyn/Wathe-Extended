package cat.rezelyn.watheextended.client.render.hud.ability;

import cat.rezelyn.watheextended.api.AssignedRole;
import cat.rezelyn.watheextended.game.mood.DepressedMoodState;
import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.component.AbilityPlayerComponent;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import pro.fazeclan.river.stupid_express.cca.AbilityCooldownComponent;
import pro.fazeclan.river.stupid_express.role.thief.packet.ThiefTakeItemC2SPacket;

/** Client-side role and targeting checks shared by ability input and HUD rendering */
public final class AbilityConditions {
  private static final String MOOD_DISABLE_ENABLED =
      "watheextended.mood.depressedAbilityDisableEnabled";
  private static final String MOOD_DISABLE_SECONDS =
      "watheextended.mood.depressedAbilityDisableTime";

  private AbilityConditions() {}

  /** Whether the ability for this tip is currently disabled by one of our rules */
  static boolean isDisabled(AbilityKey key) {
    return byMood(key) || byPlayerLimit(key);
  }

  private static boolean byMood(AbilityKey key) {
    return safely(() -> {
      MinecraftClient client = MinecraftClient.getInstance();
      PlayerEntity player = client.player;
      if (player == null || client.world == null) return false;
      return AssignedRole.findRole(player)
          .filter(role -> key.appliesTo(role.identifier().toString()))
          .map(role -> isDisabledByMood(player, role))
          .orElse(false);
    });
  }

  public static boolean isDisabledByMood(PlayerEntity player, Role role) {
    if (role.getMoodType() != Role.MoodType.REAL || !ClientConfig.getBool(MOOD_DISABLE_ENABLED, false)
        || !DepressedMoodState.isDepressed(player)) return false;
    int delaySeconds = Math.max(0, ClientConfig.getInt(MOOD_DISABLE_SECONDS, 0));
    return DepressedMoodState.ticks(player) >= delaySeconds * 20;
  }

  private static boolean byPlayerLimit(AbilityKey key) {
    if (!key.appliesTo(AbilityRoles.CLEANER)) return false;

    return safely(() -> {
      MinecraftClient client = MinecraftClient.getInstance();
      int limit = ClientConfig.getInt("watheextended.cleaner.playerLimit", 0);
      if (limit <= 0 || client.world == null) return false;
      if (!AssignedRole.hasRole(client.player, AbilityRoles.CLEANER)) return false;

      long alivePlayers =
          client.world.getPlayers().stream()
              .filter(player -> !player.isSpectator() && !player.isCreative())
              .count();
      return alivePlayers < limit;
    });
  }



  static boolean isPhantomInvisible() {
    return safely(() -> {
      MinecraftClient client = MinecraftClient.getInstance();
      return AssignedRole.hasRole(client.player, AbilityRoles.PHANTOM)
          && client.player.hasStatusEffect(StatusEffects.INVISIBILITY);
    });
  }

  static boolean canThiefSteal() {
    return safely(() -> {
      MinecraftClient client = MinecraftClient.getInstance();
      PlayerEntity thief = client.player;
      if (thief == null || !thief.getMainHandStack().isEmpty()) return false;

      return AbilityTargets.underCrosshair(client, PlayerEntity.class)
          .filter(target ->
              ThiefTakeItemC2SPacket.validateStealAttempt(
                  thief, target, AbilityCooldownComponent.KEY.get(thief)))
          .isPresent();
    });
  }

  public static boolean isDetective(MinecraftClient client) {
    return AssignedRole.hasRole(client.player, AbilityRoles.DETECTIVE);
  }

  static boolean hasInvestigationTarget(MinecraftClient client) {
    return !GameFunctions.isPlayerEliminated(client.player)
        && AbilityTargets.underCrosshair(
                client, PlayerEntity.class, 2.0, GameFunctions::isPlayerAliveAndSurvival)
            .isPresent();
  }

  public static boolean canInvestigate(MinecraftClient client) {
    return isDetective(client)
        && hasInvestigationTarget(client)
        && AbilityPlayerComponent.KEY.get(client.player).cooldown <= 0
        && PlayerShopComponent.KEY.get(client.player).balance
            >= ConfigWorldComponent.KEY.get(client.world).DetectiveAbilityPrice;
  }



  private static boolean safely(BooleanSupplier check) {
    try {
      return check.getAsBoolean();
    } catch (RuntimeException exception) {
      return false;
    }
  }
}
