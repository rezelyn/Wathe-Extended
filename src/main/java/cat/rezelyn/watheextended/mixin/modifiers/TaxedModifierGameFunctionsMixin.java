package cat.rezelyn.watheextended.mixin.modifiers;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.modifiers.TaxedModifier;
import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.game.GameConstants;
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
public class TaxedModifierGameFunctionsMixin {

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    private static void watheextended$recordTaxedKill(PlayerEntity victim, boolean spawnBody, @Nullable PlayerEntity killer, Identifier deathReason, CallbackInfo ci) {
        if (killer instanceof ServerPlayerEntity player) {
            try {
                TaxedModifier.recordKill(player.getUuid());
            } catch (Throwable ignored) {
            }
        }
    }

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;changeGameMode(Lnet/minecraft/world/GameMode;)Z", shift = At.Shift.AFTER))
    private static void watheextended$trackKilledPlayer(PlayerEntity victim, boolean spawnBody, @Nullable PlayerEntity killer, Identifier deathReason, CallbackInfo ci) {
        if (victim instanceof ServerPlayerEntity player) {
            try {
                WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(player.getServerWorld());
                component.markPlayerKilled(player.getUuid());
            } catch (Throwable ignored) {
            }
        }
    }

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("RETURN"))
    private static void watheextended$adjustKillIncreaseTime(PlayerEntity victim, boolean spawnBody, @Nullable PlayerEntity killer, Identifier deathReason, CallbackInfo ci) {
        int delta = WatheExtendedServerConfig.killIncreaseTime * 20 - GameConstants.TIME_ON_CIVILIAN_KILL;
        if (delta == 0) return;
        try {
            GameTimeComponent.KEY.get(victim.getWorld()).addTime(delta);
        } catch (Throwable ignored) {
        }
    }
}
