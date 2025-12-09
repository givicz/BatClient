package me.BATapp.batclient.gui;

import me.BATapp.batclient.utils.UIElementManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 *Screen pro úpravu UI prvků - barvy, alfa, pozice atd
 */
public class UICustomizationScreen extends Screen {
    private final Screen parent;
    private String selectedElement = null;
    private int currentTab = 0; // 0 = Colors, 1 = Effects
    
    public UICustomizationScreen(Screen parent) {
        super(Text.literal("UI Customization"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), (button) -> {
            this.close();
        }).dimensions(this.width - 110, 10, 100, 20).build());
        
        // Reset button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), (button) -> {
            UIElementManager.resetToDefaults();
            UIElementManager.savePositions();
        }).dimensions(this.width - 220, 10, 100, 20).build());
        
        // Tab buttons
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Colors"), (button) -> {
            this.currentTab = 0;
        }).dimensions(10, 40, 100, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Effects"), (button) -> {
            this.currentTab = 1;
        }).dimensions(120, 40, 100, 20).build());
        
        // Element selection buttons
        int elemY = 70;
        for (UIElementManager.UIElement elem : UIElementManager.getAllElements()) {
            String label = elem.id.substring(0, Math.min(15, elem.id.length()));
            this.addDrawableChild(ButtonWidget.builder(Text.literal(label), (button) -> {
                this.selectedElement = elem.id;
            }).dimensions(10, elemY, 200, 20).build());
            elemY += 25;
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTick) {
        //this.renderBackground(context, mouseX, mouseY, partialTick); // Vypneme dirt pozadí
        
        // Vykreslíme moderní pozadí
        int backgroundColor = 0xCC222222; // Stejné jako v našem tématu
        UIComponentRenderer.drawRoundedRect(context, 5, 5, this.width - 10, this.height - 10, 8, backgroundColor);
        UIComponentRenderer.drawRoundedBorder(context, 5, 5, this.width - 10, this.height - 10, 8, 0xFF00BFFF, 2);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, "UI Customization", 
            this.width / 2, 15, 0xFFFFFF);
        
        // Info text
        String tabName = this.currentTab == 0 ? "Colors" : "Effects";
        context.drawTextWithShadow(this.textRenderer, "Tab: " + tabName, 230, 40, 0xFFe0e0e0);
        
        if (this.selectedElement != null) {
            UIElementManager.UIElement elem = UIElementManager.getElement(this.selectedElement);
            if (elem != null) {
                context.drawTextWithShadow(this.textRenderer, "Selected: " + elem.id, 230, 60, 0xFF00d4ff);
                
                if (this.currentTab == 0) {
                    renderColorOptions(context, elem, 230, 85);
                } else {
                    renderEffectOptions(context, elem, 230, 85);
                }
            }
        }
        
        super.render(context, mouseX, mouseY, partialTick);
    }
    
    private void renderColorOptions(DrawContext context, UIElementManager.UIElement elem, int x, int y) {
        // Background color preview
        context.drawTextWithShadow(this.textRenderer, "Background: 0x" + String.format("%08X", elem.backgroundColor), x, y, 0xFFe0e0e0);
        context.fill(x + 200, y - 2, x + 220, y + 10, elem.backgroundColor);
        
        // Accent color preview
        context.drawTextWithShadow(this.textRenderer, "Accent: 0x" + String.format("%08X", elem.accentColor), x, y + 20, 0xFFe0e0e0);
        context.fill(x + 200, y + 18, x + 220, y + 30, elem.accentColor);
        
        // Text color preview
        context.drawTextWithShadow(this.textRenderer, "Text: 0x" + String.format("%08X", elem.textColor), x, y + 40, 0xFFe0e0e0);
        context.fill(x + 200, y + 38, x + 220, y + 50, elem.textColor);
    }
    
    private void renderEffectOptions(DrawContext context, UIElementManager.UIElement elem, int x, int y) {
        // Alpha slider info
        int alphaPercent = (int)(elem.alpha * 100);
        context.drawTextWithShadow(this.textRenderer, "Alpha: " + alphaPercent + "%", x, y, 0xFFe0e0e0);
        
        // Visual alpha bar
        int barLength = (int)(elem.alpha * 100);
        context.fill(x, y + 12, x + barLength, y + 20, 0xFF00d4ff);
        context.fill(x + barLength, y + 12, x + 100, y + 20, 0xFF404040);
        
        // Gradient toggle
        String gradientStatus = elem.gradientEnabled ? "ON" : "OFF";
        context.drawTextWithShadow(this.textRenderer, "Gradient: " + gradientStatus, x, y + 30, 0xFFe0e0e0);
        
        // Position info
        context.drawTextWithShadow(this.textRenderer, "Position: X=" + (int)elem.x + " Y=" + (int)elem.y, x, y + 50, 0xFFe0e0e0);
    }
    
    @Override
    public void close() {
        UIElementManager.savePositions();
        super.close();
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
