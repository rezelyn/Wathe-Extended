package cat.rezelyn.watheextended.mixin.wathe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerChatMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void watheextended$blockChatDuringGame(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (watheextended$isGameRunningAndNotOp()) {
            ci.cancel();
        }
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    private void watheextended$blockCommandsDuringGame(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if (watheextended$isGameRunningAndNotOp()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean watheextended$isGameRunningAndNotOp() {
        if (player == null) return false;
        try {
            return GameWorldComponent.KEY.get(player.getServerWorld()).isRunning()
                    && !player.hasPermissionLevel(2);
        } catch (Exception ignored) {
            return false;
        }
    }
}
