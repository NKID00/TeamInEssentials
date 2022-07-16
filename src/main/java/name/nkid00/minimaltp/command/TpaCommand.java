package name.nkid00.minimaltp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.MinimalTp;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TpaCommand {
        public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                        CommandRegistryAccess registryAccess,
                        CommandManager.RegistrationEnvironment environment) {
                dispatcher.register(
                                literal("/tpa").executes(TpaCommand::execute));
        }

        public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
                var source = c.getSource();
                var destination = source.getPlayerOrThrow();
                var uuid = destination.getUuid();
                if (MinimalTp.TpRequests.containsKey(uuid)) {
                        var request = MinimalTp.TpRequests.remove(uuid);
                        var target = request.target;
                        if (request.isValid()) {
                                var destMsg = Text.literal("已接受")
                                                .append(target.getDisplayName().copy())
                                                .append(Text.literal(String.format("的传送请求, 将在%d秒后传送",
                                                                MinimalTp.options.teleportInterval)))
                                                .setStyle(MinimalTp.MSG_STYLE);
                                source.sendFeedback(destMsg, false);

                                var targetMsg = Text.literal("向")
                                                .append(destination.getDisplayName().copy())
                                                .append(Text.literal(String.format("的传送请求被接受, 将在%d秒后传送",
                                                                MinimalTp.options.teleportInterval)))
                                                .setStyle(MinimalTp.ACCEPT_STYLE);
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
                                                .setStyle(MinimalTp.MSG_STYLE);
                                source.sendFeedback(destMsg, false);

                                var targetMsg = Text.literal("向")
                                                .append(destination.getDisplayName().copy())
                                                .append(Text.literal("的传送请求被拒绝"))
                                                .setStyle(MinimalTp.REFUSE_STYLE);
                                request.source.sendError(targetMsg);

                                return 1;
                        }
                }
                source.sendError(Text.literal("传送请求已过期或不存在"));
                return 0;
        }
}
