package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.GameComponents;
import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class ItemsCategory {

    private ItemsCategory() {}

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.items"))
                .tooltip(Text.translatable("gui.watheextended.config.category.items.tooltip"));

        World world = MinecraftClient.getInstance().world;

        // Knife
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.knife"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.knife.price")))
                        .binding(100, () -> ClientConfig.getInt("watheextended.knife.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knife.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.knife.cooldown")))
                        .binding(60, () -> ClientConfig.getInt("watheextended.knife.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knife.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Revolver
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.revolver"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.revolver.price")))
                        .binding(300, () -> ClientConfig.getInt("watheextended.revolver.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.revolver.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.revolver.cooldown")))
                        .binding(10, () -> ClientConfig.getInt("watheextended.revolver.cooldown", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.revolver.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// BACKFIRE CHANCE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.items.group.revolver.backfire"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.revolver.backfire.desc")))
                        .binding(GameComponents.getBackfire(world), () -> GameComponents.getBackfire(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:gameSettings set backfire " + (value / 100f), parent))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Grenade
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.grenade"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.grenade.price")))
                        .binding(350, () -> ClientConfig.getInt("watheextended.grenade.price", 350), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.grenade.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.grenade.cooldown")))
                        .binding(90, () -> ClientConfig.getInt("watheextended.grenade.cooldown", 90), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.grenade.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Psycho Mode
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.psychomode"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.psychomode.price")))
                        .binding(300, () -> ClientConfig.getInt("watheextended.psychoMode.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.psychoMode.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.psychomode.cooldown")))
                        .binding(300, () -> ClientConfig.getInt("watheextended.psychoMode.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.psychoMode.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Poison Vial
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.poison_vial"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.poison_vial.price")))
                        .binding(100, () -> ClientConfig.getInt("watheextended.poisonVial.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonVial.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Scorpion
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.scorpion"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.scorpion.price")))
                        .binding(50, () -> ClientConfig.getInt("watheextended.scorpion.price", 50), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.scorpion.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Firecracker
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.firecracker"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.firecracker.price")))
                        .binding(10, () -> ClientConfig.getInt("watheextended.firecracker.price", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.firecracker.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Note
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.note"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.note.price")))
                        .binding(10, () -> ClientConfig.getInt("watheextended.note.price", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.note.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Lockpick
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.lockpick"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.lockpick.price")))
                        .binding(50, () -> ClientConfig.getInt("watheextended.lockpick.price", 50), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lockpick.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.lockpick.cooldown")))
                        .binding(180, () -> ClientConfig.getInt("watheextended.lockpick.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lockpick.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Crowbar
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.crowbar"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.crowbar.price")))
                        .binding(25, () -> ClientConfig.getInt("watheextended.crowbar.price", 25), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.crowbar.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.crowbar.cooldown")))
                        .binding(10, () -> ClientConfig.getInt("watheextended.crowbar.cooldown", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.crowbar.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Body Bag
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.bodybag"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.bodybag.price")))
                        .binding(200, () -> ClientConfig.getInt("watheextended.bodyBag.price", 200), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.bodyBag.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.bodybag.cooldown")))
                        .binding(300, () -> ClientConfig.getInt("watheextended.bodyBag.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.bodyBag.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        // Blackout
        builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.blackout"))
                /// PRICE
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.price"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.blackout.price")))
                        .binding(200, () -> ClientConfig.getInt("watheextended.blackout.price", 200), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blackout.price", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                /// COOLDOWN
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.blackout.cooldown")))
                        .binding(300, () -> ClientConfig.getInt("watheextended.blackout.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blackout.cooldown", value))
                        .controller(IntegerFieldControllerBuilder::create).build())
                .build());

        if (cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.isLoaded()) {
            // Sulfuric Acid Barrel
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.sulfuricacidbarrel"))
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.sulfuricacidbarrel.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.sulfuricAcidBarrel.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.sulfuricAcidBarrel.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Hunting Knife
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.huntingknife"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.huntingknife.price")))
                            .binding(100, () -> ClientConfig.getInt("watheextended.huntingKnife.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.huntingKnife.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.huntingknife.cooldown")))
                            .binding(45, () -> ClientConfig.getInt("watheextended.huntingKnife.cooldown", 45), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.huntingKnife.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Medical Kit
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.medicalkit"))
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.medicalkit.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.medicalKit.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.medicalKit.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Pan
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.pan"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.pan.price")))
                            .binding(250, () -> ClientConfig.getInt("watheextended.pan.price", 250), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pan.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.pan.cooldown")))
                            .binding(45, () -> ClientConfig.getInt("watheextended.pan.cooldown", 45), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pan.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Poison Injector
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.poisoninjector"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.poisoninjector.price")))
                            .binding(125, () -> ClientConfig.getInt("watheextended.poisonInjector.price", 125), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonInjector.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.poisoninjector.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.poisonInjector.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonInjector.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Pill
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.pill"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.pill.price")))
                            .binding(300, () -> ClientConfig.getInt("watheextended.pill.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pill.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.pill.cooldown")))
                            .binding(180, () -> ClientConfig.getInt("watheextended.pill.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pill.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Blowgun
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.blowgun"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.blowgun.price")))
                            .binding(175, () -> ClientConfig.getInt("watheextended.blowgun.price", 175), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blowgun.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.blowgun.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.blowgun.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blowgun.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Knockout Drug
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.knockoutdrug"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.knockoutdrug.price")))
                            .binding(75, () -> ClientConfig.getInt("watheextended.knockoutDrug.price", 75), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knockoutDrug.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.knockoutdrug.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.knockoutDrug.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knockoutDrug.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Capture Device
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.capturedevice"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.capturedevice.price")))
                            .binding(100, () -> ClientConfig.getInt("watheextended.captureDevice.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.captureDevice.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.capturedevice.cooldown")))
                            .binding(60, () -> ClientConfig.getInt("watheextended.captureDevice.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.captureDevice.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Wrench
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.wrench"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.wrench.price")))
                            .binding(100, () -> ClientConfig.getInt("watheextended.wrench.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.wrench.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.wrench.cooldown")))
                            .binding(120, () -> ClientConfig.getInt("watheextended.wrench.cooldown", 120), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.wrench.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Power Restoration
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.powerrestoration"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.powerrestoration.price")))
                            .binding(300, () -> ClientConfig.getInt("watheextended.powerRestoration.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.powerRestoration.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.powerrestoration.cooldown")))
                            .binding(180, () -> ClientConfig.getInt("watheextended.powerRestoration.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.powerRestoration.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Refresh Weapon Cooldown
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.refreshweaponcooldown"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshweaponcooldown.price")))
                            .binding(300, () -> ClientConfig.getInt("watheextended.refreshWeaponCooldown.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshWeaponCooldown.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshweaponcooldown.cooldown")))
                            .binding(180, () -> ClientConfig.getInt("watheextended.refreshWeaponCooldown.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshWeaponCooldown.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Refresh Ability Cooldown
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.refreshabilitycooldown"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshabilitycooldown.price")))
                            .binding(400, () -> ClientConfig.getInt("watheextended.refreshAbilityCooldown.price", 400), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshAbilityCooldown.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshabilitycooldown.cooldown")))
                            .binding(300, () -> ClientConfig.getInt("watheextended.refreshAbilityCooldown.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshAbilityCooldown.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Refresh Potion Effect
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.refreshpotioneffect"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshpotioneffect.price")))
                            .binding(200, () -> ClientConfig.getInt("watheextended.refreshPotionEffect.price", 200), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshPotionEffect.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.refreshpotioneffect.cooldown")))
                            .binding(180, () -> ClientConfig.getInt("watheextended.refreshPotionEffect.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.refreshPotionEffect.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());
        }

        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            // Defense Vial
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.defense_vial"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.defense_vial.price")))
                            .binding(200, () -> ClientConfig.getInt("noellesroles.defenseVialPrice", 200), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.defenseVialPrice", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Delusion Vial
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.delusion_vial"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.delusion_vial.price")))
                            .binding(30, () -> ClientConfig.getInt("noellesroles.delusionVialPrice", 30), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.delusionVialPrice", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Role Mine
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.role_mine"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.role_mine.price")))
                            .binding(100, () -> ClientConfig.getInt("noellesroles.roleMinePrice", 100), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.roleMinePrice", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());
        }

        if (cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.isLoaded()) {
            // Tape
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.tape"))
                    /// PRICE
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.price"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.price")))
                            .binding(75, () -> ClientConfig.getInt("starexpress.tape.price", 75), value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tape.price", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.cooldown")))
                            .binding(20, () -> ClientConfig.getInt("starexpress.tapeCooldown", 20), value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeCooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// SUFFOCATION TIME
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.category.items.group.tape.suffocationtime"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.suffocationtime.desc")))
                            .binding(60, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerSuffocationTime, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.suffocationTime", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// TEAR CHECK COUNT
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.category.items.group.tape.tapetearcheckcount"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.tapetearcheckcount.desc")))
                            .binding(5, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerTapeTearCheckCount, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeTearCheckCount", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    /// TEAR MOOD CHANGE
                    .option(Option.<Float>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.category.items.group.tape.tapetearmoodchange"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.tapetearmoodchange.desc")))
                            .binding(0.1f, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerTapeTearMoodChange, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.tapeTearMoodChange", value))
                            .controller(ScreenUtils::floatController).build())
                    /// KILL IF CHECKED AT ZERO
                    .option(Option.<Boolean>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.category.items.group.tape.killifcheckedatzero"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.killifcheckedatzero.desc")))
                            .binding(true, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerKillIfCheckedAtZero, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.killIfCheckedAtZero", value))
                            .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.enabled" : "gui.watheextended.config.text.disabled"))).build())
                    /// DISPLAY SILENCED TIP DELAY
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.category.items.group.tape.displaysilencedtipdelay"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.tape.displaysilencedtipdelay.desc")))
                            .binding(120, cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper::getMuzzlerDisplaySilencedTipDelay, value -> ScreenUtils.stage(sendCommand, parent, "starexpress.displaySilencedTipDelay", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());
        }

        if (cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) {
            // Jerry Can
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.jerry_can"))
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.jerry_can.cooldown")))
                            .binding(0, () -> ClientConfig.getInt("stupidexpress.jerryCan.cooldown", 0), value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.jerryCan.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());

            // Lighter
            builder.group(OptionGroup.createBuilder().name(withIcon("gui.watheextended.config.category.items.group.lighter"))
                    /// COOLDOWN
                    .option(Option.<Integer>createBuilder()
                            .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                            .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.group.lighter.cooldown")))
                            .binding(0, () -> ClientConfig.getInt("stupidexpress.lighter.cooldown", 0), value -> ScreenUtils.stage(sendCommand, parent, "stupidexpress.lighter.cooldown", value))
                            .controller(IntegerFieldControllerBuilder::create).build())
                    .build());
        }

        return builder.build();
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
