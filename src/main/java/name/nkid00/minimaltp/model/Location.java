package name.nkid00.minimaltp.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record Location(BlockPos position, Identifier dimension) {
}