package me.BATapp.batclient.modules;

import me.BATapp.batclient.config.ConfigurableModule;
import me.BATapp.batclient.config.BATclient_ConfigEnums;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.utils.MathUtility;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.TexturesManager;
import me.BATapp.batclient.utils.OptimizationManager;
import me.BATapp.batclient.utils.RenderOptimizations;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.AirBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class JumpParticles extends ConfigurableModule {
    public static final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private static final List<Identifier> AVAILABLE_TEXTURES = new ArrayList<>();
    private static boolean wasJumping = false;
    
    // Limity pro spamování
    private static long lastSpawnTime = 0;
    private static final long SPAWN_COOLDOWN_MS = 150; // 150ms mezi spawny
    private static final int MAX_PARTICLES = 200; // Max 200 částic najednou

    public static void onTick() {
        if (mc.player == null || !CONFIG.jumpParticlesEnabled) return;
        updateAvailableTextures();

        boolean isJumping = !mc.player.isOnGround();
        if (isJumping && !wasJumping && mc.options.jumpKey.isPressed()) {
            // Debounce - limit spawn frequency
            long now = System.currentTimeMillis();
            if (now - lastSpawnTime >= SPAWN_COOLDOWN_MS) {
                spawnParticles();
                lastSpawnTime = now;
            }
        }
        wasJumping = isJumping;

        particles.removeIf(Particle::update);
    }

    public static void render(WorldRenderContext context) {
        if (mc.player != null && mc.world != null) {
            for (Particle particle : particles) {
                particle.render(context.matrixStack(), context.tickCounter().getTickDelta(true));
            }
        }
    }

    private static void updateAvailableTextures() {
        AVAILABLE_TEXTURES.clear();

        if (CONFIG.jumpParticlesIncludeFirefly) AVAILABLE_TEXTURES.add(TexturesManager.FIREFLY_ALT);
        if (CONFIG.jumpParticlesIncludeDollar) AVAILABLE_TEXTURES.add(TexturesManager.DOLLAR);
        if (CONFIG.jumpParticlesIncludeSnowflake) AVAILABLE_TEXTURES.add(TexturesManager.SNOWFLAKE);
        if (CONFIG.jumpParticlesIncludeHeart) AVAILABLE_TEXTURES.add(TexturesManager.HEART);
        if (CONFIG.jumpParticlesIncludeStar) AVAILABLE_TEXTURES.add(TexturesManager.STAR);
        if (CONFIG.jumpParticlesIncludeGlyphs) {
            Collections.addAll(AVAILABLE_TEXTURES, TexturesManager.GLYPH_TEXTURES);
        }

        if (AVAILABLE_TEXTURES.isEmpty()) {
            AVAILABLE_TEXTURES.add(TexturesManager.FIREFLY);
        }
    }

    private static void spawnParticles() {
        if (mc.player == null) return;
        
        // Limit total particles
        if (particles.size() >= MAX_PARTICLES) return;
        
        Vec3d pos = mc.player.getPos();
        int count = Math.min(CONFIG.jumpParticlesCount, MAX_PARTICLES - particles.size());
        
        for (int i = 0; i < count; i++) {
            Color color = Palette.getRandomColor();
            particles.add(new Particle(
                    (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(),
                    color,
                    MathUtility.random(0, 180),
                    MathUtility.random(10f, 60f)
            ));
        }
    }

    public static class Particle {
        protected float x, y, z;
        protected float px, py, pz;
        protected float motionX, motionY, motionZ;
        protected float rotationAngle, rotationSpeed;
        protected Identifier glyphTexture;
        protected long time;

        private final long lifeTimeMs;
        private float alpha = 1.0f;
        private float scale = CONFIG.jumpParticlesScale;

        public Particle(float x, float y, float z, Color color, float rotationAngle, float rotationSpeed) {
            this.x = px = x;
            this.y = py = y;
            this.z = pz = z;
            this.rotationAngle = rotationAngle;
            this.rotationSpeed = rotationSpeed;
            this.time = System.currentTimeMillis();
            this.lifeTimeMs = (long) (CONFIG.jumpParticlesLiveTime * 1000);

            int speed = CONFIG.jumpParticlesSpeed;
            this.motionX = MathUtility.random(-speed / 50f, speed / 50f);
            this.motionY = 0;
            this.motionZ = MathUtility.random(-speed / 50f, speed / 50f);

            if (!AVAILABLE_TEXTURES.isEmpty()) {
                this.glyphTexture = AVAILABLE_TEXTURES.get(new Random().nextInt(AVAILABLE_TEXTURES.size()));
            }

            this.color = color;
        }

        private final Color color;

        public boolean update() {
            double sp = Math.sqrt(motionX * motionX + motionZ * motionZ);

            px = x;
            py = y;
            pz = z;
            x += motionX;
            y += motionY;
            z += motionZ;

            boolean bounce = CONFIG.jumpParticlesPhysic == BATclient_ConfigEnums.JumpParticlesPhysic.BOUNCE;

            // Simplified physics - check only if moving significantly
            if (sp > 0.05f || motionY < 0) {
                if (posBlock(x, y - CONFIG.jumpParticlesScale / 10f, z)) {
                    if (bounce) {
                        motionY = 0.18f + MathUtility.random(0.0f, 0.05f);
                        motionX += MathUtility.random(-0.005f, 0.005f);
                        motionZ += MathUtility.random(-0.005f, 0.005f);
                    } else {
                        motionY = -motionY * 0.8f;
                    }

                    motionX *= 0.98f;
                    motionZ *= 0.98f;
                } else if (sp > 0.1f && (posBlock(x - sp, y, z - sp) || posBlock(x + sp, y, z + sp) ||
                        posBlock(x + sp, y, z - sp) || posBlock(x - sp, y, z + sp))) {
                    motionX = -motionX * 0.9f;
                    motionZ = -motionZ * 0.9f;
                }
            }

            if (bounce) motionY -= 0.035f;
            motionY *= 0.99f;

            long elapsed = System.currentTimeMillis() - time;
            float lifeFraction = Math.min(1.0f, (float) elapsed / lifeTimeMs);

            if (lifeFraction >= 0.8f) {
                float fade = 1.0f - (lifeFraction - 0.8f) / 0.2f;
                if (CONFIG.jumpParticlesDisappear == BATclient_ConfigEnums.JumpParticlesDisappear.ALPHA) {
                    alpha = fade;
                } else if (CONFIG.jumpParticlesDisappear == BATclient_ConfigEnums.JumpParticlesDisappear.SCALE) {
                    scale = CONFIG.jumpParticlesScale * fade;
                }
            }

            return elapsed > lifeTimeMs;
        }

        public void render(MatrixStack matrixStack, float tickDelta) {
            if (!CONFIG.jumpParticlesEnabled) return;

            // Check if particle is in front of camera (optimization)
            double lerpedX = MathHelper.lerp(tickDelta, px, x);
            double lerpedY = MathHelper.lerp(tickDelta, py, y);
            double lerpedZ = MathHelper.lerp(tickDelta, pz, z);
            
            if (!MathUtility.isPointInFrontOfCamera(lerpedX, lerpedY, lerpedZ)) {
                return; // Don't render particles behind the player
            }

            float baseScale = 0.07f;
            float renderScale = baseScale * scale;

            double posX = lerpedX - mc.getEntityRenderDispatcher().camera.getPos().getX();
            double posY = lerpedY - mc.getEntityRenderDispatcher().camera.getPos().getY();
            double posZ = lerpedZ - mc.getEntityRenderDispatcher().camera.getPos().getZ();

            // Don't render if too far
            double distSq = posX * posX + posY * posY + posZ * posZ;
            if (distSq > 10000) return; // 100 blocks away

            matrixStack.push();
            matrixStack.translate(posX, posY, posZ);
            matrixStack.scale(renderScale, renderScale, renderScale);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle += (1f / Math.max(1f, mc.getCurrentFps())) * rotationSpeed));

            if (glyphTexture != null && alpha > 0.01f) { // Skip render if nearly invisible
                Render2D.drawGlyphs(matrixStack, glyphTexture, new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)), 1f);
            }

            matrixStack.pop();
        }

        private boolean posBlock(double x, double y, double z) {
            if (mc.player == null || mc.world == null) return false;
            return (!(mc.world.getBlockState(BlockPos.ofFloored(x, y, z)).getBlock() instanceof AirBlock));
        }
    }

    public enum Physic {
        BOUNCE, FLY
    }

    public enum Disappear {
        ALPHA, SCALE
    }
}

