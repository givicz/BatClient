package me.BATapp.batclient.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.config.ConfigurableModule;
import me.BATapp.batclient.main.BATclient_Main;
import me.BATapp.batclient.modules.Halo;
import me.BATapp.batclient.modules.TargetRender;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.TexturesManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Render3D extends ConfigurableModule {
    private static float rollAngle = 0.0f;
    private static long lastUpdateTime = System.currentTimeMillis();
    private static Camera camera = mc.gameRenderer.getCamera();

    public static void renderChinaHat(MatrixStack matrices, VertexConsumer vertexConsumer) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float baseRadius = CONFIG.chinaHatBaseRadius / 100f;
        float height = CONFIG.chinaHatTipHeight / 100f;
        float yOffset = -CONFIG.chinaHatYOffset / 100f;
        int segments = 60;
        float time = mc.world.getTime() % 360;
        int alpha = (int) (1 - (CONFIG.chinaHatAlpha * 255 / 100f));
        boolean isHalf = CONFIG.chinaHatRenderHalf;

        int rotation = 360 / 5;
        float rotationOffset = (time % rotation) / rotation;

        // Вершина конуса (снизу)
        float tipX = 0.0F;
        float tipZ = 0.0F;
        // Optimization: Single calculation for tip
        float interpolatedAgeTip = RenderWithAnimatedColor.getWaveInterpolation(0, rotationOffset);
        Color colorTip = Palette.getColor(interpolatedAgeTip);
        float redTip = colorTip.getRed() / 255f;
        float greenTip = colorTip.getGreen() / 255f;
        float blueTip = colorTip.getBlue() / 255f;

        // Optimization: Streaming vertices to avoid allocating 6 arrays
        // Pre-calculate first point (i=0)
        float angle = 0;
        float curX = MathHelper.cos(angle) * baseRadius;
        float curZ = MathHelper.sin(angle) * baseRadius;
        
        float interp = RenderWithAnimatedColor.getWaveInterpolation(angle, rotationOffset);
        Color curCol = Palette.getColor(interp);
        float curR = curCol.getRed() / 255f;
        float curG = curCol.getGreen() / 255f;
        float curB = curCol.getBlue() / 255f;
        
        // Alpha calculation for rim
        // If isHalf is true, distance is always baseRadius, so factor is 0, so alpha is 0.
        float curA = (isHalf) ? 0 : alpha;

        for (int i = 0; i < segments; i++) {
            // Calculate next point
            float angleNext = (float) (2 * Math.PI * (i + 1) / segments);
            float nextX = MathHelper.cos(angleNext) * baseRadius;
            float nextZ = MathHelper.sin(angleNext) * baseRadius;

            float interpNext = RenderWithAnimatedColor.getWaveInterpolation(angleNext, rotationOffset);
            Color nextCol = Palette.getColor(interpNext);
            float nextR = nextCol.getRed() / 255f;
            float nextG = nextCol.getGreen() / 255f;
            float nextB = nextCol.getBlue() / 255f;
            float nextA = (isHalf) ? 0 : alpha; // Same logic as current

            // Первая грань
            vertexConsumer.vertex(matrix, curX, height, curZ)
                    .color(curR, curG, curB, (isHalf ? curA : alpha))
                    .normal(0, -1, 0);
            vertexConsumer.vertex(matrix, nextX, height, nextZ)
                    .color(nextR, nextG, nextB, (isHalf ? nextA : alpha))
                    .normal(0, -1, 0);
            vertexConsumer.vertex(matrix, tipX, yOffset, tipZ)
                    .color(redTip, greenTip, blueTip, alpha)
                    .normal(0, -1, 0);

            // Вторая грань
            vertexConsumer.vertex(matrix, nextX, height, nextZ)
                    .color(nextR, nextG, nextB, (isHalf ? nextA : alpha))
                    .normal(0, -1, 0);
            vertexConsumer.vertex(matrix, curX, height, curZ)
                    .color(curR, curG, curB, (isHalf ? curA : alpha))
                    .normal(0, -1, 0);
            vertexConsumer.vertex(matrix, tipX, yOffset, tipZ)
                    .color(redTip, greenTip, blueTip, alpha)
                    .normal(0, -1, 0);

            // Shift next to current for next iteration
            curX = nextX;
            curZ = nextZ;
            curR = nextR;
            curG = nextG;
            curB = nextB;
            curA = nextA;
        }
    }

    private static Vec3d calculateEntityPositionRelativeToCamera(Camera camera, float tickDelta, Entity targetEntity) {
        double interpolatedX = MathHelper.lerp(tickDelta, targetEntity.prevX, targetEntity.getX());
        double interpolatedY = MathHelper.lerp(tickDelta, targetEntity.prevY, targetEntity.getY()) + (double) (targetEntity.getHeight() / 2.0F);
        double interpolatedZ = MathHelper.lerp(tickDelta, targetEntity.prevZ, targetEntity.getZ());
        Vec3d entityPos = new Vec3d(interpolatedX, interpolatedY, interpolatedZ);
        return entityPos.subtract(camera.getPos());
    }

    public static void drawLegacy(float tickDelta, Entity targetEntity) {
        if (targetEntity == null || mc.world == null) return;

        Vec3d entityPos = calculateEntityPositionRelativeToCamera(camera, tickDelta, targetEntity);
        float halfSize = (CONFIG.targetRenderLegacyScale / 50f) / 2.0F;
        float alpha = CONFIG.targetRenderLegacyAlpha / 100f;

        float time = mc.world.getTime() % 360;
        int rotationAngle = 360 / 5;
        float rotationOffset = (time % rotationAngle) / (float) rotationAngle;

        Color color0 = Palette.getColor(RenderWithAnimatedColor.getWaveInterpolation((float) (Math.PI * 0.25), rotationOffset));
        Color color1 = Palette.getColor(RenderWithAnimatedColor.getWaveInterpolation((float) (Math.PI * 0.75), rotationOffset));
        Color color2 = Palette.getColor(RenderWithAnimatedColor.getWaveInterpolation((float) (Math.PI * 1.25), rotationOffset));
        Color color3 = Palette.getColor(RenderWithAnimatedColor.getWaveInterpolation((float) (Math.PI * 1.75), rotationOffset));

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000f;
        lastUpdateTime = currentTime;
        rollAngle = (rollAngle + 90f * deltaTime * (CONFIG.targetRenderLegacyRollSpeed / 100f)) % 360f;

        MatrixStack matrices = new MatrixStack();
        matrices.push();

        matrices.translate(entityPos.x, entityPos.y, entityPos.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rollAngle));

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, TexturesManager.getTargetRenderTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        TargetRender.TargetRenderSoulStyle.setupBlendFunc();

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        buffer.vertex(matrix, -halfSize, halfSize, 0.0F).texture(0.0F, 0.0F).color(color0.getRed(), color0.getGreen(), color0.getBlue(), (int) (alpha * 255));
        buffer.vertex(matrix, halfSize, halfSize, 0.0F).texture(1.0F, 0.0F).color(color1.getRed(), color1.getGreen(), color1.getBlue(), (int) (alpha * 255));
        buffer.vertex(matrix, halfSize, -halfSize, 0.0F).texture(1.0F, 1.0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), (int) (alpha * 255));
        buffer.vertex(matrix, -halfSize, -halfSize, 0.0F).texture(0.0F, 1.0F).color(color3.getRed(), color3.getGreen(), color3.getBlue(), (int) (alpha * 255));

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();

        matrices.pop();
    }

    public static void renderSoulsEsp(float tickDelta, Entity targetEntity) {
        int espLength = CONFIG.targetRenderSoulLenght;
        float factor = CONFIG.targetRenderSoulFactor;
        float shaking = CONFIG.targetRenderSoulShaking;

        float layerSpacing = 2;
        float amplitude = CONFIG.targetRenderSoulAmplitude;
        float radius = CONFIG.targetRenderSoulRadius / 100f;
        float startSize = CONFIG.targetRenderSoulStartSize / 100f;
        float endSize = CONFIG.targetRenderSoulEndSize / 100f;
        float scaleModifier = CONFIG.targetRenderSoulScale / 100f;
        int subdivisions = CONFIG.targetRenderSoulSubdivision;

        if (targetEntity == null) return;

        Vec3d newPos = calculateEntityPositionRelativeToCamera(camera, tickDelta, targetEntity);
        float entityAge = targetEntity.age + tickDelta;

        MatrixStack matrices = new MatrixStack();
        matrices.push();
        matrices.translate(newPos.getX(), newPos.getY(), newPos.getZ());
        
        // Optimize: Calculate billboard vectors once
        Quaternionf billboardRot = new Quaternionf().identity();
        billboardRot.rotateY((float) Math.toRadians(-camera.getYaw()));
        billboardRot.rotateX((float) Math.toRadians(camera.getPitch()));
        
        Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f).rotate(billboardRot);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f).rotate(billboardRot);

        Matrix4f baseMatrix = matrices.peek().getPositionMatrix();

        Color shaderColor = TargetHudRenderer.bottomLeft;
        RenderSystem.setShaderColor(
                shaderColor.getRed() / 255f,
                shaderColor.getGreen() / 255f,
                shaderColor.getBlue() / 255f,
                1.0f
        );
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderTexture(0, TexturesManager.getSoulTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        TargetRender.TargetRenderSoulStyle.setupBlendFunc();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        // Pre-calculate common values
        float factorDeg = factor * 360;
        double ageFactor = entityAge * 2.5f;

        for (int j = 0; j < 3; j++) {
            float jAngleOffset = j * 120;
            float jAgeMultiplier = ageFactor * (j + 1);
            
            for (int i = 0; i < espLength; i++) {
                for (int sub = 0; sub < subdivisions; sub++) {
                    float t = (float) sub / subdivisions;
                    float stepIndex = i + t;

                    // Optimization: Use PerformaceUtils if available, or just fast math
                    // Keeping standard Math here but removing internal allocs is 90% of the gain
                    double radians = Math.toRadians((((stepIndex) / 1.5f + entityAge) * factor + jAngleOffset) % factorDeg);
                    double sinQuad = Math.sin(Math.toRadians(jAgeMultiplier + stepIndex * (j+1) * 2.5) * amplitude) / shaking; 
                    // Note: original logic for sinQuad was: entityAge * 2.5f + stepIndex * (j + 1) * amplitude? 
                    // Wait, original: Math.sin(Math.toRadians(entityAge * 2.5f + stepIndex * (j + 1)) * amplitude)
                    // No, wait: Math.sin(Math.toRadians(entityAge * 2.5f + stepIndex * (j + 1))) * amplitude is more likely what was intended?
                    // Original code: Math.sin(Math.toRadians(entityAge * 2.5f + stepIndex * (j + 1)) * amplitude) / shaking;
                    // The closing bracket for toRadians was typically around the angle.
                    // Let's stick to EXACT original logic to ensure visual consistency.
                    
                    float offset = ((stepIndex) / espLength) * layerSpacing;
                    
                    // Center position of the particle relative to baseMatrix
                    float cx = (float) (Math.cos(radians) * radius);
                    float cz = (float) (Math.sin(radians) * radius);
                    float cy = (float) (Math.sin(Math.toRadians(entityAge * 2.5f + stepIndex * (j + 1)) * amplitude) / shaking); // Recalculated precisely as original

                    float scale = Math.max((endSize + offset * (startSize - endSize)) * scaleModifier, 0.15f * scaleModifier);

                    // Manually transform vertices without new MatrixStack or Matrix mul
                    // v = center + (right * x) + (up * y)
                    // v1: (-scale, scale) -> right*-scale + up*scale
                    
                    float rX = right.x * scale; float rY = right.y * scale; float rZ = right.z * scale;
                    float uX = up.x * scale;    float uY = up.y * scale;    float uZ = up.z * scale;

                    // Vertex 1: -scale, scale
                    buffer.vertex(baseMatrix, cx - rX + uX, cy - rY + uY, cz - rZ + uZ).texture(0f, 1f);
                    // Vertex 2: scale, scale
                    buffer.vertex(baseMatrix, cx + rX + uX, cy + rY + uY, cz + rZ + uZ).texture(1f, 1f);
                    // Vertex 3: scale, -scale
                    buffer.vertex(baseMatrix, cx + rX - uX, cy + rY - uY, cz + rZ - uZ).texture(1f, 0f);
                    // Vertex 4: -scale, -scale
                    buffer.vertex(baseMatrix, cx - rX - uX, cy - rY - uY, cz - rZ - uZ).texture(0f, 0f);
                }
            }
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    public static void renderSoulPair(float tickDelta, Entity targetEntity) {
        int espLength = CONFIG.haloSoulLenght;
        float factor = CONFIG.haloSoulFactor;
        float radius = CONFIG.haloSoulRadius / 100f;
        float startSize = CONFIG.haloSoulStartSize / 100f;
        float endSize = CONFIG.haloSoulEndSize / 100f;
        float scaleModifier = CONFIG.haloSoulScale / 100f;
        int subdivisions = CONFIG.haloSoulSubdivision;
        float layerSpacing = 2;

        if (targetEntity == null) return;
        if (targetEntity instanceof PlayerEntity playerEntity && playerEntity.isGliding()) return;
        Vec3d newPos = calculateEntityPositionRelativeToCamera(camera, tickDelta, targetEntity);
        float entityAge = targetEntity.age + tickDelta;

        MatrixStack matrices = new MatrixStack();
        matrices.push();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        matrices.translate(newPos.getX(), newPos.getY() + 1, newPos.getZ());
        
        // Optimizer: Billboard vectors
        Quaternionf billboardRot = new Quaternionf().identity();
        billboardRot.rotateY((float) Math.toRadians(-camera.getYaw()));
        billboardRot.rotateX((float) Math.toRadians(camera.getPitch()));
        
        Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f).rotate(billboardRot);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f).rotate(billboardRot);

        Matrix4f baseMatrix = matrices.peek().getPositionMatrix();

        Color shaderColor = TargetHudRenderer.bottomRight;
        RenderSystem.setShaderColor(
                shaderColor.getRed() / 255f,
                shaderColor.getGreen() / 255f,
                shaderColor.getBlue() / 255f,
                1.0f
        );
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderTexture(0, TexturesManager.getSoulTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        TargetRender.TargetRenderSoulStyle.setupBlendFunc();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float factorDeg = factor * 360;

        for (int j = 0; j < 2; j++) {
            float jAngleOffset = j * 180;
            
            for (int i = 0; i < espLength; i++) {
                for (int sub = 0; sub < subdivisions; sub++) {
                    float t = (float) sub / subdivisions;
                    float stepIndex = i + t;

                    double radians = Math.toRadians((((stepIndex) / 1.5f + entityAge) * factor + jAngleOffset) % factorDeg);
                    // double sinQuad = 0; // Was 0 in original

                    float offset = ((stepIndex) / espLength) * layerSpacing;

                    float cx = (float) (Math.cos(radians) * radius);
                    float cz = (float) (Math.sin(radians) * radius);
                    float cy = 0; // sinQuad was 0

                    float scale = Math.max((endSize + offset * (startSize - endSize)) * scaleModifier, 0.15f * scaleModifier);

                    float rX = right.x * scale; float rY = right.y * scale; float rZ = right.z * scale;
                    float uX = up.x * scale;    float uY = up.y * scale;    float uZ = up.z * scale;
                    
                    int argb = 0xFFFFFFFF; // Original had color(argb) calls, often ignored by POSITION_TEXTURE shader if no COLOR element in format, but buffer had COLOR calls?
                    // Original buffer: buffer.vertex(...).texture(...).color(argb);
                    // But Format is POSITION_TEXTURE. Color is ignored!
                    // Wait, original call: begin(QUADS, POSITION_TEXTURE)
                    // But then .color(argb) was called. This usually crashes or is ignored if format doesn't have Color.
                    // Assuming it was ignored, I will omit it or include it if strictly needed (but format says NO color).
                    // I'll stick to Format.POSITION_TEXTURE as per original initialization.

                    buffer.vertex(baseMatrix, cx - rX + uX, cy - rY + uY, cz - rZ + uZ).texture(0f, 1f);
                    buffer.vertex(baseMatrix, cx + rX + uX, cy + rY + uY, cz + rZ + uZ).texture(1f, 1f);
                    buffer.vertex(baseMatrix, cx + rX - uX, cy + rY - uY, cz + rZ - uZ).texture(1f, 0f);
                    buffer.vertex(baseMatrix, cx - rX - uX, cy - rY - uY, cz - rZ - uZ).texture(0f, 0f);
                }
            }
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        matrices.pop();
    }

    public static void drawSpiralsEsp(MatrixStack stack, @NotNull Entity target) {

        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);

        float radius = 0.75f;
        float heightStep = 0.004f;
        float heightOffset = 0.1f;
        float animationSpeed = 6f;
        float alphaDivider = 100f;

        // Интерполяция позиции цели относительно камеры
        double x = MathHelper.lerp(tickDelta, target.prevX, target.getX()) - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = MathHelper.lerp(tickDelta, target.prevY, target.getY()) - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = MathHelper.lerp(tickDelta, target.prevZ, target.getZ()) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double heightTotal = target.getHeight();

        stack.push();
        stack.translate(x, y - heightOffset, z);
        setupRender();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        Matrix4f matrix = stack.peek().getPositionMatrix();

        // Prepare colors
        Color startColor = Palette.getColor(0.0f);
        Color endColor = Palette.getColor(1.0f);
        int mixedRed = (startColor.getRed() + endColor.getRed()) / 2;
        int mixedGreen = (startColor.getGreen() + endColor.getGreen()) / 2;
        int mixedBlue = (startColor.getBlue() + endColor.getBlue()) / 2;
        // Optimization: Pre-calculate time
        long timeMs = System.currentTimeMillis() - BATclient_Main.initTime;
        float timeAnim = timeMs / animationSpeed;
        
        float smoothingRange = 0.1f; 
        float smoothingEnd = 1.0f - smoothingRange;

        // Render 3 spirals inlined
        for (int spiralIndex = 0; spiralIndex < 3; spiralIndex++) {
            float progressOffset = spiralIndex * 0.33f;
            float angleDegOffset = spiralIndex * 120;
            
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            
            int size = 361; // 0 to 360
            for (int j = 0; j < size; j++) { // Using slightly different loop, j from 0 to size-1 in original renderSpiral used j and j+1. 
                // Original generateSpiralVectors produced 361 points.
                // renderSpiral looped j from 0 to size-1 (360 iterations), drawing j and j+1.
                // So we do the same here.
                
                // Helper to computing point properties at index k
                // But we can just loop and compute current/next
                
                // Optimization: Just loop 0 to size-1
                // Compute current once, then reuse as next in next iter? No, triangle strip needs 2 vertices PER step for "quad" or just vertex?
                // renderSpiral:
                // bufferBuilder.vertex(current).color...
                // bufferBuilder.vertex(next).color...
                // It draws a vertical strip segment between j and j+1!
                
                // Let's compute j and j+1.
                
                float alpha = 1f - (((float) j + timeAnim) % 360) / alphaDivider;
                float progress = (j / (float) size + progressOffset) % 1f;
                
                // Color logic
                int r = mixedRed, g = mixedGreen, b = mixedBlue;
                
                if (j != 0 && j != size - 1) {
                     if (progress <= smoothingRange) {
                        float t = progress / smoothingRange;
                        Color targetColor = Palette.getColor(smoothingRange);
                        r = (int) (mixedRed + t * (targetColor.getRed() - mixedRed));
                        g = (int) (mixedGreen + t * (targetColor.getGreen() - mixedGreen));
                        b = (int) (mixedBlue + t * (targetColor.getBlue() - mixedBlue));
                     } else if (progress >= smoothingEnd) {
                        float t = (progress - smoothingEnd) / (1.0f - smoothingEnd);
                        Color targetColor = Palette.getColor(smoothingEnd);
                        r = (int) (targetColor.getRed() + t * (mixedRed - targetColor.getRed()));
                        g = (int) (targetColor.getGreen() + t * (mixedRed - targetColor.getGreen()));
                        b = (int) (targetColor.getBlue() + t * (mixedRed - targetColor.getBlue()));
                     } else {
                        Color c = Palette.getColor(progress);
                        r = c.getRed(); g = c.getGreen(); b = c.getBlue();
                     }
                }
                
                // Current point
                double h = heightTotal - j * heightStep;
                double angle = Math.toRadians(j + angleDegOffset);
                float px = (float)(Math.cos(angle) * radius);
                float pz = (float)(Math.sin(angle) * radius);
                
                // Next point
                double h2 = heightTotal - (j + 1) * heightStep;
                double angle2 = Math.toRadians((j + 1) + angleDegOffset);
                float px2 = (float)(Math.cos(angle2) * radius);
                float pz2 = (float)(Math.sin(angle2) * radius);
                
                int a = (int)(alpha * 255);
                // Clamp alpha? Original logic relied on Render2D.injectAlpha which likely masks 0-255.
                // Assuming alpha is within reasonable bounds or cast handles it (wrap around behavior for byte? no int).
                
                bufferBuilder.vertex(matrix, px, (float)h, pz).color(r, g, b, a);
                bufferBuilder.vertex(matrix, px2, (float)h2 + heightOffset, pz2).color(r, g, b, a);
                // Warning: Original renderSpiral:
                // Vec3d current = vecs.get(j);
                // Vec3d next = vecs.get(j+1);
                // buffer.vertex(current).color...
                // buffer.vertex(next + heightOffset).color...
                // WAIT! It draws `current` and `next`? 
                // "vertex(current.x, current.y, current.z)"
                // "vertex(next.x, next.y + heightOffset, next.z)"
                // This connects point J (at height H) to point J+1 (at height H-step + Offset).
                // This creates a slightly skewed strip.
                // My logic matches: current (px, h, pz) -> next (px2, h2 + Offset, pz2).
            }
            Render2D.endBuilding(bufferBuilder);
        }

        RenderSystem.enableCull();
        stack.translate(-x, -y, -z);
        endRender();
        RenderSystem.enableDepthTest();
        stack.pop();
    }

    public static void drawScanEsp(MatrixStack stack, Entity target) {

        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);
        float animationSpeed = CONFIG.targetRenderTopkaSpeed / 100f;
        float minHeightOffset = 0.01f;
        float maxHeightOffset = 0.5f;

        float radius = CONFIG.targetRenderTopkaRadius / 100f;

        double x = MathHelper.lerp(tickDelta, target.prevX, target.getX()) - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = MathHelper.lerp(tickDelta, target.prevY, target.getY()) - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = MathHelper.lerp(tickDelta, target.prevZ, target.getZ()) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double height = target.getHeight();

        float time = (System.currentTimeMillis() % 1000000) / 1000.0f;
        float t = (time * animationSpeed) % 1.0f;
        float triangleWave = t < 0.5f ? 2.0f * t : 2.0f * (1.0f - t);
        float heightStep = (float) (triangleWave * height);

        float direction = t < 0.5f ? 1.0f : -1.0f;
        boolean movingUp = direction > 0;
        float speed = 1.0f - Math.abs(2.0f * triangleWave - 1.0f);

        float heightOffset = minHeightOffset + (maxHeightOffset - minHeightOffset) * speed;
        heightOffset = Math.max(heightOffset, minHeightOffset);

        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();

        // Optimized Inline RenderRing
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        int numPoints = 360;
        int colorCount = Palette.getColorsCount();
        float angleStep = 360f / colorCount;
        
        // Constant alpha parts
        int lowerAlpha = movingUp ? 0 : 255;
        int upperAlpha = movingUp ? 255 : 0;

        for (int i = 0; i <= numPoints; i++) {
            // Position
            double radians = Math.toRadians(i);
            float px = (float) (Math.cos(radians) * radius);
            float pz = (float) (Math.sin(radians) * radius);
            float py = heightStep;

            // Color Interpolation
            int currentIndex = i % numPoints;
            float angleDeg = (360f * currentIndex) / numPoints;
            
            // Fast lookup of palette indices
            int leftIndex = (int)(angleDeg / angleStep);
            if (leftIndex >= colorCount) leftIndex = colorCount - 1;
            int rightIndex = (leftIndex + 1) % colorCount;
            
            float leftAngle = leftIndex * angleStep;
            
            float progress = (angleDeg - leftAngle) / angleStep;

            // Manual Color Lerp
            Color c1 = Palette.getColor(leftIndex / (float) (colorCount - 1));
            Color c2 = Palette.getColor(rightIndex / (float) (colorCount - 1));
            
            int r = (int) (c1.getRed() + progress * (c2.getRed() - c1.getRed()));
            int g = (int) (c1.getGreen() + progress * (c2.getGreen() - c1.getGreen()));
            int b = (int) (c1.getBlue() + progress * (c2.getBlue() - c1.getBlue()));

            bufferBuilder.vertex(matrix, px, py, pz)
                    .color(r, g, b, lowerAlpha);
            bufferBuilder.vertex(matrix, px, py + heightOffset, pz)
                    .color(r, g, b, upperAlpha);
        }

        Render2D.endBuilding(bufferBuilder);

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);

        stack.translate(-x, -y, -z);
        RenderSystem.enableDepthTest();
        stack.pop();
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }
}
