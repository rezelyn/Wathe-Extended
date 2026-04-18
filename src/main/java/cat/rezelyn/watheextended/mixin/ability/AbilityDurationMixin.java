package cat.rezelyn.watheextended.mixin.ability;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.agmas.noellesroles.Noellesroles", remap = false)
public class AbilityDurationMixin {

    @Inject(method = "lambda$registerPackets$9", at = @At("TAIL"), require = 0, remap = false)
    private static void watheextended$applyMorphlingDuration(MorphC2SPacket payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();
        GameWorldComponent game;
        try {
            game = GameWorldComponent.KEY.get(world);
        } catch (Throwable ignored) {
            return;
        }
        if (!game.isRole(player, Noellesroles.MORPHLING)) return;
        MorphlingPlayerComponent component = MorphlingPlayerComponent.KEY.get(player);
        if (component.getMorphTicks() <= 0) return;
        component.setMorphTicks(WatheExtendedServerConfig.morphlingAbilityDuration * 20);
    }

    @Inject(method = "lambda$registerPackets$16", at = @At("TAIL"), require = 0, remap = false)
    private static void watheextended$applyPhantomDuration(AbilityC2SPacket payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();
        GameWorldComponent game;
        try {
            game = GameWorldComponent.KEY.get(world);
        } catch (Throwable ignored) {
            return;
        }
        if (!game.isRole(player, Noellesroles.PHANTOM)) return;
        if (!player.hasStatusEffect(StatusEffects.INVISIBILITY)) return;
        int duration = WatheExtendedServerConfig.phantomAbilityDuration * 20;
        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, duration, 0, true, false, true));
        AbilityPlayerComponent abilityComponent = AbilityPlayerComponent.KEY.get(player);
        abilityComponent.cooldown = duration + WatheExtendedServerConfig.phantomAbilityCooldown * 20;
        abilityComponent.sync();
    }
}
