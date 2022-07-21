package name.nkid00.minimaltp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.MinimalTp;
import name.nkid00.minimaltp.Waypoint;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WaypointCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("/waypoint")
                .executes(WaypointCommand::executeList)
                .then(literal("add")
                        .executes(WaypointCommand::executeAddShared)
                        .then(argument("name", StringArgumentType.word())
                                .executes(WaypointCommand::executeAddCurrent)
                                .then(argument("position", BlockPosArgumentType.blockPos())
                                        .executes(WaypointCommand::executeAddGiven))))
                .then(literal("info")
                        .then(argument("name", StringArgumentType.word())
                                .executes(WaypointCommand::executeInfo)))
                .then(literal("list")
                        .executes(WaypointCommand::executeList))
                .then(literal("receive")
                        .then(argument("name", StringArgumentType.word())
                                .executes(WaypointCommand::executeReceive)))
                .then(literal("rename")
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("new name", StringArgumentType.word())
                                        .executes(WaypointCommand::executeRename))))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .executes(WaypointCommand::executeRemove))));
    }

    public static int executeAddShared(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeAddCurrent(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var name = StringArgumentType.getString(c, "name");

        var source = c.getSource();
        var dimension = source.getWorld().getDimension().effects();

        var player = source.getPlayerOrThrow();
        var position = player.getBlockPos();
        var recorder = player.getDisplayName().copy();

        if (addReversed(name, position, dimension, recorder)) return 0;

        var addMsg = Text.literal("已共享坐标" + name
                        + "(" + dimension.toString() + ", " + position.toShortString() + ")")
                .setStyle(MinimalTp.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    public static int executeAddGiven(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var name = StringArgumentType.getString(c, "name");
        var position = BlockPosArgumentType.getBlockPos(c, "position");

        var source = c.getSource();
        var dimension = source.getWorld().getDimension().effects();

        var recorder = source.getPlayerOrThrow().getDisplayName().copy();

        if (addReversed(name, position, dimension, recorder)) return 0;

        var addMsg = Text.empty()
                .append(recorder)
                .append("已共享坐标" + name + "(" + dimension.toString() + ", " + position.toShortString() + ")")
                .setStyle(MinimalTp.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    private static boolean addReversed(String name, BlockPos position, Identifier dimension, Text recorder) {
        return !MinimalTp.waypoints.add(new Waypoint(name, position, dimension, recorder));
    }

    public static int executeInfo(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeList(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeReceive(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeRemove(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var name = StringArgumentType.getString(c, "name");

        var source = c.getSource();
        if (source.hasPermissionLevel(2)) {
            if (MinimalTp.waypoints.removeIf(waypoint -> waypoint.getName().equals(name))) {
                var removeMsg = Text.literal("已移除共享坐标" + name)
                        .setStyle(MinimalTp.MSG_STYLE);
                source.sendFeedback(removeMsg, true);
                return 1;
            }

            source.sendError(Text.literal("未找到共享坐标" + name));
            return 0;
        }

        var executor = source.getPlayerOrThrow().getDisplayName().copy();

        if (MinimalTp.waypoints.removeIf(
                waypoint -> waypoint.getName().equals(name) && waypoint.getRecorder().equals(executor)
        )) {
            var removeMsg = Text.literal("已移除共享坐标" + name)
                    .setStyle(MinimalTp.MSG_STYLE);
            source.sendFeedback(removeMsg, true);
            return 1;
        }

        source.sendError(Text.literal("未找到共享坐标" + name + "或您无权限移除此坐标"));
        return 0;
    }

    public static int executeRename(CommandContext<ServerCommandSource> c) {
        var oldName = StringArgumentType.getString(c, "name");
        var newName = StringArgumentType.getString(c, "new name");

        for (Waypoint waypoint : MinimalTp.waypoints) {
            if (waypoint.getName().equals(oldName)) {
                waypoint.setName(newName);

                var renameMsg = Text.literal("已将坐标" + oldName +"重命名为" + newName)
                        .setStyle(MinimalTp.MSG_STYLE);
                c.getSource().sendFeedback(renameMsg, true);
                return 1;
            }
        }

        c.getSource().sendError(Text.literal("未找到坐标" + oldName));
        return 0;
    }

}
