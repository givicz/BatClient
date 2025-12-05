package me.BATapp.batclient.modules;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.utils.ConfigUtils;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.TexturesManager;
import me.BATapp.batclient.utils.PerformanceUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.BATapp.batclient.settings.impl.ColorSetting;
import me.BATapp.batclient.settings.impl.EnumSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;

public class JumpCircles extends SoupModule {

    public static final me.BATapp.batclient.settings.impl.BooleanSetting enabled = new me.BATapp.batclient.settings.impl.BooleanSetting("Enabled", "Enable jump circles", true);
    public static final ColorSetting color = new ColorSetting("Color", "Circle color", 0xFF00d4ff);
    public static final SliderSetting alpha = new SliderSetting("Alpha", "Alpha (0-100)", 100, 0, 100, 1);
    public static final SliderSetting liveTime = new SliderSetting("Live Time", "Lifetime (s)", 3, 1, 10, 1);
    public static final SliderSetting scaleSetting = new SliderSetting("Scale", "Scale %", 100, 10, 300, 1);
    public static final SliderSetting spinSpeed = new SliderSetting("Spin Speed", "Spin speed (deg)", 90, 0, 360, 1);
    public static final me.BATapp.batclient.settings.impl.BooleanSetting fadeOut = new me.BATapp.batclient.settings.impl.BooleanSetting("Fade Out", "Fade out when expiring", true);
    public static final EnumSetting<JumCircleStyle> style = new EnumSetting<>("Style", "Style", JumCircleStyle.CIRCLE, JumCircleStyle.class);

    private static final List<JumpCircle> CIRCLES = new ArrayList<>();
    private static boolean wasJumping = false;

    public JumpCircles() {
        super("Jump Circles", Category.PARTICLES);
    }

    public static void onTick() {
        if (!enabled.getValue()) return;
        if (mc.player instanceof PlayerEntity player) {
            boolean isJumping = !player.isOnGround();

            if (isJumping && !wasJumping && mc.options.jumpKey.isPressed()) {
                CIRCLES.add(new JumpCircle(player.getPos()));
            }

            wasJumping = isJumping;
        }
    }

    public static void renderCircles(WorldRenderContext context) {
        if (!enabled.getValue()) {
            CIRCLES.clear();
            return;
        }

        double cameraX = context.camera().getPos().x;
        double cameraY = context.camera().getPos().y;
        double cameraZ = context.camera().getPos().z;

        CIRCLES.removeIf(JumpCircle::isExpired);
        for (JumpCircle circle : CIRCLES) {
            circle.render(context, cameraX, cameraY, cameraZ);
        }
    }

    private static class JumpCircle {
        private final Vec3d position;
        private final double offsetY;
        private final long startTime; // Время создания круга
        private float rotationAngle;
        private final float angularVelocity;
        private long lastUpdateTime; // Время последнего обновления
        private final boolean isFadeOut; // Флаг для эффекта затухания

        public JumpCircle(Vec3d position) {
            this.position = position;
            this.offsetY = mc.player.getVelocity().getY();
            this.rotationAngle = 0f;
                this.angularVelocity = (float) Math.toRadians(spinSpeed.getValue());
                this.startTime = System.currentTimeMillis(); // creation time
                this.lastUpdateTime = startTime; // initialize last update
                this.isFadeOut = fadeOut.getValue(); // use setting
        }

        public boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            float elapsedTime = (currentTime - startTime) / 1000f; // elapsed seconds
            return elapsedTime > liveTime.getValue();
        }

        public void render(WorldRenderContext context, double cameraX, double cameraY, double cameraZ) {
            double x = position.x - cameraX;
            double y = position.y - cameraY - offsetY;
            double z = position.z - cameraZ;

            updateRotation();
            renderGlowCircleBufferBuilder(context.matrixStack(), x, y, z);
        }

        private void updateRotation() {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastUpdateTime) / 1000f;
            lastUpdateTime = currentTime;

            deltaTime = Math.min(deltaTime, 0.1f);

            float totalLifetime = liveTime.getValue();
            float elapsedTime = (currentTime - startTime) / 1000f;
            float remainingFraction = MathHelper.clamp((totalLifetime - elapsedTime) / totalLifetime, 0, 1);

            // Вращение до начала исчезания
            if (remainingFraction > 0.3f) {
                float frameTime = 1.0f / 60.0f;
                float normalizedDelta = deltaTime / frameTime;
                // Use optimized trigonometry
                rotationAngle -= angularVelocity * normalizedDelta;
            } else {
                float fadeFactor = remainingFraction / 0.3f; // от 1 до 0
                float frameTime = 1.0f / 60.0f;
                float normalizedDelta = deltaTime / frameTime;
                rotationAngle += angularVelocity * normalizedDelta * fadeFactor;
            }
        }

        private void renderGlowCircleBufferBuilder(MatrixStack modelMatrix, double x, double y, double z) {
            int glowAlpha = ConfigUtils.intPercentToHexInt(Math.round(alpha.getValue()));
            float liveTime = JumpCircles.liveTime.getValue();

            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);

            RenderSystem.setShaderTexture(0, TexturesManager.getJumpCircle());
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;
            float ageFraction = elapsedTime / (liveTime / 3);
            ageFraction *= 3;
            float scaleMultiplier = scaleSetting.getValue() / 100f;
            ageFraction = MathHelper.clamp(ageFraction * scaleMultiplier, 0, scaleMultiplier);

            float remainingFraction = MathHelper.clamp((liveTime - elapsedTime) / liveTime, 0, 1);

            if (remainingFraction < 0.3f) {
                float shrinkFactor = remainingFraction / 0.3f;
                ageFraction *= shrinkFactor;
            }

            float interpolatedRadius = ageFraction;
            float colorAnim = isFadeOut ? 1f - remainingFraction : 1.0f;
            float scale = interpolatedRadius * 2f;

            Color base = new Color(color.getValue());
            Color color1 = base;
            Color color2 = base;
            Color color3 = base;
            Color color4 = base;

            modelMatrix.push();
            modelMatrix.translate(x, y, z);
            modelMatrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            // Use optimized sine for rotation calculation
            float cosRot = PerformanceUtils.fastCos(rotationAngle);
            float sinRot = PerformanceUtils.fastSin(rotationAngle);
            Matrix4f matrix = modelMatrix.peek().getPositionMatrix();

            // Draw quad with interpolated radius and proper texture mapping
            buffer.vertex(matrix, -interpolatedRadius, -interpolatedRadius + scale, 0)
                    .texture(0, 1)
                    .color(color1.getRed(), color1.getGreen(), color1.getBlue(), (int) (glowAlpha * colorAnim));
            buffer.vertex(matrix, -interpolatedRadius + scale, -interpolatedRadius + scale, 0)
                    .texture(1, 1)
                    .color(color2.getRed(), color2.getGreen(), color2.getBlue(), (int) (glowAlpha * colorAnim));
            buffer.vertex(matrix, -interpolatedRadius + scale, -interpolatedRadius, 0)
                    .texture(1, 0)
                    .color(color3.getRed(), color3.getGreen(), color3.getBlue(), (int) (glowAlpha * colorAnim));
            buffer.vertex(matrix, -interpolatedRadius, -interpolatedRadius, 0)
                    .texture(0, 0)
                    .color(color4.getRed(), color4.getGreen(), color4.getBlue(), (int) (glowAlpha * colorAnim));

            modelMatrix.pop();

            Render2D.endBuilding(buffer);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }
    }

    public enum JumCircleStyle {
        CIRCLE, CIRCLE_BOLD, HEXAGON, PORTAL, SOUP
    }
}
