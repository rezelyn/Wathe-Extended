package cat.rezelyn.watheextended.mixin.client.role;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class InfectedInstinctMixin {

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$infectedInstinct(Entity entity, CallbackInfoReturnable<Integer> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        GameWorldComponent game = WatheClient.gameComponent;
        if (game == null || !game.isRole(client.player, Noellesroles.INFECTED)) return;

        if (!WatheClient.isPlayerAliveAndInSurvival()) {
            cir.setReturnValue(-1);
            cir.cancel();
            return;
        }

        if (!(entity instanceof PlayerEntity target)) {
            cir.setReturnValue(-1);
            cir.cancel();
            return;
        }

        InfectedPlayerComponent infected = InfectedPlayerComponent.KEY.get(target);
        cir.setReturnValue(infected != null && infected.infectedTicks > 0 ? 0xFF00FF00 : -1);
        cir.cancel();
    }
}
