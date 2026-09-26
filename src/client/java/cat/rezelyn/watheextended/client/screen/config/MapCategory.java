package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.GameComponents;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.api.WatheGameModes;
import org.agmas.harpymodloader.Harpymodloader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class MapCategory {

    private MapCategory() {
    }

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.title"))
                .tooltip(Text.translatable("gui.watheextended.config.category.map.tooltip"));

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
                .name(Text.translatable("gui.watheextended.config.category.map.opt.worldprotection"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.opt.worldprotection.desc")))
                .binding(false, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isBlockInteractionsProtected();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:enableWorldProtection " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// RANDOM TELEPORTATION
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.opt.rtp"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.opt.rtp.desc")))
                .binding(false, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isRtpEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:rtp " + (value ? "enable" : "disable"), parent))
                .controller(option -> BooleanControllerBuilder.create(option).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on": "gui.watheextended.config.text.off")))
                .build());
        /// ITEM BOUNDS CHECK
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.opt.itemboundscheck"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.opt.itemboundscheck.desc")))
                .binding(true, () -> {
                    try {
                        World w = MinecraftClient.getInstance().world;
                        WatheExtendedWorldComponent component = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                        return component == null || component.isItemBoundsCheckEnabled();
                    } catch (Throwable t) {
                        return true;
                    }
                }, value -> sendCommand.accept("watheextended:enableItemBoundsCheck " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// JUMP MODE
        builder.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.opt.jumpmode"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.opt.jumpmode.desc")))
                .binding("LOBBY", () -> ClientConfig.getString("watheextended.jumpMode", "LOBBY"), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.jumpMode", value))
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(java.util.List.of("DEFAULT", "LOBBY", "EVERYWHERE"))
                        .formatValue(value -> Text.translatable("gui.watheextended.config.category.map.opt.jumpmode." + value.toLowerCase(java.util.Locale.ROOT))))
                .build());
        /// AUTO START
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.opt.autostart"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.opt.autostart.desc")))
                .binding(GameComponents.getAutoStart(world), () -> GameComponents.getAutoStart(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set autoStart " + value, parent))
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 60) // max 60s because who wants it to be higher?
                        .step(1)
                        .formatValue(value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value))))
                .build());

        // Map effect
        OptionGroup.Builder mapEffect = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.map_effect"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.map_effect.tooltip")));
        /// GAMEMODE
        mapEffect.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.gamemode"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.gamemode.desc")))
                .binding("MODDED_MURDER", () -> currentGameMode(MinecraftClient.getInstance().world), value -> sendCommand.accept("watheextended.map.gameMode " + value, parent))
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(java.util.List.of("MODDED_MURDER", "MODDED_SECRET_MURDER", "MURDER", "LOOSE_ENDS", "SECRET_MURDER", "DISCOVERY"))
                        .formatValue(value -> Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.gamemode." + value.toLowerCase(java.util.Locale.ROOT))))
                .build());
        mapEffect.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.generic"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.generic.desc")))
                .binding(false, () -> {
                    try {
                        return WatheExtendedWorldComponent.KEY.get(MinecraftClient.getInstance().world).isGenericMapEffectEnabled();
                    } catch (Throwable ignored) { return false; }
                }, value -> sendCommand.accept("watheextended.map.generic " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option)
                        .formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// IN-GAME TIME
        mapEffect.option(mapTimeOption("time", "getGameTimeOfDay", sendCommand, parent));
        /// LOBBY TIME
        mapEffect.option(mapTimeOption("lobbytime", "getLobbyTimeOfDay", sendCommand, parent));
        /// GAME DURATION
        mapEffect.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.duration"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt.duration.desc")))
                .binding(10, () -> {
                    try { return WatheExtendedWorldComponent.KEY.get(MinecraftClient.getInstance().world).getGameDurationMinutes(); }
                    catch (Throwable ignored) { return 10; }
                }, value -> sendCommand.accept("watheextended.map.duration " + value, parent))
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(1, 60).step(1)
                        .formatValue(value -> Text.literal(value + "m")))
                .build());
        builder.group(mapEffect.build());

        // Variables
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.tooltip")))
                .collapsed(true);
        /// LOBBY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.lobbyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.lobbyarea.desc").styled(s -> s.withColor(0x757575))))
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
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.playarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.playarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getPlayArea(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set playArea " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// PLAY AREA OFFSET
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.playareaoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.playareaoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(playAreaOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set playAreaOffset " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// READY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.readyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.readyarea.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaDefault,
                        () -> ScreenUtils.boxToArgs(MapVariables.getReadyArea(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set readyArea " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// RESET PASTE OFFSET
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.resetpasteoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.resetpasteoffset.desc").styled(s -> s.withColor(0x757575))))
                .binding(resetPasteOffsetDefault,
                        () -> ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set resetPasteOffset " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.spawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.spawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spawnPosDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set spawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION - READY AREA
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.readyareaspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.readyareaspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(readyAreaSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("watheextended:mapVariables set readyAreaSpawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());
        /// SPAWN POSITION - SPECTATOR
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.spectatorspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.variables.opt.spectatorspawnpos.desc").styled(s -> s.withColor(0x757575))))
                .binding(spectatorSpawnDefault,
                        () -> ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(MinecraftClient.getInstance().world)),
                        value -> sendCommand.accept("wathe:mapVariables set spectatorSpawnPosition " + value.trim(), parent))
                .controller(StringControllerBuilder::create).build());

        builder.group(group.build());
        builder.group(buildRtpSlotsGroup(parent, sendCommand));
        return builder.build();
    }

    private static String currentGameMode(World world) {
        try {
            var configured = WatheExtendedWorldComponent.KEY.get(world).getGameModeSelection();
            if (configured != null) return configured;
            var mode = GameWorldComponent.KEY.get(world).getGameMode();
            if (mode == Harpymodloader.MODDED_GAMEMODE) return "MODDED_MURDER";
            if (mode == Harpymodloader.SECRET_MODDED_GAMEMODE) return "MODDED_SECRET_MURDER";
            if (mode == WatheGameModes.LOOSE_ENDS) return "LOOSE_ENDS";
            if (mode == WatheGameModes.SECRET_MURDER) return "SECRET_MURDER";
            if (mode == WatheGameModes.DISCOVERY) return "DISCOVERY";
        } catch (Throwable ignored) {}
        return "MURDER";
    }

    private static Option<String> mapTimeOption(String key, String getter, BiConsumer<String, Screen> sendCommand, Screen parent) {
        return Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt." + key))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt." + key + ".desc")))
                .binding(key.equals("lobbytime") ? "DAY" : "NIGHT", () -> {
                    try {
                        var component = WatheExtendedWorldComponent.KEY.get(MinecraftClient.getInstance().world);
                        return getter.equals("getLobbyTimeOfDay") ? component.getLobbyTimeOfDay() : component.getGameTimeOfDay();
                    } catch (Throwable ignored) { return key.equals("lobbytime") ? "DAY" : "NIGHT"; }
                }, value -> sendCommand.accept("watheextended.map." + (key.equals("lobbytime") ? "lobbyTime" : "gameTime") + " " + value, parent))
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(java.util.List.of("DAY", "NIGHT", "SUNDOWN"))
                        .formatValue(value -> Text.translatable("gui.watheextended.config.category.map.group.map_effect.opt." + key + "." + value.toLowerCase(java.util.Locale.ROOT))))
                .build();
    }

    private static OptionGroup buildRtpSlotsGroup(Screen parent, BiConsumer<String, Screen> sendCommand) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.map.group.rtp_slots"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.rtp_slots.tooltip")))
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
                    Text.translatable("gui.watheextended.config.category.map.group.rtp_slots.none").styled(style -> style.withColor(0xFF5555))));
        } else {
            for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
                final int slotId = entry.getKey();
                final String slotDefault = entry.getValue().toCommandArgs();

                group.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.map.group.rtp_slots.slot", slotId))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.map.group.rtp_slots.slot.desc").styled(style -> style.withColor(0x757575))))
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
