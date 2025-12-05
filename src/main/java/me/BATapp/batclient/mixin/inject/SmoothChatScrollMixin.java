package me.BATapp.batclient.mixin.inject;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class SmoothChatScrollMixin {
    
    @Shadow
    private int scrolledLines;
    
    private double smoothScrollTarget = 0;
    private double smoothScrollCurrent = 0;
    private static final double SMOOTHNESS = 0.15;
    
    @Inject(method = "scroll", at = @At("HEAD"), cancellable = true)
    public void onScroll(int amount, CallbackInfo ci) {
        // Smooth scroll instead of instant
        smoothScrollTarget += amount;
        
        // Clamp the target
        if (smoothScrollTarget < 0) {
            smoothScrollTarget = 0;
        }
        if (smoothScrollTarget > 100) {
            smoothScrollTarget = 100;
        }
        
        ci.cancel();
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    public void updateSmoothScroll(CallbackInfo ci) {
        // Update smooth scroll each frame (before rendering)
        if (Math.abs(smoothScrollTarget - smoothScrollCurrent) > 0.1) {
            smoothScrollCurrent += (smoothScrollTarget - smoothScrollCurrent) * SMOOTHNESS;
            
            // Update actual scrolled lines
            scrolledLines = (int) Math.round(smoothScrollCurrent);
        }
    }
}
