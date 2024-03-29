package name.nkid00.teaminess.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.nkid00.teaminess.Teaminess;

import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Inject(method = "isValidChar(C)Z", at = @At("HEAD"), cancellable = true)
    private static void isValidChar(char chr, CallbackInfoReturnable<Boolean> info) {
        if (Teaminess.options.allowFormattingCode && chr == '\u00a7') {
            info.setReturnValue(true);
        }
    }
}
