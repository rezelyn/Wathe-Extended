package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.block.HornBlock;
import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.world.ServerWorld;
import org.agmas.harpymodloader.Harpymodloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HornBlock.class)
public class HornBlockMixin {
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;startGame(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/api/GameMode;Ldev/doctor4t/wathe/api/MapEffect;I)V"))
    private void watheExtended$useConfiguredStart(ServerWorld world, GameMode ignoredMode, MapEffect ignoredEffect, int time) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            Harpymodloader.wantsToStartVannila = component.usesVanillaGameModeAtStart();
            GameFunctions.startGame(world, component.getGameModeForStart(), component.getConfiguredGameMapEffect(), component.getConfiguredGameDurationTicks());
        } catch (Throwable ignored) {
            GameFunctions.startGame(world, ignoredMode, ignoredEffect, time);
        }
    }
}
