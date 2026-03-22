package cat.rezelyn.watheextended.mixin.client.wathe;

import dev.doctor4t.ratatouille.util.TextUtils;
import dev.doctor4t.wathe.client.gui.LobbyPlayersRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Mixin(LobbyPlayersRenderer.class)
public class LobbyPlayersRendererMixin {
    @Redirect(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/ratatouille/util/TextUtils;getWithLineBreaks(Lnet/minecraft/text/Text;)Ljava/util/List;"))
    private static List<Text> watheExtended$removeThanksText(Text text) {
        String value = text.getString();
        if (value.contains("RAT / doctor4t")) {
            return Collections.emptyList();
        }
        return TextUtils.getWithLineBreaks(text);
    }
}
