package cat.rezelyn.watheextended.client.screen.guidebook;

import net.minecraft.text.Text;

public record GuidebookEntry(
        Text text,
        int color,
        boolean isHeader,
        String id,
        String descriptionKey,
        Text displayTitle,
        boolean active
) {

    public static GuidebookEntry header(Text text, int color) {
        return new GuidebookEntry(text, color, true, null, null, null, true);
    }

    public static GuidebookEntry spacer() {
        return new GuidebookEntry(Text.literal(""), 0xFFFFFF, false, null, null, null, true);
    }

    public static GuidebookEntry entry(Text text, int color, String id, String descKey, Text displayTitle, boolean active) {
        return new GuidebookEntry(text, color, false, id, descKey, displayTitle, active);
    }
}
