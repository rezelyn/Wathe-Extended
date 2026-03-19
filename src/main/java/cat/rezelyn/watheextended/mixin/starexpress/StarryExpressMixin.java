package cat.rezelyn.watheextended.mixin.starexpress;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.aussiebox.starexpress.StarryExpress", remap = false)
public class StarryExpressMixin {

    @Redirect(method = "lambda$registerPackets$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), require = 0)
    private static void watheextended$suppressStarstruckActivationSound(ServerWorld world, @Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (!WatheExtendedServerConfig.suppressAbilityVfxSfx) {
            world.playSound(except, pos, sound, category, volume, pitch);
        }
    }

    @Redirect(method = "lambda$registerPackets$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), require = 0)
    private static int watheextended$suppressStarstruckActivationParticles(ServerWorld world, ParticleEffect effect, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        if (!WatheExtendedServerConfig.suppressAbilityVfxSfx) {
            return world.spawnParticles(effect, x, y, z, count, deltaX, deltaY, deltaZ, speed);
        }
        return 0;
    }
}
