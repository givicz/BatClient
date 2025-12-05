package me.BATapp.batclient.modules;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.render.Render2D;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import me.BATapp.batclient.settings.impl.ColorSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.SoupModule;

import java.awt.*;

public class BreakingAnimation extends SoupModule {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final BooleanSetting enabled = new BooleanSetting("Breaking Effect", "Enable custom breaking animation", true);
    public static final ColorSetting color = new ColorSetting("Break Color", "Color of the breaking cube", 0xFF00d4ff);
    public static final SliderSetting duration = new SliderSetting("Duration", "Grow duration (ms)", 600, 100, 2000, 50);
    public static final BooleanSetting motionBlurEnabled = new BooleanSetting("Motion Blur", "Enable motion blur shells", true);
    public static final SliderSetting motionBlurStrength = new SliderSetting("Blur Strength", "How many blur shells (0-8)", 3, 0, 8, 1);

    public BreakingAnimation() {
        super("Breaking Animation", Category.WORLD);
    }

    private static BlockPos targetPos = null;
    private static long startTime = 0L;
    private static boolean active = false;
    private static boolean fading = false;
    private static long fadeStart = 0L;
    private static float expectedDurationMs = -1f;
    // authoritative network-driven progress
    private static boolean networkActive = false;
    private static int networkStage = -1;
    private static long networkLastUpdate = 0L;
    private static int prevNetworkStage = -1;
    private static long prevNetworkUpdateTime = 0L;
    private static float perStageMs = 150f; // measured ms per stage (server-driven)
    private static final float DEFAULT_PER_STAGE_MS = 150f;
    // smooth state
    private static float currentScale = 0f;
    private static float currentAlpha = 0f;
    private static float targetScale = 0f;
    private static float targetAlpha = 0f;
    // pending fade to avoid immediate stop when mouse is clicked briefly
    private static boolean lastAttackPressed = false;
    private static long attackReleasedTime = 0L;
    private static final long PENDING_FADE_DELAY_MS = 250L;

    public static void onTick() {
        if (!enabled.getValue()) return;
        if (mc.player == null) return;
        // Prefer authoritative network updates when available
        if (networkActive) {
            // if network reports active pos, ensure target matches
            if (targetPos == null || !targetPos.equals(targetPos)) {
                // no-op; targetPos will be set by the network handler
            }
            // keep active while networkActive
            active = true;
            fading = false;
        } else {
            // fallback to local attack key heuristic
            boolean attack = mc.options.attackKey.isPressed();
            if (attack && mc.crosshairTarget instanceof BlockHitResult bhr) {
                BlockPos pos = bhr.getBlockPos();
                if (!active || targetPos == null || !targetPos.equals(pos)) {
                    targetPos = pos;
                    active = true;
                    fading = false;
                    startTime = System.currentTimeMillis();
                    expectedDurationMs = -1f; // reset expected duration so we recalc with current tool
                }
            } else {
                if (active && !fading) {
                    // start fade
                    fading = true;
                    fadeStart = System.currentTimeMillis();
                }
            }
        }
        // Update interpolation targets
        long now = System.currentTimeMillis();
        boolean attackNow = mc.options.attackKey.isPressed();
        // handle pending fade (avoid instant fade on tiny click)
        if (attackNow) {
            attackReleasedTime = 0L;
        } else {
            if (lastAttackPressed && attackReleasedTime == 0L) {
                attackReleasedTime = now;
            }
        }
        lastAttackPressed = attackNow;
        if (targetPos == null) {
            targetScale = 0f;
            targetAlpha = 0f;
        } else if (networkActive) {
            // authoritative mapping from network stage + intra-stage progress using measured per-stage time
            float stageMs = perStageMs > 0f ? perStageMs : DEFAULT_PER_STAGE_MS;
            float intra = MathHelper.clamp((now - networkLastUpdate) / stageMs, 0f, 1f);
            float totalProg = MathHelper.clamp((networkStage + intra) / 9f, 0f, 1f);
            float baseScale = 0.1f;
            targetScale = baseScale + totalProg * (1f - baseScale);
            targetAlpha = 0.9f * totalProg;
            // if network hasn't updated for some time, start fading
            if (now - networkLastUpdate > Math.max(1000L, (long)(perStageMs * 4f)) && !fading) {
                fading = true;
                fadeStart = now;
            }
        } else if (active) {
            float growMs = expectedDurationMs > 0f ? expectedDurationMs : duration.getValue();
            float prog = MathHelper.clamp((now - startTime) / growMs, 0f, 1f);
            float baseScale = 0.1f;
            targetScale = baseScale + prog * (1f - baseScale);
            targetAlpha = 0.9f * prog;
        } else if (fading) {
            targetScale = 0f;
            targetAlpha = 0f;
        }
        // If attack was released long enough, trigger fading
        if (attackReleasedTime != 0L && !fading) {
            if (networkActive) {
                // when using network authority, wait a short moment for network updates
                if (now - attackReleasedTime >= PENDING_FADE_DELAY_MS) {
                    fading = true;
                    fadeStart = now;
                }
            } else {
                // no network authority -> if player stopped attacking and crosshair not on same block, fade immediately
                boolean crossOnSame = false;
                if (mc.crosshairTarget instanceof BlockHitResult bhr && targetPos != null) {
                    crossOnSame = bhr.getBlockPos().equals(targetPos);
                }
                if (!crossOnSame) {
                    fading = true;
                    fadeStart = now;
                } else if (now - attackReleasedTime >= PENDING_FADE_DELAY_MS) {
                    fading = true;
                    fadeStart = now;
                }
            }
        }
    }

    /**
     * Called from network mixin when server sends breaking progress updates.
     * @param pos block position
     * @param stage integer stage (usually 0..9), negative to indicate stop/clear
     */
    public static void onNetworkProgress(BlockPos pos, int stage) {
        // backward-compatible: unknown breaker id
        onNetworkProgress(pos, stage, -1);
    }

    public static void onNetworkProgress(BlockPos pos, int stage, int breakerEntityId) {
        // Only react to progress for the local player when breakerEntityId is provided
        if (breakerEntityId >= 0) {
            try {
                if (mc.player == null || mc.player.getId() != breakerEntityId) {
                    // not our breaking action
                    return;
                }
            } catch (Throwable ignored) {}
        }

        if (stage < 0) {
            // server cleared the cracking
            networkActive = false;
            networkStage = -1;
            networkLastUpdate = System.currentTimeMillis();
            if (!fading) {
                fading = true;
                fadeStart = networkLastUpdate;
            }
            return;
        }
        networkActive = true;
        networkStage = stage;
        networkLastUpdate = System.currentTimeMillis();
        if (targetPos == null || !targetPos.equals(pos)) {
            targetPos = pos;
            startTime = networkLastUpdate;
            expectedDurationMs = -1f;
        }
        active = true;
        fading = false;
            // compute immediate targets based on stage (map 0..9 -> 0..1)
            float prog = MathHelper.clamp((float) stage / 9f, 0f, 1f);
            float baseScale = 0.1f;
            targetScale = baseScale + prog * (1f - baseScale);
            targetAlpha = 0.9f * prog;
            // Update per-stage timing estimate when stage increased
            long now = System.currentTimeMillis();
            if (prevNetworkStage >= 0 && stage > prevNetworkStage) {
                long measured = (int) Math.max(1, now - prevNetworkUpdateTime);
                // smooth average to avoid jitter
                perStageMs = (perStageMs * 0.6f) + (measured * 0.4f);
            }
            prevNetworkStage = stage;
            prevNetworkUpdateTime = now;
    }

    public static void render(WorldRenderContext context) {
        if (!enabled.getValue()) return;
        if (mc.player == null) return;
        if (targetPos == null) return;

        long now = System.currentTimeMillis();
        // Compute effective duration influenced by tool speed and block hardness when starting
        if (active && expectedDurationMs < 0f) {
            expectedDurationMs = duration.getValue();
            try {
                if (mc.world != null && targetPos != null) {
                    BlockState bs = mc.world.getBlockState(targetPos);
                    // Try to get mining speed from held tool
                    ItemStack hand = mc.player.getMainHandStack();
                    float toolSpeed = hand.getMiningSpeedMultiplier(bs);
                    // If toolSpeed is > 1, reduce duration accordingly
                    if (toolSpeed > 1f) expectedDurationMs = expectedDurationMs / toolSpeed;
                }
            } catch (Throwable ignored) {
            }
        }

        float growMs = expectedDurationMs > 0f ? expectedDurationMs : duration.getValue();
        // compute fade progress (for lifecycle end)
        float fade = 0f;
        float fadeMs = 600f; // slower fade/shrink
        if (fading) {
            fade = MathHelper.clamp((now - fadeStart) / fadeMs, 0f, 1f);
        }

        // If not active and not fading and nothing to draw, ensure state cleared
        if (!active && !fading && currentScale <= 0f && currentAlpha <= 0f) return;

        // Smoothly interpolate currentScale/currentAlpha towards targets
        // Use different lerp speeds when authoritative network data is used to keep animation slow and smooth
        float lerpGrow = 0.06f;
        float lerpShrink = 0.01f;
        if (networkActive) {
            // slower, smoother progression following server crack texture
            lerpGrow = 0.03f;
            lerpShrink = 0.02f;
        }
        float lerpScale = (targetScale > currentScale) ? lerpGrow : lerpShrink;
        float lerpAlpha = (targetAlpha > currentAlpha) ? lerpGrow : lerpShrink;

        currentScale = MathHelper.lerp(lerpScale, currentScale, targetScale);
        currentAlpha = MathHelper.lerp(lerpAlpha, currentAlpha, targetAlpha);

        // If fading and we've nearly reached zero, clear state
        if (fading && currentScale < 0.005f && currentAlpha < 0.01f) {
            active = false;
            fading = false;
            targetPos = null;
            currentScale = 0f;
            currentAlpha = 0f;
            return;
        }

        float effectiveScale = currentScale;
        float alpha = currentAlpha;

        // Position relative to camera
        double camX = context.camera().getPos().x;
        double camY = context.camera().getPos().y;
        double camZ = context.camera().getPos().z;

        double x = targetPos.getX() - camX + 0.5;
        double y = targetPos.getY() - camY + 0.5;
        double z = targetPos.getZ() - camZ + 0.5;

        MatrixStack ms = context.matrixStack();
        ms.push();
        ms.translate(x, y, z);
        ms.scale(effectiveScale, effectiveScale, effectiveScale);

        Render2D.setupRender();
        // Disable depth test so the cube is visible through blocks
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        java.awt.Color c = new java.awt.Color(color.getValue(), true);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int a = (int) (alpha * 255);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        MatrixStack.Entry m = ms.peek();
        float half = 0.5f;
        // Front face (+Z)
        buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, a);
        // Back face (-Z)
        buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, a);
        // Left (-X)
        buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, a);
        // Right (+X)
        buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, a);
        // Top (+Y)
        buffer.vertex(m.getPositionMatrix(), -half, half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, half, -half).color(r, g, b, a);
        // Bottom (-Y)
        buffer.vertex(m.getPositionMatrix(), -half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, -half, -half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), half, -half, half).color(r, g, b, a);
        buffer.vertex(m.getPositionMatrix(), -half, -half, half).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        // Glow / motion blur shells: render expanding translucent shells when fading or enabled
        if (fading || motionBlurEnabled.getValue()) {
            int shells = 1;
            if (motionBlurEnabled.getValue()) shells += (int) Math.max(0, motionBlurStrength.getValue());
            if (fading) shells = Math.max(shells, 3);
            for (int i = 1; i <= shells; i++) {
                float s = 1f + i * (fade * 0.25f + 0.06f * i);
                ms.push();
                ms.scale(s, s, s);
                // Use additive blending for glow shells
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
                BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
                MatrixStack.Entry ent = ms.peek();
                int aa = (int) (255 * (1f - fade) * (0.35f / i));
                drawWireframeCube(bb, ent.getPositionMatrix(), half * (1f + i * 0.02f), r, g, b, aa);
                BufferRenderer.drawWithGlobalProgram(bb.end());
                // restore default blending
                RenderSystem.defaultBlendFunc();
                ms.pop();
            }
        }

        Render2D.endRender();
        ms.pop();
    }

    private static void drawWireframeCube(BufferBuilder buffer, org.joml.Matrix4f matrix, float half, int r, int g, int b, int a) {
        // 12 edges
        buffer.vertex(matrix, -half, -half, -half).color(r, g, b, a);
        buffer.vertex(matrix, half, -half, -half).color(r, g, b, a);

        buffer.vertex(matrix, half, -half, -half).color(r, g, b, a);
        buffer.vertex(matrix, half, -half, half).color(r, g, b, a);

        buffer.vertex(matrix, half, -half, half).color(r, g, b, a);
        buffer.vertex(matrix, -half, -half, half).color(r, g, b, a);

        buffer.vertex(matrix, -half, -half, half).color(r, g, b, a);
        buffer.vertex(matrix, -half, -half, -half).color(r, g, b, a);

        buffer.vertex(matrix, -half, half, -half).color(r, g, b, a);
        buffer.vertex(matrix, half, half, -half).color(r, g, b, a);

        buffer.vertex(matrix, half, half, -half).color(r, g, b, a);
        buffer.vertex(matrix, half, half, half).color(r, g, b, a);

        buffer.vertex(matrix, half, half, half).color(r, g, b, a);
        buffer.vertex(matrix, -half, half, half).color(r, g, b, a);

        buffer.vertex(matrix, -half, half, half).color(r, g, b, a);
        buffer.vertex(matrix, -half, half, -half).color(r, g, b, a);

        // verticals
        buffer.vertex(matrix, -half, -half, -half).color(r, g, b, a);
        buffer.vertex(matrix, -half, half, -half).color(r, g, b, a);

        buffer.vertex(matrix, half, -half, -half).color(r, g, b, a);
        buffer.vertex(matrix, half, half, -half).color(r, g, b, a);

        buffer.vertex(matrix, half, -half, half).color(r, g, b, a);
        buffer.vertex(matrix, half, half, half).color(r, g, b, a);

        buffer.vertex(matrix, -half, -half, half).color(r, g, b, a);
        buffer.vertex(matrix, -half, half, half).color(r, g, b, a);
    }
}
