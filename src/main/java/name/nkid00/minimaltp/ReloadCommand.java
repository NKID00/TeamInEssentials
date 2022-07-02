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

public class ReloadCommand {
    private static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("/tpreload").requires(source -> source.hasPermissionLevel(2)).executes(ReloadCommand::execute));
    }

    public static int execute(CommandContext<ServerCommandSource> c) {
        var source = c.getSource();
        MinimalTp.reloadConfig();
        source.sendFeedback(Text.literal("已重载配置文件").setStyle(MSG_STYLE), false);
        return 1;
    }
}
