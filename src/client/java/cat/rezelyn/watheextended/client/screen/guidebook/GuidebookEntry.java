package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.Text;

public record GuidebookEntry(
        Text text,
        int color,
        boolean isHeader,
        String id,
        String descriptionKey,
        Text displayTitle
) {

    public static GuidebookEntry header(Text text, int color) {
        return new GuidebookEntry(text, color, true, null, null, null);
    }

    public static GuidebookEntry spacer() {
        return new GuidebookEntry(Text.literal(""), 0xFFFFFF, false, null, null, null);
    }

    public static GuidebookEntry entry(Text text, int color, String id, String descKey, Text displayTitle) {
        return new GuidebookEntry(text, color, false, id, descKey, displayTitle);
    }
}
