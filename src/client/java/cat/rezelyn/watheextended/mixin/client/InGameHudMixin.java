package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.api.wathe.GameStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final int VANILLA_FADE = 40;
    private static final int EXTENDED_FADE = 300;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int heldItemTooltipFade;
    @Shadow
    private ItemStack currentStack;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void watheExtended$extendTooltipFade(CallbackInfo ci) {
        if (this.heldItemTooltipFade == VANILLA_FADE) {
            this.heldItemTooltipFade = EXTENDED_FADE;
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void watheExtended$renderItemNameAndLore(DrawContext context, CallbackInfo ci) {
        if (this.heldItemTooltipFade <= 0 || this.client.player == null || this.currentStack == null || this.currentStack.isEmpty()) {
            return;
        }

        List<Text> tooltip = this.currentStack.getTooltip(Item.TooltipContext.DEFAULT, this.client.player, TooltipType.BASIC);

        if (tooltip.isEmpty()) return;
        ci.cancel();

        // skip supporter line for Knife
        boolean isKnife = this.currentStack.isOf(Registries.ITEM.get(Identifier.of("wathe", "knife")));
        int loreStart = isKnife ? 2 : 1;

        Text cooldownLine = null;
        for (int i = loreStart; i < tooltip.size(); i++) {
            Text t = tooltip.get(i);
            if (t.getStyle().getColor() != null && t.getStyle().getColor().getRgb() == 0xC90000) {
                cooldownLine = t;
                break;
            }
        }

        List<Text> lines = new ArrayList<>();
        lines.add(tooltip.getFirst());

        if (cooldownLine != null) {
            // only show cooldown line if it exists
            lines.add(Text.literal(cooldownLine.getString()).setStyle(cooldownLine.getStyle().withItalic(false)));
        } else {
            // show regular tooltip lines
            for (int i = loreStart; i < tooltip.size() && lines.size() < 3; i++) {
                Text lore = tooltip.get(i);
                lines.add(Text.literal(lore.getString()).setStyle(lore.getStyle().withItalic(false)));
            }
        }

        TextRenderer textRenderer = this.client.textRenderer;
        int lineHeight = textRenderer.fontHeight + 2;
        int alpha = (int) Math.min(Math.min(this.heldItemTooltipFade, EXTENDED_FADE - this.heldItemTooltipFade + 10) * 255.0F / 10.0F, 255.0F);

        int scaledWidth = context.getScaledWindowWidth();
        int scaledHeight = context.getScaledWindowHeight();

        boolean staminaBarActive = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded() && cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStaminaBar() && GameStatus.isActive(this.client.world) && this.client.interactionManager != null && (this.client.interactionManager.getCurrentGameMode() == net.minecraft.world.GameMode.SURVIVAL || this.client.interactionManager.getCurrentGameMode() == net.minecraft.world.GameMode.ADVENTURE);

        int blockBottom = scaledHeight - 22 - 4 - (staminaBarActive ? 14 : 0);
        int blockTop = blockBottom - (lines.size() * lineHeight) + 2;

        for (int i = 0; i < lines.size(); i++) {
            Text line = lines.get(i);
            int textWidth = textRenderer.getWidth(line);
            int lx = (scaledWidth - textWidth) / 2;
            int ly = blockTop + (i * lineHeight);

            int baseRgb = (i == 0) ? 0xFFFFFF : (line.getStyle().getColor() != null ? line.getStyle().getColor().getRgb() : 0xFFFFFF);
            int lineColor = (alpha << 24) | (baseRgb & 0xFFFFFF);

            context.drawTextWithShadow(textRenderer, line, lx, ly, lineColor);
        }
    }
}
