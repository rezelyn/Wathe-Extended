package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.api.config.wathe.ConfigHelper.getDisableScreenShake;
import static cat.rezelyn.watheextended.api.config.wathe.ConfigHelper.setDisableScreenShake;
import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.config.ConfigBuilder.GroupBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.model.EntryDefinition;
import cat.rezelyn.orelib.api.setting.ResetPolicy;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.orelib.api.setting.Validation;
import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.render.BoxDebugRenderer;
import cat.rezelyn.watheextended.client.utils.PronounsCache;
import cat.rezelyn.watheextended.compat.sodium.SceneryCompatibility;
import net.minecraft.text.Text;

public final class ClientCategory {

  private ClientCategory() {}

  public static void build(CategoryBuilder builder, boolean isOp) {
    builder.name(text("category.client")).description(text("category.client.tooltip"));

    // Player
    builder.entry(
        new EntryDefinition.FullWidthLabel(
            "category.client.label.player",
            text("category.client.label.player").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    /// PRONOUNS
    builder.option(
        SettingKey.of("player.pronouns", String.class),
        option ->
            option
                .name(text("category.client.opt.pronouns"))
                .description(text("category.client.opt.pronouns.desc"))
                .defaultSupplier(PronounsCache::getLocalPronouns)
                .reset(ResetPolicy.NONE, ctx -> null)
                .controller(Controllers.string())
                .validator(ClientCategory::pronouns));

    // HUD
    builder.entry(
        new EntryDefinition.FullWidthLabel(
            "category.client.label.hud",
            text("category.client.label.hud").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    /// CHAT
    builder.option(
        SettingKey.of("client.showchat", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.showchat"))
                .description(text("category.client.opt.showchat.desc"))
                .binding(
                    true,
                    () -> WatheExtendedClientConfig.showChatDuringGame,
                    value -> WatheExtendedClientConfig.set("showChatDuringGame", value))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    /// STAMINA BAR
    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      builder.option(
          SettingKey.of("client.staminabar", Boolean.class),
          option ->
              option
                  .name(text("category.client.opt.staminabar"))
                  .description(text("category.client.opt.staminabar.desc"))
                  .binding(
                      false,
                      cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper
                          ::getEnableStaminaBar,
                      cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper
                          ::setEnableStaminaBar)
                  .controller(Controllers.bool(true))
                  .formatter(ConfigText::onOff));
    }

    // Instinct
    builder.entry(
        new EntryDefinition.FullWidthLabel(
            "category.client.label.instinct",
            text("category.client.label.instinct").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    /// HUD STYLE
    builder.option(
        SettingKey.of("client.instinct.hudstyle", String.class),
        option ->
            option
                .name(text("category.client.opt.instinct.hudstyle"))
                .description(text("category.client.opt.instinct.hudstyle.desc"))
                .binding(
                    "HALF_LEFT",
                    () -> WatheExtendedClientConfig.instinctHudStyle,
                    value -> WatheExtendedClientConfig.set("instinctHudStyle", value))
                .controller(
                    Controllers.selection(
                        java.util.List.of("FULL", "HALF_LEFT", "HALF_RIGHT"),
                        value ->
                            text(
                                "category.client.opt.instinct.hudstyle."
                                    + value.toLowerCase(java.util.Locale.ROOT)),
                        true))
                .formatter(value ->
                    text(
                        "category.client.opt.instinct.hudstyle."
                            + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(
                    value -> choice(value, java.util.List.of("FULL", "HALF_LEFT", "HALF_RIGHT"))));

    /// HUD OPACITY
    builder.option(
        SettingKey.of("client.instinct.hudopacity", Float.class),
        option ->
            option
                .name(text("category.client.opt.instinct.hudopacity"))
                .description(text("category.client.opt.instinct.hudopacity.desc"))
                .binding(
                    0.25f,
                    () -> WatheExtendedClientConfig.instinctHudOpacity,
                    value -> WatheExtendedClientConfig.set("instinctHudOpacity", value))
                .controller(Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                .formatter(ConfigText::percent)
                .validator(value -> range(value, 0.0f, 1.0f)));

    /// ALWAYS SHOW HUD
    builder.option(
        SettingKey.of("client.instinct.hudalwaysshow", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.instinct.hudalwaysshow"))
                .description(text("category.client.opt.instinct.hudalwaysshow.desc"))
                .binding(
                    false,
                    () -> WatheExtendedClientConfig.alwaysShowInstinctHud,
                    value -> WatheExtendedClientConfig.set("alwaysShowInstinctHud", value))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    // Keybinds
    builder.entry(
        new EntryDefinition.FullWidthLabel(
            "category.client.label.keybinds",
            text("category.client.label.keybinds").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    // INSTINCT MODE
    builder.option(
        SettingKey.of("client.instinctmode", String.class),
        option ->
            option
                .name(text("category.client.opt.instinctmode"))
                .description(text("category.client.opt.instinctmode.desc"))
                .binding(
                    "HOLD",
                    () -> WatheExtendedClientConfig.instinctMode,
                    value -> WatheExtendedClientConfig.set("instinctMode", value))
                .controller(
                    Controllers.cycling(
                        java.util.List.of("HOLD", "TOGGLE"),
                        value -> text("text." + value.toLowerCase(java.util.Locale.ROOT))))
                .formatter(value -> text("text." + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(value -> choice(value, java.util.List.of("HOLD", "TOGGLE"))));

    // PRONE MODE
    builder.option(
        SettingKey.of("client.pronemode", String.class),
        option ->
            option
                .name(text("category.client.opt.pronemode"))
                .description(text("category.client.opt.pronemode.desc"))
                .binding(
                    "TOGGLE",
                    () -> WatheExtendedClientConfig.proneMode,
                    value -> WatheExtendedClientConfig.set("proneMode", value))
                .controller(
                    Controllers.cycling(
                        java.util.List.of("HOLD", "TOGGLE"),
                        value -> text("text." + value.toLowerCase(java.util.Locale.ROOT))))
                .formatter(value -> text("text." + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(value -> choice(value, java.util.List.of("HOLD", "TOGGLE"))));

    // Visual
    builder.entry(
        new EntryDefinition.FullWidthLabel(
            "category.client.label.visual",
            text("category.client.label.visual").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    /// SCREENSHAKE
    builder.option(
        SettingKey.of("client.screenshake", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.screenshake"))
                .description(text("category.client.opt.screenshake.desc"))
                .binding(
                    true,
                    () -> !getDisableScreenShake(),
                    enabled -> setDisableScreenShake(!enabled))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    /// CUSTOM FOG
    builder.option(
        SettingKey.of("client.fog", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.fog"))
                .description(text("category.client.opt.fog.desc"))
                .binding(
                    true,
                    () -> WatheExtendedClientConfig.showFog,
                    value -> WatheExtendedClientConfig.set("showFog", value))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    /// SHOW HUD
    builder.option(
        SettingKey.of("client.hud", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.hud"))
                .description(text("category.client.opt.hud.desc"))
                .binding(
                    true,
                    () -> WatheExtendedClientConfig.showWatheHud,
                    value -> WatheExtendedClientConfig.set("showWatheHud", value))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    /// SNOWFLAKES
    builder.option(
        SettingKey.of("client.snowflakes", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.snowflakes"))
                .description(text("category.client.opt.snowflakes.desc"))
                .binding(
                    true,
                    () -> WatheExtendedClientConfig.showSnowflakes,
                    value -> WatheExtendedClientConfig.set("showSnowflakes", value))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    /// ULTRA PERFORMANCE MODE
    builder.option(
        SettingKey.of("client.ultraperfmode", Boolean.class),
        option ->
            option
                .name(text("category.client.opt.ultraperfmode"))
                .description(text("category.client.opt.ultraperfmode.desc"))
                .binding(
                    false,
                    cat.rezelyn.watheextended.api.config.wathe.ConfigHelper::getUltraPerfMode,
                    cat.rezelyn.watheextended.api.config.wathe.ConfigHelper::setUltraPerfMode)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    if (isOp) {
      builder.group("client.debug", ClientCategory::buildDebugGroup);
    }
  }

  // Debug
  private static void buildDebugGroup(GroupBuilder group) {
    group
        .name(text("category.client.group.debug").styled(style -> style.withColor(0xFF5555)))
        .description(text("category.client.group.debug.tooltip"))
        .collapsed(false)
        .option(
            SettingKey.of("client.showboxboundaries", Boolean.class),
            option ->
                option
                    .name(text("category.client.group.debug.opt.showboxboundaries"))
                    .description(text("category.client.group.debug.opt.showboxboundaries.desc"))
                    .binding(
                        false,
                        () -> BoxDebugRenderer.showBoxBoundaries,
                        value -> BoxDebugRenderer.showBoxBoundaries = value)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff))

        /// SHOW SPAWN POSITIONS
        .option(
            SettingKey.of("client.showspawnpositions", Boolean.class),
            option ->
                option
                    .name(text("category.client.group.debug.opt.showspawnpositions"))
                    .description(text("category.client.group.debug.opt.showspawnpositions.desc"))
                    .binding(
                        false,
                        () -> BoxDebugRenderer.showSpawnPositions,
                        value -> BoxDebugRenderer.showSpawnPositions = value)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff))

        /// SHOW RTP SLOTS
        .option(
            SettingKey.of("client.showrtpslots", Boolean.class),
            option ->
                option
                    .name(text("category.client.group.debug.opt.showrtpslots"))
                    .description(text("category.client.group.debug.opt.showrtpslots.desc"))
                    .binding(
                        false,
                        () -> BoxDebugRenderer.showRtpSlots,
                        value -> BoxDebugRenderer.showRtpSlots = value)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff))

        /// SHOW ROOM KEYS ASSIGEMENTS
        .option(
            SettingKey.of("client.showkeyassignments", Boolean.class),
            option ->
                option
                    .name(text("category.client.group.debug.opt.showkeyassignments"))
                    .description(text("category.client.group.debug.opt.showkeyassignments.desc"))
                    .binding(
                        false,
                        () -> BoxDebugRenderer.showKeyAssignments,
                        value -> BoxDebugRenderer.showKeyAssignments = value)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
  }

  private static Validation<String> pronouns(String value) {
    String trimmed = value.strip();
    return trimmed.length() <= 32
        ? Validation.accepted(trimmed)
        : Validation.rejected(Text.literal("Pronouns must be 32 characters or fewer."));
  }
}
