package me.BATapp.batclient.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.settings.impl.ColorSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Modern breaking animation.
 * Renders a smooth growing cube indicating breaking progress.
 */
public class BreakingAnimation extends SoupModule {
    
    public static final ColorSetting color = new ColorSetting("Color", "Animation color", 0x8000d4ff); // Default alpha 128

    public BreakingAnimation() {
        super("Breaking Animation", Category.WORLD);
    }

        // Draw Fill
        drawBox(buffer, x1, y1, z1, x2, y2, z2, r, g, b, a);
        
        try {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } catch (Exception ignored) {}
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        ms.pop();
    }

    private static void drawBox(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        Matrix4f m = new Matrix4f(); // We are using world translation in matrix stack, so this identity is fine? 
        // No, standard BufferBuilder vertex() takes matrix. 
        // But in 1.21, VertexConsumer usually takes Matrix4f. 
        // Wait, I am not passing matrix to vertex() calls? 
        // I need to use the matrix from MatrixStack!
    }

    // Rewrite render method to include matrix
    public static void render(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null || mc.player == null) return;

        // Find the breaking info for the local player
        int myId = mc.player.getId();
        var infos = world.getBlockBreakingInfos().values();
        
        BlockPos targetPos = null;
        int stage = -1;

        for (var info : infos) {
            if (info.getActorId() == myId) {
                targetPos = info.getPos();
                stage = info.getStage();
                break;
            }
        }

        if (targetPos == null || stage < 0) return;

        double camX = context.camera().getPos().x;
        double camY = context.camera().getPos().y;
        double camZ = context.camera().getPos().z;

        MatrixStack ms = context.matrixStack();
        ms.push();
        ms.translate(targetPos.getX() - camX, targetPos.getY() - camY, targetPos.getZ() - camZ);

        // Progress 0.0 to 1.0 based on stage (0-9)
        float progress = (stage + 1) / 10.0f;
        // Smooth scaling from center
        ms.translate(0.5, 0.5, 0.5);
        ms.scale(progress, progress, progress); // Grow from center
        ms.translate(-0.5, -0.5, -0.5);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest(); // Optional: see through blocks?
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        int c = color.getValue();
        float a = ((c >> 24) & 0xFF) / 255.0f;
        float r = ((c >> 16) & 0xFF) / 255.0f;
        float g = ((c >> 8) & 0xFF) / 255.0f;
        float b = (c & 0xFF) / 255.0f;
        
        // Render box
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        Matrix4f matrix = ms.peek().getPositionMatrix();

        // Box vertices
        float x1 = 0, y1 = 0, z1 = 0;
        float x2 = 1, y2 = 1, z2 = 1;

        // DOWN (0,0,0) -> (1,0,1)
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);

        // UP
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);

        // NORTH
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);

        // SOUTH
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);

        // WEST
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);

        // EAST
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);

        try {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } catch (Exception ignored) {}
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        ms.pop();
    }
}
