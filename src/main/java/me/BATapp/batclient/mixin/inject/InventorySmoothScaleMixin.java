package me.BATapp.batclient.mixin.inject;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class InventorySmoothScaleMixin {
    // Access width/height via cast to target class to avoid @Shadow warnings

    @Unique
    private float inventoryScaleAnimation = 0.8f;

    @Unique
    private float inventoryAlphaAnimation = 0f;

    @Unique
    private static final float SCALE_ANIMATION_SPEED = 0.08f;

    @Unique
    private static final float ALPHA_ANIMATION_SPEED = 0.08f;

    @Unique
    private int lastInventoryWidth = 0;

    @Unique
    private int lastInventoryHeight = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void onInventoryRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Update smooth scale and alpha animation towards 1.0
        inventoryScaleAnimation += (1.0f - inventoryScaleAnimation) * SCALE_ANIMATION_SPEED;
        inventoryAlphaAnimation += (1.0f - inventoryAlphaAnimation) * ALPHA_ANIMATION_SPEED;

        // Apply transform and alpha for opening animation
        context.getMatrices().push();
        int centerX = ((HandledScreen)(Object)this).width / 2;
        int centerY = ((HandledScreen)(Object)this).height / 2;
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(inventoryScaleAnimation, inventoryScaleAnimation, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, inventoryAlphaAnimation);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInventoryInit(CallbackInfo ci) {
        // Reset animation on inventory open
        inventoryScaleAnimation = 0.8f;
        inventoryAlphaAnimation = 0f;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onInventoryRenderTail(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Restore matrices and shader color
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        context.getMatrices().pop();
    }

    public float getInventoryScaleAnimation() {
        return inventoryScaleAnimation;
    }

    public void setInventoryScaleAnimation(float scale) {
        this.inventoryScaleAnimation = scale;
    }
}
