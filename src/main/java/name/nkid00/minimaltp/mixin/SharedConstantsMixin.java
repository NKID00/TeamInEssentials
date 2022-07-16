package name.nkid00.minimaltp.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.logging.LogUtils;

import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "isValidChar(C)Z", at = @At("HEAD"), cancellable = true)
    private static void isValidChar (char chr, CallbackInfoReturnable<Boolean> info) {
        if (chr == '\u00a7') {
            LOGGER.info("THIS IS A VALID CHAR!!");
            info.setReturnValue(true);
        }
    } 
}
