package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import cat.rezelyn.watheextended.client.render.*;
import cat.rezelyn.watheextended.client.render.entity.PlushBlockEntityRenderer;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.screen.config.ClientCategory;
import cat.rezelyn.watheextended.client.sound.InstinctLoopSound;
import cat.rezelyn.watheextended.game.LastStand;
import cat.rezelyn.watheextended.game.PlayerMovement;
import cat.rezelyn.watheextended.game.ProneState;
import cat.rezelyn.watheextended.game.compat.InstinctAccess;
import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import cat.rezelyn.watheextended.index.WatheExtendedBlocks;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import cat.rezelyn.watheextended.network.ClientConfig;
import cat.rezelyn.watheextended.network.PresetManager;
import cat.rezelyn.watheextended.network.PronounsManager;
import cat.rezelyn.watheextended.network.ServerConfig;
import dev.doctor4t.wathe.client.WatheClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.client.StarryExpressClient;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only entrypoint <br>
 * <br>
 * <b>Responsibilities:</b>
 *
 * <ul>
 *   <li>Registering renderers, HUD layers, block render layers and keybinds
 *   <li>Receiving the payloads sent by the server and forwarding them to the matching client system
 *   <li>Driving the client tick loop
 *   <li>Resetting all client-side state when joining or leaving a server
 * </ul>
 */
public class WatheExtendedClient implements ClientModInitializer {

  private static final String PRONE_KEY_TRANSLATION = "key.watheextended.prone";
  private static final String KEY_CATEGORY = "category.wathe.keybinds";

  private static final String INSTINCT_CAPACITY_KEY = "watheextended.instinct.capacity";
  private static final String INSTINCT_DRAIN_RATE_KEY = "watheextended.instinct.drainRate";
  private static final String INSTINCT_RELOAD_RATE_KEY = "watheextended.instinct.reloadRate";

  private static final int INSTINCT_ZERO_BLOCK_TICKS = 60; // 3s
  private static final int INSTINCT_HUD_HIDE_DELAY_TICKS = 20; // 1s
  private static final float INSTINCT_HUD_FADE_STEP = 0.1f;
  private static final float INSTINCT_SOUND_VOLUME = 0.8f;
  private static final float TICKS_PER_SECOND = 20.0f;

  private static final int PRONE_HOLD_TRANSITION_COOLDOWN_TICKS = 20; // 1s

  private static final float DEFAULT_INSTINCT_CAPACITY_TICKS = 1200.0f;
  private static final float DEFAULT_INSTINCT_DRAIN_RATE_TICKS_PER_SECOND = 60.0f;
  private static final float DEFAULT_INSTINCT_RELOAD_RATE_TICKS_PER_SECOND = 20.0f;
  private static final float MAX_INSTINCT_CAPACITY_TICKS = 1200.0f;

  private static boolean instinctToggled;
  private static boolean instinctKeyWasDown;
  private static boolean previousInstinctActive;
  private static float instinctCharge = DEFAULT_INSTINCT_CAPACITY_TICKS;
  private static int instinctBlockedTicks;
  private static int instinctHudIdleTicks;
  private static float instinctHudOpacity;
  private static InstinctLoopSound instinctLoopSound;

  private static KeyBinding proneKeybind;
  private static boolean proneToggled;
  private static boolean proneKeyWasDown;
  private static boolean proneRequested;
  private static int proneHoldTransitionCooldownTicks;

  public static float getInstinctCharge() {
    float capacity = getInstinctCapacity();
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
    return client.player != null
        && (client.player.isCreative()
            || client.player.isSpectator()
            || getInstinctCapacity() <= 0.0f);
  }

  public static boolean isInstinctActive() {
    if (!canActivateInstinct()) return false;
    return WatheExtendedClientConfig.isInstinctToggleMode()
        ? instinctToggled
        : WatheClient.instinctKeybind.isPressed();
  }

  public static void resetInstinctToggle() {
    instinctToggled = false;
  }

  private static boolean canActivateInstinct() {
    return canUseInstinct()
        && !isInstinctBlocked()
        && (hasUnlimitedInstinct() || instinctCharge > 0.0f);
  }

  private static boolean isInstinctBlocked() {
    return !hasUnlimitedInstinct() && instinctBlockedTicks > 0;
  }

  private static float getInstinctCapacity() {
    return Math.clamp(
        ClientConfig.getFloat(INSTINCT_CAPACITY_KEY, DEFAULT_INSTINCT_CAPACITY_TICKS),
        0.0f,
        MAX_INSTINCT_CAPACITY_TICKS);
  }

  private static float getInstinctDrainRate() {
    return Math.max(
        0.0f,
        ClientConfig.getFloat(
            INSTINCT_DRAIN_RATE_KEY, DEFAULT_INSTINCT_DRAIN_RATE_TICKS_PER_SECOND));
  }

  private static float getInstinctReloadRate() {
    return Math.max(
        0.0f,
        ClientConfig.getFloat(
            INSTINCT_RELOAD_RATE_KEY, DEFAULT_INSTINCT_RELOAD_RATE_TICKS_PER_SECOND));
  }

  /**
   * Per-tick Instinct logic
   *
   * <ul>
   *   <li>Handles the toggle key
   *   <li>Drains or recharges the charge
   *   <li>Fades the HUD in and out
   *   <li>Plays the activation / deactivation audio when the active state changes
   * </ul>
   */
  private static void tickInstinct(MinecraftClient client) {
    pollInstinctKey();

    boolean activeBeforeTick = isInstinctActive();
    updateInstinctCharge(activeBeforeTick);
    boolean activeAfterTick = isInstinctActive();

    updateInstinctHud(activeBeforeTick, activeAfterTick);

    if (activeAfterTick != previousInstinctActive) {
      if (activeAfterTick) {
        startInstinctAudio(client);
      } else {
        stopInstinctAudio(client);
      }
      previousInstinctActive = activeAfterTick;
    }
  }

  private static void pollInstinctKey() {
    boolean keyDown =
        WatheClient.instinctKeybind != null && WatheClient.instinctKeybind.isPressed();
    boolean pressedThisTick = keyDown && !instinctKeyWasDown;
    instinctKeyWasDown = keyDown;

    if (!pressedThisTick || !WatheExtendedClientConfig.isInstinctToggleMode()) return;
    if (instinctToggled) {
      instinctToggled = false;
    } else if (canActivateInstinct()) {
      instinctToggled = true;
    }
  }

  /**
   * Drains the charge while active and recharges it otherwise. Running out blocks Instinct for
   * {@value #INSTINCT_ZERO_BLOCK_TICKS} ticks and switches toggled Instinct off, used to prevent
   * the player from holding it indefinitely and causing the audio cues to repeat rapidly and
   * basically cheesing the mechanic
   *
   * <p>Unlimited Instinct keeps the charge full
   *
   * @param active whether Instinct was active at the start of this tick
   */
  private static void updateInstinctCharge(boolean active) {
    float capacity = getInstinctCapacity();
    if (hasUnlimitedInstinct()) {
      instinctCharge = capacity;
      instinctBlockedTicks = 0;
      return;
    }

    if (instinctBlockedTicks > 0) instinctBlockedTicks--;
    if (active) {
      instinctCharge -= getInstinctDrainRate() / TICKS_PER_SECOND;
    } else {
      instinctCharge += getInstinctReloadRate() / TICKS_PER_SECOND;
    }
    instinctCharge = Math.clamp(instinctCharge, 0.0f, capacity);

    if (active && instinctCharge <= 0.0f) {
      instinctBlockedTicks = INSTINCT_ZERO_BLOCK_TICKS;
      instinctToggled = false;
    }
  }

  /**
   * Fades the HUD towards its target opacity. It stays visible while Instinct is active, for
   * {@value #INSTINCT_HUD_HIDE_DELAY_TICKS} ticks afterward, or permanently if the client is using
   * the "always show" option
   */
  private static void updateInstinctHud(boolean activeBeforeTick, boolean activeAfterTick) {
    if (activeBeforeTick || activeAfterTick) {
      instinctHudIdleTicks = 0;
    } else {
      instinctHudIdleTicks = Math.min(INSTINCT_HUD_HIDE_DELAY_TICKS + 1, instinctHudIdleTicks + 1);
    }

    boolean shouldShowHud =
        !hasUnlimitedInstinct()
            && canUseInstinct()
            && (WatheExtendedClientConfig.alwaysShowInstinctHud
                || activeAfterTick
                || instinctHudIdleTicks <= INSTINCT_HUD_HIDE_DELAY_TICKS);

    if (shouldShowHud) {
      instinctHudOpacity =
          Math.min(
              WatheExtendedClientConfig.instinctHudOpacity,
              instinctHudOpacity + INSTINCT_HUD_FADE_STEP);
    } else {
      instinctHudOpacity = Math.max(0.0f, instinctHudOpacity - INSTINCT_HUD_FADE_STEP);
    }
  }

  private static void startInstinctAudio(MinecraftClient client) {
    playInstinctSound(client, WatheExtendedSounds.INSTINCT_IN);
    if (instinctLoopSound == null
        || instinctLoopSound.isDone()
        || !client.getSoundManager().isPlaying(instinctLoopSound)) {
      instinctLoopSound = new InstinctLoopSound();
      client.getSoundManager().play(instinctLoopSound);
    } else {
      instinctLoopSound.fadeIn();
    }
  }

  private static void stopInstinctAudio(MinecraftClient client) {
    playInstinctSound(client, WatheExtendedSounds.INSTINCT_OUT);
    if (instinctLoopSound != null) {
      instinctLoopSound.fadeOut();
    }
  }

  private static void playInstinctSound(MinecraftClient client, SoundEvent sound) {
    client
        .getSoundManager()
        .play(PositionedSoundInstance.ambient(sound, INSTINCT_SOUND_VOLUME, 1.0f));
  }

  /** Resets all Instinct state and stops the looping ambience. Used when joining or leaving */
  private static void resetInstinctState() {
    instinctToggled = false;
    instinctKeyWasDown = false;
    previousInstinctActive = false;
    instinctCharge = getInstinctCapacity();
    instinctBlockedTicks = 0;
    instinctHudIdleTicks = 0;
    instinctHudOpacity = 0.0f;
    if (instinctLoopSound != null) {
      MinecraftClient.getInstance().getSoundManager().stop(instinctLoopSound);
      instinctLoopSound = null;
    }
  }

  private static void tickProning(MinecraftClient client) {
    boolean toggleMode = WatheExtendedClientConfig.isProneToggleMode();
    if (!toggleMode && proneHoldTransitionCooldownTicks > 0) {
      proneHoldTransitionCooldownTicks--;
    }
    boolean keyDown = isProneKeyDown();
    boolean playerCanProne =
        client.player != null && client.player.isAlive() && !client.player.isSpectator();
    boolean inGame = playerCanProne && client.currentScreen == null;

    if (!playerCanProne) {
      proneToggled = false;
      proneKeyWasDown = false;
      proneHoldTransitionCooldownTicks = 0;
    } else if (toggleMode) {
      if (inGame && keyDown && !proneKeyWasDown) proneToggled = !proneToggled;
      proneKeyWasDown = keyDown;
    } else {
      proneToggled = false;
      proneKeyWasDown = keyDown;
    }

    boolean shouldProne = playerCanProne && (toggleMode ? proneToggled : inGame && keyDown);
    if (shouldProne == proneRequested) return;
    if (!toggleMode && proneHoldTransitionCooldownTicks > 0) return;

    proneRequested = shouldProne;
    setLocalProne(client, shouldProne);
    sendProne(client, shouldProne);
    if (!toggleMode) {
      proneHoldTransitionCooldownTicks = PRONE_HOLD_TRANSITION_COOLDOWN_TICKS;
    }
  }

  public static void resetProneToggle() {
    MinecraftClient client = MinecraftClient.getInstance();
    proneToggled = false;
    proneKeyWasDown = isProneKeyDown();
    proneRequested = false;
    proneHoldTransitionCooldownTicks = 0;
    setLocalProne(client, false);
    sendProne(client, false);
  }

  private static void resetProningState() {
    proneToggled = false;
    proneKeyWasDown = false;
    proneRequested = false;
    proneHoldTransitionCooldownTicks = 0;
    setLocalProne(MinecraftClient.getInstance(), false);
  }

  private static boolean isProneKeyDown() {
    return proneKeybind != null && proneKeybind.isPressed();
  }

  private static void setLocalProne(MinecraftClient client, boolean prone) {
    if (client.player instanceof ProneState proneState) {
      proneState.watheextended$setProneRequested(prone);
    }
  }

  private static void sendProne(MinecraftClient client, boolean prone) {
    if (client.getNetworkHandler() != null
        && ClientPlayNetworking.canSend(PlayerMovement.SetPronePayload.ID)) {
      ClientPlayNetworking.send(new PlayerMovement.SetPronePayload(prone));
    }
  }

  private static void registerKeybinds() {
    proneKeybind =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                PRONE_KEY_TRANSLATION, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY));
  }

  private static void registerRendering() {
    BoxDebugRenderer.register();

    HudRenderCallback.EVENT.register((context, tickCounter) -> LastStandRenderer.render(context));
    HudRenderCallback.EVENT.register(InstinctHudRenderer::render);

    BlockEntityRendererFactories.register(
        WatheExtendedBlockEntities.ISH_PLUSH, PlushBlockEntityRenderer::new);

    BlockRenderLayerMap.INSTANCE.putBlocks(
        RenderLayer.getCutout(),
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
        WatheExtendedBlocks.SNOWY_FLOWERING_AZALEA_LEAVES);
  }

  private static void registerLifecycle() {
    ClientLifecycleEvents.CLIENT_STARTED.register(
        client -> {
          ClientCategory.loadImages();
          unbindVanillaKeybinds(client);
        });
  }

  private static void unbindVanillaKeybinds(MinecraftClient client) {
    client.options.saveToolbarActivatorKey.setBoundKey(InputUtil.UNKNOWN_KEY);
    client.options.loadToolbarActivatorKey.setBoundKey(InputUtil.UNKNOWN_KEY);
    client.options.swapHandsKey.setBoundKey(InputUtil.UNKNOWN_KEY);
    KeyBinding.updateKeysByCode();
    client.options.write();
  }

  private static void registerTick() {
    ConfigScreen.registerTickHandler();
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          tickInstinct(client);
          tickProning(client);
          LastStandRenderer.tick();
        });
  }

  private static void registerNetworking() {
    ClientPlayNetworking.registerGlobalReceiver(
        LastStand.LastStandPayload.ID, WatheExtendedClient::handleLastStand);
    ClientPlayNetworking.registerGlobalReceiver(
        ServerConfig.SyncPayload.ID, WatheExtendedClient::handleConfigSync);
    ClientPlayNetworking.registerGlobalReceiver(
        PronounsManager.SyncPayload.ID, WatheExtendedClient::handlePronounsSync);
    ClientPlayNetworking.registerGlobalReceiver(
        PresetManager.ListPayload.ID, WatheExtendedClient::handlePresetList);
    ClientPlayNetworking.registerGlobalReceiver(
        PresetManager.ResultPayload.ID, WatheExtendedClient::handlePresetResult);
  }

  private static void handleLastStand(
      LastStand.LastStandPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> LastStandRenderer.start(payload.totalTicks()));
  }

  private static void handleConfigSync(
      ServerConfig.SyncPayload payload, ClientPlayNetworking.Context context) {
    ClientConfig.setRemoteServer(true);
    ClientConfig.update(payload.data());
    ConfigScreen.onCacheUpdated();
    GuidebookScreen.invalidateIfOpen();
  }

  private static void handlePronounsSync(
      PronounsManager.SyncPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> PronounsCache.set(payload.uuid(), payload.pronouns()));
  }

  private static void handlePresetList(
      PresetManager.ListPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ConfigScreen.onPresetList(payload));
  }

  private static void handlePresetResult(
      PresetManager.ResultPayload payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> ConfigScreen.onPresetResult(payload));
  }

  private static void registerConnectionEvents() {
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin(client));
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onDisconnect());
  }

  private static void onJoin(MinecraftClient client) {
    resetInstinctState();
    resetProningState();
    if (client.isIntegratedServerRunning()) {
      ClientConfig.setRemoteServer(false);
    }
  }

  private static void onDisconnect() {
    resetInstinctState();
    resetProningState();
    ConfigScreen.clearPendingState();
    ClientConfig.clear();
    PronounsCache.clear();
    LastStandRenderer.stop();
  }

  private static void registerItemEvents() {
    UseItemCallback.EVENT.register(WatheExtendedClient::onUseItem);
  }

  private static TypedActionResult<ItemStack> onUseItem(
      PlayerEntity player, World world, Hand hand) {
    ItemStack stack = player.getStackInHand(hand);
    if (world.isClient() && stack.getItem() == WatheExtendedItems.GUIDEBOOK) {
      MinecraftClient.getInstance().setScreen(new GuidebookScreen());
    }
    return TypedActionResult.pass(stack);
  }

  private static void registerIntegrations() {
    if (FabricLoader.getInstance().isModLoaded("starexpress")) {
      ClientLifecycleEvents.CLIENT_STARTED.register(client -> fixStarExpressAbilityBind());
    }
  }

  private static void fixStarExpressAbilityBind() {
    try {
      if (StarryExpressClient.abilityBind == null) {
        StarryExpressClient.abilityBind = NoellesrolesClient.abilityBind;
      }
    } catch (Throwable ignored) {
    }
  }

  /**
   * Initializes the client side of the mod
   *
   * <p>Order matters: the client config is loaded first since the keybinds and tick handlers read
   * it, and integrations come last so they can rely on everything else being registered
   */
  @Override
  public void onInitializeClient() {
    WatheExtendedClientConfig.load();

    registerKeybinds();
    registerRendering();
    registerLifecycle();
    registerTick();

    registerNetworking();
    registerConnectionEvents();
    registerItemEvents();

    registerIntegrations();
  }
}
