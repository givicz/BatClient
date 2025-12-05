package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.utils.ScreenAnimationManager;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public class InventoryFadeInMixin {
    
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onScreenInit(CallbackInfo ci) {
        // Start fade in animation when inventory opens
        ScreenAnimationManager.onScreenOpen(GenericContainerScreen.class);
    }
}
