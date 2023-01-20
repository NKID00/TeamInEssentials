package name.nkid00.teaminess.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import name.nkid00.teaminess.Styles;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("/help").executes(HelpCommand::execute));
    }

    private static boolean addHelpMsgLine(List<Pair<String, String>> lines, String command, String description) {
        var pair = new Pair<String, String>(command, description);
        return lines.add(pair);
    };

    public static int execute(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();
        List<Pair<String, String>> msgLines = new ArrayList<Pair<String, String>>();
        addHelpMsgLine(msgLines, "//help", "显示此帮助");
        addHelpMsgLine(msgLines, "//tp <player>", "传送到玩家");
        addHelpMsgLine(msgLines, "//tpa -> //tpaccept", "接受玩家的传送请求");
        addHelpMsgLine(msgLines, "//tpr -> //tpareject", "拒绝玩家的传送请求");
        addHelpMsgLine(msgLines, "//waypoint", "共享坐标记录点操作");
        if (source.hasPermissionLevel(2))
            addHelpMsgLine(msgLines, "//reloadoptions", "重载配置文件");

        final var HELP_MSG_BAR_TEXT = "=============================================";
        var helpMsg = Text.literal(HELP_MSG_BAR_TEXT).setStyle(Styles.HELP_BAR);
        for (Pair<String, String> pair : msgLines) {
            helpMsg.append("\n").append(Text.literal(pair.getLeft()).setStyle(Styles.HELP_CMD))
                    .append(" ").append(Text.literal(pair.getRight()).setStyle(Styles.HELP_DESC));
        }
        helpMsg.append("\n").append(Text.literal(HELP_MSG_BAR_TEXT).setStyle(Styles.HELP_BAR));

        source.sendFeedback(helpMsg, false);
        return 1;
    }
}