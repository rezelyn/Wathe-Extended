package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class WatheExtendedMapVariablesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("watheextended:mapVariables")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.literal("readyAreaSpawnPosition")
                                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                .then(CommandManager.argument("rotation", RotationArgumentType.rotation())
                                                        .executes(context -> setReadyAreaSpawnPosition(
                                                                context,
                                                                Vec3ArgumentType.getPosArgument(context, "location"),
                                                                RotationArgumentType.getRotation(context, "rotation")
                                                        ))
                                                )
                                        )
                                )
                                .then(CommandManager.literal("lobbyArea")
                                        .then(CommandManager.argument("x1", DoubleArgumentType.doubleArg())
                                                .then(CommandManager.argument("y1", DoubleArgumentType.doubleArg())
                                                        .then(CommandManager.argument("z1", DoubleArgumentType.doubleArg())
                                                                .then(CommandManager.argument("x2", DoubleArgumentType.doubleArg())
                                                                        .then(CommandManager.argument("y2", DoubleArgumentType.doubleArg())
                                                                                .then(CommandManager.argument("z2", DoubleArgumentType.doubleArg())
                                                                                        .executes(WatheExtendedMapVariablesCommand::setLobbyArea)
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int setReadyAreaSpawnPosition(CommandContext<ServerCommandSource> context,
                                                 PosArgument location,
                                                 PosArgument rotation) {
        ServerCommandSource source = context.getSource();
        Vec3d pos = location.toAbsolutePos(source);
        Vec2f rot = rotation.toAbsoluteRotation(source);

        MapVariablesWorldComponent.PosWithOrientation posWithOrientation =
                new MapVariablesWorldComponent.PosWithOrientation(pos, rot.y, rot.x);

        WatheExtendedWorldComponent.KEY.get(source.getWorld()).setReadyAreaSpawnPos(posWithOrientation);

        source.sendMessage(Text.translatable("wathe.map_variables.set",
                "readyAreaSpawnPosition",
                String.format("%.4f %.4f %.4f (yaw=%.4f, pitch=%.4f)",
                        pos.x, pos.y, pos.z, (double) rot.y, (double) rot.x)));
        return 1;
    }

    private static int setLobbyArea(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        double x1 = DoubleArgumentType.getDouble(context, "x1");
        double y1 = DoubleArgumentType.getDouble(context, "y1");
        double z1 = DoubleArgumentType.getDouble(context, "z1");
        double x2 = DoubleArgumentType.getDouble(context, "x2");
        double y2 = DoubleArgumentType.getDouble(context, "y2");
        double z2 = DoubleArgumentType.getDouble(context, "z2");
        Box box = new Box(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        WatheExtendedWorldComponent.KEY.get(source.getWorld()).setLobbyArea(box);
        source.sendFeedback(() -> Text.translatable("command.watheextended.mapvariables.lobbyarea.set",
                        String.format("%.0f %.0f %.0f -> %.0f %.0f %.0f",
                                box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ))
                .styled(s -> s.withColor(0x55FF55)), true);
        return 1;
    }
}
