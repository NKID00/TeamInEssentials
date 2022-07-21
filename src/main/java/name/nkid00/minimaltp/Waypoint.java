package name.nkid00.minimaltp;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Waypoint {
    String name;
    final BlockPos position;
    final Identifier dimension;
    final Text recorder;

    public Waypoint(String name, BlockPos position, Identifier dimension, Text recorder) {
        this.name = name;
        this.position = position;
        this.dimension = dimension;
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

    public Identifier getDimension() {
        return dimension;
    }

    public Text getRecorder() {
        return recorder;
    }
}
