package cat.rezelyn.watheextended.mixin.kinswathe;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.roles.cleaner.CleanerAbility", remap = false)
public class CleanerAbilityMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$enforceCleanerPlayerLimit(PlayerEntity player, CallbackInfo ci) {
        int limit = WatheExtendedServerConfig.cleanerPlayerLimit;
        if (limit <= 0) return;
        try {
            long aliveCount = ((ServerWorld) player.getWorld()).getPlayers().stream().filter(p -> !p.isSpectator() && !p.isCreative()).count();
            if (aliveCount < limit) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }
}
