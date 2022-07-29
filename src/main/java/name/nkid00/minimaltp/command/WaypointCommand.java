package name.nkid00.minimaltp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.MinimalTp;
import name.nkid00.minimaltp.model.Waypoint;
import name.nkid00.minimaltp.model.Location;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.Random;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WaypointCommand {
    public static Random colorIdRnd = new Random();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("/waypoint")
                .executes(WaypointCommand::executeList)
                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .executes(WaypointCommand::executeAddCurrent)
                                .then(argument("position", BlockPosArgumentType.blockPos())
                                        .executes(WaypointCommand::executeAddGiven))))
                .then(literal("info")
                        .then(argument("name", StringArgumentType.string())
                                .executes(WaypointCommand::executeInfo)))
                .then(literal("list")
                        .executes(WaypointCommand::executeList))
                .then(literal("rename")
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("new name", StringArgumentType.word())
                                        .executes(WaypointCommand::executeRename))))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.string())
                                .executes(WaypointCommand::executeRemove)))
                // xaero-map support
                .then(literal("chat")
                        .then(literal("receive").executes(WaypointCommand::executeFromChat))
                        .then(literal("send")
                                .then(argument("name", StringArgumentType.string())
                                        .executes(WaypointCommand::executeToChat)))));
    }

    // waypoint add <name> - Auto add here where the player stand to the waypoints.
    public static int executeAddCurrent(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var name = StringArgumentType.getString(c, "name");

        if (!Waypoint.isNameLegal(name)) {
            source.sendError(Text.literal("坐标记录点名称非法或为空！"));
            return 0;
        }

        var dimension = source.getWorld().getDimension().effects();

        var player = source.getPlayerOrThrow();
        var position = player.getBlockPos();
        var recorder = player.getDisplayName().copy();

        addToMap(name, new Waypoint(new Location(position, dimension), recorder));

        var addMsg = Text.literal("已添加坐标记录点 " + name
                + " (" + dimension.toString() + ", " + position.toShortString() + ")")
                .setStyle(MinimalTp.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    // waypoint add <name> <position>
    public static int executeAddGiven(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var name = StringArgumentType.getString(c, "name");

        if (!Waypoint.isNameLegal(name)) {
            source.sendError(Text.literal("坐标记录点名称非法或为空！"));
            return 0;
        }

        var position = BlockPosArgumentType.getBlockPos(c, "position");
        var dimension = source.getWorld().getDimension().effects();

        var recorder = source.getPlayerOrThrow().getDisplayName().copy();

        addToMap(name, new Waypoint(new Location(position, dimension), recorder));

        var addMsg = Text.literal("已添加坐标记录点 " + name
                + " (" + dimension.toString() + ", " + position.toShortString() + ")")
                .setStyle(MinimalTp.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    private static void addToMap(Pair<String, Waypoint> waypointPair) {
        MinimalTp.WaypointMap.put(waypointPair.getLeft(), waypointPair.getRight());
    }

    private static void addToMap(String name, Waypoint w) {
        MinimalTp.WaypointMap.put(name, w);
    }

    // waypoint info
    public static int executeInfo(CommandContext<ServerCommandSource> c) {
        var name = StringArgumentType.getString(c, "name");

        Waypoint w;
        if (MinimalTp.WaypointMap.isEmpty() || (w = MinimalTp.WaypointMap.get(name)) == null) {
            c.getSource().sendError(Text.literal("无储存的坐标记录点 " + name));
            return 0;
        }
        var infoMsg = Text.literal("坐标 " + name
                + ": " + w.location.dimension().toString()
                + ", " + w.location.position().toShortString() + ", "
                + "记录者:")
                .append(w.recorder)
                .setStyle(MinimalTp.MSG_STYLE);
        c.getSource().sendFeedback(infoMsg, false);
        return 1;
    }

    // waypoint list
    public static int executeList(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();

        if (MinimalTp.WaypointMap.isEmpty()) {
            source.sendError(Text.literal("无储存的坐标记录点"));
        } else {
            var listMsg = Text.literal("共有" + MinimalTp.WaypointMap.size() + "个坐标记录点: ")
                    .setStyle(MinimalTp.MSG_STYLE);
            Set<String> list = MinimalTp.WaypointMap.keySet();

            listMsg.append(String.join(", ", list));
            source.sendFeedback(listMsg, false);
        }
        return 1;
    }

    // waypoint remove <name>
    public static int executeRemove(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var name = StringArgumentType.getString(c, "name");

        var executor = source.getPlayerOrThrow().getDisplayName().copy();
        var authorized = source.hasPermissionLevel(2);

        Waypoint w;
        int res = 0;
        if (MinimalTp.WaypointMap.isEmpty() || (w = MinimalTp.WaypointMap.get(name)) == null) {
            source.sendError(Text.literal("无储存的坐标记录点 " + name));
        } else if (!authorized && !(w.recorder == executor)) {
            source.sendError(Text.literal("您无权限移除坐标记录点" + name));
        } else if (MinimalTp.WaypointMap.remove(name, w)) {
            var removeMsg = Text.literal("已移除坐标记录点 " + name)
                    .setStyle(MinimalTp.MSG_STYLE);
            source.sendFeedback(removeMsg, true);
            res = 1;
        } else {
            source.sendError(Text.literal("坐标记录点 " + name + " 已更改"));
        }
        return res;
    }

    // waypoint rename <name> <new name>
    public static int executeRename(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();
        var oldName = StringArgumentType.getString(c, "name");
        var newName = StringArgumentType.getString(c, "new name");

        if (!Waypoint.isNameLegal(newName)) {
            source.sendError(Text.literal("坐标记录点名称非法或为空！"));
            return 0;
        }

        var authorized = source.hasPermissionLevel(2);

        Waypoint w;
        int res = 0;
        if (MinimalTp.WaypointMap.isEmpty() || (w = MinimalTp.WaypointMap.get(oldName)) == null) {
            source.sendError(Text.literal("未找到坐标记录点 " + oldName));
        } else if (!authorized) {
            source.sendError(Text.literal("您无权限重命名坐标记录点 " + oldName));
        } else if (MinimalTp.WaypointMap.remove(oldName, w)) {
            MinimalTp.WaypointMap.put(newName, w);
            var renameMsg = Text.literal("已将坐标记录点 " + oldName + "重命名为 " + newName)
                    .setStyle(MinimalTp.MSG_STYLE);
            source.sendFeedback(renameMsg, true);
            res = 1;
        } else {
            source.sendError(Text.literal("坐标记录点 " + oldName + " 已更改"));
        }
        return res;
    }

    // waypoint pull - Store the latest shared xaero-waypoint
    public static int executeFromChat(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();

        var latest = MinimalTp.latestWaypointPair;
        String name = latest.getLeft();
        Waypoint w = latest.getRight();
        if (w.isEmpty()) {
            source.sendError(Text.literal("未找到最近的坐标分享点"));
            return 0;
        } else if (!Waypoint.isNameLegal(name)) {
            source.sendError(Text.literal("最近的坐标分享点名称非法或为空！"));
            return 0;
        }

        addToMap(latest);
        var addMsg = Text.literal("已添加坐标记录点 " + name
                + "(" + w.location.dimension().toString() + ", " + w.location.position().toShortString() + ")")
                .setStyle(MinimalTp.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    // waypoint mark <name> - mark the waypoint in the xaero-map
    public static int executeToChat(CommandContext<ServerCommandSource> c) {
        var name = StringArgumentType.getString(c, "name");

        Waypoint w;
        if (MinimalTp.WaypointMap.isEmpty() || (w = MinimalTp.WaypointMap.get(name)) == null) {
            c.getSource().sendError(Text.literal("无储存的坐标记录点 " + name));
            return 0;
        }

        String dimension = w.location.dimension().toString().split(":")[1];
        String[] contents = { "xaero-waypoint",
                name,
                name.substring(0, 1),
                String.valueOf(w.location.position().getX()),
                String.valueOf(w.location.position().getY()),
                String.valueOf(w.location.position().getZ()),
                String.valueOf(colorIdRnd.nextInt(15)),
                "false:0",
                "Internal-" + dimension + "-waypoints"
        };

        var rcvMsg = Text.literal(String.join(":", contents));
        c.getSource().sendFeedback(rcvMsg, false);
        return 1;
    }

}
