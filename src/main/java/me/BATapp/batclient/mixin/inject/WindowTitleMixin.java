package me.BATapp.batclient.mixin.inject;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowTitleMixin {
    
    private static boolean isChangingTitle = false;
    
    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true)
    public void changeTitle(String title, CallbackInfo ci) {
        // Prevent infinite recursion
        if (isChangingTitle) {
            return;
        }
        
        // Change Minecraft title to BATclient
        if (title.contains("Minecraft")) {
            isChangingTitle = true;
            ((Window) (Object) this).setTitle("BATclient 1.3.7-a (1.21.4)");
            isChangingTitle = false;
            ci.cancel();
        }
    }
}
