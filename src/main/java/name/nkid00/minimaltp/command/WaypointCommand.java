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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

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
        var world = source.getWorld();

        var player = source.getPlayerOrThrow();
        var position = player.getBlockPos();
        var recorder = player.getUuid();

        return add(name, position, world, recorder) ? 1 : 0;
    }

    public static int executeAddGiven(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var name = StringArgumentType.getString(c, "name");
        var position = BlockPosArgumentType.getBlockPos(c, "position");

        var source = c.getSource();
        var world = source.getWorld();
        var recorder = source.getPlayerOrThrow().getUuid();

        return add(name, position, world, recorder) ? 1 : 0;
    }

    private static boolean add(String name, BlockPos position, ServerWorld world, UUID recorder) {
        return MinimalTp.waypoints.add(new Waypoint(name, position, world, recorder));
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
        if (source.hasPermissionLevel(2))
            return MinimalTp.waypoints.removeIf(waypoint -> waypoint.getName().equals(name)) ? 1 : 0;

        var executor = source.getPlayerOrThrow().getUuid();
        return MinimalTp.waypoints.removeIf(
                waypoint -> waypoint.getName().equals(name) && waypoint.getRecorder().equals(executor)
        ) ? 1 : 0;
    }

    public static int executeRename(CommandContext<ServerCommandSource> c) {
        var oldName = StringArgumentType.getString(c, "name");
        var newName = StringArgumentType.getString(c, "new name");

        for (Waypoint waypoint : MinimalTp.waypoints) {
            if (waypoint.getName().equals(oldName)){
                waypoint.setName(newName);
                return 1;
            }
        }
        return 0;
    }

}
