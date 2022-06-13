package name.nkid00.minimaltp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TpaTprCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("tpa").executes(TpaTprCommand::TpaExecute));
        dispatcher.register(
                literal("tpr").executes(TpaTprCommand::TprExecute));
    }

    public static int TpaExecute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var destination = source.getPlayerOrThrow();
        var uuid = destination.getUuid();
        if (MinimalTp.TpRequests.containsKey(uuid)) {
            var request = MinimalTp.TpRequests.remove(uuid);
            if (request.isValid()) {
                request.source.sendFeedback(Text.literal("Teleportation request was accepted."), false);
                source.sendFeedback(Text.literal("Teleportation request accepted successfully."), false);
                var result = request.execute();
                if (result != 1) {
                    request.source.sendError(Text.literal("Teleportation failed. An unknown error occurred."));
                    source.sendError(Text.literal("Teleportation failed. An unknown error occurred."));
                }
                return result;
            }
        }
        source.sendError(Text.literal("Teleportation request expired or does not exist."));
        return 0;
    }

    public static int TprExecute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var destination = source.getPlayerOrThrow();
        var uuid = destination.getUuid();
        if (MinimalTp.TpRequests.containsKey(uuid)) {
            var request = MinimalTp.TpRequests.remove(uuid);
            if (request.isValid()) {
                request.source.sendFeedback(Text.literal("Teleportation request was refused."), false);
                source.sendFeedback(Text.literal("Teleportation request refused successfully."), false);
                return 1;
            }
        }
        source.sendError(Text.literal("Teleportation request expired or does not exist."));
        return 0;
    }
}
