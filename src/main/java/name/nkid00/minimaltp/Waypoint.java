package name.nkid00.minimaltp;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class Waypoint {
    String name;
    final BlockPos position;
    final ServerWorld world;
    final UUID recorder;

    public Waypoint(String name, BlockPos position, ServerWorld world, UUID recorder) {
        this.name = name;
        this.position = position;
        this.world = world;
        this.recorder = recorder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockPos getPosition() {
        return position;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public UUID getRecorder() {
        return recorder;
    }
}
