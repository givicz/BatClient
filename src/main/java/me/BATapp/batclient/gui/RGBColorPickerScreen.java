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
        UIComponentRenderer.drawRoundedRect(context, x, y, 250, 300, 8, BATSettingsScreen.COLOR_PRIMARY);
        UIComponentRenderer.drawRoundedBorder(context, x, y, 250, 300, 8, BATSettingsScreen.COLOR_ACCENT, 2);
        
        context.drawText(MinecraftClient.getInstance().textRenderer, "RGB Color Picker", x + 10, y + 10, BATSettingsScreen.COLOR_TEXT, false);
        
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
        
        context.drawText(MinecraftClient.getInstance().textRenderer, "Hue", x + 10, sliderY - 15, BATSettingsScreen.COLOR_TEXT, false);
        UIComponentRenderer.drawRoundedRect(context, paletteX, sliderY, PALETTE_WIDTH, SLIDER_HEIGHT, 4, BATSettingsScreen.COLOR_SECONDARY);
        UIComponentRenderer.drawRoundedRect(context, huePos - 2, sliderY - 2, 4, SLIDER_HEIGHT + 4, 2, BATSettingsScreen.COLOR_HIGHLIGHT);
        
        // Current color preview
        int previewY = sliderY + 40;
        int previewX = x + 20;
        UIComponentRenderer.drawRoundedRect(context, previewX, previewY, 100, 40, 4, BATSettingsScreen.COLOR_SECONDARY);
        currentColor = 0xFF000000 | (r << 16) | (g << 8) | b;
        UIComponentRenderer.drawRoundedRect(context, previewX + 2, previewY + 2, 96, 36, 3, currentColor);
        
        // RGB display
        context.drawText(MinecraftClient.getInstance().textRenderer, 
                String.format("RGB: %d, %d, %d", r, g, b), 
                x + 130, previewY + 10, BATSettingsScreen.COLOR_TEXT, false);
        
        // Buttons
        int buttonY = previewY + 50;
        
        // Accept button
        UIComponentRenderer.drawRoundedRect(context, previewX, buttonY, 100, 25, 4, BATSettingsScreen.COLOR_HIGHLIGHT);
        context.drawText(MinecraftClient.getInstance().textRenderer, "OK", previewX + 45, buttonY + 8, BATSettingsScreen.COLOR_PRIMARY, false);
        
        // Cancel button
        UIComponentRenderer.drawRoundedRect(context, previewX + 110, buttonY, 100, 25, 4, BATSettingsScreen.COLOR_ACCENT);
        context.drawText(MinecraftClient.getInstance().textRenderer, "Cancel", previewX + 135, buttonY + 8, BATSettingsScreen.COLOR_TEXT, false);
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
