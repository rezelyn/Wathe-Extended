package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.api.wathe.RolesId;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;

public final class RolesCategory {

    private RolesCategory() {
    }

    public static ConfigCategory build(Screen parent, Set<String> blacklist,
                                       Map<String, Boolean> pendingState,
                                       BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.roles"))
                .tooltip(Text.translatable("gui.watheextended.config.category.roles.tooltip"));

        try {
            Set<String> roleId = new LinkedHashSet<>();
            for (String id : RolesId.get()) {
                if (!isBlacklisted(id, blacklist)) roleId.add(id);
            }

            if (roleId.isEmpty()) {
                builder.option(LabelOption.create(
                        Text.translatable("gui.watheextended.config.roles.empty")
                                .styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, RolesDisplay.RoleDisplay> roleName = RolesDisplay.get();

                for (Map.Entry<String, List<String>> entry : sortByMods(roleId).entrySet()) {
                    OptionGroup.Builder group = OptionGroup.createBuilder()
                            .name(Text.literal(modsNamespace(entry.getKey())))
                            .collapsed(false);

                    for (String id : entry.getValue()) {
                        RolesDisplay.RoleDisplay display = roleName.get(id);
                        Text label = display != null
                                ? display.display().copy().styled(style -> style.withColor(display.color()))
                                : Text.literal(RolesDisplay.localName(id));

                        boolean roleCurrentValue = pendingState.containsKey(id)
                                ? pendingState.get(id)
                                : !ConfigHelper.getDisabledRoles().contains(id);

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(roleCurrentValue,
                                        () -> pendingState.containsKey(id)
                                                ? pendingState.get(id)
                                                : !ConfigHelper.getDisabledRoles().contains(id),
                                        enabled -> {
                                            pendingState.put(id, enabled);
                                            sendCommand.accept("setEnabledRole " + id + " " + enabled, parent);
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                                .build());
                    }
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.roles.error")
                            .styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    private static boolean isBlacklisted(String id, Set<String> blacklist) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return blacklist.contains(local);
    }

    static Map<String, List<String>> sortByMods(Set<String> ids) {
        Map<String, List<String>> map = new TreeMap<>();
        for (String id : ids) {
            int colon = id.indexOf(':');
            String ns = colon > 0 ? id.substring(0, colon) : id;
            map.computeIfAbsent(ns, k -> new ArrayList<>()).add(id);
        }
        map.values().forEach(Collections::sort);
        return map;
    }

    static String modsNamespace(String namespace) {
        return switch (namespace) {
            case "noellesroles" -> "Noelle's Roles";
            case "kinswathe" -> "Kin's Wathe";
            case "stupid_express" -> "Stupid Express";
            case "starexpress" -> "Starry Express";
            default -> RolesDisplay.localName(namespace + ":x").replace(" X", "").trim();
        };
    }
}

