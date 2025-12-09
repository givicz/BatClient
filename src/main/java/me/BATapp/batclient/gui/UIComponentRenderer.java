package me.BATapp.batclient.gui;

import me.BATapp.batclient.utils.UIElementManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;

/**
 * Advanced UI renderer s gradientem a transparencí
 * Inspirováno SoupAPI designem
 */
public class UIComponentRenderer {
    
    /**
     * Vykreslit panel s gradientem a transparencí
     */
    public static void drawGradientPanel(DrawContext context, String elementId, 
                                        int x, int y, int width, int height) {
        UIElementManager.UIElement elem = UIElementManager.getElement(elementId);
        if (elem == null) return;
        
        int bgColor = UIElementManager.applyAlpha(elem.backgroundColor, elem.alpha);
        int accentColor = UIElementManager.applyAlpha(elem.accentColor, elem.alpha);
        int radius = 8;

        drawRoundedRect(context, x, y, width, height, radius, bgColor);
        
        drawRoundedBorder(context, x, y, width, height, radius, accentColor, 2);
    }
    
    /**
     * Vykreslit gradient obdélník
     */
    public static void drawGradientRectangle(DrawContext context, int x1, int y1, int x2, int y2,
                                            int color1, int color2) {
        int height = y2 - y1;
        
        for (int i = 0; i < height; i++) {
            float progress = (float) i / height;
            int interpolated = interpolateColor(color1, color2, progress);
            fill(context, x1, y1 + i, x2, y1 + i + 1, interpolated);
        }
    }
    
    /**
     * Interpolovat mezi dvěma barvami
     */
    public static int interpolateColor(int color1, int color2, float t) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Vykreslit border
     */
    public static void drawRoundedBorder(DrawContext context, int x, int y, int width, int height, int radius, int color, int thickness) {
        // Rovné části
        fill(context, x + radius, y, x + width - radius, y + thickness, color);
        fill(context, x + radius, y + height - thickness, x + width - radius, y + height, color);
        fill(context, x, y + radius, x + thickness, y + height - radius, color);
        fill(context, x + width - thickness, y + radius, x + width, y + height - radius, color);

        // Rohy
        drawArc(context, x + radius, y + radius, radius, 180, 270, color, thickness); // Horní levý
        drawArc(context, x + width - radius, y + radius, radius, 270, 360, color, thickness); // Horní pravý
        drawArc(context, x + width - radius, y + height - radius, radius, 0, 90, color, thickness); // Dolní pravý
        drawArc(context, x + radius, y + height - radius, radius, 90, 180, color, thickness); // Dolní levý
    }

    public static void drawArc(DrawContext context, int centerX, int centerY, int radius, int startAngle, int endAngle, int color, int thickness) {
        for (int t = 0; t < thickness; t++) {
            for (int i = startAngle; i <= endAngle; i++) {
                double angle = Math.toRadians(i);
                int x = (int) (centerX + Math.cos(angle) * (radius - t));
                int y = (int) (centerY + Math.sin(angle) * (radius - t));
                fill(context, x, y, x + 1, y + 1, color);
            }
        }
    }
    
    /**
     * Vykreslí zaoblený obdélník.
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        fill(context, x + radius, y, x + width - radius, y + height, color);
        fill(context, x, y + radius, x + width, y + height - radius, color);
        drawFilledCircleCorner(context, x + radius, y + radius, radius, 0, color);
        drawFilledCircleCorner(context, x + width - radius, y + radius, radius, 1, color);
        drawFilledCircleCorner(context, x + width - radius, y + height - radius, radius, 2, color);
        drawFilledCircleCorner(context, x + radius, y + height - radius, radius, 3, color);
    }

    public static void drawFilledCircleCorner(DrawContext context, int centerX, int centerY, int radius, int quadrant, int color) {
        for (int i = 0; i <= radius; i++) {
            int y_offset = i;
            int x_offset = (int) Math.round(Math.sqrt(radius * radius - y_offset * y_offset));
            switch (quadrant) {
                case 0: // Horní levý
                    fill(context, centerX - x_offset, centerY - y_offset, centerX, centerY - y_offset + 1, color);
                    break;
                case 1: // Horní pravý
                    fill(context, centerX, centerY - y_offset, centerX + x_offset, centerY - y_offset + 1, color);
                    break;
                case 2: // Dolní pravý
                    fill(context, centerX, centerY + i, centerX + x_offset, centerY + i + 1, color);
                    break;
                case 3: // Dolní levý
                    fill(context, centerX - x_offset, centerY + i, centerX, centerY + i + 1, color);
                    break;
            }
        }
    }
    
    /**
     * Jednoduchý fill
     */
    public static void fill(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        
        if (y1 < y2) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        context.fill(x2, y2, x1, y1, color);
    }
    
    /**
     * Vykreslit text s pozadím
     */
    public static void drawTextWithBackground(DrawContext context, String elementId,
                                             String text, int x, int y) {
        UIElementManager.UIElement elem = UIElementManager.getElement(elementId);
        if (elem == null) return;
        
        int bgColor = UIElementManager.applyAlpha(elem.backgroundColor, elem.alpha);
        int textColor = UIElementManager.applyAlpha(elem.textColor, elem.alpha);
        
        // Pozadí textu
        int textWidth = 60; // Přibližná šířka
        drawGradientRectangle(context, x - 4, y - 2, x + textWidth + 4, y + 10, bgColor, bgColor);
        
        // Text
        context.drawText(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            text, x, y, textColor, false
        );
    }
    
    /**
     * Vykreslit icon s barvami
     */
    public static void drawIcon(DrawContext context, String elementId,
                               int x, int y, int size) {
        UIElementManager.UIElement elem = UIElementManager.getElement(elementId);
        if (elem == null) return;
        
        int accentColor = UIElementManager.applyAlpha(elem.accentColor, elem.alpha);
        int bgColor = UIElementManager.applyAlpha(elem.backgroundColor, elem.alpha);
        
        // Kruh jako ikona
        drawCircle(context, x + size/2, y + size/2, size/2, accentColor);
    }
    
    /**
     * Vykreslit kruh
     */
    public static void drawCircle(DrawContext context, int centerX, int centerY, int radius, int color) {
        for (int i = 0; i < 360; i += 5) {
            double angle = Math.toRadians(i);
            double nextAngle = Math.toRadians(i + 5);
            
            int x1 = (int)(centerX + Math.cos(angle) * radius);
            int y1 = (int)(centerY + Math.sin(angle) * radius);
            int x2 = (int)(centerX + Math.cos(nextAngle) * radius);
            int y2 = (int)(centerY + Math.sin(nextAngle) * radius);
            
            drawLine(context, x1, y1, x2, y2, color);
        }
    }
    
    /**
     * Vykreslit čáru
     */
    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y2, color);
    }
}
