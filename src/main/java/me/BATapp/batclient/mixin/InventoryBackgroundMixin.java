package me.BATapp.batclient.mixin;

import me.BATapp.batclient.config.BATclient_Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin pro odstranění tmavého background z Inventáře
 */
@Mixin(Screen.class)
public class InventoryBackgroundMixin {

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void onRenderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        
        // Odstranit background pro standardní inventory
        if (screen.getClass().getSimpleName().contains("GenericContainerScreen") || 
            screen.getClass().getSimpleName().contains("InventoryScreen") ||
            screen.getClass().getSimpleName().contains("CreativeInventoryScreen")) {
            ci.cancel();
        }
    }
}
