package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.watheextended.api.cca.GameComponents;
import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.hml.ModifiersDisplay;
import cat.rezelyn.watheextended.api.hml.ModifiersId;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.api.wathe.RolesId;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.*;

public final class WatheOptionsScreen {

    private static final Map<String, Boolean> pendingRoleState = new HashMap<>();
    private static final Map<String, Boolean> pendingModifierState = new HashMap<>();
    // Blacklist of roles that shouldn't be shown in the options/config screen
    // (roles needed for WATHE to function properly and therefore can't be disabled)
    private static final Set<String> BLACKLIST = Set.of(
            "civilian",
            "killer",
            "vigilante",
            "discovery_civilian",
            "loose_end"
    );
    private static Screen reopenParent = null;
    private static int waitForTicks = 0;

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (reopenParent == null) return;
            if (--waitForTicks > 0) return;
            Screen parent = reopenParent;
            reopenParent = null;
            client.execute(() -> client.setScreen(create(parent)));
        });
    }

    public static boolean isBlacklisted(String id) {
        int colon = id.indexOf(':');
        String local = colon >= 0 ? id.substring(colon + 1) : id;
        return BLACKLIST.contains(local);
    }

    // check if player is op (>=2)
    private static boolean isOp() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.hasPermissionLevel(2);
    }

    public static Screen create(Screen parent) {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("gui.watheextended.config.title"))
                .category(buildClientCategory(parent));

        if (isOp()) { // build server-side categories and options only if player is op
            builder.category(buildOptionsCategory(parent));
            builder.category(buildMapVariablesCategory(parent));
            builder.category(buildRolesCategory(parent));
            builder.category(buildModifiersCategory(parent));
        }

        return builder.build().generateScreen(parent);
    }

    private static ConfigCategory buildClientCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.client"));

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
                        v -> sendCommand("wathe:setVisual fog " + v, parent))
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
                        v -> sendCommand("wathe:setVisual hud " + v, parent))
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
                        v -> sendCommand("wathe:setVisual snow " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        if (isOp()) {
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

    private static ConfigCategory buildOptionsCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options"))
                .tooltip(Text.translatable("gui.watheextended.config.category.options.tooltip"));

        builder.group(buildGamerulesGroup(parent));
        builder.group(buildWatheOptionsGroup(parent));
        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            builder.group(buildRolesOptionsGroup(parent));
            builder.group(buildModifiersOptionsGroup(parent));
        }

        return builder.build();
    }

    private static OptionGroup buildGamerulesGroup(Screen parent) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.gamerules.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        boolean collisionsDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            collisionsDefault = wec == null || wec.isPlayerCollisionsEnabled();
        } catch (Throwable t) {
            collisionsDefault = true;
        }
        final boolean collisionsFinal = collisionsDefault;

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.collisions.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(collisionsFinal,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isPlayerCollisionsEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand("watheextended:enableCollisions " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        if (cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()) {
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.morphpsychosis.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            () -> cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.getInsanePlayersSeeMorphs(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config noellesroles enableMorphPsychosis " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {

            final boolean startSafeTimeDefault = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStartSafeTime(world);
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safepreptime.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableStartSafeTime(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe enableSafePrepTime " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());

            final int startingCooldownDefault = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getStartingCooldown(world);
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.gamerules.opt.safeprepcooldown.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(30,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getStartingCooldown(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setStartingCooldown " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildWatheOptionsGroup(Screen parent) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.wathe_options.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.backfire.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getBackfire(world),
                        () -> GameComponents.getBackfire(MinecraftClient.getInstance().world),
                        v -> sendCommand("wathe:gameSettings set backfire " + (v / 100f), parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.isLoaded()) {
            final String[] punishmentModes = cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.getPunishmentModes();
            group.option(Option.<String>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.shooterpunishment.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.getCurrentPunishment(),
                            () -> cat.rezelyn.watheextended.api.shooterpunishments.ConfigHelper.getCurrentPunishment(),
                            v -> sendCommand("watheextended:config shooterpunishments setMode " + v, parent))
                    .controller(opt -> CyclingListControllerBuilder
                            .<String>create(opt)
                            .values(Arrays.asList(punishmentModes))
                            .formatValue(v -> {
                                String spaced = v.replaceAll("([A-Z])", " $1");
                                String titled = Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
                                return Text.literal(titled);
                            }))
                    .build());
        }

        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            final boolean watheTweaksEnabled = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableWatheModify(world);

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.wathetweaks"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.wathetweaks.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableWatheModify(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe enableWatheTweaks " + v, parent))
                    .controller(TickBoxControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialcivilianincome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialcivilianincome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(0,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialCivilianIncome(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setInitialCivilianIncome " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialnnetralincome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialnnetralincome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(0,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialNeutralIncome(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setInitialNeutralIncome " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialkillerncome"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.initialkillerncome.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(100,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getInitialKillerIncome(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setInitialKillerIncome " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.increasemoneywhenkilll"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.increasemoneywhenkilll.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(100,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getIncreaseMoneyWhenKill(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setIncreaseMoneyWhenKill " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create)
                    .build());

            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.preventkillerdroprevolver"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.options.group.wathe_options.opt.preventkillerdroprevolver.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .available(watheTweaksEnabled)
                    .binding(false,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getPreventKillerDropRevolver(MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe setPreventKillerDropRevolver " + v, parent))
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        }

        return group.build();
    }

    private static OptionGroup buildRolesOptionsGroup(Screen parent) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_killer"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_killer.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getKillerDividend(world),
                        () -> GameComponents.getKillerDividend(MinecraftClient.getInstance().world),
                        v -> sendCommand("wathe:gameSettings set roleDividend killer " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_vigilante"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.roledividend_vigilante.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getVigilanteDividend(world),
                        () -> GameComponents.getVigilanteDividend(MinecraftClient.getInstance().world),
                        v -> sendCommand("wathe:gameSettings set roleDividend vigilante " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        final boolean noellesRolesTweaksEnabled = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(world);

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.tweaks.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(false,
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableNoellesRolesModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand("watheextended:config kinswathe enableNoellesRolesTweaks " + v, parent))
                .controller(TickBoxControllerBuilder::create)
                .build());

        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.conductorinstinct.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .available(noellesRolesTweaksEnabled)
                .binding(false,
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getConductorInstinctModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand("watheextended:config kinswathe modifyConductorInstinct " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());
        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.coronerinstinct"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.coronerinstinct.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .available(noellesRolesTweaksEnabled)
                .binding(false,
                        () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCoronerInstinctModify(
                                MinecraftClient.getInstance().world),
                        v -> sendCommand("watheextended:config kinswathe modifyCoronerInstinct " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bellringer").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.price.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setBellringerAbilityPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bellringer.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(120, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBellringerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setBellringerAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.bodymaker").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setBodymakerAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.bodymaker.fakerole.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(true, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getBodymakerAbilityFakeRole(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setBodymakerAbilityFakeRole " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                .build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cleaner").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.price.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setCleanerAbilityPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cleaner.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(150, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCleanerAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setCleanerAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.cook").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.cook.panprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(250, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getCookPanPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setCookPanPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.detective").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.price.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(200, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDetectiveAbilityPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.detective.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDetectiveAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDetectiveAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.dreamer").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.dreamer.initialquantity.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDreamerInitialItemQuantity(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDreamerInitialItemQuantity " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.drugmaker").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.playerlimit.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPlayerLimit(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDrugmakerPlayerLimit " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.killincome.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(50, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerGetCoins(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDrugmakerGetCoins " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.poisoninjectorprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(125, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerPoisonInjectorPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDrugmakerPoisonInjectorPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.drugmaker.blowgunprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(175, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getDrugmakerBlowgunPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setDrugmakerBlowgunPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.hunter").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.price.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(125, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setHunterAbilityPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.hunter.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(5, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getHunterAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setHunterAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.judge").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.price.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setJudgeAbilityPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.glowing.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityGlowing(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setJudgeAbilityGlowing " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.judge.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(180, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getJudgeAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setJudgeAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.kidnapper").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.kidnapper.knockoutdrugprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(75, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getKidnapperKnockoutDrugPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setKidnapperKnockoutDrugPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.licensedvillain").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.playerlimit.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainPlayerLimit(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setLicensedVillainPlayerLimit " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.licensedvillain.revolverprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getLicensedVillainRevolverPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setLicensedVillainRevolverPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.physician").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.physician.pillprice.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(300, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getPhysicianPillPrice(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setPhysicianPillPrice " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.robot").styled(style -> style.withColor(0xAAAAAA))));
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.duration.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(10, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityDuration(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setRobotAbilityDuration " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());
        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.robot.cooldown.desc")
                        .styled(style -> style.withColor(0xFFFFFF))))
                .binding(90, () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getRobotAbilityCooldown(MinecraftClient.getInstance().world), v -> sendCommand("watheextended:config kinswathe setRobotAbilityCooldown " + v, parent))
                .controller(IntegerFieldControllerBuilder::create).build());

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.starstruck").styled(style -> style.withColor(0xAAAAAA))));
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskreducescooldown.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskReducesCooldown,
                            v -> sendCommand("watheextended:config starexpress setStarstruckTaskReducesCooldown " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.taskcooldownreduction.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(5,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckTaskCooldownReduction,
                            v -> sendCommand("watheextended:config starexpress setStarstruckTaskCooldownReduction " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitycooldown.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(90,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityCooldown,
                            v -> sendCommand("watheextended:config starexpress setStarstruckAbilityCooldown " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityduration.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(15,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityDuration,
                            v -> sendCommand("watheextended:config starexpress setStarstruckAbilityDuration " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilityaffectsmovementspeed.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityAffectsMovementSpeed,
                            v -> sendCommand("watheextended:config starexpress setStarstruckAbilityAffectsMovementSpeed " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitywalkspeed.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.12f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilityWalkSpeed,
                            v -> sendCommand("watheextended:config starexpress setStarstruckAbilityWalkSpeed " + v, parent))
                    .controller(FloatFieldControllerBuilder::create).build());
            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.starstruck.abilitysprintspeed.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.15f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getStarstruckAbilitySprintSpeed,
                            v -> sendCommand("watheextended:config starexpress setStarstruckAbilitySprintSpeed " + v, parent))
                    .controller(FloatFieldControllerBuilder::create).build());

            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.roles_options.label.muzzler").styled(style -> style.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapecooldown.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(20,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeCooldown,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerTapeCooldown " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.suffocationtime.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(60,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerSuffocationTime,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerSuffocationTime " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearcheckcount.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(5,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearCheckCount,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerTapeTearCheckCount " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Float>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.tapetearmoodchange.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(0.1f,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerTapeTearMoodChange,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerTapeTearMoodChange " + v, parent))
                    .controller(FloatFieldControllerBuilder::create).build());
            group.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.killifcheckedatzero.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerKillIfCheckedAtZero,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerKillIfCheckedAtZero " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.on" : "text.watheextended.off")))
                    .build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.roles_options.opt.muzzler.displaysilencedtipdelay.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(120,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getMuzzlerDisplaySilencedTipDelay,
                            v -> sendCommand("watheextended:config starexpress setMuzzlerDisplaySilencedTipDelay " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
        }

        return group.build();
    }

    private static OptionGroup buildModifiersOptionsGroup(Screen parent) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.maximum.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1,
                        ConfigHelper::getModifierMaximum,
                        ConfigHelper::setModifierMaximum)
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        group.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.modifiers.opt.multiplier.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(1,
                        ConfigHelper::getModifierMultiplier,
                        ConfigHelper::setModifierMultiplier)
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        if (cat.rezelyn.watheextended.api.starexpress.ConfigHelper.isLoaded()) {
            group.option(LabelOption.create(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.label.allergic").styled(style -> style.withColor(0xAAAAAA))));
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.nothingchance.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(3,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicNothingChance,
                            v -> sendCommand("watheextended:config starexpress setAllergicNothingChance " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctchance.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctChance,
                            v -> sendCommand("watheextended:config starexpress setAllergicInstinctChance " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.armorchance.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicArmorChance,
                            v -> sendCommand("watheextended:config starexpress setAllergicArmorChance " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.poisonchance.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(1,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicPoisonChance,
                            v -> sendCommand("watheextended:config starexpress setAllergicPoisonChance " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
            group.option(Option.<Integer>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration"))
                    .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.options.group.modifiers_options.opt.allergic.instinctduration.desc")
                            .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(3,
                            cat.rezelyn.watheextended.api.starexpress.ConfigHelper::getAllergicInstinctDuration,
                            v -> sendCommand("watheextended:config starexpress setAllergicInstinctDuration " + v, parent))
                    .controller(IntegerFieldControllerBuilder::create).build());
        }

        return group.build();
    }

    private static ConfigCategory buildMapVariablesCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.title"))
                .tooltip(Text.translatable("gui.watheextended.config.category.mapvariables.tooltip"));

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        boolean worldProtectionDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            worldProtectionDefault = wec == null || wec.isBlockInteractionsProtected();
        } catch (Throwable t) {
            worldProtectionDefault = true;
        }
        final boolean worldProtectionFinal = worldProtectionDefault;

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.worldprotection.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(worldProtectionFinal,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isBlockInteractionsProtected();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand("watheextended:enableWorldProtection " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        boolean rtpDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            rtpDefault = wec == null || wec.isRtpEnabled();
        } catch (Throwable t) {
            rtpDefault = true;
        }
        final boolean rtpFinal = rtpDefault;

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.rtp.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(rtpFinal,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isRtpEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand("watheextended:rtp " + (v ? "enable" : "disable"), parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());

        boolean itemBoundsCheckDefault;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            itemBoundsCheckDefault = wec == null || wec.isItemBoundsCheckEnabled();
        } catch (Throwable t) {
            itemBoundsCheckDefault = true;
        }
        final boolean itemBoundsCheckFinal = itemBoundsCheckDefault;

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.itemboundscheck.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(itemBoundsCheckFinal,
                        () -> {
                            try {
                                World w = MinecraftClient.getInstance().world;
                                WatheExtendedWorldComponent c = w != null ? WatheExtendedWorldComponent.KEY.get(w) : null;
                                return c == null || c.isItemBoundsCheckEnabled();
                            } catch (Throwable t) {
                                return true;
                            }
                        },
                        v -> sendCommand("watheextended:enableItemBoundsCheck " + v, parent))
                .controller(opt -> BooleanControllerBuilder.create(opt)
                        .coloured(true)
                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                .build());


        if (cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.isLoaded()) {
            final boolean jumpNotInGameDefault = cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableJumpNotInGame(world);
            builder.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpinlobby"))
                    .description(OptionDescription.of(
                            Text.translatable("gui.watheextended.config.category.mapvariables.opt.jumpinlobby.desc")
                                    .styled(style -> style.withColor(0xFFFFFF))))
                    .binding(true,
                            () -> cat.rezelyn.watheextended.api.kinswathe.ConfigHelper.getEnableJumpNotInGame(
                                    MinecraftClient.getInstance().world),
                            v -> sendCommand("watheextended:config kinswathe enableJumpInLobby " + v, parent))
                    .controller(opt -> BooleanControllerBuilder.create(opt)
                            .coloured(true)
                            .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                    .build());

        }

        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.opt.autostart.desc")
                                .styled(style -> style.withColor(0xFFFFFF))))
                .binding(GameComponents.getAutoStart(world),
                        () -> GameComponents.getAutoStart(MinecraftClient.getInstance().world),
                        v -> sendCommand("wathe:gameSettings set autoStart " + v, parent))
                .controller(IntegerFieldControllerBuilder::create)
                .build());

        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.tooltip")))
                .collapsed(false);

        String lobbyAreaDefault = boxToArgs(MapVariables.getLobbyArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.lobbyarea.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(lobbyAreaDefault, () -> lobbyAreaDefault,
                        v -> {
                            String[] parts = v.trim().split("\\s+");
                            if (parts.length == 6) {
                                sendCommand("watheextended:mapVariables set lobbyArea "
                                        + parts[0] + " " + parts[1] + " " + parts[2] + " "
                                        + parts[3] + " " + parts[4] + " " + parts[5], parent);
                            }
                        })
                .controller(StringControllerBuilder::create)
                .build());

        String playAreaDefault = boxToArgs(MapVariables.getPlayArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playarea.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(playAreaDefault, () -> playAreaDefault,
                        v -> sendWatheMapVarCommand("playArea " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String playAreaOffsetDefault = vec3iToArgs(MapVariables.getPlayAreaOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.playareaoffset.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(playAreaOffsetDefault, () -> playAreaOffsetDefault,
                        v -> sendWatheMapVarCommand("playAreaOffset " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String readyAreaDefault = boxToArgs(MapVariables.getReadyArea(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyarea.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(readyAreaDefault, () -> readyAreaDefault,
                        v -> sendWatheMapVarCommand("readyArea " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String resetPasteOffsetDefault = vec3iToArgs(MapVariables.getResetPasteOffset(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.resetpasteoffset.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(resetPasteOffsetDefault, () -> resetPasteOffsetDefault,
                        v -> sendWatheMapVarCommand("resetPasteOffset " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String readyAreaSpawnDefault = posToArgs(MapVariables.getReadyAreaSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.readyareaspawnpos.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(readyAreaSpawnDefault, () -> readyAreaSpawnDefault,
                        v -> sendCommand("watheextended:mapVariables set readyAreaSpawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String spawnPosDefault = posToArgs(MapVariables.getSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spawnpos.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(spawnPosDefault, () -> spawnPosDefault,
                        v -> sendWatheMapVarCommand("spawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        String spectatorSpawnDefault = posToArgs(MapVariables.getSpectatorSpawnPosition(world));
        group.option(Option.<String>createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos"))
                .description(OptionDescription.of(Text.translatable("gui.watheextended.config.category.mapvariables.group.variables.opt.spectatorspawnpos.desc")
                        .styled(style -> style.withColor(0x757575))))
                .binding(spectatorSpawnDefault, () -> spectatorSpawnDefault,
                        v -> sendWatheMapVarCommand("spectatorSpawnPosition " + v.trim(), parent))
                .controller(StringControllerBuilder::create)
                .build());

        builder.group(group.build());
        builder.group(buildRtpSlotsGroup(parent));
        return builder.build();
    }

    private static OptionGroup buildRtpSlotsGroup(Screen parent) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots"))
                .description(OptionDescription.of(
                        Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.tooltip")))
                .collapsed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        Map<Integer, TeleportationSlot> slots;
        try {
            WatheExtendedWorldComponent wec = world != null
                    ? WatheExtendedWorldComponent.KEY.get(world) : null;
            slots = wec != null ? new java.util.LinkedHashMap<>(wec.getTeleportationSlots()) : new java.util.LinkedHashMap<>();
        } catch (Throwable t) {
            slots = new java.util.LinkedHashMap<>();
        }

        if (slots.isEmpty()) {
            group.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.none")
                            .styled(style -> style.withColor(0xFF5555))));
        } else {
            for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
                final int slotId = entry.getKey();
                String slotDefault = entry.getValue().toCommandArgs();

                group.option(Option.<String>createBuilder()
                        .name(Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot", slotId))
                        .description(OptionDescription.of(
                                Text.translatable("gui.watheextended.config.category.mapvariables.group.rtp_slots.slot.desc")
                                        .styled(style -> style.withColor(0x757575))))
                        .binding(slotDefault, () -> slotDefault,
                                v -> {
                                    String trimmed = v.trim();
                                    String[] parts = trimmed.split("\\s+");
                                    if (parts.length == 5) {
                                        sendCommand("watheextended:rtp slot edit " + slotId
                                                + " " + parts[0] + " " + parts[1] + " " + parts[2]
                                                + " " + parts[3] + " " + parts[4], parent);
                                    } else {
                                        sendCommand("watheextended:rtp slot remove " + slotId, parent);
                                    }
                                })
                        .controller(StringControllerBuilder::create)
                        .build());
            }
        }

        return group.build();
    }

    private static ConfigCategory buildRolesCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.roles"))
                .tooltip(Text.translatable("gui.watheextended.config.category.roles.tooltip"));

        try {
            Set<String> roleId = new LinkedHashSet<>();
            for (String id : RolesId.get()) {
                if (!isBlacklisted(id)) roleId.add(id);
            }

            if (roleId.isEmpty()) {
                builder.option(LabelOption.create(
                        Text.translatable("gui.watheextended.config.roles.empty")
                                .styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, RolesDisplay.RoleDisplay> roleName = RolesDisplay.get();

                for (Map.Entry<String, List<String>> entry : sortByMods(roleId).entrySet()) {
                    OptionGroup.Builder group = OptionGroup.createBuilder()
                            .name(Text.literal(modsNamespace(entry.getKey())))
                            .collapsed(false);

                    for (String id : entry.getValue()) {
                        RolesDisplay.RoleDisplay display = roleName.get(id);
                        Text label = display != null
                                ? display.display().copy().styled(style -> style.withColor(display.color()))
                                : Text.literal(RolesDisplay.localName(id));

                        boolean roleCurrentValue = pendingRoleState.containsKey(id)
                                ? pendingRoleState.get(id)
                                : !ConfigHelper.getDisabledRoles().contains(id);

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(roleCurrentValue,
                                        () -> pendingRoleState.containsKey(id)
                                                ? pendingRoleState.get(id)
                                                : !ConfigHelper.getDisabledRoles().contains(id),
                                        enabled -> {
                                            pendingRoleState.put(id, enabled);
                                            sendCommand("setEnabledRole " + id + " " + enabled, parent);
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true)
                                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                                .build());
                    }
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.roles.error")
                            .styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    private static ConfigCategory buildModifiersCategory(Screen parent) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers"))
                .tooltip(Text.translatable("gui.watheextended.config.category.modifiers.tooltip"));

        try {
            Set<String> modifierId = new LinkedHashSet<>(ModifiersId.get());

            if (modifierId.isEmpty()) {
                builder.option(LabelOption.create(
                        Text.translatable("gui.watheextended.config.category.modifiers.empty")
                                .styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, ModifiersDisplay.ModifierDisplay> modifierName = ModifiersDisplay.get();

                for (Map.Entry<String, List<String>> entry : sortByMods(modifierId).entrySet()) {
                    OptionGroup.Builder group = OptionGroup.createBuilder()
                            .name(Text.literal(modsNamespace(entry.getKey())))
                            .collapsed(false);

                    for (String id : entry.getValue()) {
                        ModifiersDisplay.ModifierDisplay display = modifierName.get(id);
                        Text label = display != null
                                ? display.display().copy().styled(style -> style.withColor(display.color()))
                                : Text.literal(ModifiersDisplay.localName(id));

                        boolean modCurrentValue = pendingModifierState.containsKey(id)
                                ? pendingModifierState.get(id)
                                : !ConfigHelper.getDisabledModifiers().contains(id);

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(modCurrentValue,
                                        () -> pendingModifierState.containsKey(id)
                                                ? pendingModifierState.get(id)
                                                : !ConfigHelper.getDisabledModifiers().contains(id),
                                        enabled -> {
                                            pendingModifierState.put(id, enabled);
                                            sendCommand("setEnabledModifier " + id + " " + enabled, parent);
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .coloured(true)
                                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                                .build());
                    }
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.category.modifiers.error")
                            .styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }

    // sort roles/modifiers by their namespace (mod id) group
    private static Map<String, List<String>> sortByMods(Set<String> ids) {
        Map<String, List<String>> map = new TreeMap<>();
        for (String id : ids) {
            int colon = id.indexOf(':');
            String ns = colon > 0 ? id.substring(0, colon) : id;
            map.computeIfAbsent(ns, k -> new ArrayList<>()).add(id);
        }
        // also sort the roles/modifiers alphabetically within each mod group
        map.values().forEach(Collections::sort);
        return map;
    }

    // supported mods namespace to display name
    private static String modsNamespace(String namespace) {
        return switch (namespace) {
            case "noellesroles" -> "Noelle's Roles";
            case "kinswathe" -> "Kin's Wathe";
            case "stupid_express" -> "Stupid Express";
            case "starexpress" -> "Starry Express";
            // if unsupported mod namespace, it'll just display the namespace as is
            default -> RolesDisplay.localName(namespace + ":x").replace(" X", "").trim();
        };
    }

    public static void clearPendingState() {
        pendingRoleState.clear();
        pendingModifierState.clear();
    }

    private static String rounded(double v) {
        // round double to 4 decimal places and trim zeros
        String round = String.format("%.4f", v);
        round = round.replaceAll("0+$", "").replaceAll("\\.$", "");
        return round;
    }

    private static String boxToArgs(@org.jetbrains.annotations.Nullable net.minecraft.util.math.Box box) {
        if (box == null) return "0 0 0 0 0 0";
        // convert box to command args (<minX>, <minY>, <minZ>, <maxX>, <maxY>, <maxZ>)
        return rounded(box.minX) + " " + rounded(box.minY) + " " + rounded(box.minZ)
                + " " + rounded(box.maxX) + " " + rounded(box.maxY) + " " + rounded(box.maxZ);
    }

    private static String vec3iToArgs(@org.jetbrains.annotations.Nullable net.minecraft.util.math.Vec3i v) {
        if (v == null) return "0 0 0";
        // convert Vec3i to command args (<x>, <y>, <z>)
        return v.getX() + " " + v.getY() + " " + v.getZ();
    }

    private static String posToArgs(
            @org.jetbrains.annotations.Nullable dev.doctor4t.wathe.cca.MapVariablesWorldComponent.PosWithOrientation pos) {
        if (pos == null) return "0 0 0 0 0";
        // convert position and orientation to command args (<x>, <y>, <z>, <yaw>, <pitch>)
        return rounded(pos.pos.x) + " " + rounded(pos.pos.y) + " " + rounded(pos.pos.z)
                + " " + rounded(pos.yaw) + " " + rounded(pos.pitch);
    }

    private static void sendCommand(String command, Screen currentScreen) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            player.networkHandler.sendChatCommand(command);
            // close and reopen the config screen after a few ticks to reflect the changes
            client.setScreen(null);
            reopenParent = currentScreen;
            waitForTicks = 5;
        } catch (Throwable ignored) {
        }
    }

    private static void sendWatheMapVarCommand(String subCommand, Screen parent) {
        sendCommand("wathe:mapVariables set " + subCommand, parent);
    }
}