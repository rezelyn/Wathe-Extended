package cat.rezelyn.watheextended.api.config.shooterpunishments;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import cat.rezelyn.watheextended.WatheExtended;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public final class ConfigHelper {

    public static final String[] MODES = {"default", "preventGunPickup", "killShooter"};
    private static String lastKnownMode = MODES[0];
    private static MinecraftServer server;

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("shooterpunishments");
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        // The mode lives behind a command, so the writer needs a server the World-less global
        // writer never receives; hold onto it for the lifetime of the server instead.
        ServerLifecycleEvents.SERVER_STARTED.register(startedServer -> server = startedServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(stoppedServer -> server = null);

        ServerConfig.register(Entry.globalString("shooterpunishments.currentMode", MODES[0], () -> lastKnownMode, ConfigHelper::applyMode));
    }

    private static void applyMode(String mode) {
        if (server != null) {
            try {
                server.getCommandManager().getDispatcher().execute("setShootInnocentPunishment " + mode, server.getCommandSource().withSilent().withLevel(4));
            } catch (Throwable throwable) {
            }
        }
        lastKnownMode = mode;
    }

    public static String[] getPunishmentModes() {
        return MODES;
    }

    public static String getCurrentPunishment() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && ClientConfig.isRemoteServer()) {
            return ClientConfig.getString("shooterpunishments.currentMode", lastKnownMode);
        }
        return lastKnownMode;
    }

    public static void setLastKnownMode(String mode) {
        lastKnownMode = mode;
    }

    public static void setMode(String mode) {
        ConfigUtils.apply("shooterpunishments.currentMode", mode, null);
    }
}
