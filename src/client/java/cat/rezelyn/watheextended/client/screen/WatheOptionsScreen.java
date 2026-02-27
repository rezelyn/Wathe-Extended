package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.api.hml.ModifiersDisplay;
import cat.rezelyn.watheextended.api.hml.ModifiersId;
import cat.rezelyn.watheextended.api.hml.config.DisabledModifiers;
import cat.rezelyn.watheextended.api.hml.config.DisabledRoles;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.api.wathe.RolesId;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.*;

public final class WatheOptionsScreen {

    private static Screen reopenParent = null;
    private static int waitForTicks = 0;

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (reopenParent == null) return;
            if (--waitForTicks > 0) return;
            Screen parent = reopenParent;
            reopenParent = null;
            client.execute(() -> client.setScreen(create(parent)));
        });
    }

    // Blacklist of roles that shouldn't be shown in the config screen
    private static final Set<String> ROLE_BLACKLIST = Set.of(
            // These roles are either needed for the mod to function properly so can't be disabled
            // (and the bonus roles, aka shitpost roles from noelle's roles)
            "awesome_binglus",
            "better_vigilante",
            "the_insane_damned_paranoid_killer",
            "civilian",
            "killer",
            "vigilante",
            "discovery_civilian",
            "loose_end"
    );

    public static boolean isBlacklisted(String id) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return ROLE_BLACKLIST.contains(local);
    }

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("gui.watheextended.config.title"))
                .category(buildOptionsCategory())
                .category(buildRolesCategory(parent))
                .category(buildModifiersCategory(parent))
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildOptionsCategory() {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options"))
                .tooltip(Text.translatable("gui.watheextended.config.category.options.tooltip"));

        builder.group(buildGamerulesGroup());
        builder.group(buildMapVariablesGroup());
        builder.group(buildRtpSlotsGroup());

        return builder.build();
    }

    private static OptionGroup buildGamerulesGroup() {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.gamerules.title"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.options.gamerules.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        boolean collisionsDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            collisionsDefault = wec == null || wec.isPlayerCollisionsEnabled();
        } catch (Throwable t) {
            collisionsDefault = true;
        }
        final boolean collisionsFinal = collisionsDefault;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.gamerules.collisions"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.gamerules.collisions.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(true,
                        () -> collisionsFinal,
                        v -> sendCommand("watheextended:enableCollisions " + v, null))
                .controller(TickBoxControllerBuilder::create)
                .build());

        boolean rtpDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            rtpDefault = wec == null || wec.isRtpEnabled();
        } catch (Throwable t) {
            rtpDefault = true;
        }
        final boolean rtpFinal = rtpDefault;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.gamerules.rtp"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.options.gamerules.rtp.desc")
                                .styled(style -> style.withColor(0x505050))))
                .binding(true,
                        () -> rtpFinal,
                        v -> sendCommand("watheextended:enableRtp " + v, null))
                .controller(TickBoxControllerBuilder::create)
                .build());

        boolean worldProtectionDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            worldProtectionDefault = wec == null || wec.isBlockInteractionsProtected();
        } catch (Throwable t) {
            worldProtectionDefault = true;
        }
        final boolean worldProtectionFinal = worldProtectionDefault;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.gamerules.worldprotection"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.options.gamerules.worldprotection.desc")
                                .styled(style -> style.withColor(0x505050))))
                .binding(true,
                        () -> worldProtectionFinal,
                        v -> sendCommand("watheextended:enableWorldProtection " + v, null))
                .controller(TickBoxControllerBuilder::create)
                .build());

        return group.build();
    }

    private static OptionGroup buildMapVariablesGroup() {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.title"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.options.mapvariables.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        String playAreaDefault = boxToCommandArgs(MapVariables.getPlayArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.playarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.playarea.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(playAreaDefault, () -> playAreaDefault,
                        v -> sendWatheMapVarCommand("playArea " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        String playAreaOffsetDefault = vec3iToCommandArgs(MapVariables.getPlayAreaOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.playareaoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.playareaoffset.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(playAreaOffsetDefault, () -> playAreaOffsetDefault,
                        v -> sendWatheMapVarCommand("playAreaOffset " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        String readyAreaDefault = boxToCommandArgs(MapVariables.getReadyArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.readyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.readyarea.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(readyAreaDefault, () -> readyAreaDefault,
                        v -> sendWatheMapVarCommand("readyArea " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        String resetPasteOffsetDefault = vec3iToCommandArgs(MapVariables.getResetPasteOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.resetpasteoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.resetpasteoffset.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(resetPasteOffsetDefault, () -> resetPasteOffsetDefault,
                        v -> sendWatheMapVarCommand("resetPasteOffset " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        String readyAreaSpawnDefault = posWithOrientationToCommandArgs(MapVariables.getReadyAreaSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.readyareaspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.readyareaspawnpos.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(readyAreaSpawnDefault, () -> readyAreaSpawnDefault,
                        v -> sendCommand("watheextended:mapVariables set readyAreaSpawnPosition " + v.trim(), null))
                .controller(StringControllerBuilder::create)
                .build());

        String spawnPosDefault = posWithOrientationToCommandArgs(MapVariables.getSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.spawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.spawnpos.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(spawnPosDefault, () -> spawnPosDefault,
                        v -> sendWatheMapVarCommand("spawnPosition " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        String spectatorSpawnDefault = posWithOrientationToCommandArgs(MapVariables.getSpectatorSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.mapvariables.spectatorspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.options.mapvariables.spectatorspawnpos.desc")
                        .styled(style -> style.withColor(0x505050))))
                .binding(spectatorSpawnDefault, () -> spectatorSpawnDefault,
                        v -> sendWatheMapVarCommand("spectatorSpawnPosition " + v.trim()))
                .controller(StringControllerBuilder::create)
                .build());

        return group.build();
    }

    private static OptionGroup buildRtpSlotsGroup() {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.options.rtp_slots.title"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.options.rtp_slots.tooltip")))
                .collapsed(true);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        List<TeleportationSlot> slots;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            slots = wec != null ? new ArrayList<>(wec.getTeleportationSlots()) : new ArrayList<>();
        } catch (Throwable t) {
            slots = new ArrayList<>();
        }

        if (slots.isEmpty()) {
            group.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.options.rtp_slots.none")
                            .styled(style -> style.withColor(0xFF0000))));
        } else {
            for (int i = 0; i < slots.size(); i++) {
                final int slotIndex = i + 1;
                TeleportationSlot slot = slots.get(i);
                String slotDefault = slot.toCommandArgs();

                group.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.options.rtp_slots", slotIndex))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.options.rtp_slots.desc")
                                        .styled(style -> style.withColor(0x505050))))

                        .binding(slotDefault, () -> slotDefault,
                                v -> {
                                    String trimmed = v.trim();
                                    String[] parts = trimmed.split("\\s+");
                                    if (parts.length == 5) {
                                        // edit x y z yaw pitch
                                        sendCommand("watheextended:editSlot " + slotIndex
                                                + " " + parts[0] + " " + parts[1] + " " + parts[2]
                                                + " " + parts[3] + " " + parts[4], null);
                                    } else if (trimmed.equals("delete")) {
                                        sendCommand("watheextended:removeSlot " + slotIndex, null);
                                    } else if (trimmed.isEmpty()) {
                                        sendCommand("watheextended:removeSlot " + slotIndex, null);
                                    }
                                })
                        .controller(StringControllerBuilder::create)
                        .build());
            }
        }

        return group.build();
    }

    private static ConfigCategory buildRolesCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.roles"))
                .tooltip(Text.translatable("gui.watheextended.config.category.roles.tooltip"));

        try {
            Set<String> roleId = new LinkedHashSet<>();
            for (String id : RolesId.get()) {
                if (!isBlacklisted(id)) roleId.add(id);
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

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(true,
                                        () -> !DisabledRoles.get().contains(id),
                                        enabled -> sendCommand("setEnabledRole " + id + " " + enabled, parent))
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true)
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

    private static ConfigCategory buildModifiersCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers"))
                .tooltip(Text.translatable("gui.watheextended.config.category.modifiers.tooltip"));

        try {
            Set<String> modifierId = new LinkedHashSet<>(ModifiersId.get());

            if (modifierId.isEmpty()) {
                builder.option(LabelOption.create(
                        Text.translatable("gui.watheextended.config.modifiers.empty")
                                .styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, ModifiersDisplay.ModifierDisplay> modifierName = ModifiersDisplay.get();

                for (Map.Entry<String, List<String>> entry : sortByMods(modifierId).entrySet()) {
                    OptionGroup.Builder group = OptionGroup.createBuilder()
                            .name(Text.literal(modsNamespace(entry.getKey())))
                            .collapsed(false);

                    for (String id : entry.getValue()) {
                        ModifiersDisplay.ModifierDisplay display = modifierName.get(id);
                        Text label = display != null
                                ? display.display().copy().styled(style -> style.withColor(display.color()))
                                : Text.literal(ModifiersDisplay.localName(id));

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(true,
                                        () -> !DisabledModifiers.get().contains(id),
                                        enabled -> sendCommand("setEnabledModifier " + id + " " + enabled, parent))
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true)
                                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                                .build());
                    }
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.modifiers.error")
                            .styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    // sort roles/modifiers by their namespace (mod id) group
    private static Map<String, List<String>> sortByMods(Set<String> ids) {
        Map<String, List<String>> map = new TreeMap<>();
        for (String id : ids) {
            int colon = id.indexOf(':');
            String ns = colon > 0 ? id.substring(0, colon) : id;
            map.computeIfAbsent(ns, k -> new ArrayList<>()).add(id);
        }
        // also sort the roles/modifiers alphabetically within each mod group
        map.values().forEach(Collections::sort);
        return map;
    }

    // supported mods namespace to display name
    private static String modsNamespace(String namespace) {
        return switch (namespace) {
            case "noellesroles" -> "Noelle's Roles";
            case "kinswathe" -> "Kin's Wathe";
            case "stupid_express" -> "Stupid Express";
            case "starexpress" -> "Starry Express";
            // if unsupported mod namespace, it'll just display the namespace as is
            default -> RolesDisplay.localName(namespace + ":x").replace(" X", "").trim();
        };
    }

    private static void sendWatheMapVarCommand(String subCommand) {
        sendCommand("wathe:mapVariables set " + subCommand, null);
    }

    private static String fmt(double v) {
        // Use up to 4 decimal places, then strip trailing zeros and a lone decimal point
        String s = String.format("%.4f", v);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    private static String boxToCommandArgs(@org.jetbrains.annotations.Nullable net.minecraft.util.math.Box box) {
        if (box == null) return "0 0 0 0 0 0";
        return fmt(box.minX) + " " + fmt(box.minY) + " " + fmt(box.minZ)
                + " " + fmt(box.maxX) + " " + fmt(box.maxY) + " " + fmt(box.maxZ);
    }

    private static String vec3iToCommandArgs(@org.jetbrains.annotations.Nullable net.minecraft.util.math.Vec3i v) {
        if (v == null) return "0 0 0";
        return v.getX() + " " + v.getY() + " " + v.getZ();
    }

    private static String posWithOrientationToCommandArgs(
            @org.jetbrains.annotations.Nullable dev.doctor4t.wathe.cca.MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        return fmt(pos.pos.x) + " " + fmt(pos.pos.y) + " " + fmt(pos.pos.z)
                + " " + fmt(pos.yaw) + " " + fmt(pos.pitch);
    }

    private static void sendCommand(String command, Screen currentScreen) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            // send command to server
            player.networkHandler.sendChatCommand(command);

            client.setScreen(null); // close and reopen the config screen after a few ticks to reflect the changes
            reopenParent = currentScreen;
            waitForTicks = 1;

        } catch (Throwable ignored) {
        }
    }
}