package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class ItemsCategory {

    private ItemsCategory() {
    }

    public static ConfigCategory build(Screen parent, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.items"))
                .tooltip(Text.translatable("gui.watheextended.config.category.items.tooltip"));

        // Knife
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.knife")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.knife.price")))
                .binding(100, () -> ClientConfig.getInt("watheextended.knife.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knife.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.knife.cooldown")))
                .binding(60, () -> ClientConfig.getInt("watheextended.knife.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knife.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Revolver
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.revolver")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.revolver.price")))
                .binding(300, () -> ClientConfig.getInt("watheextended.revolver.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.revolver.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.revolver.cooldown")))
                .binding(10, () -> ClientConfig.getInt("watheextended.revolver.cooldown", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.revolver.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Grenade
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.grenade")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.grenade.price")))
                .binding(350, () -> ClientConfig.getInt("watheextended.grenade.price", 350), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.grenade.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.grenade.cooldown")))
                .binding(90, () -> ClientConfig.getInt("watheextended.grenade.cooldown", 90), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.grenade.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Psycho Mode
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.psychomode")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.psychomode.price")))
                .binding(300, () -> ClientConfig.getInt("watheextended.psychoMode.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.psychoMode.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.psychomode.cooldown")))
                .binding(300, () -> ClientConfig.getInt("watheextended.psychoMode.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.psychoMode.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Lockpick
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.lockpick")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.lockpick.price")))
                .binding(50, () -> ClientConfig.getInt("watheextended.lockpick.price", 50), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lockpick.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.lockpick.cooldown")))
                .binding(180, () -> ClientConfig.getInt("watheextended.lockpick.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.lockpick.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Crowbar
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.crowbar")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.crowbar.price")))
                .binding(25, () -> ClientConfig.getInt("watheextended.crowbar.price", 25), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.crowbar.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.crowbar.cooldown")))
                .binding(10, () -> ClientConfig.getInt("watheextended.crowbar.cooldown", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.crowbar.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Bodybag
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.bodybag")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.bodybag.price")))
                .binding(200, () -> ClientConfig.getInt("watheextended.bodyBag.price", 200), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.bodyBag.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.bodybag.cooldown")))
                .binding(300, () -> ClientConfig.getInt("watheextended.bodyBag.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.bodyBag.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Blackout
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.blackout")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.blackout.price")))
                .binding(200, () -> ClientConfig.getInt("watheextended.blackout.price", 200), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blackout.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());
        /// COOLDOWN
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.blackout.cooldown")))
                .binding(300, () -> ClientConfig.getInt("watheextended.blackout.cooldown", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blackout.cooldown", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Poison Vial
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.poison_vial")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.poison_vial.price")))
                .binding(100, () -> ClientConfig.getInt("watheextended.poisonVial.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonVial.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Scorpion
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.scorpion")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.scorpion.price")))
                .binding(50, () -> ClientConfig.getInt("watheextended.scorpion.price", 50), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.scorpion.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        // Firecracker
        builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.firecracker")));
        /// PRICE
        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.text.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.firecracker.price")))
                .binding(10, () -> ClientConfig.getInt("watheextended.firecracker.price", 10), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.firecracker.price", value))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (ConfigHelper.isLoaded()) {
            // Sulfuric Acid Barrel
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.sulfuricacidbarrel")));
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.sulfuricacidbarrel.cooldown")))
                    .binding(60, () -> ClientConfig.getInt("watheextended.sulfuricAcidBarrel.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.sulfuricAcidBarrel.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Hunting Knife
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.huntingknife")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.huntingknife.price")))
                    .binding(100, () -> ClientConfig.getInt("watheextended.huntingKnife.price", 100), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.huntingKnife.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.huntingknife.cooldown")))
                    .binding(45, () -> ClientConfig.getInt("watheextended.huntingKnife.cooldown", 45), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.huntingKnife.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Medical Kit
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.medicalkit")));
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.medicalkit.cooldown")))
                    .binding(60, () -> ClientConfig.getInt("watheextended.medicalKit.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.medicalKit.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Pan
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.pan")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.pan.price")))
                    .binding(250, () -> ClientConfig.getInt("watheextended.pan.price", 250), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pan.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.pan.cooldown")))
                    .binding(45, () -> ClientConfig.getInt("watheextended.pan.cooldown", 45), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pan.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Poison Injector
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.poisoninjector")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.poisoninjector.price")))
                    .binding(125, () -> ClientConfig.getInt("watheextended.poisonInjector.price", 125), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonInjector.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.poisoninjector.cooldown")))
                    .binding(60, () -> ClientConfig.getInt("watheextended.poisonInjector.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.poisonInjector.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Pill
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.pill")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.pill.price")))
                    .binding(300, () -> ClientConfig.getInt("watheextended.pill.price", 300), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pill.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.pill.cooldown")))
                    .binding(180, () -> ClientConfig.getInt("watheextended.pill.cooldown", 180), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.pill.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Blowgun
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.blowgun")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.blowgun.price")))
                    .binding(175, () -> ClientConfig.getInt("watheextended.blowgun.price", 175), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blowgun.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.blowgun.cooldown")))
                    .binding(60, () -> ClientConfig.getInt("watheextended.blowgun.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.blowgun.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Knockout Drug
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.knockoutdrug")));
            /// PRICE
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.knockoutdrug.price")))
                    .binding(75, () -> ClientConfig.getInt("watheextended.knockoutDrug.price", 75), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knockoutDrug.price", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
            /// COOLDOWN
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.cooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.knockoutdrug.cooldown")))
                    .binding(60, () -> ClientConfig.getInt("watheextended.knockoutDrug.cooldown", 60), value -> ScreenUtils.stage(sendCommand, parent, "watheextended.knockoutDrug.cooldown", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            // Defense Vial
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.defense_vial")));
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.defense_vial.price")))
                    .binding(200, () -> ClientConfig.getInt("noellesroles.defenseVialPrice", 200), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.defenseVialPrice", value))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            // Role Mine
            builder.option(LabelOption.create(withIcon("gui.watheextended.config.category.items.label.role_mine")));
            builder.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.text.price"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.items.opt.role_mine.price")))
                    .binding(100, () -> ClientConfig.getInt("noellesroles.roleMinePrice", 100), value -> ScreenUtils.stage(sendCommand, parent, "noellesroles.roleMinePrice", value))
                    .controller(IntegerFieldControllerBuilder::create)
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
            into.append(Text.literal(text).styled(style -> style.withColor(0xAAAAAA)));
        }
    }
}
