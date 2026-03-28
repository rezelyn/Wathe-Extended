package cat.rezelyn.watheextended.mixin.client.wathe;

import cat.rezelyn.watheextended.api.ClientConfig;
import dev.doctor4t.wathe.client.util.WatheItemTooltips;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(WatheItemTooltips.class)
public class WatheItemTooltipsMixin {

    @Inject(method = "addTooltipForItem(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;Ljava/util/List;)V", at = @At("TAIL"))
    private static void watheextended$processTooltip(Item item, ItemStack itemStack, List<Text> lines, CallbackInfo ci) {
        if (!itemStack.isOf(item)) return;
        String tooltipKey = getTooltipKey(item);

        if (tooltipKey == null) return;

        List<Text> activeCooldownLines = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            net.minecraft.text.Style s = lines.get(i).getStyle();
            if (s.getColor() != null && s.getColor().getRgb() == 0xC90000) {
                activeCooldownLines.add(lines.get(i));
            }
        }

        if (lines.size() > 1) lines.subList(1, lines.size()).clear();

        for (String line : Text.translatable(tooltipKey).getString().split("\n")) {
            lines.add(Text.literal(line).styled(style -> style.withColor(0x808080)));
        }

        int seconds = getCooldownSeconds(item);
        if (!activeCooldownLines.isEmpty() || seconds > 0) {
            lines.add(Text.literal(""));
        }

        if (!activeCooldownLines.isEmpty()) {
            lines.addAll(activeCooldownLines);
        }

        if (seconds > 0) {
            lines.add(Text.literal(""));
            lines.add(Text.translatable("tooltip.watheextended.item.cooldown", formatCooldown(seconds)).formatted(Formatting.DARK_GRAY));
        }
    }

    @Unique
    private static String getTooltipKey(Item item) {
        if (item == WatheItems.GRENADE) return "tooltip.watheextended.item.grenade";
        if (item == WatheItems.KNIFE) return "tooltip.watheextended.item.knife";
        if (item == WatheItems.REVOLVER) return "tooltip.watheextended.item.revolver";
        if (item == WatheItems.CROWBAR) return "tooltip.watheextended.item.crowbar";
        if (item == WatheItems.BODY_BAG) return "tooltip.watheextended.item.body_bag";
        if (item == WatheItems.PSYCHO_MODE) return "tooltip.watheextended.item.psycho_mode";
        if (item == WatheItems.LOCKPICK) return "tooltip.watheextended.item.lockpick";
        if (item == WatheItems.BLACKOUT) return "tooltip.watheextended.item.blackout";
        return null;
    }

    @Unique
    private static int getCooldownSeconds(Item item) {
        if (item == WatheItems.GRENADE) return ClientConfig.getInt("watheextended.grenade.cooldown", 90);
        if (item == WatheItems.KNIFE) return ClientConfig.getInt("watheextended.knife.cooldown", 60);
        if (item == WatheItems.REVOLVER) return ClientConfig.getInt("watheextended.revolver.cooldown", 10);
        if (item == WatheItems.CROWBAR) return ClientConfig.getInt("watheextended.crowbar.cooldown", 10);
        if (item == WatheItems.BODY_BAG) return ClientConfig.getInt("watheextended.bodyBag.cooldown", 300);
        if (item == WatheItems.PSYCHO_MODE) return ClientConfig.getInt("watheextended.psychoMode.cooldown", 300);
        if (item == WatheItems.LOCKPICK) return ClientConfig.getInt("watheextended.lockpick.cooldown", 180);
        if (item == WatheItems.BLACKOUT) return ClientConfig.getInt("watheextended.blackout.cooldown", 300);
        return -1;
    }

    @Unique
    private static String formatCooldown(int seconds) {
        if (seconds < 60) return seconds + (seconds == 1 ? " second" : " seconds");
        int minutes = seconds / 60;
        int remaining = seconds % 60;
        String minPart = minutes + (minutes == 1 ? " minute" : " minutes");
        if (remaining == 0) return minPart;
        return minPart + " " + remaining + (remaining == 1 ? " second" : " seconds");
    }
}
