package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.util.ChatHudRenderHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ChatHud.class, priority = 2000)
public class ChatHudRestoreMixin {

    @WrapMethod(method = "render")
    private void watheextended$restoreChatHud(
            DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused,
            Operation<Void> original) {
        if (WatheExtendedClientConfig.showChatDuringGame) {
            ChatHudRenderHelper.setForcingRender(true);
        }
        try {
            original.call(context, currentTick, mouseX, mouseY, focused);
        } finally {
            ChatHudRenderHelper.setForcingRender(false);
        }
    }
}
