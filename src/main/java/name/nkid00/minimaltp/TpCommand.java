package name.nkid00.minimaltp;

import java.util.Collections;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.mixin.TeleportCommandInvoker;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class TpCommand {
    private static final Style MSG_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
    private static final Style ACCEPT_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_GREEN)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpa"));
    private static final Style REFUSE_CMD_STYLE = Style.EMPTY
            .withColor(Formatting.DARK_RED)
            .withUnderline(true)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "//tpr"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("/tp").then(
                        argument("destination", EntityArgumentType.player()).executes(TpCommand::execute)));
    }

    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        var target = source.getPlayerOrThrow();
        var destination = EntityArgumentType.getPlayer(c, "destination");
        if (target.equals(destination)) {
            source.sendError(Text.literal("请不要随意玩耍硬核自研大数据人工智能黑科技模组!"));
            return 0;
        }

        var scoreboard = source.getServer().getScoreboard();
        var target_team = scoreboard.getPlayerTeam(target.getEntityName());
        var destination_team = scoreboard.getPlayerTeam(destination.getEntityName());
        var teleportImmediately = false;
        if (target_team != null && destination_team != null && target_team.isEqual(destination_team)) {
            teleportImmediately = MinimalTp.settings.immediate_teleportation_in_team;
        } else {
            teleportImmediately = MinimalTp.settings.immediate_teleportation_between_team;
        }

        if (teleportImmediately) {
            return TeleportCommandInvoker.execute(source, Collections.singleton(target), destination);
        } else {
            MinimalTp.TpRequests.put(destination.getUuid(), new TpRequest(source, destination));

            var feedback = target.getDisplayName().copy().setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
                    .append(Text.literal(String.format("请求传送至你的位置,可以在%d秒内选择 ",
                            MinimalTp.settings.request_expiration_interval)).setStyle(MSG_STYLE))
                    .append(Text.literal("接受(//tpa)").setStyle(ACCEPT_CMD_STYLE))
                    .append(Text.literal(" 或 ").setStyle(MSG_STYLE))
                    .append(Text.literal("拒绝(//tpr)").setStyle(REFUSE_CMD_STYLE));
            destination.getCommandSource().sendFeedback(feedback, false);
            source.sendFeedback(Text.literal("已发送传送请求").setStyle(MSG_STYLE), false);

            return 1;
        }
    }
}
