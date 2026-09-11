package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.RolesDisplay;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GuidebookEntryBuilder {

    // roles/modifiers that shouldn't appear in the guidebook
    private static final Set<String> DENYLIST = Set.of(
            "discovery_civilian", // used in the discovery map effect
            "loose_end",          // used in the loose end map effect
            "secret_killer"       // used in the special murder-only round, same as killer
    );

    private GuidebookEntryBuilder() {
    }

    private static boolean isDenied(String id) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return DENYLIST.contains(local);
    }

    public static GuidebookEntrySource roles() {
        return GuidebookEntryBuilder::buildRoles;
    }

    public static GuidebookEntrySource modifiers() {
        return GuidebookEntryBuilder::buildModifiers;
    }

    public static GuidebookEntrySource gameGuide() {
        return GuidebookEntryBuilder::buildGameGuide;
    }

    private static List<GuidebookEntry> buildRoles() {
        List<GuidebookEntry> list = new ArrayList<>();
        try {
            Map<String, RolesDisplay.RoleDisplay> roles = RolesDisplay.get();
            if (roles.isEmpty()) return list;

            List<RolesDisplay.RoleDisplay> killers = new ArrayList<>();
            List<RolesDisplay.RoleDisplay> innocents = new ArrayList<>();
            List<RolesDisplay.RoleDisplay> neutrals = new ArrayList<>();

            for (RolesDisplay.RoleDisplay display : roles.values()) {
                if (isDenied(display.id())) continue;
                switch (display.side()) {
                    case KILLER -> killers.add(display);
                    case INNOCENT -> innocents.add(display);
                    case NEUTRAL -> neutrals.add(display);
                }
            }

            record Group(String headerKey, int headerColor, String iconName, List<RolesDisplay.RoleDisplay> entries) {
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
                int color = group.headerColor();
                Text text = ScreenUtils.icon(group.iconName()).copy()
                        .append(Text.literal(" ").styled(style -> style.withFont(null).withColor(color)))
                        .append(Text.translatable(group.headerKey()).styled(style -> style.withBold(true).withColor(color)));
                list.add(GuidebookEntry.header(text, color));
                for (RolesDisplay.RoleDisplay display : group.entries()) {
                    String descKey = "gui.watheextended.guidebook.role.desc." + display.id().replace(":", ".");
                    boolean active = !ConfigHelper.getDisabledRoles().contains(display.id());
                    boolean killerSided = display.side() == RolesDisplay.Side.KILLER;
                    Text icon = ScreenUtils.icon(active ? "enabled" : "disabled");
                    Text entryText = icon.copy().append(Text.literal(" ").styled(style -> style.withFont(null))).append(display.display().copy().styled(style -> style.withColor(display.color())));
                    list.add(GuidebookEntry.entry(entryText, display.color(), display.id(), descKey, display.display(), active, killerSided));
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
            for (ModifiersDisplay.ModifierDisplay display : modifiers.values()) {
                if (isDenied(display.id())) continue;
                String descKey = "gui.watheextended.guidebook.modifier.desc." + display.id().replace(":", ".");
                boolean active = !ConfigHelper.getDisabledModifiers().contains(display.id());
                Text icon = ScreenUtils.icon(active ? "enabled" : "disabled");
                Text text = icon.copy()
                        .append(Text.literal(" ").styled(style -> style.withFont(null)))
                        .append(display.display().copy().styled(style -> style.withColor(display.color())));
                list.add(GuidebookEntry.entry(text, display.color(), display.id(), descKey, display.display(), active));
            }
        } catch (Throwable ignored) {
        }
        return list;
    }

    // section header icons are placeholders reused from the existing font sheet
    private record GuideSection(String headerKey, int headerColor, String iconName, List<String> topics) {
    }

    private static final int GUIDE_HEADER_COLOR = 0xB07B2E;
    private static final int GUIDE_ENTRY_COLOR = 0x4A3728;
    private static final int GUIDE_CIVILIAN_COLOR = 0x75A743;
    private static final int GUIDE_KILLER_COLOR = 0xDC001E;

    private static final List<GuideSection> GUIDE_SECTIONS = List.of(
            new GuideSection("gui.watheextended.guidebook.left_page.guide.section.overview", GUIDE_HEADER_COLOR, "question",
                    List.of("overview", "sides", "round_flow", "voice_chat", "doors", "coins", "abilities", "bodies")),
            new GuideSection("gui.watheextended.guidebook.left_page.guide.section.civilians", GUIDE_CIVILIAN_COLOR, "civilian",
                    List.of("mood", "vigilante", "civilian_tips", "poison_tip", "shootouts")),
            new GuideSection("gui.watheextended.guidebook.left_page.guide.section.killers", GUIDE_KILLER_COLOR, "killer",
                    List.of("instinct", "killer_tips", "knife_tip", "lockpick_tip", "grenade_tip", "blackout_tip", "psycho_tip", "countering_grouping"))
    );

    private static List<GuidebookEntry> buildGameGuide() {
        List<GuidebookEntry> list = new ArrayList<>();
        try {
            boolean first = true;
            for (GuideSection section : GUIDE_SECTIONS) {
                if (!first) list.add(GuidebookEntry.spacer());
                first = false;

                int color = section.headerColor();
                Text header = ScreenUtils.icon(section.iconName()).copy()
                        .append(Text.literal(" ").styled(style -> style.withFont(null).withColor(color)))
                        .append(Text.translatable(section.headerKey()).styled(style -> style.withBold(true).withColor(color)));
                list.add(GuidebookEntry.header(header, color));

                for (String topic : section.topics()) {
                    Text title = Text.translatable("gui.watheextended.guidebook.guide.title." + topic);
                    String descKey = "gui.watheextended.guidebook.guide.desc." + topic;
                    Text text = ScreenUtils.icon("enabled").copy()
                            .append(Text.literal(" ").styled(style -> style.withFont(null)))
                            .append(title.copy().styled(style -> style.withColor(GUIDE_ENTRY_COLOR)));
                    list.add(GuidebookEntry.entry(text, GUIDE_ENTRY_COLOR, "guide:" + topic, descKey, title, true));
                }
            }
        } catch (Throwable ignored) {
        }
        return list;
    }
}
