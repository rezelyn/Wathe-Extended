package cat.rezelyn.watheextended.client.api;

import cat.rezelyn.watheextended.client.WatheExtendedClientConfig;
import cat.rezelyn.watheextended.game.PlayerMovement;
import cat.rezelyn.watheextended.game.ProneState;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side prone handling: the keybind, toggle / hold logic, and syncing the requested state to
 * the local player and the server
 */
public final class ProneClient {
  private static KeyBinding keybind;
  private static boolean toggled;
  private static boolean keyPressed;
  private static boolean requested;
  private static int keyCooldown;

  private ProneClient() {}

  public static void registerKeybind() {
    keybind =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.watheextended.prone",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.wathe.keybinds"));
  }

  /** Per-tick prone logic, call from the client tick event */
  public static void tick(MinecraftClient client) {
    boolean toggleMode = WatheExtendedClientConfig.isProneToggleMode();
    if (keyCooldown > 0) {
      keyCooldown--;
    }
    boolean keyDown = isKeyDown();
    boolean playerCanProne =
        client.player != null && client.player.isAlive() && !client.player.isSpectator();
    boolean inGame = playerCanProne && client.currentScreen == null;

    if (!playerCanProne) {
      toggled = false;
      keyPressed = false;
      keyCooldown = 0;
    } else if (toggleMode) {
      if (inGame && keyDown && !keyPressed && keyCooldown == 0) toggled = !toggled;
      keyPressed = keyDown;
    } else {
      toggled = false;
      keyPressed = keyDown;
    }

    boolean shouldProne = playerCanProne && (toggleMode ? toggled : inGame && keyDown);
    if (shouldProne == requested) return;
    if (keyCooldown > 0) return;

    requested = shouldProne;
    setLocalProne(client, shouldProne);
    sendProne(client, shouldProne);
    if (playerCanProne) keyCooldown = 20; // 1s
  }

  /** Forces the player out of prone and tells the server */
  public static void resetToggle() {
    MinecraftClient client = MinecraftClient.getInstance();
    toggled = false;
    keyPressed = isKeyDown();
    requested = false;
    keyCooldown = 0;
    setLocalProne(client, false);
    sendProne(client, false);
  }

  /** Resets local state only (no packet). Used when joining or leaving */
  public static void reset() {
    toggled = false;
    keyPressed = false;
    requested = false;
    keyCooldown = 0;
    setLocalProne(MinecraftClient.getInstance(), false);
  }

  private static boolean isKeyDown() {
    return keybind != null && keybind.isPressed();
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
}
