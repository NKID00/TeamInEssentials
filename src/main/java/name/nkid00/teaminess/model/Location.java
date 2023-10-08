package name.nkid00.teaminess.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record Location(BlockPos position, Identifier dimension) {
    public boolean isValid() {
        return position != null || dimension != null;
    }
}