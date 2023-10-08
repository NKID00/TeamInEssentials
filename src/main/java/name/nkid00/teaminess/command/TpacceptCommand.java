package name.nkid00.teaminess.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import name.nkid00.teaminess.Teaminess;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TpacceptCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
                literal("/tpaccept").executes(TpacceptCommand::execute));
        dispatcher.register(literal("/tpa").redirect(literalCommandNode).executes(TpacceptCommand::execute));
    }

    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var destination = source.getPlayerOrThrow();
        var uuid = destination.getUuid();
        if (Teaminess.TpRequests.containsKey(uuid)) {
            var request = Teaminess.TpRequests.remove(uuid);
            if (request.isValid()) {
                request.execute();
                return 1;
            }
        }
        source.sendError(Text.literal("传送请求已过期或不存在"));
        return 0;
    }
}
