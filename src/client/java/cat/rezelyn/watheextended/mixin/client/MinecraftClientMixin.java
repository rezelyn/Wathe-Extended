package cat.rezelyn.watheextended.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable
    public ClientPlayerEntity player;

    @Unique
    private static boolean isPlayerSpectating(PlayerEntity player) {
        return player != null && (player.isSpectator());
    }

    @WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
    private void watheextended$restrictInventoryScreen(MinecraftClient instance, Screen screen, Operation<Void> original) {
        if (isPlayerSpectating(player)) {
            return;
        }

        original.call(instance, screen);
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void watheextended$restrictChatScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof ChatScreen)) return;
        if (screen instanceof SleepingChatScreen) return;
        if (player == null) return;
        if (WatheClient.gameComponent == null || !WatheClient.gameComponent.isRunning()) return;
        if (WatheClient.isPlayerAliveAndInSurvival() && !player.hasPermissionLevel(2)) {
            ci.cancel();
        }
    }
}
