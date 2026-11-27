package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.config.ConfigBuilder.GroupBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.model.EntryDefinition;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.watheextended.api.RolesDisplay;
import cat.rezelyn.watheextended.api.RolesId;
import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.network.ClientConfig;
import java.util.*;
import net.minecraft.text.Text;

public final class RolesCategory {

  private RolesCategory() {}

  public static void build(CategoryBuilder builder, Set<String> denylist) {
    builder.name(text("category.roles")).description(text("category.roles.tooltip"));

    try {
      Set<String> roleId = new LinkedHashSet<>();
      for (String ID : RolesId.get()) {
        if (!ScreenUtils.isDenied(ID, denylist)) roleId.add(ID);
      }

      if (roleId.isEmpty()) {
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "category.roles.empty",
                text("category.roles.empty").styled(style -> style.withColor(0xFF5555)),
                Text.empty(),
                ctx -> true));
      } else {
        Map<String, RolesDisplay.RoleDisplay> roleName = RolesDisplay.get();

        List<String> roles = new ArrayList<>(roleId);
        roles.sort(
            Comparator.comparingInt(id -> {
              RolesDisplay.RoleDisplay display = roleName.get(id);
              return display != null
                  ? display.side().ordinal()
                  : RolesDisplay.Side.NEUTRAL.ordinal();
            }));

        for (String id : roles) {
          RolesDisplay.RoleDisplay display = roleName.get(id);

          var enabledKey = SettingKey.of("role.enabled." + id, Boolean.class);
          boolean state = !ClientConfig.getStringList("hml.disabled").contains(id);
          float chance = ClientConfig.getFloat("watheextended.roleChance." + id, 1.0f);

          Text label =
              (display != null
                  ? display.display().copy().styled(style -> style.withColor(display.color()))
                  : Text.literal(RolesDisplay.prettyName(id)));

          // Roles
          builder.group(
              "roles." + id,
              group -> {
                group.name(label).description(Text.empty()).collapsed(true);

                /// ROLE STATE
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

                /// ROLE CHANCE
                group.option(
                    SettingKey.of("watheextended.roleChance." + id, Float.class),
                    option ->
                        option
                            .name(text("text.chance"))
                            .description(text("category.roles.chance.desc"))
                            .availability(ctx -> Boolean.TRUE.equals(ctx.get(enabledKey)))
                            .defaultValue(chance)
                            .controller(
                                Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.01f, false))
                            .formatter(ConfigText::percent)
                            .validator(value -> range(value, 0.0f, 1.0f)));

                /// ROLE OPTIONS
                roleOptions(id, group);
              });
        }
      }
    } catch (Throwable throwable) {
      builder.entry(
          new EntryDefinition.FullWidthLabel(
              "category.roles.error",
              text("category.roles.error").styled(style -> style.withColor(0xFF5555)),
              Text.empty(),
              ctx -> true));
    }
  }

  private static void roleOptions(String id, GroupBuilder builder) {
    switch (id) {
      // Bellringer
      case "kinswathe:bellringer" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        // Ability
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:bellringer.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));

        /// ABILITY PRICE
        builder.option(
            SettingKey.of("kinswathe.BellringerAbilityPrice", Integer.class),
            option ->
                option
                    .name(text("text.price"))
                    .description(text("category.roles.opt.bellringer.price.desc"))
                    .defaultValue(200)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// ABILITY COOLDOWN
        builder.option(
            SettingKey.of("kinswathe.BellringerAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.bellringer.cooldown.desc"))
                    .defaultValue(120)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }

      // Bodymaker
      case "kinswathe:bodymaker" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        // Ability
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:bodymaker.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));

        /// ABILITY COOLDOWN
        builder.option(
            SettingKey.of("kinswathe.BodymakerAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.bodymaker.cooldown.desc"))
                    .defaultValue(90)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// CAN CREATE FAKE ROLES
        builder.option(
            SettingKey.of("kinswathe.BodymakerAbilityFakeRole", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.bodymaker.fakerole"))
                    .description(text("category.roles.opt.bodymaker.fakerole.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }

      // Cleaner
      case "kinswathe:cleaner" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        /// BARREL COIN BONUS
        builder.option(
            SettingKey.of("watheextended.cleaner.acidBarrelCoinBonusEnabled", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.cleaner.acidbarrelbonusenabled"))
                    .description(text("category.roles.opt.cleaner.acidbarrelbonusenabled.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));

        /// COIN BONUS AMOUNT
        builder.option(
            SettingKey.of("watheextended.cleaner.acidBarrelCoins", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.cleaner.acidbarrelcoins"))
                    .description(text("category.roles.opt.cleaner.acidbarrelcoins.desc"))
                    .defaultValue(50)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                    .availability(ctx ->
                        Boolean.TRUE.equals(
                            ctx.get(
                                SettingKey.of(
                                    "watheextended.cleaner.acidBarrelCoinBonusEnabled",
                                    Boolean.class)))));

        // Ability
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:cleaner.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));

        /// ABILITY PRICE
        builder.option(
            SettingKey.of("kinswathe.CleanerAbilityPrice", Integer.class),
            option ->
                option
                    .name(text("text.price"))
                    .description(text("category.roles.opt.cleaner.price.desc"))
                    .defaultValue(200)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// ABILITY COOLDOWN
        builder.option(
            SettingKey.of("kinswathe.CleanerAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.cleaner.cooldown.desc"))
                    .defaultValue(150)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// ABILITY PLAYER LIMIT
        builder.option(
            SettingKey.of("watheextended.cleaner.playerLimit", Integer.class),
            option ->
                option
                    .name(text("text.playerlimit"))
                    .description(text("category.roles.opt.cleaner.playerlimit.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }

      // Detective
      case "kinswathe:detective" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        // Ability
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:detective.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));

        /// ABILITY PRICE
        builder.option(
            SettingKey.of("kinswathe.DetectiveAbilityPrice", Integer.class),
            option ->
                option
                    .name(text("text.price"))
                    .description(text("category.roles.opt.detective.price.desc"))
                    .defaultValue(200)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// ABILITY COOLDOWN
        builder.option(
            SettingKey.of("kinswathe.DetectiveAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.detective.cooldown.desc"))
                    .defaultValue(90)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }

      // Dreamer
      case "kinswathe:dreamer" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        /// DREAM IMPRINT ITEM COUNT
        builder.option(
            SettingKey.of("kinswathe.DreamerInitialItemQuantity", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.dreamer.initialquantity"))
                    .description(text("category.roles.opt.dreamer.initialquantity.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// IMPRINT NOTIFICATION
        builder.option(
            SettingKey.of("watheextended.dreamer.imprintNotificationEnabled", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.dreamer.imprintnotification"))
                    .description(text("category.roles.opt.dreamer.imprintnotification.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }

      // Drugmaker
      case "kinswathe:drugmaker" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;

        /// PLAYER LIMIT
        builder.option(
            SettingKey.of("kinswathe.DrugmakerPlayerLimit", Integer.class),
            option ->
                option
                    .name(text("text.playerlimit"))
                    .description(text("category.roles.opt.drugmaker.playerlimit.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));

        /// HAS PASSIVE INCOME
        builder.option(
            SettingKey.of("kinswathe.DrugmakerGetCoins", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.drugmaker.killincome"))
                    .description(text("category.roles.opt.drugmaker.killincome.desc"))
                    .defaultValue(50)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Hacker
      case "kinswathe:hacker" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("kinswathe.HackerPlayerLimit", Integer.class),
            option ->
                option
                    .name(text("text.playerlimit"))
                    .description(text("category.roles.opt.hacker.playerlimit.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.HackerGenerateWithMimic", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.hacker.generatewithmimic"))
                    .description(text("category.roles.opt.hacker.generatewithmimic.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true)));
        builder.option(
            SettingKey.of("kinswathe.HackerHackingTime", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.hacker.hackingtime"))
                    .description(text("category.roles.opt.hacker.hackingtime.desc"))
                    .defaultValue(30)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.HackerHasShop", Boolean.class),
            option ->
                option
                    .name(text("text.hasshop"))
                    .description(text("category.roles.opt.hacker.hasshop.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Hunter
      case "kinswathe:hunter" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:hunter.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("kinswathe.HunterAbilityPrice", Integer.class),
            option ->
                option
                    .name(text("text.price"))
                    .description(text("category.roles.opt.hunter.price.desc"))
                    .defaultValue(125)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.HunterAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.hunter.cooldown.desc"))
                    .defaultValue(5)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Judge
      case "kinswathe:judge" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:judge.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("kinswathe.JudgeAbilityPrice", Integer.class),
            option ->
                option
                    .name(text("text.price"))
                    .description(text("category.roles.opt.judge.price.desc"))
                    .defaultValue(300)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.JudgeAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.judge.cooldown.desc"))
                    .defaultValue(180)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.JudgeAbilityGlowing", Integer.class),
            option ->
                option
                    .name(text("text.duration"))
                    .description(text("category.roles.opt.judge.duration.desc"))
                    .defaultValue(90)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Licensed Villian
      case "kinswathe:licensed_villain" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("kinswathe.LicensedVillainPlayerLimit", Integer.class),
            option ->
                option
                    .name(text("text.playerlimit"))
                    .description(text("category.roles.opt.licensedvillain.playerlimit.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Physician
      case "kinswathe:physician" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("kinswathe.DisablePhysicianDeathReason", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.physician.disabledeathreason"))
                    .description(text("category.roles.opt.physician.disabledeathreason.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("watheextended.medicalKit.visiblePlayerCount", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.physician.medicalkitcountvisible"))
                    .description(text("category.roles.opt.physician.medicalkitcountvisible.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Robot
      case "kinswathe:robot" -> {
        if (!cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) return;
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.kinswathe:robot.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("kinswathe.RobotAbilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.robot.cooldown.desc"))
                    .defaultValue(90)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("kinswathe.RobotAbilityDuration", Integer.class),
            option ->
                option
                    .name(text("text.duration"))
                    .description(text("category.roles.opt.robot.duration.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Voodoo
      case "noellesroles:voodoo" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.voodooShotLikeEvil", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.voodoo.shotlikeevil"))
                    .description(text("category.roles.opt.voodoo.shotlikeevil.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.noellesroles:voodoo.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("noellesroles.voodooNonKillerDeaths", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.voodoo.nonkillerdeaths"))
                    .description(text("category.roles.opt.voodoo.nonkillerdeaths.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Morphling
      case "noellesroles:morphling" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.alias(
            "roles.noellesroles.insanePlayersSeeMorphs",
            SettingKey.of("noellesroles.insanePlayersSeeMorphs", Boolean.class),
            text("category.roles.opt.morphling.morphpsychosis"),
            text("category.roles.opt.morphling.morphpsychosis.desc"));
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.noellesroles:morphling.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("watheextended.morphling.canCancelAbility", Boolean.class),
            option ->
                option
                    .name(text("text.cancancelability"))
                    .description(text("category.roles.opt.morphling.cancancelability.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("watheextended.morphling.abilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.morphling.cooldown.desc"))
                    .defaultValue(60)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("watheextended.morphling.abilityDuration", Integer.class),
            option ->
                option
                    .name(text("text.duration"))
                    .description(text("category.roles.opt.morphling.duration.desc"))
                    .defaultValue(35)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Phantom
      case "noellesroles:phantom" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.noellesroles:phantom.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("watheextended.phantom.canCancelAbility", Boolean.class),
            option ->
                option
                    .name(text("text.cancancelability"))
                    .description(text("category.roles.opt.phantom.cancancelability.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("watheextended.phantom.abilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.phantom.cooldown.desc"))
                    .defaultValue(60)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("watheextended.phantom.abilityDuration", Integer.class),
            option ->
                option
                    .name(text("text.duration"))
                    .description(text("category.roles.opt.phantom.duration.desc"))
                    .defaultValue(30)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Conductor
      case "noellesroles:conductor" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.playerCountToMakeConducterKeyVisible", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.conductor.keycountvisible"))
                    .description(text("category.roles.opt.conductor.keycountvisible.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Bartender
      case "noellesroles:bartender" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.maximumDefenseVials", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.bartender.maxdefensevials"))
                    .description(text("category.roles.opt.bartender.maxdefensevials.desc"))
                    .defaultValue(1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.alias(
            "roles.noellesroles.defenseVialPrice",
            SettingKey.of("noellesroles.defenseVialPrice", Integer.class),
            text("category.roles.opt.bartender.defensevialprice"),
            text("category.roles.opt.bartender.defensevialprice.desc"));
        builder.option(
            SettingKey.of("noellesroles.defenseMaximumTime", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.bartender.defensemaximumtime"))
                    .description(text("category.roles.opt.bartender.defensemaximumtime.desc"))
                    .defaultValue(-1)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
      }
      // Trapper
      case "noellesroles:trapper" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.trapperSeesNames", Boolean.class),
            option ->
                option
                    .name(text("text.seesnames"))
                    .description(text("category.roles.opt.trapper.seesnames.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.alias(
            "roles.noellesroles.roleMinePrice",
            SettingKey.of("noellesroles.roleMinePrice", Integer.class),
            text("category.roles.opt.trapper.mineprice"),
            text("category.roles.opt.trapper.mineprice.desc"));
      }
      // Guesser
      case "noellesroles:guesser" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.guesserCanUseInstinct", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.guesser.canuseinstinct"))
                    .description(text("category.roles.opt.guesser.canuseinstinct.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Infected
      case "noellesroles:infected" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.infectedKillTime", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.infected.killtime"))
                    .description(text("category.roles.opt.infected.killtime.desc"))
                    .defaultValue(1100)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("noellesroles.infectedCoughChance", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.infected.coughchance"))
                    .description(text("category.roles.opt.infected.coughchance.desc"))
                    .defaultValue(5)
                    .controller(Controllers.numberSlider(Integer.class, 0, 100, 1, false))
                    .formatter(value -> Text.literal(value + "%"))
                    .validator(value -> range(value, 0, 100)));
      }
      // Recon
      case "noellesroles:recon" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.reconsSeeNames", Boolean.class),
            option ->
                option
                    .name(text("text.seesnames"))
                    .description(text("category.roles.opt.recon.seesnames.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Executioner
      case "noellesroles:executioner" -> {
        if (!cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("noellesroles.executionCanPickUpGun", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.executioner.canpickupgun"))
                    .description(text("category.roles.opt.executioner.canpickupgun.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Necromancer
      case "stupid_express:necromancer" -> {
        if (!ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("stupidexpress.necromancerHasShop", Boolean.class),
            option ->
                option
                    .name(text("text.hasshop"))
                    .description(text("category.roles.opt.necromancer.hasshop.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("watheextended.necromancer.abilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.necromancer.cooldown.desc"))
                    .defaultValue(180)
                    .controller(Controllers.numberField(Integer.class, 0, Integer.MAX_VALUE / 20)));
      }
      // Arsonist
      case "stupid_express:arsonist" -> {
        if (!ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("watheextended.arsonist.dousedNotificationEnabled", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.arsonist.dousednotification"))
                    .description(text("category.roles.opt.arsonist.dousednotification.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("watheextended.arsonist.dousedNotificationDelay", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.arsonist.dousednotificationdelay"))
                    .description(text("category.roles.opt.arsonist.dousednotificationdelay.desc"))
                    .defaultValue(10)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE))
                    .availability(ctx ->
                        Boolean.TRUE.equals(
                            ctx.get(
                                SettingKey.of(
                                    "watheextended.arsonist.dousedNotificationEnabled",
                                    Boolean.class)))));
        builder.option(
            SettingKey.of("stupidexpress.arsonistKeepsGameGoing", Boolean.class),
            option ->
                option
                    .name(text("text.keepsgamegoing"))
                    .description(text("category.roles.opt.arsonist.keepsgamegoing.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Amnesiac
      case "stupid_express:amnesiac" -> {
        if (!ConfigHelper.isLoaded()) return;
        builder.option(
            SettingKey.of("stupidexpress.amnesiacKeepsGameGoing", Boolean.class),
            option ->
                option
                    .name(text("text.keepsgamegoing"))
                    .description(text("category.roles.opt.amnesiac.keepsgamegoing.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("stupidexpress.bodiesGlowToAmnesiac", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.amnesiac.bodiesglow"))
                    .description(text("category.roles.opt.amnesiac.bodiesglow.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("stupidexpress.amnesiacGlowsDifferently", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.amnesiac.glowsdifferently"))
                    .description(text("category.roles.opt.amnesiac.glowsdifferently.desc"))
                    .defaultValue(false)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
      }
      // Starstruck
      case "starexpress:starstruck" -> {
        if (!cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) return;
        builder.entry(
            new EntryDefinition.FullWidthLabel(
                "roles.starexpress:starstruck.text.ability",
                text("text.ability").styled(style -> style.withColor(0xAAAAAA)),
                Text.empty(),
                ctx -> true));
        builder.option(
            SettingKey.of("starexpress.abilityCooldown", Integer.class),
            option ->
                option
                    .name(text("text.cooldown"))
                    .description(text("category.roles.opt.starstruck.cooldown.desc"))
                    .defaultValue(90)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("starexpress.abilityDuration", Integer.class),
            option ->
                option
                    .name(text("text.duration"))
                    .description(text("category.roles.opt.starstruck.duration.desc"))
                    .defaultValue(15)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("starexpress.taskReducesCooldown", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.starstruck.taskreducescooldown"))
                    .description(text("category.roles.opt.starstruck.taskreducescooldown.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("starexpress.taskCooldownReduction", Integer.class),
            option ->
                option
                    .name(text("category.roles.opt.starstruck.taskcooldownreduction"))
                    .description(text("category.roles.opt.starstruck.taskcooldownreduction.desc"))
                    .defaultValue(5)
                    .controller(
                        Controllers.numberField(
                            Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        builder.option(
            SettingKey.of("starexpress.abilityAffectsMovementSpeed", Boolean.class),
            option ->
                option
                    .name(text("category.roles.opt.starstruck.affectsmovementspeed"))
                    .description(text("category.roles.opt.starstruck.affectsmovementspeed.desc"))
                    .defaultValue(true)
                    .controller(Controllers.bool(true))
                    .formatter(ConfigText::onOff));
        builder.option(
            SettingKey.of("starexpress.abilityWalkSpeed", Float.class),
            option ->
                option
                    .name(text("category.roles.opt.starstruck.walkspeed"))
                    .description(text("category.roles.opt.starstruck.walkspeed.desc"))
                    .defaultValue(0.12f)
                    .controller(
                        Controllers.numberField(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE))
                    .formatter(value -> Text.literal(ScreenUtils.format(value)))
                    .validator(value -> range(value, -Float.MAX_VALUE, Float.MAX_VALUE)));
        builder.option(
            SettingKey.of("starexpress.abilitySprintSpeed", Float.class),
            option ->
                option
                    .name(text("category.roles.opt.starstruck.sprintspeed"))
                    .description(text("category.roles.opt.starstruck.sprintspeed.desc"))
                    .defaultValue(0.15f)
                    .controller(
                        Controllers.numberField(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE))
                    .formatter(value -> Text.literal(ScreenUtils.format(value)))
                    .validator(value -> range(value, -Float.MAX_VALUE, Float.MAX_VALUE)));
      }
    }
  }
}
