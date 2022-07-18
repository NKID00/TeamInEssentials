package name.nkid00.minimaltp.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @Shadow
    public static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination)
            throws CommandSyntaxException {
        throw new AssertionError();
    }
}
