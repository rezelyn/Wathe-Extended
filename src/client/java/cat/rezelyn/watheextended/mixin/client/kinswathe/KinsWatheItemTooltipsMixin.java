package cat.rezelyn.watheextended.mixin.client.kinswathe;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class KinsWatheItemTooltipsMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void watheextended$addKinsWatheTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (!ConfigHelper.isLoaded()) return;

        List<Text> lines = cir.getReturnValue();
        if (lines.isEmpty()) return;

        ItemStack stack = (ItemStack) (Object) this;
        String tooltipKey = getTooltipKey(stack.getItem());
        if (tooltipKey == null) return;

        List<Text> activeCooldownLines = new java.util.ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            Style style = lines.get(i).getStyle();
            if (style.getColor() != null && style.getColor().getRgb() == 0xC90000) {
                activeCooldownLines.add(lines.get(i));
            }
        }

        if (lines.size() > 1) lines.subList(1, lines.size()).clear();

        for (String line : Text.translatable(tooltipKey).getString().split("\n")) {
            lines.add(Text.literal(line).styled(style -> style.withColor(0x808080)));
        }

        int seconds = getCooldownSeconds(stack.getItem());
        if (!activeCooldownLines.isEmpty() || seconds > 0) {
            lines.add(Text.literal(""));
        }

        if (!activeCooldownLines.isEmpty()) {
            lines.addAll(activeCooldownLines);
        }

        if (seconds > 0) {
            lines.add(Text.translatable("tooltip.watheextended.item.cooldown", formatCooldown(seconds)).formatted(Formatting.DARK_GRAY));
        }
    }

    @Unique
    private static String getTooltipKey(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        if (!"kinswathe".equals(id.getNamespace())) return null;
        return switch (id.getPath()) {
            case "sulfuric_acid_barrel" -> "tooltip.watheextended.item.sulfuric_acid_barrel";
            case "hunting_knife" -> "tooltip.watheextended.item.hunting_knife";
            case "medical_kit" -> "tooltip.watheextended.item.medical_kit";
            case "pan" -> "tooltip.watheextended.item.pan";
            case "poison_injector" -> "tooltip.watheextended.item.poison_injector";
            case "pill" -> "tooltip.watheextended.item.pill";
            case "blowgun" -> "tooltip.watheextended.item.blowgun";
            case "knockout_drug" -> "tooltip.watheextended.item.knockout_drug";
            default -> null;
        };
    }

    @Unique
    private static int getCooldownSeconds(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        if (!"kinswathe".equals(id.getNamespace())) return -1;
        return switch (id.getPath()) {
            case "sulfuric_acid_barrel" -> ClientConfig.getInt("watheextended.sulfuricAcidBarrel.cooldown", 60);
            case "hunting_knife" -> ClientConfig.getInt("watheextended.huntingKnife.cooldown", 45);
            case "medical_kit" -> ClientConfig.getInt("watheextended.medicalKit.cooldown", 60);
            case "pan" -> ClientConfig.getInt("watheextended.pan.cooldown", 45);
            case "poison_injector" -> ClientConfig.getInt("watheextended.poisonInjector.cooldown", 60);
            case "pill" -> ClientConfig.getInt("watheextended.pill.cooldown", 120);
            case "blowgun" -> ClientConfig.getInt("watheextended.blowgun.cooldown", 60);
            case "knockout_drug" -> ClientConfig.getInt("watheextended.knockoutDrug.cooldown", 60);
            default -> -1;
        };
    }

    @Unique
    private static String formatCooldown(int seconds) {
        String sec = Text.translatable(seconds == 1 ? "tooltip.watheextended.item.cooldown.second" : "tooltip.watheextended.item.cooldown.seconds").getString();
        if (seconds < 60) return seconds + " " + sec;
        int minutes = seconds / 60;
        int remaining = seconds % 60;
        String min = Text.translatable(minutes == 1 ? "tooltip.watheextended.item.cooldown.minute" : "tooltip.watheextended.item.cooldown.minutes").getString();
        String minPart = minutes + " " + min;
        if (remaining == 0) return minPart;
        String remSec = Text.translatable(remaining == 1 ? "tooltip.watheextended.item.cooldown.second" : "tooltip.watheextended.item.cooldown.seconds").getString();
        return minPart + " " + remaining + " " + remSec;
    }
}
