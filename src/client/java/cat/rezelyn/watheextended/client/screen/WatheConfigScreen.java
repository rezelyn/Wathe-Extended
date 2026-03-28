package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.client.screen.config.*;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WatheConfigScreen {

    private static final Map<String, String> pendingChanges = new HashMap<>();
    private static final Map<String, Boolean> pendingRoleState = new HashMap<>();
    private static final Map<String, Boolean> pendingModifierState = new HashMap<>();

    // Blacklist of roles that shouldn't be shown in the config screen
    // As these are needed by WATHE to function properly and so are not meant to be disabled
    private static final Set<String> BLACKLIST = Set.of(
            "civilian",
            "killer",
            "vigilante",
            "discovery_civilian",
            "loose_end"
    );

    private static Screen savedParent = null;
    private static boolean awaitingSync = false;

    private static final int REOPEN_DELAY_TICKS = 3;
    private static int reopenAtTick = -1;
    private static int clientTick = 0;

    public static void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTick++;
            if (reopenAtTick >= 0 && clientTick >= reopenAtTick && client.currentScreen == null) {
                reopenAtTick = -1;
                Screen parent = savedParent;
                client.setScreen(create(parent));
            }
        });
    }

    public static Screen create(Screen parent) {
        savedParent = parent;
        pendingChanges.clear();
        awaitingSync = false;
        reopenAtTick = -1;

        boolean op = isOp();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("gui.watheextended.config.title"))
                .category(ClientCategory.build(parent, op, WatheConfigScreen::stageCommand))
                .save(WatheConfigScreen::flushPendingChanges);

        if (op) {
            builder.category(OptionsCategory.build(parent, WatheConfigScreen::stageCommand));
            builder.category(MapVariablesCategory.build(parent, WatheConfigScreen::stageCommand));
            builder.category(ItemsCategory.build(parent, WatheConfigScreen::stageCommand));
            builder.category(RolesCategory.build(parent, BLACKLIST, pendingRoleState, WatheConfigScreen::stageCommand));
            builder.category(ModifiersCategory.build(parent, pendingModifierState, WatheConfigScreen::stageCommand));
        }

        return builder.build().generateScreen(parent);
    }

    public static void clearPendingState() {
        pendingChanges.clear();
        pendingRoleState.clear();
        pendingModifierState.clear();
        savedParent = null;
        awaitingSync = false;
        reopenAtTick = -1;
    }

    public static void onCacheUpdated() {
        if (!awaitingSync) return;
        awaitingSync = false;
        reopenAtTick = clientTick + REOPEN_DELAY_TICKS;
    }

    private static boolean isOp() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.hasPermissionLevel(2);
    }

    static void stageCommand(String command, Screen currentScreen) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            int space = command.indexOf(' ');
            if (space > 0) {
                String key = command.substring(0, space);
                String value = command.substring(space + 1);
                if (key.contains(".") && !key.contains(":")) {
                    pendingChanges.put(key, value);
                } else {
                    pendingChanges.put("cmd:" + command, "");
                }
            } else {
                pendingChanges.put("cmd:" + command, "");
            }
        } catch (Throwable ignored) {
        }
    }

    private static void flushPendingChanges() {
        if (pendingChanges.isEmpty()) return;
        try {
            ClientPlayNetworking.send(new ServerConfig.ChangePayload(new HashMap<>(pendingChanges)));
            pendingChanges.clear();
            awaitingSync = true;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) client.execute(() -> client.setScreen(null));
        } catch (Throwable ignored) {
        }
    }
}
