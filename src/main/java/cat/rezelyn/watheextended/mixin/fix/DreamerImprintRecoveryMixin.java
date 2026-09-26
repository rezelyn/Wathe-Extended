package cat.rezelyn.watheextended.mixin.fix;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

// fix: successful Dream Imprints must give back one Dream Imprint item to the Dreamer
@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.roles.dreamer.DreamerComponent", remap = false)
public abstract class DreamerImprintRecoveryMixin {

    @Shadow @Final
    private PlayerEntity player;

    @Shadow
    public int dreamArmor;

    @Shadow
    public UUID dreamerUUID;

    @Inject(method = "teleportToDreamer", at = @At("RETURN"), require = 0, remap = false)
    private void watheextended$replenishDreamImprint(CallbackInfo ci) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return;
        if (dreamArmor <= 0) return;
        if (dreamerUUID == null) return;

        ServerPlayerEntity dreamer = serverWorld.getServer().getPlayerManager().getPlayer(dreamerUUID);
        if (dreamer == null) return;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!gameWorld.isRole(dreamer, KinsWatheRoles.DREAMER)) return;
        if (!GameFunctions.isPlayerAliveAndSurvival(player) || !GameFunctions.isPlayerAliveAndSurvival(dreamer)) return;

        DreamerKillerComponent progress = DreamerKillerComponent.KEY.get(dreamer);
        if (progress.hasBecomeKiller) return;
        if (progress.dreamerRequired > 0 && progress.dreamerCounts >= progress.dreamerRequired) return;

        dreamer.giveItemStack(new ItemStack(KinsWatheItems.DREAM_IMPRINT));
    }
}
