package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.*;

public final class GuidebookPageContent {

    public static final String[] PAGE_LABELS = {
            "gui.watheextended.guidebook.right_page.roles.subtitle.desc",
            "gui.watheextended.guidebook.right_page.roles.subtitle.abilities",
            "gui.watheextended.guidebook.right_page.roles.subtitle.items"
    };

    private GuidebookPageContent() {}

    public static PageResult resolve(String descKey, String id, int page) {
        return resolve(descKey, id, page, false);
    }

    public static PageResult resolve(String descKey, String id, int page, boolean isKillerSided) {
        String key = switch (page) {
            case 0 -> descKey;
            case 1 -> buildKey(descKey, id, "abilities");
            default -> buildKey(descKey, id, "items");
        };

        List<Text> result = new ArrayList<>();
        if (key != null) {
            Text raw = Text.translatable(key);
            String string = raw.getString();
            if (!string.isEmpty() && !string.equals(key)) {
                string = resolveAllergicPlaceholders(id, string);
                string = resolvePlaceholders(id, string);
                for (String line : string.split("\\\\n|\\n", -1)) {
                    result.add(parseLine(line));
                }
            }
        }

        if (page == 2 && id != null) {
            if (GuidebookEntryItems.hasExplicitRegistration(id) || (result.isEmpty() && isKillerSided)) {
                List<GuidebookEntryItems.RoleItem> autoItems = GuidebookEntryItems.getItemsForRole(id, isKillerSided);
                if (!autoItems.isEmpty()) {
                    result.clear();
                    for (int i = 0; i < autoItems.size(); i++) {
                        GuidebookEntryItems.RoleItem item = autoItems.get(i);
                        result.add(item.toText());
                        Text desc = item.descText();
                        if (desc != null) result.add(desc);
                        if (i < autoItems.size() - 1) result.add(Text.literal(""));
                    }
                }
            }
        }

        if (result.isEmpty()) {
            return new PageResult(List.of(noContent(page)), true);
        }

        return new PageResult(result, false);
    }

    private static String resolveAllergicPlaceholders(String id, String string) {
        if (id == null || !id.contains("allergic")) return string;
        if (!string.contains("?/?")) return string;
        try {
            if (!isLoaded()) return string;
            int nothing = getAllergicNothingChance();
            int instinct = getAllergicInstinctChance();
            int armor = getAllergicArmorChance();
            int poison = getAllergicPoisonChance();
            int total = nothing + instinct + armor + poison;
            string = replaceFirst(string, "?/?", nothing + "/" + total);
            string = replaceFirst(string, "?/?", instinct + "/" + total);
            string = replaceFirst(string, "?/?", armor + "/" + total);
            string = replaceFirst(string, "?/?", poison + "/" + total);
        } catch (Throwable ignored) {
        }
        return string;
    }

    private static String resolvePlaceholders(String id, String string) {
        if (id == null || !string.contains("%s")) return string;
        try {
            if (id.contains("introverted")) {
                int crowdCount = ClientConfig.getInt("watheextended.introverted.crowdCount", 3);
                float crowdRange = ClientConfig.getFloat("watheextended.introverted.crowdRange", 5.0f);
                string = replaceFirst(string, "%s", String.valueOf(crowdCount));
                string = replaceFirst(string, "%s", crowdRange == (int) crowdRange ? String.valueOf((int) crowdRange) : String.valueOf(crowdRange));

            } else if (id.contains("taxed")) {
                int killThreshold = ClientConfig.getInt("watheextended.taxed.killThreshold", 1);
                int killWindow = ClientConfig.getInt("watheextended.taxed.killWindowSeconds", 60);
                float reduction = ClientConfig.getFloat("watheextended.taxed.coinReduction", 0.50f);
                int percent = Math.round(reduction * 100f);
                string = replaceFirst(string, "%s", String.valueOf(killThreshold));
                string = replaceFirst(string, "%s", String.valueOf(killWindow));
                string = replaceFirst(string, "%s", String.valueOf(percent));

            } else if (id.contains("adaptive")) {
                float penalty = ClientConfig.getFloat("watheextended.adaptive.penaltyReduction", 0.25f);
                float bonus = ClientConfig.getFloat("watheextended.adaptive.bonusMultiplier", 0.50f);
                int penaltyPercent = Math.round((1f - penalty) * 100f);
                int bonusPercent = Math.round(bonus * 100f);
                string = replaceFirst(string, "%s", String.valueOf(penaltyPercent));
                string = replaceFirst(string, "%s", String.valueOf(bonusPercent));
            }
        } catch (Throwable ignored) {
        }
        return string;
    }

    private static String replaceFirst(String string, String placeholder, String replacement) {
        int idx = string.indexOf(placeholder);
        if (idx < 0) return string;
        return string.substring(0, idx) + replacement + string.substring(idx + placeholder.length());
    }

    public static Text parseLine(String line) {
        MutableText result = Text.literal("");
        int i = 0;
        Style currentStyle = Style.EMPTY;
        StringBuilder builder = new StringBuilder();

        while (i < line.length()) {
            if (line.startsWith("{icon:", i)) {
                int end = line.indexOf("}", i);
                if (end != -1) {
                    if (!builder.isEmpty()) {
                        result.append(Text.literal(builder.toString()).setStyle(currentStyle));
                        builder.setLength(0);
                    }
                    String iconName = line.substring(i + 6, end);
                    Text iconText = ScreenUtils.icon(iconName);
                    if (iconText.getString().isEmpty()) {
                        result.append(Text.literal("{icon:" + iconName + "}").setStyle(currentStyle));
                    } else {
                        result.append(iconText);
                    }
                    i = end + 1;
                    continue;
                }
            }

            if (line.charAt(i) == '\u00a7' && i + 1 < line.length()) {
                char code = Character.toLowerCase(line.charAt(i + 1));
                if (!builder.isEmpty()) {
                    result.append(Text.literal(builder.toString()).setStyle(currentStyle));
                    builder.setLength(0);
                }
                currentStyle = applyFormattingCode(currentStyle, code);
                i += 2;
                continue;
            }
            builder.append(line.charAt(i));
            i++;
        }

        if (!builder.isEmpty()) {
            result.append(Text.literal(builder.toString()).setStyle(currentStyle));
        }
        return result;
    }

    private static Style applyFormattingCode(Style base, char code) {
        Formatting format = Formatting.byCode(code);
        if (format == null) return base;
        if (format == Formatting.RESET) return Style.EMPTY;
        if (format.isColor()) return base.withColor(format);
        return switch (format) {
            case BOLD -> base.withBold(true);
            case ITALIC -> base.withItalic(true);
            case UNDERLINE -> base.withUnderline(true);
            case STRIKETHROUGH -> base.withStrikethrough(true);
            case OBFUSCATED -> base.withObfuscated(true);
            default -> base;
        };
    }

    private static Text noContent(int page) {
        return Text.translatable(switch (page) {
            case 1 -> "gui.watheextended.guidebook.right_page.roles.subtitle.no_abilities";
            case 2 -> "gui.watheextended.guidebook.right_page.roles.subtitle.no_items";
            default -> "gui.watheextended.guidebook.right_page.roles.subtitle.no_desc";
        }).styled(style -> style.withColor(0xFF5555));
    }

    private static String buildKey(String key, String id, String type) {
        if (key == null || id == null) return null;
        String rawId = id.replace(":", ".");
        if (key.contains(".desc." + rawId)) {
            return key.replace(".desc." + rawId, "." + type + "." + rawId);
        }
        int lastDot = key.lastIndexOf(".desc.");
        if (lastDot >= 0) {
            return key.substring(0, lastDot) + "." + type + "." + rawId;
        }
        return null;
    }

    public record PageResult(List<Text> lines, boolean noContent) {
    }
}
