package me.BATapp.batclient.animation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.BATapp.batclient.render.Render2D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Lightweight placing animation manager.
 * Stores active placing animations per-block and renders a smooth translucent overlay.
 */
public final class PlacingAnimationManager {

    private static final Map<BlockPos, PlacingAnimation> ACTIVE_ANIMATIONS = Maps.newConcurrentMap();
    private static final Set<BlockPos> HIDDEN_BLOCKS = Sets.newCopyOnWriteArraySet();

    public static void addAnimation(net.minecraft.client.world.ClientWorld world, BlockState state, BlockPos pos, net.minecraft.entity.player.PlayerEntity placer, net.minecraft.util.Hand hand) {
        if (pos == null || world == null) return;
        PlacingAnimation animation = new PlacingAnimation(pos, state, placer);
        PlacingAnimation old = ACTIVE_ANIMATIONS.put(pos, animation);
        if (old != null) {
            // carry over progress to keep smooth
            animation.progress = Math.max(animation.progress, old.progress);
        }
        hideBlock(pos);
    }

    public static void hideBlock(BlockPos pos) {
        if (pos == null) return;
        HIDDEN_BLOCKS.add(pos);
    }

    public static void showBlock(BlockPos pos, boolean removeAnimation) {
        if (pos == null) return;
        if (HIDDEN_BLOCKS.remove(pos)) markBlockForRender(pos);
        if (removeAnimation) {
            ACTIVE_ANIMATIONS.remove(pos);
        }
    }

    public static boolean isHidden(BlockPos pos) {
        return pos != null && HIDDEN_BLOCKS.contains(pos);
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
        Iterator<BlockPos> it = HIDDEN_BLOCKS.iterator();
        while (it.hasNext()) {
            BlockPos p = it.next();
            showBlock(p, true);
        }
        HIDDEN_BLOCKS.clear();
        ACTIVE_ANIMATIONS.clear();
    }

    public static void tick() {
        Iterator<Map.Entry<BlockPos, PlacingAnimation>> it = ACTIVE_ANIMATIONS.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (it.hasNext()) {
            Map.Entry<BlockPos, PlacingAnimation> e = it.next();
            PlacingAnimation a = e.getValue();
            a.update(now);
            if (a.isFinished()) {
                it.remove();
                HIDDEN_BLOCKS.remove(e.getKey());
                markBlockForRender(e.getKey());
            }
        }
    }

    public static void render(WorldRenderContext context) {
        if (ACTIVE_ANIMATIONS.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        double camX = context.camera().getPos().x;
        double camY = context.camera().getPos().y;
        double camZ = context.camera().getPos().z;

        MatrixStack ms = context.matrixStack();
        for (PlacingAnimation a : ACTIVE_ANIMATIONS.values()) {
            double x = a.pos.getX() - camX + 0.5;
            double y = a.pos.getY() - camY + 0.5;
            double z = a.pos.getZ() - camZ + 0.5;

            ms.push();
            ms.translate(x, y, z);
            // apply slide/rotation/scale similar to FBP placing animation
            float p = a.getProgress();
            float slide = (1.0f - p) * a.slidePower; // slide from offset to zero
            // translate slightly along player's forward (use angleY)
            float dx = (float) (Math.sin(a.angleY) * slide);
            float dz = (float) (Math.cos(a.angleY) * slide);
            ms.translate(dx, -0.25f * (1.0f - p), dz);
            // rotation around Y
            ms.multiplyPositionMatrix(new org.joml.Matrix4f().rotationY(-a.angleY * (1.0f - p)));
            float s = a.startScale + (1.0f - a.startScale) * p;
            ms.scale(s, s, s);

            Render2D.setupRender();
            // draw translucent colored cube overlay with eased alpha
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 1, 1, 0);
            java.awt.Color c = new java.awt.Color(0x00d4ff);
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            int aByte = (int) (180 * (1.0f - (1.0f - p) * 0.6f));

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            var m = ms.peek();
            float half = 0.5f;
            buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, aByte);
            buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, aByte);

            BufferRenderer.drawWithGlobalProgram(buffer.end());

            Render2D.endRender();
            ms.pop();
        }
    }

    private static class PlacingAnimation {
        final BlockPos pos;
        final BlockState state;
        final long startMs;
        float progress = 0f;
        final long durationMs = 600L; // smooth place animation duration

        // slide / rotation fields
        final float angleY;
        final float slidePower;
        final float startScale;
        float currentProgress = 0f;

        PlacingAnimation(BlockPos pos, BlockState state, net.minecraft.entity.player.PlayerEntity placer) {
            this.pos = pos;
            this.state = state;
            this.startMs = System.currentTimeMillis();
            // derive simple angle from player look so animation direction feels natural
            float yaw = placer == null ? 0f : placer.getYaw();
            this.angleY = (float) Math.toRadians(yaw);
            this.slidePower = 0.4f + (float) (Math.random() * 0.4);
            this.startScale = 0.8f;
        }

        void update(long now) {
            this.progress = Math.min(1f, (now - startMs) / (float) durationMs);
            // smooth progress easing
            this.currentProgress = exponent(-0.7f, this.progress);
        }

        float getProgress() { return currentProgress; }

        boolean isFinished() { return progress >= 1f; }

        private float exponent(float curve, float time) {
            double base = curve > 0.0F ? -Math.log((double)curve) : Math.log((double)(-curve)) - 1.0D;
            return (float)(base * Math.pow(1.0D / base + 1.0D, (double)time) - base);
        }
    }
}

