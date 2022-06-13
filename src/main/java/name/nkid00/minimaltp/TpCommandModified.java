package name.nkid00.minimaltp;

import java.util.Collections;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.mixin.TeleportCommandInvoker;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TpCommandModified {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        if (source.hasPermissionLevel(2)) {
            // operators, execute as vanilla
            return TeleportCommandInvoker.execute(source, Collections.singleton(source.getEntityOrThrow()), EntityArgumentType.getEntity(c, "destination"));
        }
        // non-operators
        var target = c.getSource().getPlayerOrThrow();
        var destination = EntityArgumentType.getPlayer(c, "destination");
        var scoreboard = source.getServer().getScoreboard();
        var target_team = scoreboard.getPlayerTeam(target.getEntityName());
        var destination_team = scoreboard.getPlayerTeam(destination.getEntityName());
        if (target_team != null && destination_team != null && target_team.isEqual(destination_team)) {
            // destination player is in the same team, teleport immediately
            source.sendFeedback(Text.literal("Teleporting to teammate..."), false);
            var result = TeleportCommandInvoker.execute(source, Collections.singleton(target), destination);
            if (result != 1) {
                source.sendError(Text.literal("Teleportation failed. An unknown error occurred."));
            }
            return result;
        }
        // destination player is in other teams, send request
        MinimalTp.TpRequests.put(destination.getUuid(), new TpRequest(source, destination));
        var feedback = target.getDisplayName().copy()
            .append(" requested teleportation to you. You may ")
            .append(
                Text.literal("accept it (/tpa)")
                .setStyle(
                    Style.EMPTY
                    .withColor(Formatting.GREEN)
                    .withUnderline(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa"))
                )
            )
            .append(" or ")
            .append(
                Text.literal("refuse it (/tpr)")
                .setStyle(
                    Style.EMPTY
                    .withColor(Formatting.RED)
                    .withUnderline(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpr"))
                )
            )
            .append(String.format(" in %d seconds.", MinimalTp.EXPIRATION_INTERVAL));
        destination.getCommandSource().sendFeedback(feedback, false);
        source.sendFeedback(Text.literal("Teleportation request sent."), false);
        return 0;
    }
}
