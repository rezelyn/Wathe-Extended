package cat.rezelyn.watheextended.mixin.wathe;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class GameFunctionsMixin {

    @Inject(
            method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;changeGameMode(Lnet/minecraft/world/GameMode;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private static void watheextended$trackKilledPlayer(
            PlayerEntity victim,
            boolean spawnBody,
            @Nullable PlayerEntity killer,
            Identifier deathReason,
            CallbackInfo ci
    ) {
        if (victim instanceof ServerPlayerEntity serverPlayer) {
            try {
                WatheExtendedWorldComponent wec =
                        WatheExtendedWorldComponent.KEY.get(serverPlayer.getServerWorld());
                wec.markPlayerKilled(serverPlayer.getUuid());
            } catch (Throwable ignored) {
            }
        }
    }
}
