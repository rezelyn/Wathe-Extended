package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.hml.ModifiersDisplay;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GuidebookEntryBuilder {

    // roles/modifiers that shouldn't appear in the guidebook
    private static final Set<String> BLACKLIST = Set.of(
            "awesome_binglus",
            "better_vigilante",
            "the_insane_damned_paranoid_killer",
            "discovery_civilian",
            "loose_end"
    );

    private GuidebookEntryBuilder() {
    }

    private static boolean isBlacklisted(String id) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return BLACKLIST.contains(local);
    }

    public static GuidebookEntrySource roles() {
        return GuidebookEntryBuilder::buildRoles;
    }

    public static GuidebookEntrySource modifiers() {
        return GuidebookEntryBuilder::buildModifiers;
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
                if (isBlacklisted(d.id())) continue;
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
                    new Group("gui.watheextended.guidebook.left_page.roles.side.killer", 0xDC001E, "killer", killers),
                    new Group("gui.watheextended.guidebook.left_page.roles.side.civilian", 0x75A743, "civilian", innocents),
                    new Group("gui.watheextended.guidebook.left_page.roles.side.neutral", 0xDE6F00, "neutral", neutrals)
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
                    boolean active = !ConfigHelper.getDisabledRoles().contains(d.id());
                    boolean killerSided = d.side() == RolesDisplay.Side.KILLER;
                    Text icon = GuidebookIcons.icon(active ? "enabled" : "disabled");
                    Text entryText = icon.copy()
                            .append(Text.literal(" ").styled(s -> s.withFont(null)))
                            .append(d.display().copy().styled(s -> s.withColor(d.color())));
                    list.add(GuidebookEntry.entry(entryText, d.color(), d.id(), descKey, d.display(), active, killerSided));
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
                if (isBlacklisted(d.id())) continue;
                String descKey = "gui.watheextended.guidebook.modifier.desc." + d.id().replace(":", ".");
                boolean active = !ConfigHelper.getDisabledModifiers().contains(d.id());
                Text icon = GuidebookIcons.icon(active ? "enabled" : "disabled");
                Text entryText = icon.copy()
                        .append(Text.literal(" ").styled(s -> s.withFont(null)))
                        .append(d.display().copy().styled(s -> s.withColor(d.color())));
                list.add(GuidebookEntry.entry(entryText, d.color(), d.id(), descKey, d.display(), active));
            }
        } catch (Throwable ignored) {
        }
        return list;
    }
}

