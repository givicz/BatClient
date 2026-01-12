package me.BATapp.batclient.mixin.access;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("floatingItemTimeLeft")
    int getFloatingItemTimeLeft();

    @Accessor("postProcessor")
    net.minecraft.client.gl.PostEffectProcessor getPostProcessor();
}
