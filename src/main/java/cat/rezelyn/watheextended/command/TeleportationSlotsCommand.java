package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TeleportationSlotsCommand {

    private static Text feedback(boolean enabled, String enabledKey, String disabledKey) {
        return Text.translatable(enabled ? enabledKey : disabledKey)
                .styled(style -> style.withColor(enabled ? 0x55FF55 : 0xFF5555));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("watheextended:rtp")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("enable")
                                .executes(ctx -> setEnabled(ctx, true)))
                        .then(CommandManager.literal("disable")
                                .executes(ctx -> setEnabled(ctx, false)))
                        .then(CommandManager.literal("slot")
                                .then(CommandManager.literal("add")
                                        .executes(TeleportationSlotsCommand::addSlotFromPlayerPos)
                                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                .then(CommandManager.argument("rotation", RotationArgumentType.rotation())
                                                        .executes(TeleportationSlotsCommand::addSlotExplicit))))
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("index", IntegerArgumentType.integer(1))
                                                .executes(TeleportationSlotsCommand::removeSlot)))
                                .then(CommandManager.literal("edit")
                                        .then(CommandManager.argument("index", IntegerArgumentType.integer(1))
                                                .executes(TeleportationSlotsCommand::editSlotFromPlayerPos)
                                                .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                        .then(CommandManager.argument("rotation", RotationArgumentType.rotation())
                                                                .executes(TeleportationSlotsCommand::editSlotExplicit)))))
                                .then(CommandManager.literal("list")
                                        .executes(TeleportationSlotsCommand::listSlots)))
        );
    }

    private static int setEnabled(CommandContext<ServerCommandSource> context, boolean enabled) {
        ServerCommandSource source = context.getSource();
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        wec.setRtpEnabled(enabled);
        source.sendMessage(feedback(enabled,
                "command.watheextended.rtp_slot.enabled",
                "command.watheextended.rtp_slot.disabled"));
        return 1;
    }

    private static int addSlotFromPlayerPos(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        Vec3d pos = player.getPos();
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        return addSlotInternal(source, pos.x, pos.y, pos.z, yaw, pitch);
    }

    private static int addSlotExplicit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Vec3d pos = Vec3ArgumentType.getPosArgument(context, "location").toAbsolutePos(source);
        Vec2f rot = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
        return addSlotInternal(source, pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    private static int addSlotInternal(ServerCommandSource source, double x, double y, double z, float yaw, float pitch) {
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        TeleportationSlot slot = new TeleportationSlot(x, y, z, yaw, pitch);
        wec.addTeleportationSlot(slot);
        int index = wec.getTeleportationSlots().size();
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.added",
                index, slot.toString()));
        return 1;
    }

    private static int removeSlot(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int index = IntegerArgumentType.getInteger(context, "index");
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        List<TeleportationSlot> slots = wec.getTeleportationSlots();
        if (index < 1 || index > slots.size()) {
            source.sendError(Text.translatable("command.watheextended.rtp_slot.invalid", index, slots.size()));
            return 0;
        }
        TeleportationSlot removed = slots.get(index - 1);
        wec.removeTeleportationSlot(index - 1);
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.removed",
                index, removed.toString()));
        return 1;
    }

    private static int editSlotFromPlayerPos(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        int index = IntegerArgumentType.getInteger(context, "index");
        Vec3d pos = player.getPos();
        return editSlotInternal(source, index, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
    }

    private static int editSlotExplicit(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int index = IntegerArgumentType.getInteger(context, "index");
        Vec3d pos = Vec3ArgumentType.getPosArgument(context, "location").toAbsolutePos(source);
        Vec2f rot = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
        return editSlotInternal(source, index, pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    private static int editSlotInternal(ServerCommandSource source, int index,
                                        double x, double y, double z, float yaw, float pitch) {
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        List<TeleportationSlot> slots = wec.getTeleportationSlots();
        if (index < 1 || index > slots.size()) {
            source.sendError(Text.translatable("command.watheextended.rtp_slot.invalid", index, slots.size()));
            return 0;
        }
        TeleportationSlot slot = new TeleportationSlot(x, y, z, yaw, pitch);
        wec.editTeleportationSlot(index - 1, slot);
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.edited",
                index, slot.toString()));
        return 1;
    }

    private static int listSlots(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(source.getWorld());
        List<TeleportationSlot> slots = wec.getTeleportationSlots();
        if (slots.isEmpty()) {
            source.sendMessage(Text.translatable("command.watheextended.rtp_slot.list_empty"));
            return 0;
        }
        source.sendMessage(Text.translatable("command.watheextended.rtp_slot.list_header", slots.size()));
        for (int i = 0; i < slots.size(); i++) {
            source.sendMessage(Text.literal("  §7[" + (i + 1) + "]§r " + slots.get(i).toString()));
        }
        return slots.size();
    }
}

