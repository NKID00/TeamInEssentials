package name.nkid00.minimaltp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class TpaTprCommand {
    private static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
    private static final Style ACCEPT_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
    private static final Style REFUSE_STYLE = Style.EMPTY.withColor(Formatting.RED);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("/tpa").executes(TpaTprCommand::TpaExecute));
        dispatcher.register(
                literal("/tpr").executes(TpaTprCommand::TprExecute));
    }

    public static int TpaExecute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var destination = source.getPlayerOrThrow();
        var uuid = destination.getUuid();
        if (MinimalTp.TpRequests.containsKey(uuid)) {
            var request = MinimalTp.TpRequests.remove(uuid);
            var target = request.target;
            if (request.isValid()) {
                var destMsg = Text.literal("已接受")
                        .append(target.getDisplayName().copy())
                        .append(Text.literal("的传送请求, 将在%d秒后传送"))
                        .setStyle(MSG_STYLE);
                source.sendFeedback(destMsg, false);

                var targetMsg = Text.literal("向")
                        .append(destination.getDisplayName().copy())
                        .append(Text.literal(String.format("的传送请求被接受, 将在%d秒后传送",
                                MinimalTp.settings.teleport_interval)))
                        .setStyle(ACCEPT_STYLE);
                request.source.sendFeedback(targetMsg, false);

                request.execute();
                return 1;
            }
        }
        source.sendError(Text.literal("传送请求已过期或不存在"));
        return 0;
    }

    public static int TprExecute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var destination = source.getPlayerOrThrow();
        var uuid = destination.getUuid();
        if (MinimalTp.TpRequests.containsKey(uuid)) {
            var request = MinimalTp.TpRequests.remove(uuid);
            var target = request.target;
            if (request.isValid()) {
                var destMsg = Text.literal("已拒绝")
                        .append(target.getDisplayName().copy())
                        .append(Text.literal("的传送请求"))
                        .setStyle(MSG_STYLE);
                source.sendFeedback(destMsg, false);

                var targetMsg = Text.literal("向")
                        .append(destination.getDisplayName().copy())
                        .append(Text.literal("的传送请求被拒绝"))
                        .setStyle(REFUSE_STYLE);
                request.source.sendError(targetMsg);

                return 1;
            }
        }
        source.sendError(Text.literal("传送请求已过期或不存在"));
        return 0;
    }
}
