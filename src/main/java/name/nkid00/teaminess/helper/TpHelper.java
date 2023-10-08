package name.nkid00.teaminess.helper;

import java.util.Collections;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.teaminess.Styles;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import name.nkid00.teaminess.Teaminess;
import name.nkid00.teaminess.mixin.TeleportCommandMixin;

public class TpHelper {
    public static int teleportImmediately(ServerPlayerEntity target,
            ServerPlayerEntity destination) throws CommandSyntaxException {
        var feedback = Text.literal("已将")
                .append(target.getDisplayName().copy())
                .append("传送至")
                .append(destination.getDisplayName().copy());
        destination.sendMessage(feedback);
        return TeleportCommandMixin.execute(target.getCommandSource(), Collections.singleton(target), destination);
    }

    public static void teleportTimed(ServerPlayerEntity target, ServerPlayerEntity destination) {
        var targetMsg = Text.literal("向")
                .append(destination.getDisplayName().copy())
                .append(Text.literal(String.format("的传送请求被接受, 将在%d秒后传送",
                        Teaminess.options.teleportInterval)))
                .setStyle(Styles.ACCEPTED);
        target.sendMessage(targetMsg);
        var destMsg = Text.literal("已接受")
                .append(target.getDisplayName().copy())
                .append(Text.literal(String.format("的传送请求, 将在%d秒后传送",
                        Teaminess.options.teleportInterval)))
                .setStyle(Styles.NORMAL_MSG);
        destination.sendMessage(destMsg);
        Teaminess.TELEPORT_TIMER.schedule(new TpTask(target, destination), Teaminess.options.teleportInterval, TimeUnit.SECONDS);
    }

    private static class TpTask extends TimerTask {
        private final ServerPlayerEntity target;
        private final ServerPlayerEntity destination;

        public TpTask(ServerPlayerEntity target,
                ServerPlayerEntity destination) {
            this.target = target;
            this.destination = destination;
        }

        public void run() {
            if (target.isDisconnected() || destination.isDisconnected()) {
                return;
            }
            try {
                TpHelper.teleportImmediately(target, destination);
            } catch (CommandSyntaxException ignored) {
            }
        }
    }
}
