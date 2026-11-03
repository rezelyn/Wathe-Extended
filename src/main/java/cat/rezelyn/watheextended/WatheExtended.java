package cat.rezelyn.watheextended;

import cat.rezelyn.watheextended.command.*;
import cat.rezelyn.watheextended.game.*;
import cat.rezelyn.watheextended.game.modifiers.WatheExtendedModifiers;
import cat.rezelyn.watheextended.game.roles.arsonist.ArsonistDousedNotification;
import cat.rezelyn.watheextended.index.*;
import cat.rezelyn.watheextended.network.PronounsManager;
import cat.rezelyn.watheextended.network.ConfigEntries;
import cat.rezelyn.watheextended.network.ServerNetworking;
import cat.rezelyn.watheextended.network.ServerTick;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common (server + client) entrypoint <br>
 * <br>
 * Only wires things together, the actual logic lives in:
 *
 * <ul>
 *   <li>{@link ConfigEntries} - every setting exposed through {@code ServerConfig}
 *   <li>{@link ServerNetworking} - payload types, receivers and join sync
 *   <li>{@link ServerTick} - the world tick loop
 * </ul>
 */
public class WatheExtended implements ModInitializer {

    public static final String MOD_ID = "watheextended";
    public static final Logger LOGGER = LoggerFactory.getLogger(WatheExtended.class);

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

        ConfigEntries.register();
        ServerNetworking.register();
        ServerTick.register();

        GameEvents.register();
        LastStand.register();
        ArsonistDousedNotification.register();

        registerCommands();

        LOGGER.info("Initialization complete!");
    }
}