package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class GuidebookPageContent {

    public static final String[] PAGE_LABELS = {
            "gui.watheextended.guidebook.subtitle.desc",
            "gui.watheextended.guidebook.subtitle.abilities",
            "gui.watheextended.guidebook.subtitle.items"
    };

    private GuidebookPageContent() {
    }

    public static PageResult resolve(String baseDescKey, String id, int page) {
        String key = switch (page) {
            case 0 -> baseDescKey;
            case 1 -> buildKey(baseDescKey, id, "abilities");
            default -> buildKey(baseDescKey, id, "items");
        };

        if (key == null) {
            return new PageResult(List.of(noContent(page)), true);
        }

        Text raw = Text.translatable(key);
        String str = raw.getString();

        if (str.isEmpty() || str.equals(key)) {
            return new PageResult(List.of(noContent(page)), true);
        }

        List<Text> result = new ArrayList<>();
        for (String line : str.split("\\\\n|\\n", -1)) {
            result.add(parseLine(line));
        }
        return new PageResult(result, false);
    }

    public static Text parseLine(String line) {
        if (!line.contains("{icon:")) {
            return Text.literal(line);
        }

        MutableText result = Text.literal("");
        int i = 0;
        while (i < line.length()) {
            int start = line.indexOf("{icon:", i);
            if (start == -1) {
                result.append(Text.literal(line.substring(i)));
                break;
            }
            if (start > i) {
                result.append(Text.literal(line.substring(i, start)));
            }
            int end = line.indexOf("}", start);
            if (end == -1) {
                result.append(Text.literal(line.substring(start)));
                break;
            }
            String iconName = line.substring(start + 6, end); // 6 = length of "{icon:"
            Text iconText = GuidebookIcons.icon(iconName);
            if (iconText.getString().isEmpty()) {
                result.append(Text.literal("{icon:" + iconName + "}"));
            } else {
                result.append(iconText);
            }
            i = end + 1;
        }
        return result;
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
