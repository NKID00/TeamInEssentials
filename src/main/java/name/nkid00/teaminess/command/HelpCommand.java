package name.nkid00.minimaltp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import name.nkid00.minimaltp.MinimalTp;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class HelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("/help").requires(source -> source.hasPermissionLevel(2)).executes(HelpCommand::execute));
    }

    public static int execute(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();
        source.sendFeedback(Text.literal("帮助 -> //help").setStyle(MinimalTp.MSG_STYLE), false);
        return 1;
    }
}