package me.BATapp.batclient.particle.ambient;

import me.BATapp.batclient.modules.AmbientParticle;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.render.TargetHudRenderer;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.TexturesManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.BATapp.batclient.config.ConfigurableModule.CONFIG;

public class FireFly extends DefaultAmbientParticle {
    private final List<Trail> trails = new ArrayList<>();
    private final Color color;

    public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
        super(posX, posY, posZ, motionX, motionY, motionZ);
        this.color = CONFIG.ambientParticlesRandomColor ? Palette.getRandomColor() : TargetHudRenderer.bottomLeft;
    }

    @Override
    public boolean tick() {
        if (mc.player == null || mc.world == null) return false;
        if (mc.player.squaredDistanceTo(posX, posY, posZ) > 100) age -= 4;
        else if (!mc.world.getBlockState(new BlockPos((int) posX, (int) posY, (int) posZ)).isAir()) age -= 8;
        else age--;

        if (age < 0)
            return true;

        trails.removeIf(Trail::update);

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        trails.add(new Trail(new Vec3d(prevPosX, prevPosY, prevPosZ), new Vec3d(posX, posY, posZ), this.color));

        motionX *= 0.99f;
        motionY *= 0.99f;
        motionZ *= 0.99f;

        return false;
    }

    @Override
    public void render(WorldRenderContext context) {
        float tickDelta = context.tickCounter().getTickDelta(true);
        if (!trails.isEmpty()) {
            Camera camera = context.camera();
            for (Trail ctx : trails) {
                Vec3d pos = ctx.interpolate(1f);
                MatrixStack matrices = new MatrixStack();
                matrices.translate(pos.x, pos.y, pos.z);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

                float alphaFactor = (float) ctx.animation(tickDelta);
                int alpha = (int) (255 * ((float) age / (float) maxAge) * alphaFactor);
                Color withAlpha = Render2D.injectAlpha(ctx.color(), alpha);

                Render2D.drawGlyphs(matrices, TexturesManager.FIREFLY, withAlpha, AmbientParticle.particlesWithTrailScale.getValue() / 100f);
            }
        }
    }
}

