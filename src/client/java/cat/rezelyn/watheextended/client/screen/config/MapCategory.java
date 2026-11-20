package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.config.ConfigBuilder.GroupBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.model.EntryDefinition;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.orelib.api.setting.Validation;
import cat.rezelyn.watheextended.api.GameComponents;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.utils.TeleportationSlot;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public final class MapCategory {

  private MapCategory() {}

  public static void build(CategoryBuilder builder) {
    builder.name(text("category.map.title")).description(text("category.map.tooltip"));

    World world = MinecraftClient.getInstance().world;
    String lobbyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getLobbyArea(world));
    String playAreaDefault = ScreenUtils.boxToArgs(MapVariables.getPlayArea(world));
    String playAreaOffsetDefault = ScreenUtils.vec3iToArgs(MapVariables.getPlayAreaOffset(world));
    String readyAreaDefault = ScreenUtils.boxToArgs(MapVariables.getReadyArea(world));
    String resetPasteOffsetDefault =
        ScreenUtils.vec3iToArgs(MapVariables.getResetPasteOffset(world));
    String spawnPosDefault = ScreenUtils.posToArgs(MapVariables.getSpawnPosition(world));
    String readyAreaSpawnDefault =
        ScreenUtils.posToArgs(MapVariables.getReadyAreaSpawnPosition(world));
    String spectatorSpawnDefault =
        ScreenUtils.posToArgs(MapVariables.getSpectatorSpawnPosition(world));

    builder.option(
        SettingKey.of("watheextended.blockProtection", Boolean.class),
        option ->
            option
                .name(text("category.map.opt.worldprotection"))
                .description(text("category.map.opt.worldprotection.desc"))
                .defaultValue(false)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    builder.option(
        SettingKey.of("watheextended.rtpEnabled", Boolean.class),
        option ->
            option
                .name(text("category.map.opt.rtp"))
                .description(text("category.map.opt.rtp.desc"))
                .defaultValue(false)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    builder.option(
        SettingKey.of("watheextended.itemBoundsCheck", Boolean.class),
        option ->
            option
                .name(text("category.map.opt.itemboundscheck"))
                .description(text("category.map.opt.itemboundscheck.desc"))
                .defaultValue(true)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    builder.option(
        SettingKey.of("watheextended.jumpMode", String.class),
        option ->
            option
                .name(text("category.map.opt.jumpmode"))
                .description(text("category.map.opt.jumpmode.desc"))
                .defaultValue("LOBBY")
                .controller(
                    Controllers.cycling(
                        java.util.List.of("DEFAULT", "LOBBY", "EVERYWHERE"),
                        value ->
                            text(
                                "category.map.opt.jumpmode."
                                    + value.toLowerCase(java.util.Locale.ROOT))))
                .formatter(value ->
                    text("category.map.opt.jumpmode." + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(
                    value -> choice(value, java.util.List.of("DEFAULT", "LOBBY", "EVERYWHERE"))));
    builder.option(
        SettingKey.of("command.autoStart", Integer.class),
        option ->
            option
                .name(text("category.map.opt.autostart"))
                .description(text("category.map.opt.autostart.desc"))
                .defaultValue(GameComponents.getAutoStart(world))
                .controller(Controllers.numberSlider(Integer.class, 0, 60, 1, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value)))
                .validator(value -> range(value, 0, 60)));

    // Map effect
    builder.group(
        "map.mapEffect",
        mapEffect -> {
          mapEffect
              .name(text("category.map.group.map_effect"))
              .description(text("category.map.group.map_effect.tooltip"));
          mapEffect.option(
              SettingKey.of("watheextended.map.gameMode", String.class),
              option ->
                  option
                      .name(text("category.map.group.map_effect.opt.gamemode"))
                      .description(text("category.map.group.map_effect.opt.gamemode.desc"))
                      .defaultValue("MODDED_MURDER")
                      .controller(
                          Controllers.cycling(
                              java.util.List.of(
                                  "MODDED_MURDER",
                                  "MODDED_SECRET_MURDER",
                                  "MURDER",
                                  "LOOSE_ENDS",
                                  "SECRET_MURDER",
                                  "DISCOVERY"),
                              value ->
                                  text(
                                      "category.map.group.map_effect.opt.gamemode."
                                          + value.toLowerCase(java.util.Locale.ROOT))))
                      .formatter(value ->
                          text(
                              "category.map.group.map_effect.opt.gamemode."
                                  + value.toLowerCase(java.util.Locale.ROOT)))
                      .validator(value ->
                          choice(
                              value,
                              java.util.List.of(
                                  "MODDED_MURDER",
                                  "MODDED_SECRET_MURDER",
                                  "MURDER",
                                  "LOOSE_ENDS",
                                  "SECRET_MURDER",
                                  "DISCOVERY"))));
          mapEffect.option(
              SettingKey.of("watheextended.secretMurderChance", Integer.class),
              option ->
                  option
                      .name(text("category.map.group.map_effect.opt.secretmurder"))
                      .description(text("category.map.group.map_effect.opt.secretmurder.desc"))
                      .defaultValue(GameComponents.getSecretMurderRoundChance(world))
                      .controller(Controllers.numberSlider(Integer.class, 0, 100, 1, false))
                      .formatter(value ->
                          Text.literal(String.format(java.util.Locale.ROOT, "%d%%", value)))
                      .validator(value -> range(value, 0, 100)));
          mapEffect.option(
              SettingKey.of("watheextended.map.generic", Boolean.class),
              option ->
                  option
                      .name(text("category.map.group.map_effect.opt.generic"))
                      .description(text("category.map.group.map_effect.opt.generic.desc"))
                      .defaultValue(false)
                      .controller(Controllers.bool(true))
                      .formatter(ConfigText::onOff));
          mapTimeOption(mapEffect, "time");
          mapTimeOption(mapEffect, "lobbytime");
          mapEffect.option(
              SettingKey.of("watheextended.map.duration", Integer.class),
              option ->
                  option
                      .name(text("category.map.group.map_effect.opt.duration"))
                      .description(text("category.map.group.map_effect.opt.duration.desc"))
                      .defaultValue(10)
                      .controller(Controllers.numberSlider(Integer.class, 1, 60, 1, false))
                      .formatter(value -> Text.literal(value + "m"))
                      .validator(value -> range(value, 1, 60)));
        });

    // Variables
    builder.group(
        "map.variables",
        group -> {
          group
              .name(text("category.map.group.variables"))
              .description(text("category.map.group.variables.tooltip"))
              .collapsed(true);
          group.option(
              SettingKey.of("command.lobbyArea", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.lobbyarea"))
                      .description(
                          text("category.map.group.variables.opt.lobbyarea.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(lobbyAreaDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 6, false)));
          group.option(
              SettingKey.of("command.playArea", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.playarea"))
                      .description(
                          text("category.map.group.variables.opt.playarea.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(playAreaDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 6, false)));
          group.option(
              SettingKey.of("command.playAreaOffset", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.playareaoffset"))
                      .description(
                          text("category.map.group.variables.opt.playareaoffset.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(playAreaOffsetDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 3, false)));
          group.option(
              SettingKey.of("command.readyArea", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.readyarea"))
                      .description(
                          text("category.map.group.variables.opt.readyarea.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(readyAreaDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 6, false)));
          group.option(
              SettingKey.of("command.resetPasteOffset", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.resetpasteoffset"))
                      .description(
                          text("category.map.group.variables.opt.resetpasteoffset.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(resetPasteOffsetDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 3, false)));
          group.option(
              SettingKey.of("command.spawnPosition", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.spawnpos"))
                      .description(
                          text("category.map.group.variables.opt.spawnpos.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(spawnPosDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 5, false)));
          group.option(
              SettingKey.of("command.readyAreaSpawnPosition", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.readyareaspawnpos"))
                      .description(
                          text("category.map.group.variables.opt.readyareaspawnpos.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(readyAreaSpawnDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 5, false)));
          group.option(
              SettingKey.of("command.spectatorSpawnPosition", String.class),
              option ->
                  option
                      .name(text("category.map.group.variables.opt.spectatorspawnpos"))
                      .description(
                          text("category.map.group.variables.opt.spectatorspawnpos.desc")
                              .styled(s -> s.withColor(0x757575)))
                      .defaultValue(spectatorSpawnDefault)
                      .controller(Controllers.string())
                      .validator(value -> coordinates(value, 5, false)));
        });
    builder.group("map.rtpSlots", MapCategory::buildRtpSlotsGroup);
  }

  private static void mapTimeOption(GroupBuilder group, String key) {
    group.option(
        SettingKey.of(
            "watheextended.map." + (key.equals("lobbytime") ? "lobbyTime" : "gameTime"),
            String.class),
        option ->
            option
                .name(text("category.map.group.map_effect.opt." + key))
                .description(text("category.map.group.map_effect.opt." + key + ".desc"))
                .defaultValue(key.equals("lobbytime") ? "DAY" : "NIGHT")
                .controller(
                    Controllers.cycling(
                        java.util.List.of("DAY", "NIGHT", "SUNDOWN"),
                        value ->
                            text(
                                "category.map.group.map_effect.opt."
                                    + key
                                    + "."
                                    + value.toLowerCase(java.util.Locale.ROOT))))
                .formatter(value ->
                    text(
                        "category.map.group.map_effect.opt."
                            + key
                            + "."
                            + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(value -> choice(value, java.util.List.of("DAY", "NIGHT", "SUNDOWN"))));
  }

  private static void buildRtpSlotsGroup(GroupBuilder group) {
    group
        .name(text("category.map.group.rtp_slots"))
        .description(text("category.map.group.rtp_slots.tooltip"))
        .collapsed(true);

    World world = MinecraftClient.getInstance().world;

    Map<Integer, TeleportationSlot> slots;
    try {
      WatheExtendedWorldComponent component =
          world != null ? WatheExtendedWorldComponent.KEY.get(world) : null;
      slots =
          component != null
              ? new LinkedHashMap<>(component.getTeleportationSlots())
              : new LinkedHashMap<>();
    } catch (Throwable t) {
      slots = new LinkedHashMap<>();
    }

    if (slots.isEmpty()) {
      group.entry(
          new EntryDefinition.FullWidthLabel(
              "category.map.group.rtp_slots.none",
              text("category.map.group.rtp_slots.none").styled(style -> style.withColor(0xFF5555)),
              Text.empty(),
              ctx -> true));
    } else {
      for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
        final int slotId = entry.getKey();
        final String slotDefault = entry.getValue().toCommandArgs();

        group.option(
            SettingKey.of("command.rtp." + slotId, String.class),
            option ->
                option
                    .name(text("category.map.group.rtp_slots.slot", slotId))
                    .description(
                        text("category.map.group.rtp_slots.slot.desc")
                            .styled(style -> style.withColor(0x757575)))
                    .defaultValue(slotDefault)
                    .controller(Controllers.string())
                    .validator(value -> coordinates(value, 5, true)));
      }
    }
  }

  private static Validation<String> coordinates(String value, int count, boolean allowEmpty) {
    String trimmed = value.strip().replaceAll("\\s+", " ");
    if (allowEmpty && trimmed.isEmpty()) return Validation.accepted(trimmed);
    String[] parts = trimmed.split(" ");
    if (parts.length != count)
      return Validation.rejected(Text.literal("Expected " + count + " coordinates."));
    try {
      for (String part : parts) {
        if (count == 3) Integer.parseInt(part);
        else if (!Double.isFinite(Double.parseDouble(part))) throw new NumberFormatException();
      }
    } catch (NumberFormatException invalid) {
      return Validation.rejected(Text.literal("Enter valid coordinates."));
    }
    return Validation.accepted(trimmed);
  }
}
