package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.config.ConfigBuilder.GroupBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.model.EntryDefinition;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.ModifiersId;
import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.network.ClientConfig;
import java.util.Map;
import net.minecraft.text.Text;

public final class ModifiersCategory {

  private ModifiersCategory() {}

  public static void build(CategoryBuilder builder) {
    builder.name(text("category.modifiers")).description(text("category.modifiers.tooltip"));

    try {
      if (ModifiersId.get().isEmpty()) {
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "category.modifiers.empty",
                text("category.modifiers.empty").styled(style -> style.withColor(0xFF5555)),
                Text.empty(),
                ctx -> true));
      } else {
        Map<String, ModifiersDisplay.ModifierDisplay> modifierName = ModifiersDisplay.get();

        for (String id : ModifiersId.get()) {
          ModifiersDisplay.ModifierDisplay display = modifierName.get(id);

          var enabledKey = SettingKey.of("modifier.enabled." + id, Boolean.class);
          boolean state = !ClientConfig.getStringList("hml.disabledModifiers").contains(id);
          float chance = ClientConfig.getFloat("watheextended.modifierChance." + id, 1.0f);

          Text label =
              (display != null
                  ? display.display().copy().styled(style -> style.withColor(display.color()))
                  : Text.literal(ModifiersDisplay.prettyName(id)));

          // Modifiers
          builder.group(
              "modifiers." + id,
              group -> {
                group.name(label).description(Text.empty()).collapsed(true);

                /// MODIFIER STATE
                group.option(
                    enabledKey,
                    option ->
                        option
                            .name(text("text.enabled"))
                            .description(
                                Text.literal(id).styled(style -> style.withColor(0x505050)))
                            .defaultValue(state)
                            .controller(Controllers.bool(true))
                            .formatter(ConfigText::onOff));

                /// MODIFIER CHANCE
                group.option(
                    SettingKey.of("watheextended.modifierChance." + id, Float.class),
                    option ->
                        option
                            .name(text("text.chance"))
                            .description(text("category.modifiers.chance.desc"))
                            .availability(ctx -> Boolean.TRUE.equals(ctx.get(enabledKey)))
                            .defaultValue(chance)
                            .controller(
                                Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                            .formatter(ConfigText::percent)
                            .validator(value -> range(value, 0.0f, 1.0f)));

                /// MODIFIER OPTIONS
                modifierOptions(id, group);
              });
        }
      }
    } catch (Throwable t) {
      builder.entry(
          new EntryDefinition.FullWidthLabel(
              "category.modifiers.error",
              text("category.modifiers.error").styled(style -> style.withColor(0xFF5555)),
              Text.empty(),
              ctx -> true));
    }
  }

  private static void modifierOptions(String id, GroupBuilder builder) {
    switch (id) {
      // Introverted
      case "watheextended:introverted" -> {

        /// CROWD COUNT
        builder.option(
            SettingKey.of("watheextended.introverted.crowdCount", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.introverted.crowdcount"))
                    .description(text("category.modifiers.opt.introverted.crowdcount.desc"))
                    .defaultValue(3)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// CROWD RANGE
        builder.option(
            SettingKey.of("watheextended.introverted.crowdRange", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.introverted.crowdrange"))
                    .description(text("category.modifiers.opt.introverted.crowdrange.desc"))
                    .defaultValue(5.0f)
                    .controller(
                        Controllers.numberField(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE))
                    .formatter(value -> Text.literal(ScreenUtils.format(value)))
                    .validator(value -> range(value, -Float.MAX_VALUE, Float.MAX_VALUE)));

        /// IN CROWD DRAIN MULTIPLIER
        builder.option(
            SettingKey.of("watheextended.introverted.crowdDrainMultiplier", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.introverted.crowddrainmultiplier"))
                    .description(
                        text("category.modifiers.opt.introverted.crowddrainmultiplier.desc"))
                    .defaultValue(2.0f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 2.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 2.0f)));

        /// ALONE DRAIN MULTIPLIER
        builder.option(
            SettingKey.of("watheextended.introverted.aloneDrainMultiplier", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.introverted.alonedrainmultiplier"))
                    .description(
                        text("category.modifiers.opt.introverted.alonedrainmultiplier.desc"))
                    .defaultValue(0.5f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 2.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 2.0f)));
      }

      // Taxed
      case "watheextended:taxed" -> {

        /// INCOME PENALTY
        builder.option(
            SettingKey.of("watheextended.taxed.coinReduction", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.taxed.coinreduction"))
                    .description(text("category.modifiers.opt.taxed.coinreduction.desc"))
                    .defaultValue(0.50f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 1.0f)));

        /// KILL AMOUNT THRESHOLD
        builder.option(
            SettingKey.of("watheextended.taxed.killThreshold", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.taxed.killthreshold"))
                    .description(text("category.modifiers.opt.taxed.killthreshold.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// KILL TIME WINDOW
        builder.option(
            SettingKey.of("watheextended.taxed.killWindowSeconds", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.taxed.killwindowseconds"))
                    .description(text("category.modifiers.opt.taxed.killwindowseconds.desc"))
                    .defaultValue(60)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }

      // Adaptive
      case "watheextended:adaptive" -> {

        /// INCOME REDUCTION
        builder.option(
            SettingKey.of("watheextended.adaptive.penaltyReduction", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.adaptive.penaltyreduction"))
                    .description(text("category.modifiers.opt.adaptive.penaltyreduction.desc"))
                    .defaultValue(0.25f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 1.0f)));

        /// INCOME BONUS
        builder.option(
            SettingKey.of("watheextended.adaptive.bonusMultiplier", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.adaptive.bonusmultiplier"))
                    .description(text("category.modifiers.opt.adaptive.bonusmultiplier.desc"))
                    .defaultValue(0.50f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 1.0f)));
      }

      // Guesser
      case "noellesroles:guesser" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;

        /// ALLOW CIVILIAN GUESSERS
        builder.option(
            SettingKey.of("noellesroles.allowCivillianGuessers", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.guesser.allowcivillian"))
                    .description(text("category.modifiers.opt.guesser.allowcivillian.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// WRONG GUESS MODE
        builder.option(
            SettingKey.of("noellesroles.guesserDiesAfterIncorrectGuess", String.class),
            option ->
                option
                    .name(text("category.modifiers.opt.guesser.wrongguessmode"))
                    .description(text("category.modifiers.opt.guesser.wrongguessmode.desc"))
                    .defaultValue("death")
                    .controller(
                        Controllers.selection(
                            java.util.List.of("none", "death", "explode"),
                            value -> Text.literal(value.toUpperCase()),
                            true))
                    .formatter(value -> Text.literal(value.toUpperCase()))
                    .validator(
                        value -> choice(value, java.util.List.of("none", "death", "explode"))));
      }

      // Lovers
      case "stupid_express:lovers" -> {
        if (!ConfigHelper.isLoaded()) return;

        /// FORBIDDEN LOVERS
        builder.option(
            SettingKey.of("watheextended.forbiddenLovers", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.forbidden"))
                    .description(text("category.modifiers.opt.lovers.forbidden.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// FORBIDDEN LOVERS CHANCE
        builder.option(
            SettingKey.of("watheextended.forbiddenLovers.chance", Float.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.forbidden.chance"))
                    .description(text("category.modifiers.opt.lovers.forbidden.chance.desc"))
                    .defaultValue(0.25f)
                    .controller(Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                    .formatter(ConfigText::percent)
                    .validator(value -> range(value, 0.0f, 1.0f)));

        /// KNOW IMMEDIATELY
        builder.option(
            SettingKey.of("stupidexpress.loversKnowImmediately", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.knowimmediately"))
                    .description(text("category.modifiers.opt.lovers.knowimmediately.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// WIN WITH KILLERS
        builder.option(
            SettingKey.of("stupidexpress.loversWinWithKillers", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.winwithkillers"))
                    .description(text("category.modifiers.opt.lovers.winwithkillers.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// WIN WITH CIVILIANS
        builder.option(
            SettingKey.of("stupidexpress.loversWinWithCivilians", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.winwithcivilians"))
                    .description(text("category.modifiers.opt.lovers.winwithcivilians.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// GLOW TO EACH-OTHER
        builder.option(
            SettingKey.of("stupidexpress.loversGlowToEachother", Boolean.class),
            option ->
                option
                    .name(text("category.modifiers.opt.lovers.glowtoeachother"))
                    .description(text("category.modifiers.opt.lovers.glowtoeachother.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }

      // Allergic
      case "starexpress:allergic" -> {
        if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) return;

        /// NOTHING CHANCE
        builder.option(
            SettingKey.of("starexpress.nothingChance", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.allergic.nothingchance"))
                    .description(text("category.modifiers.opt.allergic.nothingchance.desc"))
                    .defaultValue(3)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// INSTINCT CHANCE
        builder.option(
            SettingKey.of("starexpress.instinctChance", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.allergic.instinctchance"))
                    .description(text("category.modifiers.opt.allergic.instinctchance.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// DEFENSE SHIELD CHANCE
        builder.option(
            SettingKey.of("starexpress.armorChance", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.allergic.armorchance"))
                    .description(text("category.modifiers.opt.allergic.armorchance.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// POISON CHANCE
        builder.option(
            SettingKey.of("starexpress.poisonChance", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.allergic.poisonchance"))
                    .description(text("category.modifiers.opt.allergic.poisonchance.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// INSTINCT DURATION
        builder.option(
            SettingKey.of("starexpress.instinctDuration", Integer.class),
            option ->
                option
                    .name(text("category.modifiers.opt.allergic.instinctduration"))
                    .description(text("category.modifiers.opt.allergic.instinctduration.desc"))
                    .defaultValue(3)
                    .controller(Controllers.numberSlider(Integer.class, 0, 10, 1, false))
                    .formatter(
                        value -> Text.literal(String.format(java.util.Locale.ROOT, "%ds", value)))
                    .validator(value -> range(value, 0, 10)));
      }
    }
  }
}
