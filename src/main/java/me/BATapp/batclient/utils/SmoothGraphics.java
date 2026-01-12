package me.BATapp.batclient.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmoothGraphics {

    private static final Map<String, Identifier> textureCache = new HashMap<>();

    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        if (width <= 0 || height <= 0) return;
        String cacheKey = String.format("rounded_rect:%d:%d:%d:%d", width, height, radius, color);

        if (textureCache.containsKey(cacheKey)) {
            Identifier textureId = textureCache.get(cacheKey);
            RenderSystem.setShaderTexture(0, textureId);
            context.drawTexture(textureId, x, y, 0, 0, width, height, width, height);
            return;
        }

        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(color, true));
            g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, radius * 2, radius * 2));

            g2d.dispose();

            NativeImage nativeImage = toNativeImage(image);
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            Identifier textureId = textureManager.registerDynamicTexture("smooth_graphics/" + cacheKey, new NativeImageBackedTexture(nativeImage));

            textureCache.put(cacheKey, textureId);

            RenderSystem.setShaderTexture(0, textureId);
            context.drawTexture(textureId, x, y, 0, 0, width, height, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static NativeImage toNativeImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        byte[] bytes = stream.toByteArray();
        return NativeImage.read(new ByteArrayInputStream(bytes));
    }
}