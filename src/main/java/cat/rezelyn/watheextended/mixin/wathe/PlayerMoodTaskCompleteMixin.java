package cat.rezelyn.watheextended.mixin.wathe;

import cat.rezelyn.watheextended.api.noellesroles.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerMoodComponent.class)
public class PlayerMoodTaskCompleteMixin {

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking;send(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/packet/CustomPayload;)V"), require = 0)
    private void watheextended$giveNoteOnTaskComplete(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
        if (!ConfigHelper.isLoaded()) return;
        try {
            GameWorldComponent gwc = GameWorldComponent.KEY.get(player.getServerWorld());
            if (gwc.isRole(player, org.agmas.noellesroles.Noellesroles.AWESOME_BINGLUS)) {
                player.getInventory().insertStack(new ItemStack(WatheItems.NOTE, 4));
            }
        } catch (Throwable ignored) {}
    }
}
