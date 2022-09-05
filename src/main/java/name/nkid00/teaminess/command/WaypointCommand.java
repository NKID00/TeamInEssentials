package name.nkid00.teaminess.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.teaminess.Teaminess;
import name.nkid00.teaminess.model.Waypoint;
import name.nkid00.teaminess.model.Location;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WaypointCommand {

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
                        .then(literal("get").executes(WaypointCommand::executeFromChat))
                        .then(literal("send")
                                .then(argument("name", StringArgumentType.string())
                                        .executes(WaypointCommand::executeToChat)))));
    }

    // waypoint add <name> - Auto add here where the player stand to the waypoints.
    public static int executeAddCurrent(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        return addWaypoint(c, false);
    }

    // waypoint add <name> <position>
    public static int executeAddGiven(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        return addWaypoint(c, true);
    }

    private static int addWaypoint(CommandContext<ServerCommandSource> c, boolean given) throws CommandSyntaxException {
        var source = c.getSource();
        var name = StringArgumentType.getString(c, "name");

        if (isNameInvalid(name)) {
            source.sendError(Text.literal("坐标记录点名称非法或为空！"));
            return 0;
        }

        BlockPos position;
        Text recorder;
        if (given) {
            position = BlockPosArgumentType.getBlockPos(c, "position");
            recorder = source.getPlayerOrThrow().getDisplayName().copy();
        } else {
            var player = source.getPlayerOrThrow();
            position = player.getBlockPos();
            recorder = player.getDisplayName().copy();
        }
        Identifier dimension = source.getWorld().getDimension().effects();

        Waypoint w = new Waypoint(new Location(position, dimension), recorder);
        if (w.isInvalid()) {
            source.sendError(Text.literal("添加坐标记录点时出现未知错误"));
            return 0;
        } else if (!addToMap(name, w)) {
            source.sendError(Text.literal("坐标记录点 ").append(decorateName(name)).append(" 已存在"));
            return 0;
        }
        var addMsg = Text.literal("已添加坐标记录点 ").append(decorateName(name, w))
                .append(" (" + position.toShortString() + ", " + dimension.toString() + ")")
                .setStyle(Teaminess.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    // waypoint info
    public static int executeInfo(CommandContext<ServerCommandSource> c) {
        var name = StringArgumentType.getString(c, "name");

        Waypoint w;
        if (Teaminess.WaypointMap.isEmpty() || (w = Teaminess.WaypointMap.get(name)) == null) {
            c.getSource().sendError(Text.literal("无储存的坐标记录点 " + name));
            return 0;
        }

        var infoMsg = Text.literal("坐标 ").append(decorateName(name, w))
                .append(": " + w.getLocation().position().toShortString()
                        + ", " + w.getLocation().dimension().toString() + ", "
                        + "记录者:")
                .append(w.getRecorder())
                .setStyle(Teaminess.MSG_STYLE);
        c.getSource().sendFeedback(infoMsg, false);
        return 1;
    }

    // waypoint list
    public static int executeList(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();

        if (Teaminess.WaypointMap.isEmpty()) {
            source.sendError(Text.literal("无储存的坐标记录点"));
        } else {
            var listMsg = Text.literal("共有" + Teaminess.WaypointMap.size() + "个坐标记录点: ")
                    .setStyle(Teaminess.MSG_STYLE);

            Text[] decorated = Teaminess.WaypointMap.keySet().stream()
                    .map(WaypointCommand::decorateName)
                    .toArray(Text[]::new);
            listMsg.append(decorated[0]); // No comma before the first name
            Arrays.stream(decorated).skip(1).forEach(text -> listMsg.append(", ").append(text));

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
        if (Teaminess.WaypointMap.isEmpty() || (w = Teaminess.WaypointMap.get(name)) == null) {
            source.sendError(Text.literal("无储存的坐标记录点 " + name));
        } else if (!authorized && !(w.getRecorder() == executor)) {
            source.sendError(Text.literal("您无权限移除坐标记录点 ")
                    .append(decorateName(name, w)));
        } else if (Teaminess.WaypointMap.remove(name, w)) {
            var removeMsg = Text.literal("已移除坐标记录点 ")
                    .append(decorateName(name, w))
                    .setStyle(Teaminess.MSG_STYLE);
            source.sendFeedback(removeMsg, true);
            res = 1;
        } else {
            source.sendError(Text.literal("坐标记录点 ").append(decorateName(name, w))
                    .append(" 已更改"));
        }
        return res;
    }

    // waypoint rename <name> <new name>
    public static int executeRename(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();
        var oldName = StringArgumentType.getString(c, "name");
        var newName = StringArgumentType.getString(c, "new name");

        if (isNameInvalid(newName)) {
            source.sendError(Text.literal("新坐标记录点名称非法或为空！"));
            return 0;
        }

        var authorized = source.hasPermissionLevel(2);

        Waypoint w;
        int res = 0;
        if (Teaminess.WaypointMap.isEmpty() || (w = Teaminess.WaypointMap.get(oldName)) == null) {
            source.sendError(Text.literal("无储存的坐标记录点 " + oldName));
        } else if (!authorized) {
            source.sendError(Text.literal("您无权限重命名坐标记录点 ")
                    .append(decorateName(oldName, w)));
        } else if (Teaminess.WaypointMap.remove(oldName, w)) {
            Teaminess.WaypointMap.put(newName, w);
            var renameMsg = Text.literal("已将坐标记录点 ").append(decorateName(oldName, w))
                    .append(" 重命名为 ").append(decorateName(newName, w))
                    .setStyle(Teaminess.MSG_STYLE);
            source.sendFeedback(renameMsg, true);
            res = 1;
        } else {
            source.sendError(Text.literal("坐标记录点 ").append(decorateName(oldName, w))
                    .append(" 已更改"));
        }
        return res;
    }

    // waypoint chat get - Store the latest shared xaero-waypoint
    public static int executeFromChat(CommandContext<ServerCommandSource> c) {
        if (!Teaminess.COMPATIBLE_MAP_MODS){
            c.getSource().sendError(Text.literal("无兼容的地图模组"));
            return 0;
        }

        var source = c.getSource();
        var latest = Teaminess.latestWaypointPair;
        String name = latest.getLeft();
        Waypoint w = latest.getRight();
        if (w.isInvalid()) {
            source.sendError(Text.literal("未找到最近的坐标分享点"));
            return 0;
        } else if (isNameInvalid(name)) {
            source.sendError(Text.literal("最近的坐标分享点名称非法或为空！"));
            return 0;
        } else if (!addToMap(latest)) {
            source.sendError(Text.literal("坐标记录点 ").append(decorateName(name)).append(" 已存在"));
            return 0;
        }
        var addMsg = Text.literal("已添加坐标记录点 ").append(decorateName(name, w))
                .append(" (" + w.getLocation().position().toShortString()
                        + ", " + w.getLocation().dimension().toString() + ")")
                .setStyle(Teaminess.MSG_STYLE);
        source.sendFeedback(addMsg, true);
        return 1;
    }

    // waypoint chat send <name> - mark the waypoint in the xaero-map
    public static int executeToChat(CommandContext<ServerCommandSource> c) {
        if (!Teaminess.COMPATIBLE_MAP_MODS){
            c.getSource().sendError(Text.literal("无兼容的地图模组"));
            return 0;

        }

        var name = StringArgumentType.getString(c, "name");
        Waypoint w;
        if (Teaminess.WaypointMap.isEmpty() || (w = Teaminess.WaypointMap.get(name)) == null) {
            c.getSource().sendError(Text.literal("无储存的坐标记录点 " + name));
            return 0;
        }

        String dimension = w.getLocation().dimension().toString().split(":")[1];
        String[] contents = {"xaero-waypoint",
                name,
                name.substring(0, 1),
                String.valueOf(w.getLocation().position().getX()),
                String.valueOf(w.getLocation().position().getY()),
                String.valueOf(w.getLocation().position().getZ()),
                String.valueOf(w.getColorId()),
                "false:0",
                "Internal-" + dimension + "-waypoints"
        };

        var rcvMsg = Text.literal(String.join(":", contents));
        c.getSource().sendFeedback(rcvMsg, false);
        return 1;
    }

    private static boolean addToMap(Pair<String, Waypoint> pair) {
        return addToMap(pair.getLeft(), pair.getRight());
    }

    private static boolean addToMap(String name, Waypoint w) {
        return Teaminess.WaypointMap.putIfAbsent(name, w) == null;
    }

    private static boolean isNameInvalid(String name) {
        return name.trim().length() == 0;
    }

    private static MutableText decorateName(String name) {
        return decorateName(name, Teaminess.WaypointMap.get(name));
    }

    // Format the name with hover text about the given waypoint
    private static MutableText decorateName(String name, Waypoint w) {
        String[] hoverStrComposition = {
                String.valueOf(w.getLocation().position().getX()),
                String.valueOf(w.getLocation().position().getY()),
                String.valueOf(w.getLocation().position().getZ()),
                w.getLocation().dimension().toString()
        };
        Text hoverText = Text.literal(String.join(", ", hoverStrComposition));

        Style style = Style.EMPTY.withColor(Formatting.byColorIndex(w.getColorId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

        return Text.literal(name).setStyle(style);
    }
}
