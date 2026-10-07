package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.command.*;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.game.modifiers.WatheExtendedModifiers;
import cat.rezelyn.watheextended.game.modifiers.introverted.IntrovertedModifier;
import cat.rezelyn.watheextended.game.roles.arsonist.ArsonistDousedNotification;
import cat.rezelyn.watheextended.game.utils.TeleportationHandler;
import cat.rezelyn.watheextended.index.*;
import cat.rezelyn.watheextended.network.ConfigSync;
import cat.rezelyn.watheextended.network.PresetManager;
import cat.rezelyn.watheextended.network.PronounsManager;
import cat.rezelyn.watheextended.network.ServerConfig;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common (server + client) entrypoint <br>
 * <br>
 * <b>Responsibilities:</b>
 *
 * <ul>
 *   <li>Bootstrapping registries, configs and third-party integrations
 *   <li>Exposing every tweakable setting through {@link ServerConfig} so it can be synced to and
 *       edited from the in-game config screen
 *   <li>Registering network payloads and their server-side receivers
 *   <li>Driving the world tick loop
 * </ul>
 */
public class WatheExtended implements ModInitializer {

  public static final String MOD_ID = "watheextended";
  public static final Logger LOGGER = LoggerFactory.getLogger(WatheExtended.class);

  private static final int PERMISSION_LEVEL = 2;
  private static final int CONFIG_COMMAND_PERMISSION_LEVEL = 4;
  private static final String COMMAND_CHANGE_PREFIX = "cmd:";

  private static final int ITEM_TICK_INTERVAL = 20;
  private static final int ITEM_BOUNDS_CHECK_INTERVAL = 5;

  public static @NotNull Identifier id(String name) {
    return Identifier.of(MOD_ID, name);
  }

  private static void registerContent() {
    WatheExtendedGroup.initialize();
    WatheExtendedItems.initialize();
    WatheExtendedBlocks.initialize();
    WatheExtendedBlockEntities.initialize();
    WatheExtendedSounds.initialize();
    WatheExtendedModifiers.initialize();
  }

  private static void loadConfigs() {
    WatheExtendedServerConfig.load();
    PronounsManager.load();
    ItemPrices.registerAll();
    ItemPrices.applyAll();
    ItemCooldowns.registerAll();
    ItemCooldowns.applyAll();
  }

  private static void registerIntegrations() {
    cat.rezelyn.watheextended.api.config.hml.ConfigHelper.registerEntries();
    cat.rezelyn.watheextended.api.config.kinswathe.ConfigHelper.registerEntries();
    cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.registerEntries();
    cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.registerEntries();
    cat.rezelyn.watheextended.api.config.starexpress.ConfigHelper.registerEntries();
  }

  private static void registerCommands() {
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> {
          MapVariablesCommand.register(dispatcher);
          TeleportationSlotsCommand.register(dispatcher);
          GamemodeRulesCommand.register(dispatcher);
          MapEffectCommand.register(dispatcher);
          AddonsConfigCommand.register(dispatcher);
          PronounsCommand.register(dispatcher);
        });
  }

  /**
   * Clears effects from every players once the game is stopping: all vanilla status effects, Wathe
   * poison and Noelle's Roles infection <br>
   * Does nothing until the game is in {@code STOPPING} state
   *
   * @param world the world the player is in
   * @param player the player to clean up
   */
  public static void clearEffects(World world, ServerPlayerEntity player) {
    if (world == null || player == null) return;
    try {
      GameWorldComponent game = GameWorldComponent.KEY.get(world);
      if (game.getGameStatus() != GameWorldComponent.GameStatus.STOPPING) return;
      for (StatusEffectInstance effect : new ArrayList<>(player.getStatusEffects())) {
        player.removeStatusEffect(effect.getEffectType());
      }
      clearPoison(player);
      clearInfection(player);
    } catch (Throwable ignored) {
    }
  }

  private static void clearPoison(ServerPlayerEntity player) {
    try {
      PlayerPoisonComponent poison = PlayerPoisonComponent.KEY.get(player);
      poison.poisoner = null;
      poison.reset();
    } catch (Throwable ignored) {
    }
  }

  private static void clearInfection(ServerPlayerEntity player) {
    try {
      InfectedPlayerComponent infected = InfectedPlayerComponent.KEY.get(player);
      infected.reset();
    } catch (Throwable ignored) {
    }
  }

  /**
   * Exposes every setting of this mod through {@link ServerConfig}
   *
   * <p>Two kinds of entries exist:
   *
   * <ul>
   *   <li><b>world</b> entries live in {@link WatheExtendedWorldComponent} and are stored per world
   *   <li><b>global</b> entries live in {@link WatheExtendedServerConfig} and are stored in the
   *       config file
   * </ul>
   *
   * Registry keys are part of the wire/preset format, so renaming one breaks saved presets
   */
  private static void registerServerConfigEntries() {
    registerWorldEntries();
    registerGlobalEntries();

    WatheExtendedServerConfig.ROLEPLAY_ITEM_DEFAULTS.forEach(
        (id, def) ->
            ServerConfig.register(
                ServerConfig.Entry.globalBool(
                    "watheextended.roleplayItems." + id,
                    def,
                    () -> WatheExtendedServerConfig.isRoleplayItemEnabled(id),
                    value -> WatheExtendedServerConfig.setRoleplayItemEnabled(id, value))));
  }

  /** Registers the per-world entries (map setup and game rule toggles) */
  private static void registerWorldEntries() {
    // Map Effect
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.gameMode",
            "MODDED_MURDER",
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameModeSelection(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setConfiguredGameMode(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.gameTime",
            "NIGHT",
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameTimeOfDay(),
            (world, value) -> WatheExtendedWorldComponent.KEY.get(world).setGameTimeOfDay(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldBool(
            "watheextended.map.generic",
            false,
            world -> WatheExtendedWorldComponent.KEY.get(world).isGenericMapEffectEnabled(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setGenericMapEffectEnabled(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldString(
            "watheextended.map.lobbyTime",
            "DAY",
            world -> WatheExtendedWorldComponent.KEY.get(world).getLobbyTimeOfDay(),
            (world, value) -> WatheExtendedWorldComponent.KEY.get(world).setLobbyTimeOfDay(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldInt(
            "watheextended.map.duration",
            10,
            world -> WatheExtendedWorldComponent.KEY.get(world).getGameDurationMinutes(),
            (world, value) ->
                WatheExtendedWorldComponent.KEY.get(world).setGameDurationMinutes(value)));
    ServerConfig.register(
        ServerConfig.Entry.worldInt(
            "watheextended.secretMurderChance",
            10,
            world ->
                Math.round(GameWorldComponent.KEY.get(world).getSecretMurderRoundChance() * 100),
            (world, value) ->
                GameWorldComponent.KEY
                    .get(world)
                    .setSecretMurderRoundChance(Math.clamp(value, 0, 100) / 100.0f)));

    // Gamerules
    registerWorldFlag(
        "watheextended.playerCollisions",
        true,
        WatheExtendedWorldComponent::isPlayerCollisionsEnabled,
        WatheExtendedWorldComponent::setPlayerCollisionsEnabled);
    registerWorldFlag(
        "watheextended.rtpEnabled",
        false,
        WatheExtendedWorldComponent::isRtpEnabled,
        WatheExtendedWorldComponent::setRtpEnabled);
    registerWorldFlag(
        "watheextended.blockProtection",
        false,
        WatheExtendedWorldComponent::isBlockInteractionsProtected,
        WatheExtendedWorldComponent::setBlockInteractionsProtected);
    registerWorldFlag(
        "watheextended.itemBoundsCheck",
        true,
        WatheExtendedWorldComponent::isItemBoundsCheckEnabled,
        WatheExtendedWorldComponent::setItemBoundsCheckEnabled);
    registerWorldFlag(
        "watheextended.forbiddenLovers",
        false,
        WatheExtendedWorldComponent::isForbiddenLoversEnabled,
        WatheExtendedWorldComponent::setForbiddenLoversEnabled);
  }

  /** Registers the global entries, grouped by feature */
  private static void registerGlobalEntries() {
    // General
    registerFloat(
        "watheextended.forbiddenLovers.chance",
        0.25f,
        () -> WatheExtendedServerConfig.forbiddenLoversChance,
        "forbiddenLoversChance");
    registerBool(
        "watheextended.suppressAbilityVfxSfx",
        false,
        () -> WatheExtendedServerConfig.suppressAbilityVfxSfx,
        "suppressAbilityVfxSfx");
    registerString(
        "watheextended.jumpMode", "LOBBY", () -> WatheExtendedServerConfig.jumpMode, "jumpMode");
    registerString(
        "watheextended.shootInnocentPunishmentMode",
        "DEFAULT",
        () -> WatheExtendedServerConfig.shootInnocentPunishmentMode,
        "shootInnocentPunishmentMode");
    registerInt(
        "watheextended.killIncreaseTime",
        60,
        () -> WatheExtendedServerConfig.killIncreaseTime,
        "killIncreaseTime");

    // Introverted Modifier
    registerInt(
        "watheextended.introverted.crowdCount",
        3,
        () -> WatheExtendedServerConfig.introvertedCrowdCount,
        "introvertedCrowdCount");
    registerFloat(
        "watheextended.introverted.crowdRange",
        5.0f,
        () -> WatheExtendedServerConfig.introvertedCrowdRange,
        "introvertedCrowdRange");
    registerFloat(
        "watheextended.introverted.crowdDrainMultiplier",
        2.0f,
        () -> WatheExtendedServerConfig.introvertedCrowdDrainMultiplier,
        "introvertedCrowdDrainMultiplier");
    registerFloat(
        "watheextended.introverted.aloneDrainMultiplier",
        0.5f,
        () -> WatheExtendedServerConfig.introvertedAloneDrainMultiplier,
        "introvertedAloneDrainMultiplier");

    // Taxed Modifier
    registerFloat(
        "watheextended.taxed.coinReduction",
        0.50f,
        () -> WatheExtendedServerConfig.taxedCoinReduction,
        "taxedCoinReduction");
    registerInt(
        "watheextended.taxed.killThreshold",
        1,
        () -> WatheExtendedServerConfig.taxedKillThreshold,
        "taxedKillThreshold");
    registerInt(
        "watheextended.taxed.killWindowSeconds",
        60,
        () -> WatheExtendedServerConfig.taxedKillWindowSeconds,
        "taxedKillWindowSeconds");

    // Adaptive Modifier
    registerFloat(
        "watheextended.adaptive.penaltyReduction",
        0.50f,
        () -> WatheExtendedServerConfig.adaptivePenaltyReduction,
        "adaptivePenaltyReduction");
    registerFloat(
        "watheextended.adaptive.bonusMultiplier",
        0.50f,
        () -> WatheExtendedServerConfig.adaptiveBonusMultiplier,
        "adaptiveBonusMultiplier");

    // Cleaner
    registerInt(
        "watheextended.cleaner.playerLimit",
        10,
        () -> WatheExtendedServerConfig.cleanerPlayerLimit,
        "cleanerPlayerLimit");
    registerInt(
        "watheextended.cleaner.acidBarrelCoins",
        50,
        () -> WatheExtendedServerConfig.cleanerAcidBarrelCoins,
        "cleanerAcidBarrelCoins",
        ItemPrices::applyAll);
    registerBool(
        "watheextended.cleaner.acidBarrelCoinBonusEnabled",
        false,
        () -> WatheExtendedServerConfig.cleanerAcidBarrelCoinBonusEnabled,
        "cleanerAcidBarrelCoinBonusEnabled",
        ItemPrices::applyAll);

    // Morphling
    registerBool(
        "watheextended.morphling.canCancelAbility",
        true,
        () -> WatheExtendedServerConfig.morphlingCanCancelAbility,
        "morphlingCanCancelAbility");
    registerInt(
        "watheextended.morphling.abilityDuration",
        35,
        () -> WatheExtendedServerConfig.morphlingAbilityDuration,
        "morphlingAbilityDuration");
    registerInt(
        "watheextended.morphling.abilityCooldown",
        60,
        () -> WatheExtendedServerConfig.morphlingAbilityCooldown,
        "morphlingAbilityCooldown");

    // Phantom
    registerBool(
        "watheextended.phantom.canCancelAbility",
        true,
        () -> WatheExtendedServerConfig.phantomCanCancelAbility,
        "phantomCanCancelAbility");
    registerInt(
        "watheextended.phantom.abilityDuration",
        30,
        () -> WatheExtendedServerConfig.phantomAbilityDuration,
        "phantomAbilityDuration");
    registerInt(
        "watheextended.phantom.abilityCooldown",
        0,
        () -> WatheExtendedServerConfig.phantomAbilityCooldown,
        "phantomAbilityCooldown");

    // Mood
    registerBool(
        "watheextended.mood.depressedAbilityDisableEnabled",
        false,
        () -> WatheExtendedServerConfig.moodDisableAbilityWhenDepressed,
        "depressedAbilityDisableEnabled");
    registerInt(
        "watheextended.mood.depressedAbilityDisableTime",
        30,
        () -> WatheExtendedServerConfig.moodDisableAbilityWhenDepressedDelay,
        "depressedAbilityDisableTime");
    registerBool(
        "watheextended.mood.depressedPreventSprintEnabled",
        true,
        () -> WatheExtendedServerConfig.moodDisableSprintingWhenDepressed,
        "depressedPreventSprintEnabled");

    // Instinct
    registerFloat(
        "watheextended.instinct.capacity",
        1200.0f,
        () -> WatheExtendedServerConfig.instinctCapacity,
        "instinctCapacity");
    registerFloat(
        "watheextended.instinct.drainRate",
        60.0f,
        () -> WatheExtendedServerConfig.instinctDrainRate,
        "instinctDrainRate");
    registerFloat(
        "watheextended.instinct.reloadRate",
        20.0f,
        () -> WatheExtendedServerConfig.instinctReloadRate,
        "instinctReloadRate");

    // Passive Income Balance
    registerInt(
        "watheextended.balance.basePassiveIncome",
        5,
        () -> WatheExtendedServerConfig.basePassiveIncome,
        "basePassiveIncome");
    registerBool(
        "watheextended.balance.adjustPassiveIncome",
        false,
        () -> WatheExtendedServerConfig.adjustPassiveIncome,
        "adjustPassiveIncome");
    registerInt(
        "watheextended.balance.maxPassiveIncomeDistance",
        10,
        () -> WatheExtendedServerConfig.maxPassiveIncomeDistance,
        "maxPassiveIncomeDistance");
    registerInt(
        "watheextended.balance.minPassiveIncome",
        0,
        () -> WatheExtendedServerConfig.minPassiveIncome,
        "minPassiveIncome");

    // Last Stand
    registerBool(
        "watheextended.lastStand.enabled",
        false,
        () -> WatheExtendedServerConfig.lastStandEnabled,
        "lastStandEnabled");
    registerInt(
        "watheextended.lastStand.cooldown",
        30,
        () -> WatheExtendedServerConfig.lastStandCooldown,
        "lastStandCooldown");

    // Notifications
    registerBool(
        "watheextended.dreamer.imprintNotificationEnabled",
        true,
        () -> WatheExtendedServerConfig.dreamerImprintNotificationEnabled,
        "dreamerImprintNotificationEnabled");
    registerBool(
        "watheextended.arsonist.dousedNotificationEnabled",
        true,
        () -> WatheExtendedServerConfig.arsonistDousedNotificationEnabled,
        "arsonistDousedNotificationEnabled");
    registerInt(
        "watheextended.arsonist.dousedNotificationDelay",
        10,
        () -> WatheExtendedServerConfig.arsonistDousedNotificationDelay,
        "arsonistDousedNotificationDelay");
  }

  /**
   * Registers a per-world boolean entry backed by {@link WatheExtendedWorldComponent}. If the
   * component cannot be read or written (e.g. the world is not fully set up), the default is
   * reported and writes are ignored
   *
   * @param key registry key
   * @param def default value, also returned when the component is unavailable
   * @param getter reads the flag from the component
   * @param setter writes the flag to the component
   */
  private static void registerWorldFlag(
      String key,
      boolean def,
      Predicate<WatheExtendedWorldComponent> getter,
      BiConsumer<WatheExtendedWorldComponent, Boolean> setter) {
    ServerConfig.register(
        ServerConfig.Entry.worldBool(
            key,
            def,
            world -> {
              try {
                return getter.test(WatheExtendedWorldComponent.KEY.get(world));
              } catch (Throwable throwable) {
                return def;
              }
            },
            (world, value) -> {
              try {
                setter.accept(WatheExtendedWorldComponent.KEY.get(world), value);
              } catch (Throwable ignored) {
              }
            }));
  }

  /**
   * Registers a global int entry
   *
   * @param key registry key
   * @param def default value
   * @param field name of the {@link WatheExtendedServerConfig} field, used for writes
   */
  private static void registerInt(String key, int def, IntSupplier getter, String field) {
    registerInt(key, def, getter, field, () -> {});
  }

  /**
   * Same as {@link #registerInt(String, int, IntSupplier, String)}, running {@code afterSet} after
   * each write
   */
  private static void registerInt(
      String key, int def, IntSupplier getter, String field, Runnable afterSet) {
    ServerConfig.register(
        ServerConfig.Entry.globalInt(
            key,
            def,
            () -> getter.getAsInt(),
            value -> {
              WatheExtendedServerConfig.set(field, value);
              afterSet.run();
            }));
  }

  /** Registers a global float entry. See {@link #registerInt(String, int, IntSupplier, String)} */
  private static void registerFloat(String key, float def, FloatSupplier getter, String field) {
    ServerConfig.register(
        ServerConfig.Entry.globalFloat(
            key,
            def,
            () -> getter.getAsFloat(),
            value -> WatheExtendedServerConfig.set(field, value)));
  }

  /**
   * Registers a global boolean entry. See {@link #registerInt(String, int, IntSupplier, String)}
   */
  private static void registerBool(String key, boolean def, BooleanSupplier getter, String field) {
    registerBool(key, def, getter, field, () -> {});
  }

  /**
   * Same as {@link #registerBool(String, boolean, BooleanSupplier, String)}, running {@code
   * afterSet} after each write
   */
  private static void registerBool(
      String key, boolean def, BooleanSupplier getter, String field, Runnable afterSet) {
    ServerConfig.register(
        ServerConfig.Entry.globalBool(
            key,
            def,
            () -> getter.getAsBoolean(),
            value -> {
              WatheExtendedServerConfig.set(field, value);
              afterSet.run();
            }));
  }

  /** Registers a global string entry. See {@link #registerInt(String, int, IntSupplier, String)} */
  private static void registerString(
      String key, String def, Supplier<String> getter, String field) {
    ServerConfig.register(
        ServerConfig.Entry.globalString(
            key, def, () -> getter.get(), value -> WatheExtendedServerConfig.set(field, value)));
  }

  /** Registers all payload types and their server-side receivers */
  private static void registerNetworking() {
    registerPayloadTypes();

    ServerPlayNetworking.registerGlobalReceiver(
        ServerConfig.ChangePayload.ID, WatheExtended::handleConfigChange);
    ServerPlayNetworking.registerGlobalReceiver(
        PronounsManager.UpdatePayload.ID, WatheExtended::handlePronounsUpdate);
    ServerPlayNetworking.registerGlobalReceiver(
        PresetManager.ActionPayload.ID, WatheExtended::handlePresetAction);
    ServerPlayNetworking.registerGlobalReceiver(
        PlayerMovement.SetPronePayload.ID, PlayerMovement::handleSetProne);
  }

  /** Registers the codecs of every S2C and C2S payload */
  private static void registerPayloadTypes() {
    // Config
    PayloadTypeRegistry.playS2C()
        .register(ServerConfig.SyncPayload.ID, ServerConfig.SyncPayload.CODEC);
    PayloadTypeRegistry.playC2S()
        .register(ServerConfig.ChangePayload.ID, ServerConfig.ChangePayload.CODEC);
    // Pronouns
    PayloadTypeRegistry.playC2S()
        .register(PronounsManager.UpdatePayload.ID, PronounsManager.UpdatePayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PronounsManager.SyncPayload.ID, PronounsManager.SyncPayload.CODEC);
    // Last Stand
    PayloadTypeRegistry.playS2C()
        .register(LastStand.LastStandPayload.ID, LastStand.LastStandPayload.CODEC);
    // Presets
    PayloadTypeRegistry.playC2S()
        .register(PresetManager.ActionPayload.ID, PresetManager.ActionPayload.CODEC);
    PayloadTypeRegistry.playC2S()
        .register(PlayerMovement.SetPronePayload.ID, PlayerMovement.SetPronePayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PresetManager.ListPayload.ID, PresetManager.ListPayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(PresetManager.ResultPayload.ID, PresetManager.ResultPayload.CODEC);
  }

  /**
   * Applies config changes sent from the client config screen, then re-syncs everyone
   *
   * <p>Entries whose key starts with {@value #COMMAND_CHANGE_PREFIX} are executed as silent
   * commands; all others are applied to the {@link ServerConfig} registry in one batch. Requires
   * permission level {@value #PERMISSION_LEVEL}
   */
  private static void handleConfigChange(
      ServerConfig.ChangePayload payload, ServerPlayNetworking.Context context) {
    ServerPlayerEntity player = context.player();
    if (!player.hasPermissionLevel(PERMISSION_LEVEL)) return;

    MinecraftServer server = context.server();
    server.execute(
        () -> {
          Map<String, String> registryChanges = new LinkedHashMap<>();
          for (Map.Entry<String, String> entry : payload.changes().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(COMMAND_CHANGE_PREFIX)) {
              runConfigCommand(server, player, key.substring(COMMAND_CHANGE_PREFIX.length()));
            } else {
              registryChanges.put(key, entry.getValue());
            }
          }
          if (!registryChanges.isEmpty())
            ServerConfig.applyChanges(registryChanges, server.getOverworld());
          ServerConfig.broadcastToAll(server);
        });
  }

  /** Runs a command requested by the config screen */
  private static void runConfigCommand(
      MinecraftServer server, ServerPlayerEntity player, String command) {
    try {
      server
          .getCommandManager()
          .getDispatcher()
          .execute(
              command,
              player.getCommandSource().withLevel(CONFIG_COMMAND_PERMISSION_LEVEL).withSilent());
    } catch (Throwable ignored) {
    }
  }

  /** Stores player's pronouns and broadcasts the result to all players */
  private static void handlePronounsUpdate(
      PronounsManager.UpdatePayload payload, ServerPlayNetworking.Context context) {
    UUID uuid = context.player().getUuid();
    String pronouns = payload.pronouns().trim();
    MinecraftServer server = context.server();

    server.execute(
        () -> {
          PronounsManager.set(uuid, pronouns);
          PronounsManager.SyncPayload sync =
              new PronounsManager.SyncPayload(uuid, PronounsManager.get(uuid));
          for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(target, sync);
          }
        });
  }

  /**
   * Handles preset management requests (list, save, load, override, delete)
   *
   * <p>Requires permission level {@value #PERMISSION_LEVEL}. Exceptions are logged and reported
   * back to the requester through {@link PresetManager.ResultPayload}
   */
  private static void handlePresetAction(
      PresetManager.ActionPayload payload, ServerPlayNetworking.Context context) {
    ServerPlayerEntity player = context.player();
    if (!player.hasPermissionLevel(PERMISSION_LEVEL)) return;

    MinecraftServer server = context.server();
    server.execute(
        () -> {
          try {
            runPresetAction(payload, player, server);
          } catch (Exception exception) {
            LOGGER.error("Preset action failed", exception);
            String message =
                exception.getMessage() == null ? "Preset action failed" : exception.getMessage();
            ServerPlayNetworking.send(
                player, new PresetManager.ResultPayload(false, payload.action(), message));
          }
        });
  }

  /** Executes a single preset action against the current server config snapshot */
  private static void runPresetAction(
      PresetManager.ActionPayload payload, ServerPlayerEntity player, MinecraftServer server)
      throws IOException {
    ServerWorld overworld = server.getOverworld();

    switch (payload.action()) {
      case "list" -> PresetManager.sendList(player);
      case "save" -> {
        PresetManager.save(
            payload.name(), payload.description(), player, ServerConfig.snapshot(overworld));
        PresetManager.sendList(player);
      }
      case "load" -> {
        PresetManager.apply(PresetManager.load(payload.id()), overworld);
        ServerConfig.broadcastToAll(server);
        ServerPlayNetworking.send(player, new PresetManager.ResultPayload(true, "load", ""));
      }
      case "override" -> {
        PresetManager.Preset preset = PresetManager.load(payload.id());
        PresetManager.save(
            preset.metadata().name(),
            preset.metadata().description(),
            player,
            ServerConfig.snapshot(overworld));
        PresetManager.sendList(player);
        ServerPlayNetworking.send(player, new PresetManager.ResultPayload(true, "override", ""));
      }
      case "delete" -> {
        PresetManager.delete(payload.id());
        PresetManager.sendList(player);
      }
      default -> throw new IllegalArgumentException("Unknown preset action");
    }
  }

  /** Registers the player join hook */
  private static void registerConnectionEvents() {
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> {
          ServerPlayerEntity joining = handler.player;
          server.execute(() -> onPlayerJoin(joining, server));
        });
  }

  /**
   * Syncs config, presets and all known pronouns to a joining player and restores their in-game
   * state: players killed during a running game rejoin as spectators, and item state is re-applied
   */
  private static void onPlayerJoin(ServerPlayerEntity joining, MinecraftServer server) {
    ServerConfig.sendToPlayer(joining);
    PresetManager.sendList(joining);
    PronounsManager.getAll()
        .forEach(
            (uuid, pronouns) ->
                ServerPlayNetworking.send(
                    joining, new PronounsManager.SyncPayload(uuid, pronouns)));

    try {
      ServerWorld overworld = server.getOverworld();
      GameWorldComponent game = GameWorldComponent.KEY.get(overworld);
      WatheExtendedWorldComponent extended = WatheExtendedWorldComponent.KEY.get(overworld);

      if (game.isRunning() && extended.isPlayerKilled(joining.getUuid())) {
        joining.changeGameMode(GameMode.SPECTATOR);
      }
      PlayerItem.applyItemState(joining, overworld);
    } catch (Throwable ignored) {
    }
  }

  /** Registers the end-of-world-tick hook */
  private static void registerTick() {
    ServerTickEvents.END_WORLD_TICK.register(WatheExtended::tickWorld);
  }

  /**
   * Per-world tick logic
   *
   * <ul>
   *   <li>While the game is {@code STOPPING}: strips lingering effects from all players
   *   <li>Every tick: Random Teleportation handling and Introverted modifier
   *   <li>Every {@value #ITEM_TICK_INTERVAL} ticks: per-player item updates
   *   <li>Every {@value #ITEM_BOUNDS_CHECK_INTERVAL} ticks during an {@code ACTIVE} game: Item
   *       Bounds Check (if enabled)
   *   <li>World only: config sync and Last Stand
   * </ul>
   */
  private static void tickWorld(ServerWorld world) {
    GameWorldComponent game;
    try {
      game = GameWorldComponent.KEY.get(world);
    } catch (Throwable throwable) {
      return;
    }
    if (game == null) return;

    GameWorldComponent.GameStatus status = game.getGameStatus();
    long worldTime = world.getTime();

    if (status == GameWorldComponent.GameStatus.STOPPING) {
      for (ServerPlayerEntity player : world.getPlayers()) {
        clearEffects(world, player);
      }
    }

    TeleportationHandler.tick(world, status, worldTime);

    if (worldTime % ITEM_TICK_INTERVAL == 0) {
      PlayerItem.tickAll(world);
    }

    if (status == GameWorldComponent.GameStatus.ACTIVE
        && worldTime % ITEM_BOUNDS_CHECK_INTERVAL == 0) {
      tickItemBounds(world);
    }

    IntrovertedModifier.tick(world);

    if (world.getRegistryKey() == World.OVERWORLD) {
      ConfigSync.tick(world);
      LastStand.tick(world);
    }
  }

  /** Runs the Item Bounds Check if it is enabled */
  private static void tickItemBounds(ServerWorld world) {
    try {
      WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
      if (component != null && component.isItemBoundsCheckEnabled()) {
        ItemBoundsChecker.tick(world);
      }
    } catch (Throwable ignored) {
    }
  }

  /**
   * Initializes the mod
   *
   * <p>Order matters: registries first, then configs (which the item price/cooldown tables read),
   * then integrations, then runtime hooks.
   */
  @Override
  public void onInitialize() {
    LOGGER.info("Initializing...");

    registerContent();
    loadConfigs();
    registerIntegrations();

    registerServerConfigEntries();
    registerNetworking();
    registerConnectionEvents();
    registerTick();

    GameEvents.register();
    LastStand.register();
    ArsonistDousedNotification.register();

    registerCommands();

    LOGGER.info("Initialization complete!");
  }

  @FunctionalInterface
  private interface FloatSupplier {
    float getAsFloat();
  }
}
