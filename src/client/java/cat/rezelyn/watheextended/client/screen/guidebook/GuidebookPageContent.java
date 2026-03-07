package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public final class GuidebookPageContent {

    public static final String[] PAGE_LABELS = {
            "gui.watheextended.guidebook.right_page.roles.subtitle.desc",
            "gui.watheextended.guidebook.right_page.roles.subtitle.abilities",
            "gui.watheextended.guidebook.right_page.roles.subtitle.items"
    };

    private GuidebookPageContent() {
    }

    public static PageResult resolve(String baseDescKey, String id, int page) {
        return resolve(baseDescKey, id, page, false);
    }

    public static PageResult resolve(String baseDescKey, String id, int page, boolean isKillerSided) {
        String key = switch (page) {
            case 0 -> baseDescKey;
            case 1 -> buildKey(baseDescKey, id, "abilities");
            default -> buildKey(baseDescKey, id, "items");
        };

        List<Text> result = new ArrayList<>();

        // resolve lang-based content
        if (key != null) {
            Text raw = Text.translatable(key);
            String str = raw.getString();
            if (!str.isEmpty() && !str.equals(key)) {
                str = resolveAllergicPlaceholders(id, str);
                for (String line : str.split("\\\\n|\\n", -1)) {
                    result.add(parseLine(line));
                }
            }
        }

        if (page == 2 && id != null) {
            if (RoleItemsRegistry.hasExplicitRegistration(id) || (result.isEmpty() && isKillerSided)) {
                List<RoleItemsRegistry.RoleItem> autoItems = RoleItemsRegistry.getItemsForRole(id, isKillerSided);
                if (!autoItems.isEmpty()) {
                    result.clear();
                    for (int i = 0; i < autoItems.size(); i++) {
                        RoleItemsRegistry.RoleItem item = autoItems.get(i);
                        result.add(item.toText());
                        // optional description sub-line
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

    private static String resolveAllergicPlaceholders(String id, String str) {
        if (id == null || !id.contains("allergic")) return str;
        if (!str.contains("?/?")) return str;
        try {
            if (!cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) return str;
            int nothing = cat.rezelyn.watheextended.api.starexpress.ConfigHelper.getAllergicNothingChance();
            int instinct = cat.rezelyn.watheextended.api.starexpress.ConfigHelper.getAllergicInstinctChance();
            int armor = cat.rezelyn.watheextended.api.starexpress.ConfigHelper.getAllergicArmorChance();
            int poison = cat.rezelyn.watheextended.api.starexpress.ConfigHelper.getAllergicPoisonChance();
            int total = nothing + instinct + armor + poison;
            str = replaceFirstPlaceholder(str, nothing, total);
            str = replaceFirstPlaceholder(str, instinct, total);
            str = replaceFirstPlaceholder(str, armor, total);
            str = replaceFirstPlaceholder(str, poison, total);
        } catch (Throwable ignored) {
        }
        return str;
    }

    private static String replaceFirstPlaceholder(String str, int value, int total) {
        int idx = str.indexOf("?/?");
        if (idx < 0) return str;
        return str.substring(0, idx) + value + "/" + total + str.substring(idx + 3);
    }

    public static Text parseLine(String line) {
        MutableText result = Text.literal("");
        int i = 0;
        Style currentStyle = Style.EMPTY;
        StringBuilder buf = new StringBuilder();

        while (i < line.length()) {
            if (line.startsWith("{icon:", i)) {
                int end = line.indexOf("}", i);
                if (end != -1) {
                    if (!buf.isEmpty()) {
                        result.append(Text.literal(buf.toString()).setStyle(currentStyle));
                        buf.setLength(0);
                    }
                    String iconName = line.substring(i + 6, end);
                    Text iconText = GuidebookIcons.icon(iconName);
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
                if (!buf.isEmpty()) {
                    result.append(Text.literal(buf.toString()).setStyle(currentStyle));
                    buf.setLength(0);
                }
                currentStyle = applyFormattingCode(currentStyle, code);
                i += 2;
                continue;
            }
            buf.append(line.charAt(i));
            i++;
        }

        if (!buf.isEmpty()) {
            result.append(Text.literal(buf.toString()).setStyle(currentStyle));
        }
        return result;
    }

    private static Style applyFormattingCode(Style base, char code) {
        Formatting fmt = Formatting.byCode(code);
        if (fmt == null) return base;
        if (fmt == Formatting.RESET) return Style.EMPTY;
        if (fmt.isColor()) return base.withColor(fmt);
        // formatting modifiers
        return switch (fmt) {
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

    private static String buildKey(String baseDescKey, String id, String pageType) {
        if (baseDescKey == null || id == null) return null;
        String dotId = id.replace(":", ".");
        if (baseDescKey.contains(".desc." + dotId)) {
            return baseDescKey.replace(".desc." + dotId, "." + pageType + "." + dotId);
        }
        int lastDot = baseDescKey.lastIndexOf(".desc.");
        if (lastDot >= 0) {
            return baseDescKey.substring(0, lastDot) + "." + pageType + "." + dotId;
        }
        return null;
    }

    public record PageResult(List<Text> lines, boolean noContent) {
    }
}
