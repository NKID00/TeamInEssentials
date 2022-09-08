package name.nkid00.teaminess.model;

import name.nkid00.teaminess.Teaminess;
import name.nkid00.teaminess.helper.TpHelper;

import net.minecraft.server.network.ServerPlayerEntity;

public class TpRequest {
    public final ServerPlayerEntity target;
    public final ServerPlayerEntity destination;
    public final long ExpirationTime;

    public TpRequest(ServerPlayerEntity target, ServerPlayerEntity destination) {
        this.target = target;
        this.destination = destination;
        this.ExpirationTime = System.currentTimeMillis() + Teaminess.options.responseInterval * 1000;
    }

    public void execute() {
        TpHelper.teleportTimed(target, destination);
    }

    public boolean isValid() {
        return System.currentTimeMillis() < this.ExpirationTime;
    }
}
