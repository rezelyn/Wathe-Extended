package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.wathe.GameComponents;
import cat.rezelyn.watheextended.api.wathe.MapVariables;
import cat.rezelyn.watheextended.api.ClientConfig;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
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

    private MapVariablesCategory() {
    }

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.title"))
                .tooltip(Text.translatable("gui.watheextended.config.category.mapvariables.tooltip"));

        World world = MinecraftClient.getInstance().world;
        String lobbyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getLobbyArea(world));
        String playAreaDefault = ScreenUtils.boxToArgs(MapVariables.getPlayArea(world));
        String playAreaOffsetDefault = ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(world));
        String readyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getReadyArea(world));
        String resetPasteOffsetDefault = ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(world));
        String spawnPosDefault = ScreenUtils.posToArgs(MapVariables.getSpawnPosition(world));
        String readyAreaSpawnDefault = ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(world));
        String spectatorSpawnDefault = ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(world));

        /// WORLD PROTECTION
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection.desc")))
                .binding(false, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isBlockInteractionsProtected();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:enableWorldProtection " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());
        /// RANDOM TELEPORTATION
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp.desc")))
                .binding(false, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isRtpEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:rtp " + (value ? "enable" : "disable"), parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled": "gui.watheextended.config.text.disabled")))
                .build());
        /// ITEM BOUNDS CHECK
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck.desc")))
                .binding(true, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isItemBoundsCheckEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:enableItemBoundsCheck " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled")))
                .build());
        /// JUMP MODE
        builder.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpmode"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpmode.desc")))
                .binding("LOBBY", () -> ClientConfig.getString("watheextended.jumpMode", "DEFAULT"), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.jumpMode", value))
                .controller(opt -> CyclingListControllerBuilder.create(opt).values(java.util.Arrays.asList("DEFAULT", "LOBBY", "EVERYWHERE")).formatValue(Text::literal))
                .build());
        /// AUTO START
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart.desc")))
                .binding(GameComponents.getAutoStart(world), () -> GameComponents.getAutoStart(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set autoStart " + value, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Variables
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.tooltip")))
                .collapsed(false);
        /// LOBBY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(lobbyAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getLobbyArea(MinecraftClient.getInstance().world)),
                        value -> {
                            String[] parts = value.trim().split("\\s+");
                            if (parts.length == 6)
                                sendCommand.accept("watheextended:mapVariables set lobbyArea " + parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5], parent);
                        })
                .controller(StringControllerBuilder::create).build());
        /// PLAY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getPlayArea(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set playArea " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// PLAY AREA OFFSET
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set playAreaOffset " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// READY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getReadyArea(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set readyArea " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// RESET PASTE OFFSET
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(resetPasteOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set resetPasteOffset " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spawnPosDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set spawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION - READY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("watheextended:mapVariables set readyAreaSpawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION - SPECTATOR
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spectatorSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set spectatorSpawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        builder.group(group.build());
        builder.group(buildRtpSlotsGroup(parent, sendCommand));
        return builder.build();
    }

    private static OptionGroup buildRtpSlotsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.tooltip")))
                .collapsed(true);

        World world = MinecraftClient.getInstance().world;

        Map<Integer, TeleportationSlot> slots;
        try {
            WatheExtendedWorldComponent component = world != null ? WatheExtendedWorldComponent.KEY.get(world) : null;
            slots = component != null ? new LinkedHashMap<>(component.getTeleportationSlots()) : new LinkedHashMap<>();
        } catch (Throwable t) {
            slots = new LinkedHashMap<>();
        }

        if (slots.isEmpty()) {
            group.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.none").styled(style -> style.withColor(0xFF5555))));
        } else {
            for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
                final int slotId = entry.getKey();
                final String slotDefault = entry.getValue().toCommandArgs();

                group.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot", slotId))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot.desc").styled(style -> style.withColor(0x757575))))
                        .binding(slotDefault, () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                TeleportationSlot slot = component != null ? component.getTeleportationSlots().get(slotId) : null;
                                return slot != null ? slot.toCommandArgs() : slotDefault;
                            } catch (Throwable t) {
                                return slotDefault;
                            }
                        }, value -> {
                            String trimmed = value.trim();
                            String[] parts = trimmed.split("\\s+");
                            if (parts.length == 5) {
                                sendCommand.accept("watheextended:rtp slot edit " + slotId + " " + parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4], parent);
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
