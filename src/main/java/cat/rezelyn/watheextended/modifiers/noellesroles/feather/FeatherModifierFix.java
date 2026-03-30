package cat.rezelyn.watheextended.modifiers.noellesroles.feather;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.agmas.noellesroles.Noellesroles;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class FeatherModifierFix {

    private FeatherModifierFix() {}

    public static void applyOnGameStart(ServerWorld world) {
        if (!ConfigHelper.isLoaded()) return;
        try {
            WorldModifierComponent component = WorldModifierComponent.KEY.get(world);
            if (component == null) return;

            Modifier feather = Noellesroles.FEATHER;
            if (feather == null) return;

            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                try {
                    if (component.isModifier(sp, feather)) {
                        sp.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.SLOW_FALLING, -1, 0, false, false));
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
