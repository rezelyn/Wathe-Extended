package cat.rezelyn.watheextended.modifiers.noellesroles;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class FeatherModifierFix {

    private FeatherModifierFix() {}

    public static void applyOnGameStart(ServerWorld world) {
        if (!cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) return;
        try {
            org.agmas.harpymodloader.component.WorldModifierComponent wmc = org.agmas.harpymodloader.component.WorldModifierComponent.KEY.get(world);
            if (wmc == null) return;

            org.agmas.harpymodloader.modifiers.Modifier feather = org.agmas.noellesroles.Noellesroles.FEATHER;
            if (feather == null) return;

            for (net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                try {
                    if (wmc.isModifier(sp, feather)) {
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
