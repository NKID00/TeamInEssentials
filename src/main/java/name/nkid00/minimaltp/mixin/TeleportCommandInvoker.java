package name.nkid00.minimaltp.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;

@Mixin(TeleportCommand.class)
public interface TeleportCommandInvoker {
    @Invoker("execute")
    public static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
        throw new AssertionError();
    }
}