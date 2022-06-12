package name.nkid00.minimaltp.mixin;

import name.nkid00.minimaltp.TpCommandModified;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @Inject(method = "register(Lcom/mojang/brigadier/CommandDispatcher;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void register(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci, LiteralCommandNode<ServerCommandSource> literalCommandNode) {
        dispatcher.register(
            literal("tp")
            .then( // both operators and non-operators
                argument("destination", EntityArgumentType.player())
                .executes(TpCommandModified::execute)
            )
            // operators
            .requires(source -> source.hasPermissionLevel(2))
            .redirect(literalCommandNode)
        );
        ci.cancel();
    }

    @Invoker("execute")
    public static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
        throw new AssertionError();
    }
}
