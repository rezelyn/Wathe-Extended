package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.api.wathe.MapVariables;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class TeleportationSlotsCommand {

    private static Text feedback(boolean enabled, String enabledKey, String disabledKey) {
        return Text.translatable(enabled ? enabledKey : disabledKey).styled(style -> style.withColor(enabled ? 0x55FF55 : 0xFF5555));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("watheextended:rtp").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("enable").executes(ctx -> setEnabled(ctx, true))).then(CommandManager.literal("disable").executes(ctx -> setEnabled(ctx, false))).then(CommandManager.literal("slot").then(CommandManager.literal("add").executes(TeleportationSlotsCommand::addSlotFromPlayerPos).then(CommandManager.argument("location", Vec3ArgumentType.vec3()).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes(TeleportationSlotsCommand::addSlotExplicit)))).then(CommandManager.literal("remove").then(CommandManager.argument("id", IntegerArgumentType.integer(1)).executes(TeleportationSlotsCommand::removeSlot))).then(CommandManager.literal("edit").then(CommandManager.argument("id", IntegerArgumentType.integer(1)).executes(TeleportationSlotsCommand::editSlotFromPlayerPos).then(CommandManager.argument("location", Vec3ArgumentType.vec3()).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes(TeleportationSlotsCommand::editSlotExplicit))))).then(CommandManager.literal("list").executes(TeleportationSlotsCommand::listSlots))));
    }

    private static int setEnabled(CommandContext<ServerCommandSource> context, boolean enabled) {
        ServerCommandSource source = context.getSource();
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        wec.setRtpEnabled(enabled);
        return 1;
    }

    private static int addSlotFromPlayerPos(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        Vec3d pos = player.getPos();
        return addSlotInternal(source, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
    }

    private static int addSlotExplicit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Vec3d pos = Vec3ArgumentType.getPosArgument(context, "location").toAbsolutePos(source);
        Vec2f rot = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
        return addSlotInternal(source, pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    private static int addSlotInternal(ServerCommandSource source, double x, double y, double z, float yaw, float pitch) {
        if (!isInsideReadyArea(source, x, y, z)) return 0;
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        TeleportationSlot slot = new TeleportationSlot(x, y, z, yaw, pitch);
        int id = wec.addTeleportationSlot(slot);
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.added", id, slot.toString()));
        return 1;
    }

    private static int removeSlot(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, "id");
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        TeleportationSlot removed = wec.getTeleportationSlots().get(id);
        if (removed == null) {
            source.sendError(Text.translatable("command.watheextended.rtp_slot.invalid", id));
            return 0;
        }
        wec.removeTeleportationSlot(id);
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.removed", id, removed.toString()));
        return 1;
    }

    private static int editSlotFromPlayerPos(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        int id = IntegerArgumentType.getInteger(context, "id");
        Vec3d pos = player.getPos();
        return editSlotInternal(source, id, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
    }

    private static int editSlotExplicit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, "id");
        Vec3d pos = Vec3ArgumentType.getPosArgument(context, "location").toAbsolutePos(source);
        Vec2f rot = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
        return editSlotInternal(source, id, pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    private static int editSlotInternal(ServerCommandSource source, int id, double x, double y, double z, float yaw, float pitch) {
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        if (!wec.getTeleportationSlots().containsKey(id)) {
            source.sendError(Text.translatable("command.watheextended.rtp_slot.invalid", id));
            return 0;
        }
        if (!isInsideReadyArea(source, x, y, z)) return 0;
        TeleportationSlot slot = new TeleportationSlot(x, y, z, yaw, pitch);
        wec.editTeleportationSlot(id, slot);
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.edited", id, slot.toString()));
        return 1;
    }

    private static int listSlots(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        Map<Integer, TeleportationSlot> slots = wec.getTeleportationSlots();
        if (slots.isEmpty()) {
            source.sendMessage(Text.translatable("command.watheextended.rtp_slot.list_empty"));
            return 0;
        }
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.list_header", slots.size()));
        for (Map.Entry<Integer, TeleportationSlot> entry : slots.entrySet()) {
            source.sendMessage(Text.literal("  §7[#" + entry.getKey() + "]§r " + entry.getValue().toString()));
        }
        return slots.size();
    }

    private static boolean isInsideReadyArea(ServerCommandSource source, double x, double y, double z) {
        Box readyArea = MapVariables.getReadyArea(source.getWorld());
        if (readyArea != null && !readyArea.contains(x, y, z)) {
            source.sendError(Text.translatable("command.watheextended.rtp_slot.outside_ready_area", String.format("%.2f %.2f %.2f", x, y, z)));
            return false;
        }
        return true;
    }
}
