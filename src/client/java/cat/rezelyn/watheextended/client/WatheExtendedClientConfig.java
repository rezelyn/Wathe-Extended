package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class WatheExtendedClientConfig {

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("client.json5").toFile();
    public static boolean showChatDuringGame = true;

    private WatheExtendedClientConfig() {}

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        showChatDuringGame = ClientConfig.readBool(CONFIG_FILE, "hud.showChatDuringGame", true);
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            String content =
                    "{\n" +
                            "  \"hud\": {\n" +
                            "    // Show the chat HUD.\n" +
                            "    // Non-OP players will still be restricted to send messages and commands while a game is active.\n" +
                            "    \"showChatDuringGame\": " + showChatDuringGame + "\n" +
                            "  }\n" +
                            "}\n";
            Files.writeString(CONFIG_FILE.toPath(), content);
        } catch (IOException ignored) {
        }
    }

    public static boolean getShowChatDuringGame() {
        return showChatDuringGame;
    }

    public static void setShowChatDuringGame(boolean value) {
        showChatDuringGame = value;
        save();
    }
}
