package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.api.hml.AssignedModifier;
import cat.rezelyn.watheextended.api.wathe.AssignedRole;
import cat.rezelyn.watheextended.api.cca.GameStatus;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LimitedInventoryScreen.class)
public class LimitedInventoryScreenMixin {

    @Redirect(
        method = "drawBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFFFFFF)V"
        )
    )
    private void watheExtended$removeGameSprite(DrawContext context, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha) {
        // suppress game.png background sprite rendering
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void watheExtended$renderRoleAndModifiers(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!GameStatus.State(client.world)) return;

            AssignedRole.Assigned role = AssignedRole.getRole(client.player);
            List<AssignedModifier.Assigned> modifiers = AssignedModifier.getModifiers(client.player);
            if (role == null) return;

            int width = client.getWindow().getScaledWidth();
            int spacing = 5;

            Text roleLabel = Text.translatable("gui.watheextended.inventory.role");
            String roleName = role.text().getString();
            int roleWidth = client.textRenderer.getWidth(roleName);
            int roleLabelWidth = client.textRenderer.getWidth(roleLabel);
            int roleTotalWidth = roleLabelWidth + roleWidth + spacing;
            int roleX = width / 2 - roleTotalWidth / 2;
            int roleY = 18;

            context.drawTextWithShadow(client.textRenderer, roleLabel, roleX, roleY, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.literal(roleName), roleX + roleLabelWidth + spacing, roleY, role.color());

            int modifiersY = roleY + client.textRenderer.fontHeight + 2;

            if (modifiers.isEmpty()) {
                Text noMods = Text.translatable("gui.watheextended.inventory.no_modifiers");
                int w = client.textRenderer.getWidth(noMods);
                int x = width / 2 - w / 2;
                context.drawTextWithShadow(client.textRenderer, noMods, x, modifiersY, 0x555555);
            } else {
                Text label = Text.translatable("gui.watheextended.inventory.modifiers");
                int labelWidth = client.textRenderer.getWidth(label);
                int totalWidth = labelWidth;
                for (int i = 0; i < modifiers.size(); i++) {
                    totalWidth += client.textRenderer.getWidth(modifiers.get(i).text());
                    if (i < modifiers.size() - 1) totalWidth += client.textRenderer.getWidth(", ");
                }

                int cursorX = width / 2 - totalWidth / 2;
                context.drawTextWithShadow(client.textRenderer, label, cursorX, modifiersY, 0xFFFFFF);
                cursorX += labelWidth;

                for (int i = 0; i < modifiers.size(); i++) {
                    AssignedModifier.Assigned mod = modifiers.get(i);
                    Text modText = mod.text();
                    int color = mod.color();

                    context.drawTextWithShadow(client.textRenderer, modText, cursorX, modifiersY, color);
                    cursorX += client.textRenderer.getWidth(modText);

                    if (i < modifiers.size() - 1) {
                        Text sep = Text.literal(", ").styled(style -> style.withColor(0xFFFFFF));
                        context.drawTextWithShadow(client.textRenderer, sep, cursorX, modifiersY, 0xFFFFFF);
                        cursorX += client.textRenderer.getWidth(sep);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }
}


