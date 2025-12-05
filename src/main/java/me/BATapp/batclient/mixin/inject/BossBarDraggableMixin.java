package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.utils.DraggableElementPositions;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarDraggableMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderBossBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // BossBar position is now managed via DraggableElementPositions
    }
}
