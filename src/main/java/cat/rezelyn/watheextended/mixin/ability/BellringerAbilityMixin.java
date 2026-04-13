package cat.rezelyn.watheextended.mixin.ability;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.roles.bellringer.BellringerAbility", remap = false)
public class BellringerAbilityMixin {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;playSoundToPlayer(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), require = 0)
    private static void watheextended$suppressBellringerSound(ServerPlayerEntity player, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (!WatheExtendedServerConfig.suppressAbilityVfxSfx) {
            player.playSoundToPlayer(sound, category, volume, pitch);
        }
    }
}
