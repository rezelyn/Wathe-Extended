package cat.rezelyn.watheextended.mixin.client.hud.inventory;

import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.entity.NoteEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class BinglusInstinctMixin {

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$binglusInstinct(Entity entity, CallbackInfoReturnable<Integer> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        GameWorldComponent game = WatheClient.gameComponent;
        if (game == null) return;
        if (!ConfigHelper.isLoaded()) return;

        try {
            if (!game.isRole(client.player, org.agmas.noellesroles.Noellesroles.AWESOME_BINGLUS)) return;
        } catch (Throwable t) {
            return;
        }

        if (!WatheClient.instinctKeybind.isPressed() || !WatheClient.isPlayerAliveAndInSurvival()) {
            cir.setReturnValue(-1);
            cir.cancel();
            return;
        }

        cir.setReturnValue(entity instanceof NoteEntity ? 14392576 : -1);
        cir.cancel();
    }
}
