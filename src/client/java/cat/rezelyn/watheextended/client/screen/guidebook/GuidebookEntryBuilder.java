package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.hml.ModifiersDisplay;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.client.screen.WatheOptionsScreen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GuidebookEntryBuilder {

    private GuidebookEntryBuilder() {
    }

    public static GuidebookEntrySource roles() {
        return GuidebookEntryBuilder::buildRoles;
    }

    public static GuidebookEntrySource modifiers() {
        return GuidebookEntryBuilder::buildModifiers;
    }

    public static GuidebookEntrySource items() {
        return List::of;
    }

    private static List<GuidebookEntry> buildRoles() {
        List<GuidebookEntry> list = new ArrayList<>();
        try {
            Map<String, RolesDisplay.RoleDisplay> roles = RolesDisplay.get();
            if (roles.isEmpty()) return list;

            List<RolesDisplay.RoleDisplay> killers = new ArrayList<>();
            List<RolesDisplay.RoleDisplay> innocents = new ArrayList<>();
            List<RolesDisplay.RoleDisplay> neutrals = new ArrayList<>();

            for (RolesDisplay.RoleDisplay d : roles.values()) {
                if (WatheOptionsScreen.isBlacklisted(d.id())) continue;
                switch (d.side()) {
                    case KILLER -> killers.add(d);
                    case INNOCENT -> innocents.add(d);
                    case NEUTRAL -> neutrals.add(d);
                }
            }

            record Group(String headerKey, int headerColor, String iconName,
                         List<RolesDisplay.RoleDisplay> entries) {
            }

            List<Group> groups = List.of(
                    new Group("gui.watheextended.guidebook.roles.side.killer",   0xDC001E, "killer",   killers),
                    new Group("gui.watheextended.guidebook.roles.side.innocent", 0x75A743, "civilian", innocents),
                    new Group("gui.watheextended.guidebook.roles.side.neutral",  0xA5C4FB, "neutral",  neutrals)
            );

            boolean first = true;
            for (Group group : groups) {
                if (group.entries().isEmpty()) continue;
                if (!first) list.add(GuidebookEntry.spacer());
                first = false;
                int col = group.headerColor();
                Text headerText = GuidebookIcons.icon(group.iconName())
                        .copy()
                        .append(Text.literal(" ").styled(s -> s.withFont(null).withColor(col)))
                        .append(Text.translatable(group.headerKey()).styled(s -> s.withBold(true).withColor(col)));
                list.add(GuidebookEntry.header(headerText, col));
                for (RolesDisplay.RoleDisplay d : group.entries()) {
                    String descKey = "gui.watheextended.guidebook.role.desc." + d.id().replace(":", ".");
                    list.add(GuidebookEntry.entry(d.display(), d.color(), d.id(), descKey, d.display()));
                }
            }
        } catch (Throwable ignored) {
        }
        return list;
    }

    private static List<GuidebookEntry> buildModifiers() {
        List<GuidebookEntry> list = new ArrayList<>();
        try {
            Map<String, ModifiersDisplay.ModifierDisplay> modifiers = ModifiersDisplay.get();
            if (modifiers.isEmpty()) return list;
            for (ModifiersDisplay.ModifierDisplay d : modifiers.values()) {
                String descKey = "gui.watheextended.guidebook.modifier.desc." + d.id().replace(":", ".");
                list.add(GuidebookEntry.entry(d.display(), d.color(), d.id(), descKey, d.display()));
            }
        } catch (Throwable ignored) {
        }
        return list;
    }
}

