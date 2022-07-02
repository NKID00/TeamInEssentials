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
            if (request.isValid()) {
                source.sendFeedback(Text.literal(String.format("将在%d秒后传送", MinimalTp.settings.teleport_interval))
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
                request.source.sendFeedback(
                        Text.literal(String.format("传送请求被接受, 将在%d秒后传送", MinimalTp.settings.teleport_interval))
                                .setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
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
            if (request.isValid()) {
                source.sendFeedback(Text.literal("已拒绝传送请求")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
                request.source.sendError(Text.literal("传送请求被拒绝"));
                return 1;
            }
        }
        source.sendError(Text.literal("传送请求已过期或不存在"));
        return 0;
    }
}
