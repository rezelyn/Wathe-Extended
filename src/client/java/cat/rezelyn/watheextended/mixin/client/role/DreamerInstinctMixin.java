package cat.rezelyn.watheextended.mixin.client.role;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.BsXinQin.kinswathe.roles.dreamer.DreamerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class DreamerInstinctMixin {

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$dreamerImprint(Entity entity, CallbackInfoReturnable<Integer> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        GameWorldComponent game = WatheClient.gameComponent;
        if (game == null || !game.isRole(client.player, KinsWatheRoles.DREAMER)) return;

        if (!(entity instanceof PlayerEntity target) || !WatheClient.isPlayerAliveAndInSurvival()) {
            cir.setReturnValue(-1);
            cir.cancel();
            return;
        }

        DreamerComponent imprint = DreamerComponent.KEY.get(target);
        boolean imprintedByThisDreamer = imprint != null && client.player.getUuid().equals(imprint.dreamerUUID) && imprint.dreamArmor > 0;
        cir.setReturnValue(imprintedByThisDreamer ? KinsWatheRoles.DREAMER.color() : -1);
        cir.cancel();
    }
}
