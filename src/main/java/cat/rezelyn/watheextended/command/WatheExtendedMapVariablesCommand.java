package cat.rezelyn.watheextended.command;

import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
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
}

