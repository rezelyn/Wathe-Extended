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
    private static String instinctHudStyle = "HALF_LEFT";
    private static float instinctHudOpacity = 0.25f;
    private static boolean alwaysShowInstinctHud;

    private WatheExtendedClientConfig() {}

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        ClientConfig.Reader reader = ClientConfig.reader(CONFIG_FILE);
        showChatDuringGame = reader.getBool("hud.showChatDuringGame", true);
        instinctMode = normalizeInstinctMode(reader.getString("instinct.mode", "HOLD"));
        instinctHudStyle = normalizeInstinctHudStyle(reader.getString("instinct.hudStyle", "HALF_LEFT"));
        instinctHudOpacity = normalizeInstinctHudOpacity(reader.getFloat("instinct.hudOpacity", 0.25f));
        alwaysShowInstinctHud = reader.getBool("instinct.alwaysShowHud", false);
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
                            "    \"mode\": \"" + instinctMode + "\",\n" +
                            "    // Shape of the Instinct charge HUD.\n" +
                            "    \"hudStyle\": \"" + instinctHudStyle + "\",\n" +
                            "    // HUD opacity, from 0.0 (transparent) to 1.0 (opaque).\n" +
                            "    \"hudOpacity\": " + instinctHudOpacity + ",\n" +
                            "    // Keep the Instinct HUD visible while Instinct is available.\n" +
                            "    \"alwaysShowHud\": " + alwaysShowInstinctHud + "\n" +
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

    public static String getInstinctHudStyle() {
        return instinctHudStyle;
    }

    public static void setInstinctHudStyle(String value) {
        instinctHudStyle = normalizeInstinctHudStyle(value);
        save();
    }

    public static float getInstinctHudOpacity() {
        return instinctHudOpacity;
    }

    public static void setInstinctHudOpacity(float value) {
        instinctHudOpacity = normalizeInstinctHudOpacity(value);
        save();
    }

    public static boolean getAlwaysShowInstinctHud() {
        return alwaysShowInstinctHud;
    }

    public static void setAlwaysShowInstinctHud(boolean value) {
        alwaysShowInstinctHud = value;
        save();
    }

    private static String normalizeInstinctMode(String value) {
        return "TOGGLE".equalsIgnoreCase(value) ? "TOGGLE" : "HOLD";
    }

    private static String normalizeInstinctHudStyle(String value) {
        if ("HALF_LEFT".equalsIgnoreCase(value)) return "HALF_LEFT";
        if ("HALF_RIGHT".equalsIgnoreCase(value)) return "HALF_RIGHT";
        return "FULL";
    }

    private static float normalizeInstinctHudOpacity(float value) {
        return Math.clamp(value, 0.0f, 1.0f);
    }
}
