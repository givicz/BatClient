package me.BATapp.batclient.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.font.FontRenderer;
import me.BATapp.batclient.font.FontRenderers;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.TexturesManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL40C;

import java.awt.*;

import static me.BATapp.batclient.config.ConfigurableModule.CONFIG;

public class WatermarkRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void render(DrawContext context) {
        if (!CONFIG.waterMarkEnabled) return;

        // ✅ DEFENZIVNÍ KONTROLA - font musí být inicializovaný
        if (FontRenderers.sf_bold == null) {
            return; // Zkrátíme render, pokud font není ready
        }

        if (mc.player == null) return;

        FontRenderer font = FontRenderers.sf_bold; // Přiřazení pro jistotu

        float x = CONFIG.waterMarkX;
        float y = CONFIG.waterMarkY;
        float xOffset = -14f;
        float yOffset = -14f;

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(0, 0, 10);
        renderBackground(context, font, x + 2.5f + xOffset, y + yOffset);
        renderName(context, font, x + xOffset, y + yOffset);
        renderHead(context, x + xOffset, y + yOffset);
        matrixStack.pop();
    }

    private static void renderBackground(DrawContext context, FontRenderer font, float x, float y) {
        assert mc.player != null;
        float width = font.getStringWidth(mc.player.getName().getString()) + 20;
        float height = 14;
        x += 13;
        if (CONFIG.mctiersEnabled) width += 18;
        y += 16;

        Render2D.drawRound(context.getMatrices(), x + 0.5f, y + 0.5f, width, height, 2, Palette.getBackColor());
    }

    private static void renderName(DrawContext context, FontRenderer font, float x, float y) {
        assert mc.player != null;
        String displayName = mc.player.getName().getString();
        y += 21.5f;
        MatrixStack stack = context.getMatrices();

        font.drawString(stack, displayName, x + 32, y, Palette.getTextColor());

        if (!CONFIG.mctiersEnabled) return;
        float textWidth = font.getStringWidth(displayName);

        stack.push();
        stack.translate(x + 40 + textWidth, y - 3.7, 0);
        stack.scale(0.3f, 0.3f, 0.3f);
        context.drawTexture(RenderLayer::getGuiTextured, TexturesManager.getMC_TiersGameModeTexture(), 0, 0, 0, 0, 40, 40, 1268, 1153, 1268, 1153, Palette.getTextColor());
        stack.pop();

        Render2D.renderRoundedGradientRect(stack, TargetHudRenderer.topLeft, TargetHudRenderer.topLeft, TargetHudRenderer.bottomRight, TargetHudRenderer.bottomRight, x + 36 + textWidth, y - 4.5f, 1, 13.5f, 0);
    }

    private static void renderHead(DrawContext context, float x, float y) {
        assert mc.player != null;
        Identifier texture = mc.player.getSkinTextures().texture();
        float scale = 0.3f;
        float r = 9;

        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x + 3.5f + 20, y + 3.5f + 20, 0);
        stack.scale(scale, scale, scale);
        stack.translate(-(x + 3.5f + 20), -(y + 3.5f + 20), 0);
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT);
        RenderSystem.colorMask(true, true, true, true);
        Render2D.drawRound(stack, x + 3.5f, y + 3.5f, 40, 40, r, Palette.getBackColor());
        Render2D.setupRender();
        Render2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 1, 1, 1, 1, x + 3.5f, y + 3.5f, x + 3.5f + 40, y + 3.5f + 40, r, 3);
        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        Render2D.renderTexture(stack, texture, x + 3.5f, y + 3.5f, 40, 40, 8, 8, 8, 8, 64, 64);
        Render2D.renderTexture(stack, texture, x + 3.5f, y + 3.5f, 40, 40, 40, 8, 8, 8, 64, 64);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        stack.pop();
    }
}