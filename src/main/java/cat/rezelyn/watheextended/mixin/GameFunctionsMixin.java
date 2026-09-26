package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class GameFunctionsMixin {
    @Inject(method = "initializeGame", at = @At("HEAD"))
    private static void watheExtended$applyConfiguredGameTime(ServerWorld world, CallbackInfo ci) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            GameWorldComponent.KEY.get(world).setMapEffect(component.getConfiguredGameMapEffect());
        } catch (Throwable ignored) {}
    }

    @Inject(method = "initializeGame", at = @At("TAIL"))
    private static void watheExtended$restoreConfiguredTrainTime(ServerWorld world, CallbackInfo ci) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            if (!component.isGenericMapEffectEnabled()) {
                WatheExtendedWorldComponent.setTrainTime(world, component.getGameTimeOfDay());
            }
        } catch (Throwable ignored) {}
    }

    @Inject(method = "finalizeGame", at = @At("RETURN"))
    private static void watheExtended$applyConfiguredLobbyTime(ServerWorld world, CallbackInfo ci) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            WatheExtendedWorldComponent.setTrainTime(world, component.getLobbyTimeOfDay());
        } catch (Throwable ignored) {}
    }
}
