package name.nkid00.minimaltp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.SharedConstants;

import name.nkid00.minimaltp.MinimalTp;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Inject(method = "isValidChar(C)Z", at = @At("HEAD"), cancellable = true)
    private static void isValidChar(char chr, CallbackInfoReturnable<Boolean> info) {
        if (MinimalTp.options.allowFormattingCode && chr == '\u00a7') {
            info.setReturnValue(true);
        }
    }
}
