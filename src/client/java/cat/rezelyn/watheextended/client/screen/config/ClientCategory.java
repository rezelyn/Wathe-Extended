package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.wathe.GameComponents;
import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.game.PronounsManager;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public final class ClientCategory {

    private ClientCategory() {
    }

    public static ConfigCategory build(Screen parent, boolean isOp, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client"));

        // Player
        builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.player").styled(style -> style.withColor(0xAAAAAA))));
        /// PRONOUNS
        builder.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.pronouns"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.opt.pronouns.desc")))
                .binding("", PronounsCache::getLocalPronouns, value -> {
                    try {
                        ClientPlayNetworking.send(new PronounsManager.UpdatePayload(value.trim()));
                    } catch (Throwable ignored) {
                    }
                })
                .controller(StringControllerBuilder::create)
                .build());

        // HUD
        builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.hud").styled(style -> style.withColor(0xAAAAAA))));
        /// TOGGLE CHAT
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.showchat"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.opt.showchat.desc")))
                .binding(true, WatheExtendedClientConfig::getShowChatDuringGame, WatheExtendedClientConfig::setShowChatDuringGame)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            /// STAMINA BAR
            builder.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.client.opt.staminabar"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.opt.staminabar.desc")))
                    .binding(false, cat.rezelyn.watheextended.api.kinswathe.ConfigHelper::getEnableStaminaBar, cat.rezelyn.watheextended.api.kinswathe.ConfigHelper::setEnableStaminaBar)
                    .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                    .build());
        }

        // Visual
        builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.visual").styled(style -> style.withColor(0xAAAAAA))));
        /// DISABLE SCREEN SHAKE
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.disablescreenshake"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.opt.disablescreenshake.desc")))
                .binding(false, cat.rezelyn.watheextended.api.wathe.ConfigHelper::getDisableScreenShake, cat.rezelyn.watheextended.api.wathe.ConfigHelper::setDisableScreenShake)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// TOGGLE FOG
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.fog"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("gui.watheextended.config.category.client.opt.fog.desc"))
                        .customImage(new ScreenUtils.ImageRenderer(
                                Identifier.of("watheextended", "textures/gui/config/fog_enabled.png"),
                                Identifier.of("watheextended", "textures/gui/config/fog_disabled.png"),
                                1920, 1009))
                        .build())
                .binding(true, () -> GameComponents.getFog(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:setVisual fog " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// TOGGLE HUD
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.hud"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("gui.watheextended.config.category.client.opt.hud.desc"))
                        .customImage(new ScreenUtils.ImageRenderer(
                                Identifier.of("watheextended", "textures/gui/config/hud_enabled.png"),
                                Identifier.of("watheextended", "textures/gui/config/hud_disabled.png"),
                                1920, 1009))
                        .build())
                .binding(true, () -> GameComponents.getHud(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:setVisual hud " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// TOGGLE SNOWFLAKES
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.snowflakes"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("gui.watheextended.config.category.client.opt.snowflakes.desc"))
                        .customImage(new ScreenUtils.ImageRenderer(
                                Identifier.of("watheextended", "textures/gui/config/snowflakes_enabled.png"),
                                Identifier.of("watheextended", "textures/gui/config/snowflakes_disabled.png"),
                                1920, 1009))
                        .build())
                .binding(true, () -> GameComponents.getSnow(MinecraftClient.getInstance().world), value -> sendCommand.accept("wathe:setVisual snow " + value, parent))
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        /// ULTRA PERFORMANCE MODE
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.opt.ultraperfmode"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.opt.ultraperfmode.desc")))
                .binding(false, cat.rezelyn.watheextended.api.wathe.ConfigHelper::getUltraPerfMode, cat.rezelyn.watheextended.api.wathe.ConfigHelper::setUltraPerfMode)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                .build());
        if (isOp) {
            builder.group(buildDebugGroup());
        }

        return builder.build();
    }

    private static OptionGroup buildDebugGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.group.debug").styled(style -> style.withColor(0xFF5555)))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.group.debug.tooltip")))
                .collapsed(false)

                /// SHOW BOX BOUNDARIES
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showboxboundaries"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showboxboundaries.desc")))
                        .binding(false, () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showBoxBoundaries, value -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showBoxBoundaries = value)
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                        .build())
                /// SHOW RTP SLOTS
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showrtpslots"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showrtpslots.desc")))
                        .binding(false, () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showRtpSlots, value -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showRtpSlots = value)
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                        .build())
                /// SHOW KEY ASSIGNMENTS
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showkeyassignments"))
                        .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showkeyassignments.desc")))
                        .binding(false, () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showKeyAssignments, value -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showKeyAssignments = value)
                        .controller(option -> BooleanControllerBuilder.create(option).coloured(true).formatValue(value -> Text.translatable(value ? "gui.watheextended.config.text.on" : "gui.watheextended.config.text.off")))
                        .build())

                .build();
    }
}
