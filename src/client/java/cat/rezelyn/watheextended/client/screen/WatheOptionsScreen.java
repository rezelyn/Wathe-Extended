package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.watheextended.client.screen.config.*;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WatheOptionsScreen {

    private static final Map<String, Boolean> pendingRoleState = new HashMap<>();
    private static final Map<String, Boolean> pendingModifierState = new HashMap<>();

    // Blacklist of roles that shouldn't be shown in the options/config screen
    // (roles needed for WATHE to function properly and therefore can't be disabled)
    private static final Set<String> BLACKLIST = Set.of(
            "civilian",
            "killer",
            "vigilante",
            "discovery_civilian",
            "loose_end"
    );

    public static Screen create(Screen parent) {
        boolean op = isOp();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("gui.watheextended.config.title"))
                .category(ClientCategory.build(parent, op, WatheOptionsScreen::sendCommand));

        if (op) {
            builder.category(OptionsCategory.build(parent, WatheOptionsScreen::sendCommand));
            builder.category(MapVariablesCategory.build(parent, WatheOptionsScreen::sendCommand));
            builder.category(RolesCategory.build(parent, BLACKLIST, pendingRoleState, WatheOptionsScreen::sendCommand));
            builder.category(ModifiersCategory.build(parent, pendingModifierState, WatheOptionsScreen::sendCommand));
        }

        return builder.build().generateScreen(parent);
    }

    public static void clearPendingState() {
        pendingRoleState.clear();
        pendingModifierState.clear();
    }

    // check if player is op (>=2)
    private static boolean isOp() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.hasPermissionLevel(2);
    }

    private static void sendCommand(String command, Screen currentScreen) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            player.networkHandler.sendChatCommand(command);
            client.setScreen(null);
        } catch (Throwable ignored) {
        }
    }
}