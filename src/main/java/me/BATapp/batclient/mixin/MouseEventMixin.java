package me.BATapp.batclient.mixin;

import me.BATapp.batclient.utils.UIInteractionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin pro zachycení mouse events pro UI drag/resize
 */
@Mixin(Mouse.class)
public abstract class MouseEventMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private static void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return;
        
        double mouseX = client.mouse.getX();
        double mouseY = client.mouse.getY();
        
        if (action == 1) { // GLFW_PRESS
            UIInteractionHandler.onMouseDown((int)mouseX, (int)mouseY, button);
        } else if (action == 0) { // GLFW_RELEASE
            UIInteractionHandler.onMouseUp((int)mouseX, (int)mouseY, button);
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private static void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return;
        
        UIInteractionHandler.onMouseMove((int)x, (int)y);
    }
}
