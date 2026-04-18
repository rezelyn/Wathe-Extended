package cat.rezelyn.watheextended.mixin.ability;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.Noellesroles", remap = false)
public class AbilityCancelMixin {

    @Inject(method = "lambda$registerPackets$16", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void watheextended$handleAbilityCancel(AbilityC2SPacket payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();
        GameWorldComponent game;
        try {
            game = GameWorldComponent.KEY.get(world);
        } catch (Throwable ignored) {
            return;
        }

        // morphling: cancel active morph
        if (game.isRole(player, Noellesroles.MORPHLING)) {
            MorphlingPlayerComponent component = MorphlingPlayerComponent.KEY.get(player);
            if (component.morphTicks > 0) {
                component.stopMorph();
                component.sync();
            }
            ci.cancel();
            return;
        }

        // phantom: cancel invisibility
        if (game.isRole(player, Noellesroles.PHANTOM)) {
            AbilityPlayerComponent abilityComponent = AbilityPlayerComponent.KEY.get(player);
            if (abilityComponent.cooldown > 0 && player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                player.removeStatusEffect(StatusEffects.INVISIBILITY);
                ci.cancel();
            }
        }
    }
}
