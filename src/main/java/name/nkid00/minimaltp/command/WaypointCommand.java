package name.nkid00.minimaltp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

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
                                .then(argument("position", Vec3ArgumentType.vec3())
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

    public static int executeAddCurrent(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeAddGiven(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeInfo(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeRemove(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeReceive(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeRename(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }

    public static int executeList(CommandContext<ServerCommandSource> c) {
        //TODO
        return 0;
    }
}
