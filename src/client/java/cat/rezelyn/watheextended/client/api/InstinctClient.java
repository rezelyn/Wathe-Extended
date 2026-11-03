package cat.rezelyn.watheextended.client.api;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.client.sound.InstinctLoopSound;
import cat.rezelyn.watheextended.game.compat.InstinctAccess;
import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;

/**
 * Client-side Instinct state machine <br>
 * <br>
 * <b>Responsibilities:</b>
 *
 * <ul>
 *   <li>Handles the toggle / hold key
 *   <li>Drains or recharges the charge
 *   <li>Fades the HUD in and out
 *   <li>Plays the activation / deactivation audio when the active state changes
 * </ul>
 */
public final class InstinctClient {

  private static final String CAPACITY_KEY = "watheextended.instinct.capacity";
  private static final String DRAIN_RATE_KEY = "watheextended.instinct.drainRate";
  private static final String RELOAD_RATE_KEY = "watheextended.instinct.reloadRate";

  private static final int ZERO_BLOCK_TICKS = 60; // 3s
  private static final int HUD_HIDE_DELAY_TICKS = 20; // 1s
  private static final float HUD_FADE_STEP = 0.1f;
  private static final float SOUND_VOLUME = 0.8f;
  private static final float TICKS_PER_SECOND = 20.0f;

  private static final float DEFAULT_CAPACITY_TICKS = 1200.0f;
  private static final float DEFAULT_DRAIN_RATE_TICKS_PER_SECOND = 60.0f;
  private static final float DEFAULT_RELOAD_RATE_TICKS_PER_SECOND = 20.0f;
  private static final float MAX_CAPACITY_TICKS = 1200.0f;

  private static boolean toggled;
  private static boolean keyWasDown;
  private static boolean previousActive;
  private static float charge = DEFAULT_CAPACITY_TICKS;
  private static int blockedTicks;
  private static int hudIdleTicks;
  private static float hudOpacity;
  private static InstinctLoopSound loopSound;

  private InstinctClient() {}

  /** Charge as a 0..1 fraction of the capacity */
  public static float getCharge() {
    float capacity = getCapacity();
    return capacity <= 0.0f ? 1.0f : charge / capacity;
  }

  public static boolean shouldRenderHud() {
    return !hasUnlimited() && hudOpacity > 0.0f;
  }

  public static float getHudOpacity() {
    return hudOpacity;
  }

  public static boolean canUse() {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null || WatheClient.instinctKeybind == null) return false;
    try {
      return InstinctAccess.canUse(client.player);
    } catch (Throwable ignored) {
      return false;
    }
  }

  public static boolean hasUnlimited() {
    MinecraftClient client = MinecraftClient.getInstance();
    return client.player != null
        && (client.player.isCreative() || client.player.isSpectator() || getCapacity() <= 0.0f);
  }

  public static boolean isActive() {
    if (!canActivate()) return false;
    return WatheExtendedClientConfig.isInstinctToggleMode()
        ? toggled
        : WatheClient.instinctKeybind.isPressed();
  }

  public static void resetToggle() {
    toggled = false;
  }

  /** Resets all Instinct state and stops the looping ambience. Used when joining or leaving */
  public static void reset() {
    toggled = false;
    keyWasDown = false;
    previousActive = false;
    charge = getCapacity();
    blockedTicks = 0;
    hudIdleTicks = 0;
    hudOpacity = 0.0f;
    if (loopSound != null) {
      MinecraftClient.getInstance().getSoundManager().stop(loopSound);
      loopSound = null;
    }
  }

  /** Per-tick Instinct logic, call from the client tick event */
  public static void tick(MinecraftClient client) {
    pollKey();

    boolean activeBeforeTick = isActive();
    updateCharge(activeBeforeTick);
    boolean activeAfterTick = isActive();

    updateHud(activeBeforeTick, activeAfterTick);

    if (activeAfterTick != previousActive) {
      if (activeAfterTick) {
        startAudio(client);
      } else {
        stopAudio(client);
      }
      previousActive = activeAfterTick;
    }
  }

  private static boolean canActivate() {
    return canUse() && !isBlocked() && (hasUnlimited() || charge > 0.0f);
  }

  private static boolean isBlocked() {
    return !hasUnlimited() && blockedTicks > 0;
  }

  private static float getCapacity() {
    return Math.clamp(
        ClientConfig.getFloat(CAPACITY_KEY, DEFAULT_CAPACITY_TICKS), 0.0f, MAX_CAPACITY_TICKS);
  }

  private static float getDrainRate() {
    return Math.max(
        0.0f, ClientConfig.getFloat(DRAIN_RATE_KEY, DEFAULT_DRAIN_RATE_TICKS_PER_SECOND));
  }

  private static float getReloadRate() {
    return Math.max(
        0.0f, ClientConfig.getFloat(RELOAD_RATE_KEY, DEFAULT_RELOAD_RATE_TICKS_PER_SECOND));
  }

  private static void pollKey() {
    boolean keyDown =
        WatheClient.instinctKeybind != null && WatheClient.instinctKeybind.isPressed();
    boolean pressedThisTick = keyDown && !keyWasDown;
    keyWasDown = keyDown;

    if (!pressedThisTick || !WatheExtendedClientConfig.isInstinctToggleMode()) return;
    if (toggled) {
      toggled = false;
    } else if (canActivate()) {
      toggled = true;
    }
  }

  /**
   * Drains the charge while active and recharges it otherwise. Running out blocks Instinct for
   * {@value #ZERO_BLOCK_TICKS} ticks and switches toggled Instinct off, used to prevent the player
   * from holding it indefinitely and causing the audio cues to repeat rapidly and basically
   * cheesing the mechanic
   *
   * <p>Unlimited Instinct keeps the charge full
   *
   * @param active whether Instinct was active at the start of this tick
   */
  private static void updateCharge(boolean active) {
    float capacity = getCapacity();
    if (hasUnlimited()) {
      charge = capacity;
      blockedTicks = 0;
      return;
    }

    if (blockedTicks > 0) blockedTicks--;
    if (active) {
      charge -= getDrainRate() / TICKS_PER_SECOND;
    } else {
      charge += getReloadRate() / TICKS_PER_SECOND;
    }
    charge = Math.clamp(charge, 0.0f, capacity);

    if (active && charge <= 0.0f) {
      blockedTicks = ZERO_BLOCK_TICKS;
      toggled = false;
    }
  }

  /**
   * Fades the HUD towards its target opacity. It stays visible while Instinct is active, for
   * {@value #HUD_HIDE_DELAY_TICKS} ticks afterward, or permanently if the client is using the
   * "always show" option
   */
  private static void updateHud(boolean activeBeforeTick, boolean activeAfterTick) {
    if (activeBeforeTick || activeAfterTick) {
      hudIdleTicks = 0;
    } else {
      hudIdleTicks = Math.min(HUD_HIDE_DELAY_TICKS + 1, hudIdleTicks + 1);
    }

    boolean shouldShowHud =
        !hasUnlimited()
            && canUse()
            && (WatheExtendedClientConfig.alwaysShowInstinctHud
                || activeAfterTick
                || hudIdleTicks <= HUD_HIDE_DELAY_TICKS);

    if (shouldShowHud) {
      hudOpacity =
          Math.min(WatheExtendedClientConfig.instinctHudOpacity, hudOpacity + HUD_FADE_STEP);
    } else {
      hudOpacity = Math.max(0.0f, hudOpacity - HUD_FADE_STEP);
    }
  }

  private static void startAudio(MinecraftClient client) {
    playSound(client, WatheExtendedSounds.INSTINCT_IN);
    if (loopSound == null || loopSound.isDone() || !client.getSoundManager().isPlaying(loopSound)) {
      loopSound = new InstinctLoopSound();
      client.getSoundManager().play(loopSound);
    } else {
      loopSound.fadeIn();
    }
  }

  private static void stopAudio(MinecraftClient client) {
    playSound(client, WatheExtendedSounds.INSTINCT_OUT);
    if (loopSound != null) {
      loopSound.fadeOut();
    }
  }

  private static void playSound(MinecraftClient client, SoundEvent sound) {
    client.getSoundManager().play(PositionedSoundInstance.ambient(sound, SOUND_VOLUME, 1.0f));
  }
}
