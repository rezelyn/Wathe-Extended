package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.config.ConfigBuilder.GroupBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.model.EntryDefinition;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.watheextended.api.GameComponents;
import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import cat.rezelyn.watheextended.network.ClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public final class GameCategory {

  private GameCategory() {}

  public static void build(CategoryBuilder builder) {
    builder.name(text("category.game")).description(text("category.game.tooltip"));

    builder.group("game.gamerules", GameCategory::gamerulesGroup);
    builder.group("game.watheOptions", GameCategory::watheOptionsGroup);
    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      builder.group("game.extraOptions", GameCategory::extraOptionsGroup);
    }
  }

  private static void gamerulesGroup(GroupBuilder group) {
    group
        .name(text("category.game.group.gamerules"))
        .description(text("category.game.group.gamerules.tooltip"))
        .collapsed(false);

    World client = MinecraftClient.getInstance().world;

    group.option(
        SettingKey.of("watheextended.playerCollisions", Boolean.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.collisions"))
                .description(text("category.game.group.gamerules.opt.collisions.desc"))
                .defaultValue(ClientConfig.getBool("watheextended.playerCollisions", true))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    group.option(
        SettingKey.of("watheextended.suppressAbilityVfxSfx", Boolean.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.suppressabilityvfxsfx"))
                .description(text("category.game.group.gamerules.opt.suppressabilityvfxsfx.desc"))
                .defaultValue(ClientConfig.getBool("watheextended.suppressAbilityVfxSfx", false))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    if (ConfigHelper.isLoaded()) {
      group.option(
          SettingKey.of("noellesroles.insanePlayersSeeMorphs", Boolean.class),
          option ->
              option
                  .name(text("category.game.group.gamerules.opt.morphpsychosis"))
                  .description(text("category.game.group.gamerules.opt.morphpsychosis.desc"))
                  .defaultValue(false)
                  .controller(Controllers.bool(true))
                  .formatter(ConfigText::onOff));
    }

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.game.group.gamerules.label.laststand",
            text("category.game.group.gamerules.label.laststand")
                .styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("watheextended.lastStand.enabled", Boolean.class),
        option ->
            option
                .name(text("text.enabled"))
                .description(text("category.game.group.gamerules.opt.laststand.desc"))
                .defaultValue(ClientConfig.getBool("watheextended.lastStand.enabled", false))
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));

    group.option(
        SettingKey.of("watheextended.lastStand.cooldown", Integer.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.laststandcooldown"))
                .description(text("category.game.group.gamerules.opt.laststandcooldown.desc"))
                .defaultValue(30)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.game.label.instinct",
            text("category.client.label.instinct").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("watheextended.instinct.capacity", Float.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.instinctcapacity"))
                .description(text("category.game.group.gamerules.opt.instinctcapacity.desc"))
                .defaultValue(60.0f)
                .controller(Controllers.numberSlider(Float.class, 0.0f, 60.0f, 1.0f, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "%.0fs", value)))
                .validator(value -> range(value, 0.0f, 60.0f)));
    group.option(
        SettingKey.of("watheextended.instinct.drainRate", Float.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.instinctdrainrate"))
                .description(text("category.game.group.gamerules.opt.instinctdrainrate.desc"))
                .defaultValue(3.0f)
                .controller(Controllers.numberSlider(Float.class, 0.0f, 10.0f, 0.1f, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "-%.1fs/s", value)))
                .validator(value -> range(value, 0.0f, 10.0f)));
    group.option(
        SettingKey.of("watheextended.instinct.reloadRate", Float.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.instinctreloadrate"))
                .description(text("category.game.group.gamerules.opt.instinctreloadrate.desc"))
                .defaultValue(1.0f)
                .controller(Controllers.numberSlider(Float.class, 0.0f, 10.0f, 0.1f, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "+%.1fs/s", value)))
                .validator(value -> range(value, 0.0f, 10.0f)));

    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      final boolean startSafeTimeDefault =
          cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.getEnableStartSafeTime(
              client);
      group.entry(
          new EntryDefinition.FullWidthLabel(
              "category.game.group.gamerules.label.safepreptime",
              text("category.game.group.gamerules.label.safepreptime")
                  .styled(style -> style.withColor(0xAAAAAA)),
              Text.empty(),
              ctx -> true));
      group.option(
          SettingKey.of("kinswathe.EnableStartSafeTime", Boolean.class),
          option ->
              option
                  .name(text("text.enabled"))
                  .description(text("category.game.group.gamerules.opt.safepreptime.desc"))
                  .defaultValue(startSafeTimeDefault)
                  .controller(Controllers.bool(true))
                  .formatter(ConfigText::onOff));

      group.option(
          SettingKey.of("kinswathe.StartingCooldown", Integer.class),
          option ->
              option
                  .name(text("category.game.group.gamerules.opt.safeprepcooldown"))
                  .description(text("category.game.group.gamerules.opt.safeprepcooldown.desc"))
                  .defaultValue(30)
                  .controller(Controllers.numberSlider(Integer.class, 0, 60, 1, false))
                  .formatter(
                      value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value)))
                  .validator(value -> range(value, 0, 60)));
    }
  }

  private static void watheOptionsGroup(GroupBuilder group) {
    group
        .name(text("category.game.group.wathe_options"))
        .description(text("category.game.group.wathe_options.tooltip"))
        .collapsed(false);

    World world = MinecraftClient.getInstance().world;

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.game.group.wathe_options.label.global",
            text("category.game.group.wathe_options.label.global")
                .styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("watheextended.killIncreaseTime", Integer.class),
        option ->
            option
                .name(text("category.game.group.gamerules.opt.killincreasetime"))
                .description(text("category.game.group.gamerules.opt.killincreasetime.desc"))
                .defaultValue(60)
                .controller(Controllers.numberSlider(Integer.class, 0, 60, 1, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value)))
                .validator(value -> range(value, 0, 60)));

    group.option(
        SettingKey.of("watheextended.shootInnocentPunishmentMode", String.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.shooterpunishment"))
                .description(text("category.game.group.wathe_options.opt.shooterpunishment.desc"))
                .defaultValue("DEFAULT")
                .controller(
                    Controllers.cycling(
                        java.util.List.of("DEFAULT", "PREVENT_PICKUP", "KILL_SHOOTER", "KILL_BOTH"),
                        value ->
                            text(
                                "category.game.group.wathe_options.opt.shooterpunishment."
                                    + value.toLowerCase(java.util.Locale.ROOT))))
                .formatter(value ->
                    text(
                        "category.game.group.wathe_options.opt.shooterpunishment."
                            + value.toLowerCase(java.util.Locale.ROOT)))
                .validator(value ->
                    choice(
                        value,
                        java.util.List.of(
                            "DEFAULT", "PREVENT_PICKUP", "KILL_SHOOTER", "KILL_BOTH"))));

    group.option(
        SettingKey.of("command.backfire", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.backfire"))
                .description(text("category.game.group.wathe_options.opt.backfire.desc"))
                .defaultValue(GameComponents.getBackfire(world))
                .controller(Controllers.numberSlider(Integer.class, 0, 100, 1, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "%d%%", value)))
                .validator(value -> range(value, 0, 100)));

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.game.group.wathe_options.label.mood",
            text("category.game.group.wathe_options.label.mood")
                .styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("watheextended.mood.depressedPreventSprintEnabled", Boolean.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.preventsprint"))
                .description(text("category.game.group.wathe_options.opt.preventsprint.desc"))
                .defaultValue(true)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    group.option(
        SettingKey.of("watheextended.mood.depressedAbilityDisableEnabled", Boolean.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.disableability"))
                .description(text("category.game.group.wathe_options.opt.disableability.desc"))
                .defaultValue(true)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    group.option(
        SettingKey.of("watheextended.mood.depressedAbilityDisableTime", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.disableabilitytime"))
                .description(text("category.game.group.wathe_options.opt.disableabilitytime.desc"))
                .defaultValue(0)
                .controller(Controllers.numberSlider(Integer.class, 0, 30, 1, false))
                .formatter(
                    value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value)))
                .validator(value -> range(value, 0, 30))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(
                            SettingKey.of(
                                "watheextended.mood.depressedAbilityDisableEnabled",
                                Boolean.class)))));

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.roles",
            text("category.roles").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("command.killerDividend", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.roledividend_killer"))
                .description(text("category.game.group.wathe_options.opt.roledividend_killer.desc"))
                .defaultValue(GameComponents.getKillerDividend(world))
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
    group.option(
        SettingKey.of("command.vigilanteDividend", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.roledividend_vigilante"))
                .description(
                    text("category.game.group.wathe_options.opt.roledividend_vigilante.desc"))
                .defaultValue(GameComponents.getVigilanteDividend(world))
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.modifiers",
            text("category.modifiers").styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));
    group.option(
        SettingKey.of("hml.modifierMaximum", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.modifiers_maximum"))
                .description(text("category.game.group.wathe_options.opt.modifiers_maximum.desc"))
                .defaultValue(1)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
    group.option(
        SettingKey.of("hml.modifierMultiplier", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.modifiers_multiplier"))
                .description(
                    text("category.game.group.wathe_options.opt.modifiers_multiplier.desc"))
                .defaultValue(1)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

    group.entry(
        new EntryDefinition.FullWidthLabel(
            "category.game.group.gamerules.label.economy",
            text("category.game.group.gamerules.label.economy")
                .styled(style -> style.withColor(0xAAAAAA)),
            Text.empty(),
            ctx -> true));

    group.option(
        SettingKey.of("watheextended.balance.basePassiveIncome", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.base_passive_income"))
                .description(text("category.game.group.wathe_options.opt.base_passive_income.desc"))
                .defaultValue(5)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
    group.option(
        SettingKey.of("watheextended.balance.adjustPassiveIncome", Boolean.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.adjust_passive_income"))
                .description(
                    text("category.game.group.wathe_options.opt.adjust_passive_income.desc"))
                .defaultValue(false)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff));
    group.option(
        SettingKey.of("watheextended.balance.maxPassiveIncomeDistance", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.max_passive_income_distance"))
                .description(
                    text("category.game.group.wathe_options.opt.max_passive_income_distance.desc"))
                .defaultValue(10)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(
                            SettingKey.of(
                                "watheextended.balance.adjustPassiveIncome", Boolean.class)))));
    group.option(
        SettingKey.of("watheextended.balance.minPassiveIncome", Integer.class),
        option ->
            option
                .name(text("category.game.group.wathe_options.opt.min_passive_income"))
                .description(text("category.game.group.wathe_options.opt.min_passive_income.desc"))
                .defaultValue(0)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(
                            SettingKey.of(
                                "watheextended.balance.adjustPassiveIncome", Boolean.class)))));
  }

  private static void extraOptionsGroup(GroupBuilder group) {
    group
        .name(text("category.game.group.extra_options"))
        .description(text("category.game.group.extra_options.tooltip"))
        .collapsed(false);

    World world = MinecraftClient.getInstance().world;

    group.option(
        SettingKey.of("kinswathe.EnableWatheModify", Boolean.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.wathetweaks"))
                .description(text("category.game.group.extra_options.opt.wathetweaks.desc"))
                .defaultValue(false)
                .controller(Controllers.tickBox()));

    group.option(
        SettingKey.of("kinswathe.InitialCivilianIncome", Integer.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.initialcivilianincome"))
                .description(
                    text("category.game.group.extra_options.opt.initialcivilianincome.desc"))
                .defaultValue(0)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(SettingKey.of("kinswathe.EnableWatheModify", Boolean.class)))));

    group.option(
        SettingKey.of("kinswathe.InitialNeutralIncome", Integer.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.initialnnetralincome"))
                .description(
                    text("category.game.group.extra_options.opt.initialnnetralincome.desc"))
                .defaultValue(0)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(SettingKey.of("kinswathe.EnableWatheModify", Boolean.class)))));

    group.option(
        SettingKey.of("kinswathe.InitialKillerIncome", Integer.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.initialkillerncome"))
                .description(text("category.game.group.extra_options.opt.initialkillerncome.desc"))
                .defaultValue(100)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(SettingKey.of("kinswathe.EnableWatheModify", Boolean.class)))));

    group.option(
        SettingKey.of("kinswathe.IncreaseMoneyWhenKill", Integer.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.increasemoneywhenkilll"))
                .description(
                    text("category.game.group.extra_options.opt.increasemoneywhenkilll.desc"))
                .defaultValue(100)
                .controller(
                    Controllers.numberField(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(SettingKey.of("kinswathe.EnableWatheModify", Boolean.class)))));

    group.option(
        SettingKey.of("kinswathe.EnableNoellesRolesModify", Boolean.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.noellestweaks"))
                .description(text("category.game.group.extra_options.opt.noellestweaks.desc"))
                .defaultValue(false)
                .controller(Controllers.tickBox()));

    group.option(
        SettingKey.of("kinswathe.ConductorInstinctModify", Boolean.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.conductorinstinct"))
                .description(text("category.game.group.extra_options.opt.conductorinstinct.desc"))
                .defaultValue(false)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff)
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(
                            SettingKey.of("kinswathe.EnableNoellesRolesModify", Boolean.class)))));

    group.option(
        SettingKey.of("kinswathe.CoronerInstinctModify", Boolean.class),
        option ->
            option
                .name(text("category.game.group.extra_options.opt.coronerinstinct"))
                .description(text("category.game.group.extra_options.opt.coronerinstinct.desc"))
                .defaultValue(false)
                .controller(Controllers.bool(true))
                .formatter(ConfigText::onOff)
                .availability(ctx ->
                    Boolean.TRUE.equals(
                        ctx.get(
                            SettingKey.of("kinswathe.EnableNoellesRolesModify", Boolean.class)))));
  }
}
