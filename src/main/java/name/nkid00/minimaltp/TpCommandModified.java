package name.nkid00.minimaltp;

import java.util.Collections;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.mixin.TeleportCommandMixin;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TpCommandModified {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var source = c.getSource();
        if (source.hasPermissionLevel(2)) {
            // operators, execute as vanilla
            return TeleportCommandMixin.execute(source, Collections.singleton(source.getEntityOrThrow()), EntityArgumentType.getEntity(c, "destination"));
        }
        // non-operators
        var target = c.getSource().getPlayerOrThrow();
        var destination = EntityArgumentType.getPlayer(c, "destination");
        var scoreboard = source.getServer().getScoreboard();
        if (scoreboard.getPlayerTeam(target.getEntityName()).isEqual(scoreboard.getPlayerTeam(destination.getEntityName()))) {
            // destination player is in the same team, teleport immediately
            source.sendFeedback(Text.literal("Teleporting to teammate..."), false);
            var result = TeleportCommandMixin.execute(source, Collections.singleton(target), destination);
            if (result != 1) {
                source.sendError(Text.literal("Teleportation failed. An unknown error occurred."));
            }
            return result;
        }
        // destination player is in other teams, send request
        MinimalTp.TpRequests.put(destination.getUuid(), new TpRequest(source, destination));
        destination.getCommandSource().sendFeedback(Text.literal("<> requests to teleport to you. You may accept it with /tpa or refuse it with /tpr in <> seconds."), false);
        source.sendFeedback(Text.literal("Teleportation request sent."), false);
        return 0;
    }
}
