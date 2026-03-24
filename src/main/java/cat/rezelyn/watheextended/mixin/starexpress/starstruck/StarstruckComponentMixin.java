package cat.rezelyn.watheextended.mixin.starexpress.starstruck;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.aussiebox.starexpress.cca.StarstruckComponent", remap = false)
public class StarstruckComponentMixin {

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), require = 0)
    private int watheextended$suppressStarstruckParticles(ServerWorld world, ParticleEffect effect, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        if (!WatheExtendedServerConfig.suppressAbilityVfxSfx) {
            return world.spawnParticles(effect, x, y, z, count, deltaX, deltaY, deltaZ, speed);
        }
        return 0;
    }
}
