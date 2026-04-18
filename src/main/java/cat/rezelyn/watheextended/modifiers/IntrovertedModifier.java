package cat.rezelyn.watheextended.modifiers;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.mixin.PlayerMoodDirectAccessor;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.harpymodloader.component.WorldModifierComponent;

public final class IntrovertedModifier {

    private IntrovertedModifier() {}

    public static void tick(ServerWorld world) {
        try {
            if (!GameWorldComponent.KEY.get(world).isRunning()) return;

            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            long gameStartWorldTime = component.getGameStartWorldTime();
            if (gameStartWorldTime < 0 || world.getTime() - gameStartWorldTime < GameConstants.TIME_TO_FIRST_TASK) return;

            WorldModifierComponent worldComponent = WorldModifierComponent.KEY.get(world);

            for (PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                if (!GameFunctions.isPlayerAliveAndSurvival(sp)) continue;
                if (!worldComponent.isModifier(sp, WatheExtendedModifiers.INTROVERTED)) continue;

                // only affect roles that are subject to the mood system
                Role role = GameWorldComponent.KEY.get(world).getRole(sp);
                if (role == null || role.getMoodType() == Role.MoodType.NONE || role.getMoodType() == Role.MoodType.FAKE)
                    continue;
                if (role.canUseKiller()) continue;

                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(sp);
                int nearbyPlayers = countNearbyAliveSurvivalPlayers(world, sp);

                // if crowd count exceed = faster drain
                if (nearbyPlayers >= WatheExtendedServerConfig.getIntrovertedCrowdCount()) {
                    mood.setMood(mood.getMood() - GameConstants.MOOD_DRAIN * WatheExtendedServerConfig.getIntrovertedCrowdDrainMultiplier());
                } else {
                    // alone or with one other player = slower drain
                    float newMood = Math.clamp(mood.getMood() + GameConstants.MOOD_DRAIN * WatheExtendedServerConfig.getIntrovertedAloneDrainMultiplier(), 0.0f, 1.0f);
                    ((PlayerMoodDirectAccessor) mood).watheextended$setMoodDirect(newMood);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static int countNearbyAliveSurvivalPlayers(ServerWorld world, ServerPlayerEntity self) {
        float rangeSq = WatheExtendedServerConfig.getIntrovertedCrowdRange() * WatheExtendedServerConfig.getIntrovertedCrowdRange();
        int count = 0;
        for (PlayerEntity other : world.getPlayers()) {
            if (other == self) continue;
            if (!(other instanceof ServerPlayerEntity sp)) continue;
            if (!GameFunctions.isPlayerAliveAndSurvival(sp)) continue;
            if (other.squaredDistanceTo(self) <= rangeSq) count++;
        }
        return count;
    }
}
