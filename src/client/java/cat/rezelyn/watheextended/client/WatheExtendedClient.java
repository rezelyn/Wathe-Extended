package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import cat.rezelyn.watheextended.client.render.BoxDebugRenderer;
import cat.rezelyn.watheextended.client.render.IshPlushBlockEntityRenderer;
import cat.rezelyn.watheextended.client.render.InstinctHudRenderer;
import cat.rezelyn.watheextended.api.InstinctAccess;
import cat.rezelyn.watheextended.client.render.LastStandRenderer;
import cat.rezelyn.watheextended.client.sound.InstinctLoopSound;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.config.ClientCategory;
import cat.rezelyn.watheextended.game.LastStand;
import cat.rezelyn.watheextended.game.PronounsManager;
import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.TypedActionResult;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.client.StarryExpressClient;
import dev.doctor4t.wathe.client.WatheClient;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;

public class WatheExtendedClient implements ClientModInitializer {
    private static final int INSTINCT_ZERO_BLOCK_TICKS = 60; // 3s
    private static final int INSTINCT_HUD_HIDE_DELAY_TICKS = 20; // 1s
    private static final float INSTINCT_HUD_FADE_STEP = 0.1f;

    @Override
    public void onInitializeClient() {
        WatheExtendedClientConfig.load();
        BoxDebugRenderer.register();
        ConfigScreen.registerTickHandler();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean instinctKeyDown = WatheClient.instinctKeybind != null && WatheClient.instinctKeybind.isPressed();
            if (WatheExtendedClientConfig.isInstinctToggleMode() && instinctKeyDown && !instinctKeyWasDown) {
                if (instinctToggled) {
                    instinctToggled = false;
                } else if (!isInstinctBlocked() && (hasUnlimitedInstinct() || instinctCharge > 0.0f) && canUseInstinct()) {
                    instinctToggled = true;
                }
            }
            instinctKeyWasDown = instinctKeyDown;
            tickInstinct(client);
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ClientCategory.loadImages());
        HudRenderCallback.EVENT.register((context, tickCounter) -> LastStandRenderer.render(context));
        HudRenderCallback.EVENT.register(InstinctHudRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> LastStandRenderer.tick());

        BlockEntityRendererFactories.register(WatheExtendedBlockEntities.ISH_PLUSH, IshPlushBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                WatheExtendedBlocks.ANTHRACITE_STEEL_ORNAMENT,
                WatheExtendedBlocks.KHAKI_STEEL_ORNAMENT,
                WatheExtendedBlocks.MAROON_STEEL_ORNAMENT,
                WatheExtendedBlocks.MUNTZ_STEEL_ORNAMENT,
                WatheExtendedBlocks.NAVY_STEEL_ORNAMENT,
                WatheExtendedBlocks.SNOWY_OAK_LEAVES,
                WatheExtendedBlocks.SNOWY_SPRUCE_LEAVES,
                WatheExtendedBlocks.SNOWY_BIRCH_LEAVES,
                WatheExtendedBlocks.SNOWY_JUNGLE_LEAVES,
                WatheExtendedBlocks.SNOWY_ACACIA_LEAVES,
                WatheExtendedBlocks.SNOWY_DARK_OAK_LEAVES,
                WatheExtendedBlocks.SNOWY_MANGROVE_LEAVES,
                WatheExtendedBlocks.SNOWY_CHERRY_LEAVES,
                WatheExtendedBlocks.SNOWY_AZALEA_LEAVES,
                WatheExtendedBlocks.SNOWY_FLOWERING_AZALEA_LEAVES
        );

        ClientPlayNetworking.registerGlobalReceiver(LastStand.LastStandPayload.ID, (payload, context) -> context.client().execute(() -> LastStandRenderer.start(payload.totalTicks())));
        ClientPlayNetworking.registerGlobalReceiver(ServerConfig.SyncPayload.ID, (payload, context) -> {
            ClientConfig.setRemoteServer(true);
            ClientConfig.update(payload.data());
            ConfigScreen.onCacheUpdated();
            GuidebookScreen.invalidateIfOpen();
        });
        ClientPlayNetworking.registerGlobalReceiver(PronounsManager.SyncPayload.ID, (payload, context) -> context.client().execute(() -> PronounsCache.set(payload.uuid(), payload.pronouns())));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            resetInstinctState();
            if (client.isIntegratedServerRunning()) {
                ClientConfig.setRemoteServer(false);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetInstinctState();
            ConfigScreen.clearPendingState();
            ClientConfig.clear();
            PronounsCache.clear();
            LastStandRenderer.stop();
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) return TypedActionResult.pass(player.getStackInHand(hand));
            var stack = player.getStackInHand(hand);
            if (stack.getItem() == WatheExtendedItems.GUIDEBOOK) {
                MinecraftClient.getInstance().setScreen(new GuidebookScreen());
                return TypedActionResult.pass(stack);
            }
            return TypedActionResult.pass(stack);
        });

        if (FabricLoader.getInstance().isModLoaded("starexpress")) {
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> fixStarExpressAbilityBind());
        }
    }

    private static void fixStarExpressAbilityBind() {
        if (!FabricLoader.getInstance().isModLoaded("starexpress")) return;
        try {
            if (StarryExpressClient.abilityBind == null) {
                StarryExpressClient.abilityBind = NoellesrolesClient.abilityBind;
            }
        } catch (Throwable ignored) {
        }
    }

    private static final float DEFAULT_INSTINCT_CAPACITY = 100.0f;
    private static final float DEFAULT_INSTINCT_DRAIN_RATE = 25.0f;
    private static final float DEFAULT_INSTINCT_RELOAD_RATE = 25.0f;
    private static final float INSTINCT_FULL_CAPACITY_SECONDS = 60.0f;
    private static boolean instinctToggled;
    private static boolean instinctKeyWasDown;
    private static boolean previousInstinctActive;
    private static float instinctCharge = INSTINCT_FULL_CAPACITY_SECONDS;
    private static int instinctBlockedTicks;
    private static int instinctHudIdleTicks;
    private static float instinctHudOpacity;
    private static InstinctLoopSound instinctLoopSound;

    private static void tickInstinct(MinecraftClient client) {
        boolean activeBeforeTick = isInstinctActive();
        if (hasUnlimitedInstinct()) {
            instinctCharge = getInstinctCapacitySeconds();
            instinctBlockedTicks = 0;
        } else {
            if (instinctBlockedTicks > 0) instinctBlockedTicks--;
            if (activeBeforeTick) {
                instinctCharge -= getInstinctDrainRate() / 10.0f / 20.0f;
            } else {
                instinctCharge += getInstinctReloadRate() / 10.0f / 20.0f;
            }
        }
        instinctCharge = Math.max(0.0f, Math.min(getInstinctCapacitySeconds(), instinctCharge));

        if (!hasUnlimitedInstinct() && activeBeforeTick && instinctCharge <= 0.0f) {
            instinctCharge = 0.0f;
            instinctBlockedTicks = INSTINCT_ZERO_BLOCK_TICKS;
            instinctToggled = false;
        }

        boolean activeAfterTick = isInstinctActive();
        if (activeBeforeTick || activeAfterTick) {
            instinctHudIdleTicks = 0;
        } else {
            instinctHudIdleTicks = Math.min(INSTINCT_HUD_HIDE_DELAY_TICKS + 1, instinctHudIdleTicks + 1);
        }
        boolean shouldShowHud = !hasUnlimitedInstinct() && canUseInstinct()
                && (WatheExtendedClientConfig.getAlwaysShowInstinctHud()
                || activeAfterTick || instinctHudIdleTicks <= INSTINCT_HUD_HIDE_DELAY_TICKS);
        if (shouldShowHud) {
            instinctHudOpacity = Math.min(WatheExtendedClientConfig.getInstinctHudOpacity(), instinctHudOpacity + INSTINCT_HUD_FADE_STEP);
        } else {
            instinctHudOpacity = Math.max(0.0f, instinctHudOpacity - INSTINCT_HUD_FADE_STEP);
        }
        if (activeAfterTick != previousInstinctActive) {
            if (activeAfterTick) {
                startInstinctAudio(client);
            } else {
                stopInstinctAudio(client);
            }
            previousInstinctActive = activeAfterTick;
        }
    }

    private static void startInstinctAudio(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.ambient(WatheExtendedSounds.INSTINCT_IN, 0.8f, 1.0f));
        if (instinctLoopSound == null || instinctLoopSound.isDone() || !client.getSoundManager().isPlaying(instinctLoopSound)) {
            instinctLoopSound = new InstinctLoopSound();
            client.getSoundManager().play(instinctLoopSound);
        } else {
            instinctLoopSound.fadeIn();
        }
    }

    private static void stopInstinctAudio(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.ambient(WatheExtendedSounds.INSTINCT_OUT, 0.8f, 1.0f));
        if (instinctLoopSound != null) {
            instinctLoopSound.fadeOut();
        }
    }

    private static float getInstinctCapacity() {
        return Math.clamp(ClientConfig.getFloat("watheextended.instinct.capacity", DEFAULT_INSTINCT_CAPACITY), 0.0f, 100.0f);
    }

    private static float getInstinctCapacitySeconds() {
        return INSTINCT_FULL_CAPACITY_SECONDS * getInstinctCapacity() / 100.0f;
    }

    private static float getInstinctDrainRate() {
        return Math.max(0.0f, ClientConfig.getFloat("watheextended.instinct.drainRate", DEFAULT_INSTINCT_DRAIN_RATE));
    }

    private static float getInstinctReloadRate() {
        return Math.max(0.0f, ClientConfig.getFloat("watheextended.instinct.reloadRate", DEFAULT_INSTINCT_RELOAD_RATE));
    }

    public static float getInstinctCharge() {
        float capacity = getInstinctCapacitySeconds();
        return capacity <= 0.0f ? 1.0f : instinctCharge / capacity;
    }

    public static boolean shouldRenderInstinctHud() {
        return !hasUnlimitedInstinct() && instinctHudOpacity > 0.0f;
    }

    public static float getInstinctHudOpacity() {
        return instinctHudOpacity;
    }

    public static boolean canUseInstinct() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || WatheClient.instinctKeybind == null) return false;
        try {
            return InstinctAccess.canUse(client.player);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean hasUnlimitedInstinct() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && (client.player.isCreative() || client.player.isSpectator() || getInstinctCapacity() <= 0.0f);
    }

    private static boolean isInstinctBlocked() {
        return !hasUnlimitedInstinct() && instinctBlockedTicks > 0;
    }

    public static boolean isInstinctActive() {
        if (!canUseInstinct() || isInstinctBlocked()) return false;
        if (!hasUnlimitedInstinct() && instinctCharge <= 0.0f) return false;
        return WatheExtendedClientConfig.isInstinctToggleMode() ? instinctToggled : WatheClient.instinctKeybind.isPressed();
    }

    public static void resetInstinctToggle() {
        instinctToggled = false;
    }

    private static void resetInstinctState() {
        instinctToggled = false;
        instinctKeyWasDown = false;
        previousInstinctActive = false;
        instinctCharge = getInstinctCapacitySeconds();
        instinctBlockedTicks = 0;
        instinctHudIdleTicks = 0;
        instinctHudOpacity = 0.0f;
        if (instinctLoopSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(instinctLoopSound);
            instinctLoopSound = null;
        }
    }
}
