package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.cca.GameComponents;
import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import cat.rezelyn.watheextended.game.PronounsManager;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public final class ClientCategory {

    private ClientCategory() {}

    public static ConfigCategory build(Screen parent, boolean isOp, BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client"));

        builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.player").styled(s -> s.withColor(0xAAAAAA))));

        builder.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.pronouns"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.pronouns.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding("",
                        PronounsCache::getLocalPronouns,
                        v -> {
                            try {
                                ClientPlayNetworking.send(
                                        new PronounsManager.UpdatePayload(v.trim()));
                            } catch (Throwable ignored) {
                            }
                        })
                .controller(StringControllerBuilder::create)
                .build());

        builder.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.client.label.visual").styled(s -> s.withColor(0xAAAAAA))));

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            builder.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.client.option.staminabar"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.client.option.staminabar.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            cat.rezelyn.watheextended.api.kinswathe.ConfigHelper::getEnableStaminaBar,
                            cat.rezelyn.watheextended.api.kinswathe.ConfigHelper::setEnableStaminaBar)
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
        }

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.ultraperfmode"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.ultraperfmode.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        cat.rezelyn.watheextended.api.wathe.ConfigHelper::getUltraPerfMode,
                        cat.rezelyn.watheextended.api.wathe.ConfigHelper::setUltraPerfMode)
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.disablescreenshake"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.disablescreenshake.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        cat.rezelyn.watheextended.api.wathe.ConfigHelper::getDisableScreenShake,
                        cat.rezelyn.watheextended.api.wathe.ConfigHelper::setDisableScreenShake)
                .controller(TickBoxControllerBuilder::create)
                .build());

        final boolean fogDefault = GameComponents.getFog(MinecraftClient.getInstance().world);
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.visual.fog"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.visual.fog.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(fogDefault,
                        () -> GameComponents.getFog(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:setVisual fog " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        final boolean hudDefault = GameComponents.getHud(MinecraftClient.getInstance().world);
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.visual.hud"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.visual.hud.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(hudDefault,
                        () -> GameComponents.getHud(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:setVisual hud " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        final boolean snowDefault = GameComponents.getSnow(MinecraftClient.getInstance().world);
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.visual.snow"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.visual.snow.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(snowDefault,
                        () -> GameComponents.getSnow(MinecraftClient.getInstance().world),
                        v -> sendCommand.accept("wathe:setVisual snow " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.option.showchatduringgame"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.option.showchatduringgame.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(true,
                        WatheExtendedClientConfig::getShowChatDuringGame,
                        WatheExtendedClientConfig::setShowChatDuringGame)
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        if (isOp) {
            builder.group(buildDebugGroup());
        }

        return builder.build();
    }

    private static OptionGroup buildDebugGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client.group.debug")
                        .styled(style -> style.withColor(0xFF5555)))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.client.group.debug.tooltip")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .collapsed(false)
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showboxboundaries"))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showboxboundaries.desc")
                                        .styled(style -> style.withColor(0xFFFFFF))))
                        .binding(false,
                                () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showBoxBoundaries,
                                v -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showBoxBoundaries = v)
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .coloured(true)
                                .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showrtpslots"))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showrtpslots.desc")
                                        .styled(style -> style.withColor(0xFFFFFF))))
                        .binding(false,
                                () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showRtpSlots,
                                v -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showRtpSlots = v)
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .coloured(true)
                                .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showkeyassignments"))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.category.client.group.debug.opt.showkeyassignments.desc")
                                        .styled(style -> style.withColor(0xFFFFFF))))
                        .binding(false,
                                () -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showKeyAssignments,
                                v -> cat.rezelyn.watheextended.client.debug.BoxDebugRenderer.showKeyAssignments = v)
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .coloured(true)
                                .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                        .build())
                .build();
    }
}
