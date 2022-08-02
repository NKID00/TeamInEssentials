package name.nkid00.teaminess.message;

import name.nkid00.teaminess.Teaminess;
import name.nkid00.teaminess.model.Waypoint;
import name.nkid00.teaminess.model.Location;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import static java.lang.Integer.parseInt;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.message.MessageType.Parameters;

public class ChatMessage {
    public static void onChatMessage(SignedMessage message, ServerPlayerEntity sender, Parameters parameters) {
        recordWaypoint(message.toString(), sender);
    }

    public static void recordWaypoint(String message, ServerPlayerEntity sender) {
        if (message == null
                /*
                 * The shortest legal Xaero waypoint sharing message is
                 * "xaero-waypoint:a:a:0:0:0:0:true:0:Internal-the_end-waypoints",
                 * whose length is 61.
                 */
                || message.length() < 61
                || !message.startsWith("xaero-waypoint:")) {
            return;
        }

        /*
         * Xaero Waypoint Sharing Format
         * [xaero-waypoint:
         * name(String):
         * initial(String, one character):
         * x(int):
         * y(int):
         * z(int):
         * color(int, 0-15):
         * disabled(boolean):
         * type(int):
         * dimension(string, Internal-overworld/the_neither/the_end-waypoints)]
         */
        String[] params = message.split(":");
        if (params.length == 10) {
            BlockPos position;
            Identifier dimension;

            try {
                position = new BlockPos(parseInt(params[3]), parseInt(params[4]), parseInt(params[5]));
                dimension = new Identifier("minecraft", params[9].split("-")[1]);
                Teaminess.latestWaypointPair = new Pair<>(params[1], new Waypoint(
                        new Location(position, dimension),
                        sender.getDisplayName().copy(), parseInt(params[6])));
            } catch (NumberFormatException | InvalidIdentifierException ignored) {
            }
        }
    }
}
