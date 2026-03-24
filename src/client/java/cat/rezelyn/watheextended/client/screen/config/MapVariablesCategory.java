package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.wathe.GameComponents;
import cat.rezelyn.watheextended.api.wathe.MapVariables;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class MapVariablesCategory {

    private MapVariablesCategory() {}

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.title"))
                .tooltip(Text.translatable("gui.watheextended.config.category.mapvariables.tooltip"));

        World world = MinecraftClient.getInstance().world;

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isBlockInteractionsProtected();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand.accept("watheextended:enableWorldProtection " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isRtpEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand.accept("watheextended:rtp " + (v ? "enable" : "disable"), parent))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(true,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isItemBoundsCheckEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand.accept("watheextended:enableItemBoundsCheck " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            builder.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpinlobby"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpinlobby.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableJumpNotInGame(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand.accept("watheextended:config kinswathe enableJumpInLobby " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                    .build());
        }

        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getAutoStart(world),
                        () -> GameComponents.getAutoStart(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:gameSettings set autoStart " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.tooltip")))
                .collapsed(false);

        String lobbyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getLobbyArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(lobbyAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getLobbyArea(MinecraftClient.getInstance().world)),
                        v -> {
                            String[] parts = v.trim().split("\\s+");
                            if (parts.length == 6)
                                sendCommand.accept("watheextended:mapVariables set lobbyArea " + parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5], parent);
                        })
                .controller(StringControllerBuilder::create).build());

        String playAreaDefault = ScreenUtils.boxToArgs(MapVariables.getPlayArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getPlayArea(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set playArea " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String playAreaOffsetDefault = ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set playAreaOffset " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String readyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getReadyArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getReadyArea(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set readyArea " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String resetPasteOffsetDefault = ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(resetPasteOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set resetPasteOffset " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String spawnPosDefault = ScreenUtils.posToArgs(MapVariables.getSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spawnPosDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpawnPosition(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set spawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String readyAreaSpawnDefault = ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("watheextended:mapVariables set readyAreaSpawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        String spectatorSpawnDefault = ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spectatorSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(MinecraftClient.getInstance().world)),
                        v -> sendCommand.accept("wathe:mapVariables set spectatorSpawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        builder.group(group.build());
        builder.group(buildRtpSlotsGroup(parent, sendCommand));
        return builder.build();
    }

    private static OptionGroup buildRtpSlotsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.tooltip")))
                .collapsed(false);

        World world = MinecraftClient.getInstance().world;

        Map<Integer, TeleportationSlot> slots;
        try {
            WatheExtendedWorldComponent wec = world != null ? WatheExtendedWorldComponent.KEY.get(world) : null;
            slots = wec != null ? new LinkedHashMap<>(wec.getTeleportationSlots()) : new LinkedHashMap<>();
        } catch (Throwable t) {
            slots = new LinkedHashMap<>();
        }

        if (slots.isEmpty()) {
            group.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.none")
                            .styled(style -> style.withColor(0xFF5555))));
        } else {
            for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
                final int slotId = entry.getKey();
                final String slotDefault = entry.getValue().toCommandArgs();

                group.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot", slotId))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot.desc")
                                        .styled(style -> style.withColor(0x757575))))
                        .binding(slotDefault,
                                () -> {
                                    try {
                                        World w = MinecraftClient.getInstance().world;
                                        WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                        TeleportationSlot live = c != null ? c.getTeleportationSlots().get(slotId) : null;
                                        return live != null ? live.toCommandArgs() : slotDefault;
                                    } catch (Throwable t) {
                                        return slotDefault;
                                    }
                                },
                                v -> {
                                    String trimmed = v.trim();
                                    String[] parts = trimmed.split("\\s+");
                                    if (parts.length == 5) {
                                        sendCommand.accept("watheextended:rtp slot edit " + slotId
                                                + " " + parts[0] + " " + parts[1] + " " + parts[2]
                                                + " " + parts[3] + " " + parts[4], parent);
                                    } else {
                                        sendCommand.accept("watheextended:rtp slot remove " + slotId, parent);
                                    }
                                })
                        .controller(StringControllerBuilder::create)
                        .build());
            }
        }

        return group.build();
    }
}
