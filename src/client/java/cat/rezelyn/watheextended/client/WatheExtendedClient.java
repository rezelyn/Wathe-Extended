package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.client.api.InstinctClient;
import cat.rezelyn.watheextended.client.api.ProneClient;
import cat.rezelyn.watheextended.client.network.ClientNetworking;
import cat.rezelyn.watheextended.client.render.ClientRendering;
import cat.rezelyn.watheextended.client.render.hud.LastStandRenderer;
import cat.rezelyn.watheextended.client.screen.ConfigScreen;
import cat.rezelyn.watheextended.client.screen.GuidebookScreen;
import cat.rezelyn.watheextended.client.screen.config.ClientCategory;
import cat.rezelyn.watheextended.client.utils.CopyPositionHandler;
import cat.rezelyn.watheextended.client.utils.PronounsCache;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import cat.rezelyn.watheextended.network.ClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.client.StarryExpressClient;

/**
 * Client-only entrypoint <br>
 * <br>
 * Only wires things together, the actual logic lives in:
 *
 * <ul>
 *   <li>{@link InstinctClient} - Instinct charge, HUD and audio
 *   <li>{@link ProneClient} - prone keybind and state sync
 *   <li>{@link ClientRendering} - renderers, HUD layers, render layers
 *   <li>{@link ClientNetworking} - server payload receivers
 * </ul>
 */
public class WatheExtendedClient implements ClientModInitializer {

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
          InstinctClient.tick(client);
          ProneClient.tick(client);
          LastStandRenderer.tick();
        });
  }

  private static void registerConnectionEvents() {
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin(client));
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onDisconnect());
  }

  private static void onJoin(MinecraftClient client) {
    InstinctClient.reset();
    ProneClient.reset();
    if (client.isIntegratedServerRunning()) {
      ClientConfig.setRemoteServer(false);
    }
  }

  private static void onDisconnect() {
    InstinctClient.reset();
    ProneClient.reset();
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
    if (world.isClient() && player.isCreative() && stack.isOf(WatheExtendedItems.COPY_POSITION)) {
      CopyPositionHandler.use(player);
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

    ProneClient.registerKeybind();
    ClientRendering.register();
    registerLifecycle();
    registerTick();

    ClientNetworking.register();
    registerConnectionEvents();
    registerItemEvents();

    registerIntegrations();
  }
}
