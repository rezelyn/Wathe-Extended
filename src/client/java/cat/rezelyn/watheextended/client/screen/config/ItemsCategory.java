package cat.rezelyn.watheextended.client.screen.config;

import static cat.rezelyn.watheextended.client.screen.config.ConfigText.*;

import cat.rezelyn.orelib.api.config.ConfigBuilder.CategoryBuilder;
import cat.rezelyn.orelib.api.controller.Controllers;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ItemsCategory {

  private ItemsCategory() {}

  public static void build(CategoryBuilder builder) {
    builder.name(text("category.items")).description(text("category.items.tooltip"));

    // Roleplay Items
    if (FabricLoader.getInstance().isModLoaded("watheextraitems")) {
      builder.group(
          "items.roleplayItems",
          roleplayItems -> {
            roleplayItems
                .name(withIcon("gui.watheextended.config.category.items.group.roleplay_items"))
                .description(text("category.items.group.roleplay_items.tooltip"));
            WatheExtendedServerConfig.ROLEPLAY_ITEM_DEFAULTS.forEach((id, def) -> {
              String key = "watheextended.roleplayItems." + id;
              roleplayItems.option(
                  SettingKey.of(key, Boolean.class),
                  option ->
                      option
                          .name(Text.translatable("item.watheextraitems." + id))
                          .description(
                              Text.translatable(
                                  def
                                      ? "gui.watheextended.config.category.items.group.roleplay_items.opt.item.desc.on"
                                      : "gui.watheextended.config.category.items.group.roleplay_items.opt.item.desc.off"))
                          .defaultValue(def)
                          .controller(Controllers.bool(true))
                          .formatter(ConfigText::onOff));
            });
            roleplayItems.collapsed(true);
          });
    }

    // Knife
    builder.group(
        "items.knife",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.knife"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.knife.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.knife.price"))
                            .defaultValue(100)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.knife.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.knife.cooldown"))
                            .defaultValue(60)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Revolver
    builder.group(
        "items.revolver",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.revolver"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.revolver.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.revolver.price"))
                            .defaultValue(300)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.revolver.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.revolver.cooldown"))
                            .defaultValue(10)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .alias(
                    "items.command.backfire",
                    SettingKey.of("command.backfire", Integer.class),
                    text("category.game.group.wathe_options.opt.backfire"),
                    text("category.game.group.wathe_options.opt.backfire.desc"))
                .collapsed(true));

    // Grenade
    builder.group(
        "items.grenade",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.grenade"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.grenade.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.grenade.price"))
                            .defaultValue(350)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.grenade.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.grenade.cooldown"))
                            .defaultValue(90)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Psycho Mode
    builder.group(
        "items.psychomode",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.psychomode"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.psychoMode.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.psychomode.price"))
                            .defaultValue(300)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.psychoMode.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.psychomode.cooldown"))
                            .defaultValue(300)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Poison Vial
    builder.group(
        "items.poison_vial",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.poison_vial"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.poisonVial.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.poison_vial.price"))
                            .defaultValue(100)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Scorpion
    builder.group(
        "items.scorpion",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.scorpion"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.scorpion.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.scorpion.price"))
                            .defaultValue(50)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Firecracker
    builder.group(
        "items.firecracker",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.firecracker"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.firecracker.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.firecracker.price"))
                            .defaultValue(10)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Note
    builder.group(
        "items.note",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.note"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.note.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.note.price"))
                            .defaultValue(10)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Lockpick
    builder.group(
        "items.lockpick",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.lockpick"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.lockpick.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.lockpick.price"))
                            .defaultValue(50)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.lockpick.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.lockpick.cooldown"))
                            .defaultValue(180)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Crowbar
    builder.group(
        "items.crowbar",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.crowbar"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.crowbar.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.crowbar.price"))
                            .defaultValue(25)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.crowbar.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.crowbar.cooldown"))
                            .defaultValue(10)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Body Bag
    builder.group(
        "items.bodybag",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.bodybag"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.bodyBag.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.bodybag.price"))
                            .defaultValue(200)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.bodyBag.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.bodybag.cooldown"))
                            .defaultValue(300)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    // Blackout
    builder.group(
        "items.blackout",
        group ->
            group
                .name(withIcon("gui.watheextended.config.category.items.group.blackout"))
                .description(Text.empty())
                .option(
                    SettingKey.of("watheextended.blackout.price", Integer.class),
                    option ->
                        option
                            .name(text("text.price"))
                            .description(text("category.items.group.blackout.price"))
                            .defaultValue(200)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .option(
                    SettingKey.of("watheextended.blackout.cooldown", Integer.class),
                    option ->
                        option
                            .name(text("text.cooldown"))
                            .description(text("category.items.group.blackout.cooldown"))
                            .defaultValue(300)
                            .controller(
                                Controllers.numberField(
                                    Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .collapsed(true));

    if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
      // Sulfuric Acid Barrel
      builder.group(
          "items.sulfuricacidbarrel",
          group ->
              group
                  .name(
                      withIcon("gui.watheextended.config.category.items.group.sulfuricacidbarrel"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.sulfuricAcidBarrel.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.sulfuricacidbarrel.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Hunting Knife
      builder.group(
          "items.huntingknife",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.huntingknife"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.huntingKnife.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.huntingknife.price"))
                              .defaultValue(100)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.huntingKnife.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.huntingknife.cooldown"))
                              .defaultValue(45)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Medical Kit
      builder.group(
          "items.medicalkit",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.medicalkit"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.medicalKit.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.medicalkit.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Pan
      builder.group(
          "items.pan",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.pan"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.pan.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.pan.price"))
                              .defaultValue(250)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.pan.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.pan.cooldown"))
                              .defaultValue(45)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Poison Injector
      builder.group(
          "items.poisoninjector",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.poisoninjector"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.poisonInjector.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.poisoninjector.price"))
                              .defaultValue(125)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.poisonInjector.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.poisoninjector.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Pill
      builder.group(
          "items.pill",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.pill"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.pill.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.pill.price"))
                              .defaultValue(300)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.pill.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.pill.cooldown"))
                              .defaultValue(180)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Blowgun
      builder.group(
          "items.blowgun",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.blowgun"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.blowgun.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.blowgun.price"))
                              .defaultValue(175)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.blowgun.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.blowgun.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Knockout Drug
      builder.group(
          "items.knockoutdrug",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.knockoutdrug"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.knockoutDrug.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.knockoutdrug.price"))
                              .defaultValue(75)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.knockoutDrug.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.knockoutdrug.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Capture Device
      builder.group(
          "items.capturedevice",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.capturedevice"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.captureDevice.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.capturedevice.price"))
                              .defaultValue(100)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.captureDevice.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.capturedevice.cooldown"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Wrench
      builder.group(
          "items.wrench",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.wrench"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.wrench.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.wrench.price"))
                              .defaultValue(100)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.wrench.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.wrench.cooldown"))
                              .defaultValue(120)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Power Restoration
      builder.group(
          "items.powerrestoration",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.powerrestoration"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.powerRestoration.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.powerrestoration.price"))
                              .defaultValue(300)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.powerRestoration.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.powerrestoration.cooldown"))
                              .defaultValue(180)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Refresh Weapon Cooldown
      builder.group(
          "items.refreshweaponcooldown",
          group ->
              group
                  .name(
                      withIcon(
                          "gui.watheextended.config.category.items.group.refreshweaponcooldown"))
                  .option(
                      SettingKey.of("watheextended.refreshWeaponCooldown.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.refreshweaponcooldown.price"))
                              .defaultValue(300)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.refreshWeaponCooldown.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(
                                  text("category.items.group.refreshweaponcooldown.cooldown"))
                              .defaultValue(180)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Refresh Ability Cooldown
      builder.group(
          "items.refreshabilitycooldown",
          group ->
              group
                  .name(
                      withIcon(
                          "gui.watheextended.config.category.items.group.refreshabilitycooldown"))
                  .option(
                      SettingKey.of("watheextended.refreshAbilityCooldown.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(
                                  text("category.items.group.refreshabilitycooldown.price"))
                              .defaultValue(400)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.refreshAbilityCooldown.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(
                                  text("category.items.group.refreshabilitycooldown.cooldown"))
                              .defaultValue(300)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Refresh Potion Effect
      builder.group(
          "items.refreshpotioneffect",
          group ->
              group
                  .name(
                      withIcon("gui.watheextended.config.category.items.group.refreshpotioneffect"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("watheextended.refreshPotionEffect.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.refreshpotioneffect.price"))
                              .defaultValue(200)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("watheextended.refreshPotionEffect.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(
                                  text("category.items.group.refreshpotioneffect.cooldown"))
                              .defaultValue(180)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));
    }

    if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
      // Defense Vial
      builder.group(
          "items.defense_vial",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.defense_vial"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("noellesroles.defenseVialPrice", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.defense_vial.price"))
                              .defaultValue(200)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Delusion Vial
      builder.group(
          "items.delusion_vial",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.delusion_vial"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("noellesroles.delusionVialPrice", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.delusion_vial.price"))
                              .defaultValue(30)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Role Mine
      builder.group(
          "items.role_mine",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.role_mine"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("noellesroles.roleMinePrice", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.role_mine.price"))
                              .defaultValue(100)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));
    }

    if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
      // Tape
      builder.group(
          "items.tape",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.tape"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("starexpress.tape.price", Integer.class),
                      option ->
                          option
                              .name(text("text.price"))
                              .description(text("category.items.group.tape.price"))
                              .defaultValue(75)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("starexpress.tapeCooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.tape.cooldown"))
                              .defaultValue(20)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("starexpress.suffocationTime", Integer.class),
                      option ->
                          option
                              .name(text("category.items.group.tape.suffocationtime"))
                              .description(text("category.items.group.tape.suffocationtime.desc"))
                              .defaultValue(60)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("starexpress.tapeTearCheckCount", Integer.class),
                      option ->
                          option
                              .name(text("category.items.group.tape.tapetearcheckcount"))
                              .description(
                                  text("category.items.group.tape.tapetearcheckcount.desc"))
                              .defaultValue(5)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .option(
                      SettingKey.of("starexpress.tapeTearMoodChange", Float.class),
                      option ->
                          option
                              .name(text("category.items.group.tape.tapetearmoodchange"))
                              .description(
                                  text("category.items.group.tape.tapetearmoodchange.desc"))
                              .defaultValue(0.1f)
                              .controller(
                                  Controllers.numberSlider(Float.class, 0.0f, 1.0f, 0.1f, false))
                              .formatter(ConfigText::percent)
                              .validator(value -> range(value, 0.0f, 1.0f)))
                  .option(
                      SettingKey.of("starexpress.killIfCheckedAtZero", Boolean.class),
                      option ->
                          option
                              .name(text("category.items.group.tape.killifcheckedatzero"))
                              .description(
                                  text("category.items.group.tape.killifcheckedatzero.desc"))
                              .defaultValue(true)
                              .controller(Controllers.bool(true))
                              .formatter(ConfigText::onOff))
                  .option(
                      SettingKey.of("starexpress.displaySilencedTipDelay", Integer.class),
                      option ->
                          option
                              .name(text("category.items.group.tape.displaysilencedtipdelay"))
                              .description(
                                  text("category.items.group.tape.displaysilencedtipdelay.desc"))
                              .defaultValue(120)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));
    }

    if (ConfigHelper.isLoaded()) {
      // Jerry Can
      builder.group(
          "items.jerry_can",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.jerry_can"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("stupidexpress.jerryCan.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.jerry_can.cooldown"))
                              .defaultValue(0)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));

      // Lighter
      builder.group(
          "items.lighter",
          group ->
              group
                  .name(withIcon("gui.watheextended.config.category.items.group.lighter"))
                  .description(Text.empty())
                  .option(
                      SettingKey.of("stupidexpress.lighter.cooldown", Integer.class),
                      option ->
                          option
                              .name(text("text.cooldown"))
                              .description(text("category.items.group.lighter.cooldown"))
                              .defaultValue(0)
                              .controller(
                                  Controllers.numberField(
                                      Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                  .collapsed(true));
    }
  }

  private static Text withIcon(String langKey) {
    String text = Text.translatable(langKey).getString();
    if (text.isEmpty()) return Text.empty();

    MutableText result = Text.empty();
    StringBuilder segment = new StringBuilder();
    boolean isIcon = false;

    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      boolean isIconChar = c >= '\uE000' && c <= '\uF8FF';

      if (i == 0) {
        isIcon = isIconChar;
      } else if (isIconChar != isIcon) {
        flush(result, segment.toString(), isIcon);
        segment.setLength(0);
        isIcon = isIconChar;
      }
      segment.append(c);
    }

    if (!segment.isEmpty()) flush(result, segment.toString(), isIcon);
    return result;
  }

  private static void flush(MutableText into, String text, boolean isIcon) {
    if (text.isEmpty()) return;
    if (isIcon) {
      into.append(Text.literal(text).setStyle(ScreenUtils.ICON_STYLE));
    } else {
      into.append(Text.literal(text).styled(style -> style.withColor(0xFFFFFF)));
    }
  }
}
