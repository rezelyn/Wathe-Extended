package cat.rezelyn.watheextended.mixin.client.item;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.ItemCooldownManager;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemStack.class, priority = 2000)
public class AddonsItemTooltipsMixin {

    @Inject(method = "appendAttributeModifiersTooltip", at = @At("HEAD"), cancellable = true)
    private void watheextended$hideAttributeTooltip(Consumer<Text> consumer, PlayerEntity player, CallbackInfo ci) {
        if (((ItemStack) (Object) this).isOf(WatheItems.BAT)) ci.cancel();
    }

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void watheextended$addModTooltips(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> lines = cir.getReturnValue();
        if (lines.isEmpty()) return;

        Identifier id = Registries.ITEM.getId(((ItemStack) (Object) this).getItem());
        String tooltipKey = getTooltipKey(id);
        if (tooltipKey == null) return;

        List<Text> activeCooldownLines = new ArrayList<>();
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

        int seconds = getCooldownSeconds(id);

        if ((id.getNamespace().equals("stupid_express") || id.getNamespace().equals("starexpress")) && activeCooldownLines.isEmpty() && player != null) {
            Item registeredItem = Registries.ITEM.get(id);
            ItemCooldownManager cooldowns = player.getItemCooldownManager();
            if (registeredItem != null && cooldowns.isCoolingDown(registeredItem)) {
                float progress = cooldowns.getCooldownProgress(registeredItem, 0f);
                int remaining = Math.max(1, (int) Math.ceil(progress * seconds));
                activeCooldownLines.add(Text.translatable("tooltip.watheextended.item.cooldown.active", formatCooldownShort(remaining)).styled(style -> style.withColor(0xC90000))
                );
            }
        }

        if (!activeCooldownLines.isEmpty() || seconds > 0) {
            lines.add(Text.literal(""));
        }

        if (!activeCooldownLines.isEmpty()) {
            lines.addAll(activeCooldownLines);
        }

        if (seconds > 0 && activeCooldownLines.isEmpty()) {
            lines.add(Text.translatable("tooltip.watheextended.item.cooldown", formatCooldown(seconds)).formatted(Formatting.DARK_GRAY));
        }
    }

    @Unique
    private static String getTooltipKey(Identifier id) {
        return switch (id.getNamespace()) {
            case "kinswathe" -> {
                if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) yield null;
                yield switch (id.getPath()) {
                    case "sulfuric_acid_barrel" -> "tooltip.watheextended.item.sulfuric_acid_barrel";
                    case "hunting_knife" -> "tooltip.watheextended.item.hunting_knife";
                    case "medical_kit" -> "tooltip.watheextended.item.medical_kit";
                    case "pan" -> "tooltip.watheextended.item.pan";
                    case "poison_injector" -> "tooltip.watheextended.item.poison_injector";
                    case "pill" -> "tooltip.watheextended.item.pill";
                    case "blowgun" -> "tooltip.watheextended.item.blowgun";
                    case "knockout_drug" -> "tooltip.watheextended.item.knockout_drug";
                    case "capture_device" -> "tooltip.watheextended.item.capture_device";
                    case "wrench" -> "tooltip.watheextended.item.wrench";
                    case "icon_power_restoration" -> "tooltip.watheextended.item.icon_power_restoration";
                    case "icon_weapon_cooldown_refresh" -> "tooltip.watheextended.item.icon_weapon_cooldown_refresh";
                    case "icon_ability_cooldown_refresh" -> "tooltip.watheextended.item.icon_ability_cooldown_refresh";
                    case "icon_potion_effect_refresh" -> "tooltip.watheextended.item.icon_potion_effect_refresh";
                    default -> null;
                };
            }
            case "stupid_express" -> {
                if (!cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) yield null;
                yield switch (id.getPath()) {
                    case "jerry_can" -> "tooltip.watheextended.item.jerry_can";
                    case "lighter" -> "tooltip.watheextended.item.lighter";
                    default -> null;
                };
            }
            case "starexpress" -> {
                if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) yield null;
                yield "tape".equals(id.getPath()) ? "tooltip.watheextended.item.tape" : null;
            }
            default -> null;
        };
    }

    @Unique
    private static int getCooldownSeconds(Identifier id) {
        return switch (id.getNamespace()) {
            case "kinswathe" -> switch (id.getPath()) {
                case "sulfuric_acid_barrel" -> ClientConfig.getInt("watheextended.sulfuricAcidBarrel.cooldown", 60);
                case "hunting_knife" -> ClientConfig.getInt("watheextended.huntingKnife.cooldown", 45);
                case "medical_kit" -> ClientConfig.getInt("watheextended.medicalKit.cooldown", 60);
                case "pan" -> ClientConfig.getInt("watheextended.pan.cooldown", 45);
                case "poison_injector" -> ClientConfig.getInt("watheextended.poisonInjector.cooldown", 60);
                case "pill" -> ClientConfig.getInt("watheextended.pill.cooldown", 120);
                case "blowgun" -> ClientConfig.getInt("watheextended.blowgun.cooldown", 60);
                case "knockout_drug" -> ClientConfig.getInt("watheextended.knockoutDrug.cooldown", 60);
                case "capture_device" -> ClientConfig.getInt("watheextended.captureDevice.cooldown", 60);
                case "wrench" -> ClientConfig.getInt("watheextended.wrench.cooldown", 120);
                case "icon_power_restoration" -> ClientConfig.getInt("watheextended.powerRestoration.cooldown", 180);
                case "icon_weapon_cooldown_refresh" ->
                        ClientConfig.getInt("watheextended.refreshWeaponCooldown.cooldown", 180);
                case "icon_ability_cooldown_refresh" ->
                        ClientConfig.getInt("watheextended.refreshAbilityCooldown.cooldown", 300);
                case "icon_potion_effect_refresh" ->
                        ClientConfig.getInt("watheextended.refreshPotionEffect.cooldown", 180);
                default -> -1;
            };
            case "stupid_express" -> switch (id.getPath()) {
                case "jerry_can" -> {
                    int value = ClientConfig.getInt("stupidexpress.jerryCan.cooldown", 0);
                    yield value > 0 ? value : dynamicJerryCanCooldown();
                }
                case "lighter" -> {
                    int value = ClientConfig.getInt("stupidexpress.lighter.cooldown", 0);
                    if (value > 0) yield value;
                    int jerrycan = ClientConfig.getInt("stupidexpress.jerryCan.cooldown", 0);
                    yield jerrycan > 0 ? jerrycan : dynamicJerryCanCooldown();
                }
                default -> -1;
            };
            case "starexpress" ->
                    "tape".equals(id.getPath()) ? ClientConfig.getInt("starexpress.tapeCooldown", 20) : -1;
            default -> -1;
        };
    }

    @Unique
    private static int dynamicJerryCanCooldown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return 20;
        int playerCount = client.world.getPlayers().size();
        return Math.max(20, (int) (45 - 1.66 * playerCount));
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

    @Unique
    private static String formatCooldownShort(int seconds) {
        if (seconds < 60) return seconds + "s";
        int minutes = seconds / 60;
        int remaining = seconds % 60;
        return remaining == 0 ? minutes + "m" : minutes + "m" + remaining + "s";
    }
}
