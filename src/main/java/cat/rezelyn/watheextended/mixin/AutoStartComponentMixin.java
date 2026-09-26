package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.AutoStartComponent;
import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.world.ServerWorld;
import org.agmas.harpymodloader.Harpymodloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AutoStartComponent.class)
public class AutoStartComponentMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;startGame(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/api/GameMode;Ldev/doctor4t/wathe/api/MapEffect;I)V"))
    private void watheExtended$useConfiguredStart(ServerWorld world, GameMode ignoredMode, MapEffect ignoredEffect, int time) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            GameMode mode = component.getGameModeForStart();
            Harpymodloader.wantsToStartVannila = component.usesVanillaGameModeAtStart();
            GameFunctions.startGame(world, mode, component.getConfiguredGameMapEffect(), component.getConfiguredGameDurationTicks());
        } catch (Throwable ignored) {
            GameFunctions.startGame(world, ignoredMode, ignoredEffect, time);
        }
    }
}
