package me.BATapp.batclient.mixin;

import me.BATapp.batclient.modules.FullBright;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Inject(method = "pack(II)I", at = @At("HEAD"), cancellable = true)
    private static void onPack(int blockLight, int skyLight, CallbackInfoReturnable<Integer> cir) {
        try {
            if (FullBright.enabled.getValue()) {
                // packed format: (sky << 20) | (block << 4)
                int packed = (15 << 20) | (15 << 4);
                cir.setReturnValue(packed);
            }
        } catch (Throwable ignored) {
        }
    }
}
