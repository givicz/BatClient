package me.BATapp.batclient.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class RGBColorPickerScreen extends Screen {
    private final Screen parentScreen;
    private final Consumer<Integer> callback;
    private int currentColor;
    
    private int r, g, b;
    private float hueSlider = 0;
    private float satSlider = 1;
    private float valSlider = 1;
    
    private static final int PALETTE_WIDTH = 200;
    private static final int PALETTE_HEIGHT = 150;
    private static final int SLIDER_HEIGHT = 20;
    private static final int PADDING = 20;

    public RGBColorPickerScreen(Screen parentScreen, int initialColor, Consumer<Integer> callback) {
        super(Text.literal("RGB Color Picker"));
        this.parentScreen = parentScreen;
        this.callback = callback;
        this.currentColor = initialColor;
        
        // Extract RGB
        this.r = (initialColor >> 16) & 0xFF;
        this.g = (initialColor >> 8) & 0xFF;
        this.b = initialColor & 0xFF;
        
        rgbToHsv();
    }

    private void rgbToHsv() {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;
        
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;
        
        // Hue
        if (delta == 0) {
            hueSlider = 0;
        } else if (max == rf) {
            hueSlider = ((gf - bf) / delta % 6) / 6.0f;
        } else if (max == gf) {
            hueSlider = ((bf - rf) / delta + 2) / 6.0f;
        } else {
            hueSlider = ((rf - gf) / delta + 4) / 6.0f;
        }
        
        if (hueSlider < 0) hueSlider += 1;
        
        // Saturation
        satSlider = max == 0 ? 0 : delta / max;
        
        // Value
        valSlider = max;
    }

    private void hsvToRgb() {
        float h = hueSlider * 6;
        float s = satSlider;
        float v = valSlider;
        
        float c = v * s;
        float x = c * (1 - Math.abs(h % 2 - 1));
        float m = v - c;
        
        float rf, gf, bf;
        if (h < 1) {
            rf = c; gf = x; bf = 0;
        } else if (h < 2) {
            rf = x; gf = c; bf = 0;
        } else if (h < 3) {
            rf = 0; gf = c; bf = x;
        } else if (h < 4) {
            rf = 0; gf = x; bf = c;
        } else if (h < 5) {
            rf = x; gf = 0; bf = c;
        } else {
            rf = c; gf = 0; bf = x;
        }
        
        r = (int) ((rf + m) * 255);
        g = (int) ((gf + m) * 255);
        b = (int) ((bf + m) * 255);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        int x = (this.width - 250) / 2;
        int y = (this.height - 300) / 2;
        
        // Background
        context.fill(x, y, x + 250, y + 300, 0xFF1a1a2e);
        context.fill(x + 1, y + 1, x + 249, y + 299, 0xFF16213e);
        
        context.drawText(MinecraftClient.getInstance().textRenderer, "RGB Color Picker", x + 10, y + 10, 0xFFe0e0e0, false);
        
        int paletteX = x + 20;
        int paletteY = y + 30;
        
        // Draw color palette
        for (int py = 0; py < PALETTE_HEIGHT; py++) {
            float val = 1.0f - (py / (float) PALETTE_HEIGHT);
            for (int px = 0; px < PALETTE_WIDTH; px++) {
                float sat = px / (float) PALETTE_WIDTH;
                
                float c = val * sat;
                float h = hueSlider * 6;
                float x1 = c * (1 - Math.abs(h % 2 - 1));
                float m = val - c;
                
                float rf, gf, bf;
                if (h < 1) {
                    rf = c; gf = x1; bf = 0;
                } else if (h < 2) {
                    rf = x1; gf = c; bf = 0;
                } else if (h < 3) {
                    rf = 0; gf = c; bf = x1;
                } else if (h < 4) {
                    rf = 0; gf = x1; bf = c;
                } else if (h < 5) {
                    rf = x1; gf = 0; bf = c;
                } else {
                    rf = c; gf = 0; bf = x1;
                }
                
                int color = 0xFF000000 |
                        (((int)((rf + m) * 255)) << 16) |
                        (((int)((gf + m) * 255)) << 8) |
                        (int)((bf + m) * 255);
                
                context.fill(paletteX + px, paletteY + py, paletteX + px + 1, paletteY + py + 1, color);
            }
        }
        
        // Draw hue slider
        int sliderY = paletteY + PALETTE_HEIGHT + 20;
        int huePos = paletteX + (int)(hueSlider * PALETTE_WIDTH);
        
        context.drawText(MinecraftClient.getInstance().textRenderer, "Hue", x + 10, sliderY - 15, 0xFFe0e0e0, false);
        context.fill(paletteX, sliderY, paletteX + PALETTE_WIDTH, sliderY + SLIDER_HEIGHT, 0xFF333333);
        context.fill(huePos - 2, sliderY - 2, huePos + 2, sliderY + SLIDER_HEIGHT + 2, 0xFF00d4ff);
        
        // Current color preview
        int previewY = sliderY + 40;
        int previewX = x + 20;
        context.fill(previewX, previewY, previewX + 100, previewY + 40, 0xFF333333);
        currentColor = 0xFF000000 | (r << 16) | (g << 8) | b;
        context.fill(previewX + 2, previewY + 2, previewX + 98, previewY + 38, currentColor);
        
        // RGB display
        context.drawText(MinecraftClient.getInstance().textRenderer, 
                String.format("RGB: %d, %d, %d", r, g, b), 
                x + 130, previewY + 10, 0xFFe0e0e0, false);
        
        // Buttons
        int buttonY = previewY + 50;
        
        // Accept button
        context.fill(previewX, buttonY, previewX + 100, buttonY + 25, 0xFF00d4ff);
        context.drawText(MinecraftClient.getInstance().textRenderer, "OK", previewX + 35, buttonY + 8, 0xFF1a1a2e, false);
        
        // Cancel button
        context.fill(previewX + 110, buttonY, previewX + 210, buttonY + 25, 0xFF8a8a8a);
        context.drawText(MinecraftClient.getInstance().textRenderer, "Cancel", previewX + 130, buttonY + 8, 0xFF1a1a2e, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        int x = (this.width - 250) / 2;
        int y = (this.height - 300) / 2;
        
        int paletteX = x + 20;
        int paletteY = y + 30;
        
        // Palette click
        if (mouseX >= paletteX && mouseX <= paletteX + PALETTE_WIDTH &&
            mouseY >= paletteY && mouseY <= paletteY + PALETTE_HEIGHT) {
            int px = (int)(mouseX - paletteX);
            int py = (int)(mouseY - paletteY);
            
            satSlider = px / (float) PALETTE_WIDTH;
            valSlider = 1.0f - (py / (float) PALETTE_HEIGHT);
            hsvToRgb();
            return true;
        }
        
        // Hue slider
        int sliderY = paletteY + PALETTE_HEIGHT + 20;
        if (mouseX >= paletteX && mouseX <= paletteX + PALETTE_WIDTH &&
            mouseY >= sliderY && mouseY <= sliderY + SLIDER_HEIGHT) {
            hueSlider = Math.max(0, Math.min(1, (float)(mouseX - paletteX) / PALETTE_WIDTH));
            hsvToRgb();
            return true;
        }
        
        // Buttons
        int previewY = sliderY + 40;
        int buttonY = previewY + 50;
        int previewX = x + 20;
        
        // OK button
        if (mouseX >= previewX && mouseX <= previewX + 100 &&
            mouseY >= buttonY && mouseY <= buttonY + 25) {
            callback.accept(currentColor);
            this.client.setScreen(parentScreen);
            return true;
        }
        
        // Cancel button
        if (mouseX >= previewX + 110 && mouseX <= previewX + 210 &&
            mouseY >= buttonY && mouseY <= buttonY + 25) {
            this.client.setScreen(parentScreen);
            return true;
        }
        
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
    }
}
