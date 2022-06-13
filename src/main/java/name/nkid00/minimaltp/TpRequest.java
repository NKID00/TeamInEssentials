package name.nkid00.minimaltp;

import java.util.Collections;
import java.util.TimerTask;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.minimaltp.mixin.TeleportCommandInvoker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class TpRequest {
    public final ServerCommandSource source;
    public final PlayerEntity target;
    public final PlayerEntity destination;
    public final long ExpirationTime;

    public TpRequest(ServerCommandSource source, PlayerEntity destination) throws CommandSyntaxException {
        this.source = source;
        this.target = source.getPlayerOrThrow();
        this.destination = destination;
        this.ExpirationTime = System.currentTimeMillis() + MinimalTp.settings.requestExpirationInterval * 1000;
    }

    public void execute() throws CommandSyntaxException {
        MinimalTp.TELEPORT_TIMER.schedule(new TpTask(this), MinimalTp.settings.teleportInterval * 1000);
    }

    public int executeImmediately() throws CommandSyntaxException {
        return TeleportCommandInvoker.execute(source, Collections.singleton(target), destination);
    }

    public boolean isValid() {
        return System.currentTimeMillis() < this.ExpirationTime;
    }

    class TpTask extends TimerTask {
        private final TpRequest request;
        
        public TpTask(TpRequest request) {
            this.request = request;
        }
        
        public void run() {
            try {
                request.executeImmediately();
            } catch (CommandSyntaxException e) {
            }
        }
    }
}
