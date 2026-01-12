package me.BATapp.batclient.animation;

import me.BATapp.batclient.render.Render2D;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Modern placing animation manager.
 * Renders the actual block model with a Scale + Fade animation.
 */
public final class PlacingAnimationManager {

    private static final Map<BlockPos, Animation> animations = new ConcurrentHashMap<>();
    private static final Set<BlockPos> hiddenBlocks = new CopyOnWriteArraySet<>();

    public static void addAnimation(net.minecraft.client.world.ClientWorld world, BlockState state, BlockPos pos, net.minecraft.entity.player.PlayerEntity placer, net.minecraft.util.Hand hand) {
        if (pos == null || world == null) return;
        animations.put(pos, new Animation(pos, state));
        hideBlock(pos);
    }

    public static void hideBlock(BlockPos pos) {
        if (pos == null) return;
        hiddenBlocks.add(pos);
    }

    public static void showBlock(BlockPos pos, boolean removeAnimation) {
        if (pos == null) return;
        if (hiddenBlocks.remove(pos)) markBlockForRender(pos);
        if (removeAnimation) {
            animations.remove(pos);
        }
    }

    public static boolean isHidden(BlockPos pos) {
        return pos != null && hiddenBlocks.contains(pos);
    }

    private static void markBlockForRender(BlockPos pos) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;
            BlockState state = mc.world.getBlockState(pos);
            try {
                java.lang.reflect.Method m = mc.world.getClass().getMethod("updateListeners", BlockPos.class, BlockState.class, BlockState.class, int.class);
                m.invoke(mc.world, pos, state, state, 2);
            } catch (NoSuchMethodException e) {
                // fallback: try obfuscated name
                try {
                    java.lang.reflect.Method m2 = mc.world.getClass().getMethod("method_8413", BlockPos.class, BlockState.class, BlockState.class, int.class);
                    m2.invoke(mc.world, pos, state, state, 2);
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {}
    }

    public static void clear() {
        Iterator<BlockPos> it = hiddenBlocks.iterator();
        while (it.hasNext()) {
            BlockPos p = it.next();
            showBlock(p, true);
        }
        hiddenBlocks.clear();
        animations.clear();
    }

    public static void tick() {
        if (animations.isEmpty()) return;
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<BlockPos, Animation>> it = animations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Animation> e = it.next();
            Animation a = e.getValue();
            if (a.isFinished(now)) {
                it.remove();
                hiddenBlocks.remove(e.getKey());
                markBlockForRender(e.getKey());
            }
        }
    }

    public static void render(WorldRenderContext context) {
        if (animations.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        Vec3d camPos = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorTextureLightNormalProgram);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        long now = System.currentTimeMillis();

        for (Animation anim : animations.values()) {
            matrices.push();
            matrices.translate(anim.pos.getX() - camPos.x, anim.pos.getY() - camPos.y, anim.pos.getZ() - camPos.z);

            float progress = anim.getProgress(now);
            
            // Animation: Scale from center
            matrices.translate(0.5, 0.5, 0.5);
            // Elastic-like scale or Smooth Step
            float scale = easeOutBack(progress);
            matrices.scale(scale, scale, scale);
            matrices.translate(-0.5, -0.5, -0.5);

            // Fade in
            RenderSystem.setShaderColor(1f, 1f, 1f, Math.min(1f, progress * 1.5f)); // Fade in slightly faster than scale

            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            
            mc.getBlockRenderManager().renderBlock(
                    anim.state,
                    anim.pos,
                    mc.world,
                    matrices,
                    buffer,
                    false,
                    Random.create()
            );

            try {
                BufferRenderer.drawWithGlobalProgram(buffer.end());
            } catch (Exception ignored) {}
            
            matrices.pop();
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float)Math.pow(x - 1, 3) + c1 * (float)Math.pow(x - 1, 2);
    }
    
    private static class Animation {
        final BlockPos pos;
        final BlockState state;
        final long startTime;
        final long duration = 250L; // 250ms fast animation

        Animation(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
            this.startTime = System.currentTimeMillis();
        }

        float getProgress(long now) {
            return Math.min(1f, (now - startTime) / (float) duration);
        }

        boolean isFinished(long now) {
            return (now - startTime) >= duration;
        }
    }
}

