package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class WatheExtendedClientConfig {

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("client.json5").toFile();
    public static boolean showChatDuringGame = true;
    private static String instinctMode = "HOLD";

    private WatheExtendedClientConfig() {}

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        ClientConfig.Reader reader = ClientConfig.reader(CONFIG_FILE);
        showChatDuringGame = reader.getBool("hud.showChatDuringGame", true);
        instinctMode = normalizeInstinctMode(reader.getString("instinct.mode", "HOLD"));
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
                            "  },\n" +
                            "  \"instinct\": {\n" +
                            "    // How the Instinct keybind is activated.\n" +
                            "    \"mode\": \"" + instinctMode + "\"\n" +
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

    public static String getInstinctMode() {
        return instinctMode;
    }

    public static void setInstinctMode(String value) {
        instinctMode = normalizeInstinctMode(value);
        WatheExtendedClient.resetInstinctToggle();
        save();
    }

    public static boolean isInstinctToggleMode() {
        return "TOGGLE".equals(instinctMode);
    }

    private static String normalizeInstinctMode(String value) {
        return "TOGGLE".equalsIgnoreCase(value) ? "TOGGLE" : "HOLD";
    }
}
