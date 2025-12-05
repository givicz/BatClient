package me.BATapp.batclient.gui;

import me.BATapp.batclient.config.BATclient_Config;
import me.BATapp.batclient.modules.FPS;
import me.BATapp.batclient.modules.Keystroke;
import me.BATapp.batclient.modules.MouseMove;
import me.BATapp.batclient.modules.PotionsHud;
import me.BATapp.batclient.utils.UIElementManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * UI customization screen s drag/resize a module toggles
 */
public class UICustomizationScreenSimple extends Screen {
    private UIElementManager.UIElement currentElement;
    private UIElementManager.UIElement draggingElement;
    private int scrollY = 0;
    private int tabIndex = 0; // 0 = Elements, 1 = Modules, 2 = Settings

    public UICustomizationScreenSimple() {
        super(Text.of("UI Customization"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "UI Customization & Module Settings", 
            this.width / 2, 10, 0xFF00d4ff);
        
        // Tab buttons
        this.renderTabs(context, mouseX, mouseY);
        
        // Content based on tab
        if (tabIndex == 0) {
            this.renderElementList(context, mouseX, mouseY);
            this.renderElementControls(context, mouseX, mouseY);
        } else if (tabIndex == 1) {
            this.renderModuleToggles(context, mouseX, mouseY);
        } else if (tabIndex == 2) {
            this.renderSettings(context, mouseX, mouseY);
        }
        
        // Close button
        this.drawButton(context, 10, this.height - 30, 100, 20, "Close");
    }
    
    private void renderTabs(DrawContext context, int mouseX, int mouseY) {
        int y = 30;
        int tabWidth = 130;
        
        for (int i = 0; i < 3; i++) {
            int x = 10 + (i * (tabWidth + 5));
            int bgColor = tabIndex == i ? 0xFF0066FF : 0xFF333333;
            context.fill(x, y, x + tabWidth, y + 20, bgColor);
            
            String label = switch(i) {
                case 0 -> "Elements & UI";
                case 1 -> "Modules";
                case 2 -> "Settings";
                default -> "";
            };
            
            int textX = x + (tabWidth - this.textRenderer.getWidth(label)) / 2;
            context.drawText(this.textRenderer, label, textX, y + 6, 0xFFFFFF, false);
        }
    }
    
    private void renderElementList(DrawContext context, int mouseX, int mouseY) {
        context.fill(10, 55, 200, this.height - 40, 0x80111111);
        context.drawText(this.textRenderer, "UI Elements:", 15, 60, 0xFF00d4ff, false);
        
        int y = 75;
        int maxY = this.height - 50;
        
        for (UIElementManager.UIElement elem : UIElementManager.getAllElements()) {
            if (y > maxY) break;
            
            boolean selected = elem == currentElement;
            int bgColor = selected ? 0xFF0066FF : 0xFF333333;
            
            context.fill(15, y, 195, y + 20, bgColor);
            String label = elem.id + " (" + elem.width + "x" + elem.height + ")";
            context.drawText(this.textRenderer, label, 20, y + 6, 0xFFFFFF, false);
            
            y += 25;
        }
    }
    
    private void renderElementControls(DrawContext context, int mouseX, int mouseY) {
        int startX = 220;
        int startY = 55;
        int width = this.width - 230;
        
        context.fill(startX, startY, startX + width, startY + 200, 0x80111111);
        
        if (currentElement != null) {
            context.drawText(this.textRenderer, "Selected: " + currentElement.id, startX + 5, startY + 5, 0xFF00d4ff, false);
            
            int controlY = startY + 25;
            context.drawText(this.textRenderer, "Position: " + (int)currentElement.x + ", " + (int)currentElement.y, startX + 5, controlY, 0xFFe0e0e0, false);
            controlY += 15;
            context.drawText(this.textRenderer, "Size: " + currentElement.width + "x" + currentElement.height, startX + 5, controlY, 0xFFe0e0e0, false);
            
            // Size control buttons
            controlY += 25;
            this.drawButton(context, startX + 5, controlY, 45, 20, "50%");
            this.drawButton(context, startX + 55, controlY, 45, 20, "75%");
            this.drawButton(context, startX + 105, controlY, 45, 20, "100%");
            this.drawButton(context, startX + 155, controlY, 45, 20, "150%");
            
            controlY += 25;
            this.drawButton(context, startX + 5, controlY, 50, 20, "-W");
            this.drawButton(context, startX + 60, controlY, 50, 20, "+W");
            this.drawButton(context, startX + 115, controlY, 50, 20, "-H");
            this.drawButton(context, startX + 170, controlY, 50, 20, "+H");
        }
    }
    
    private void renderModuleToggles(DrawContext context, int mouseX, int mouseY) {
        context.fill(10, 55, this.width - 10, this.height - 40, 0x80111111);
        context.drawText(this.textRenderer, "Module Toggles:", 15, 60, 0xFF00d4ff, false);
        
        int y = 80;
        
        // UI Modules
        this.drawToggleButton(context, 15, y, 300, 20, "Keystroke Display", Keystroke.enabled.getValue());
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "FPS Display", FPS.enabled.getValue());
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Mouse Move", BATclient_Config.mouseMoveEnabled);
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Potions HUD", BATclient_Config.hudBetterPotionsHudEnabled);
        
        y += 30;
        
        // Combat/Visual Modules
        this.drawToggleButton(context, 15, y, 300, 20, "Target HUD", BATclient_Config.targetHudEnabled);
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Target Render", BATclient_Config.targetRenderEnabled);
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Hit Bubbles (Attack Circle)", BATclient_Config.hitBubblesEnabled);
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Hit Sound (Click Sound)", BATclient_Config.hitSoundEnabled);
        y += 25;
        this.drawToggleButton(context, 15, y, 300, 20, "Swing Hand Animation", BATclient_Config.swingHandEnabled);
    }
    
    private void renderSettings(DrawContext context, int mouseX, int mouseY) {
        context.fill(10, 55, this.width - 10, this.height - 40, 0x80111111);
        context.drawText(this.textRenderer, "Module Configuration:", 15, 60, 0xFF00d4ff, false);
        
        int y = 80;
        context.drawText(this.textRenderer, "Swing Hand Speed: " + BATclient_Config.swingHand_speed, 15, y, 0xFFe0e0e0, false);
        y += 25;
        context.drawText(this.textRenderer, "Swing Hand Scale: " + String.format("%.2f", BATclient_Config.swingHand_scale), 15, y, 0xFFe0e0e0, false);
        y += 30;
        
        this.drawButton(context, 15, y, 120, 20, "Reset All");
        this.drawButton(context, 140, y, 120, 20, "Save Config");
    }
    
    private void drawToggleButton(DrawContext context, int x, int y, int width, int height, String label, boolean enabled) {
        int bgColor = enabled ? 0xFF00AA33 : 0xFFAA0000;
        context.fill(x, y, x + width, y + height, bgColor);
        
        String status = enabled ? "[ON]" : "[OFF]";
        String fullLabel = label + " " + status;
        int textX = x + 5;
        if (this.textRenderer.getWidth(fullLabel) > width - 10) {
            context.drawText(this.textRenderer, label, textX, y + 6, 0xFFFFFF, false);
            context.drawText(this.textRenderer, status, x + width - this.textRenderer.getWidth(status) - 5, y + 6, 0xFFFFFF, false);
        } else {
            context.drawText(this.textRenderer, fullLabel, textX, y + 6, 0xFFFFFF, false);
        }
    }
    
    private void drawButton(DrawContext context, int x, int y, int width, int height, String label) {
        context.fill(x, y, x + width, y + height, 0xFF0066CC);
        int textX = x + (width - this.textRenderer.getWidth(label)) / 2;
        context.drawText(this.textRenderer, label, textX, y + 6, 0xFFFFFF, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (int)mouseX;
        int y = (int)mouseY;
        
        // Tab buttons
        for (int i = 0; i < 3; i++) {
            int tabX = 10 + (i * 135);
            if (x >= tabX && x <= tabX + 130 && y >= 30 && y <= 50) {
                tabIndex = i;
                return true;
            }
        }
        
        // Close button
        if (x >= 10 && x <= 110 && y >= this.height - 30 && y <= this.height - 10) {
            this.close();
            return true;
        }
        
        if (tabIndex == 0) {
            return handleElementTab(x, y);
        } else if (tabIndex == 1) {
            return handleModuleTab(x, y);
        } else if (tabIndex == 2) {
            return handleSettingsTab(x, y);
        }
        
        return true;
    }
    
    private boolean handleElementTab(int x, int y) {
        // Element selection
        int elemY = 75;
        for (UIElementManager.UIElement elem : UIElementManager.getAllElements()) {
            if (x >= 15 && x <= 195 && y >= elemY && y <= elemY + 20) {
                currentElement = elem;
                
                if (elem.isMouseOver(x, y)) {
                    if (elem.isResizeHandleOver(x, y)) {
                        elem.startResize(x, y);
                        draggingElement = elem;
                    } else {
                        elem.startDrag(x, y);
                        draggingElement = elem;
                    }
                }
                return true;
            }
            elemY += 25;
        }
        
        // Size control buttons
        if (currentElement != null) {
            int startX = 220;
            int controlY = 105;
            
            if (x >= startX + 5 && x <= startX + 50 && y >= controlY && y <= controlY + 20) {
                UIElementManager.scaleElement(currentElement.id, 0.5f);
                return true;
            }
            if (x >= startX + 55 && x <= startX + 100 && y >= controlY && y <= controlY + 20) {
                UIElementManager.scaleElement(currentElement.id, 0.75f);
                return true;
            }
            if (x >= startX + 105 && x <= startX + 150 && y >= controlY && y <= controlY + 20) {
                UIElementManager.scaleElement(currentElement.id, 1.0f);
                return true;
            }
            if (x >= startX + 155 && x <= startX + 200 && y >= controlY && y <= controlY + 20) {
                UIElementManager.scaleElement(currentElement.id, 1.5f);
                return true;
            }
            
            controlY += 25;
            if (x >= startX + 5 && x <= startX + 55 && y >= controlY && y <= controlY + 20) {
                currentElement.width = Math.max(30, currentElement.width - 10);
                return true;
            }
            if (x >= startX + 60 && x <= startX + 110 && y >= controlY && y <= controlY + 20) {
                currentElement.width = currentElement.width + 10;
                return true;
            }
            if (x >= startX + 115 && x <= startX + 165 && y >= controlY && y <= controlY + 20) {
                currentElement.height = Math.max(20, currentElement.height - 10);
                return true;
            }
            if (x >= startX + 170 && x <= startX + 220 && y >= controlY && y <= controlY + 20) {
                currentElement.height = currentElement.height + 10;
                return true;
            }
        }
        
        return false;
    }
    
    private boolean handleModuleTab(int x, int y) {
        int toggleY = 80;
        int toggleWidth = 300;
        
        // Keystroke
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            Keystroke.enabled.setValue(!Keystroke.enabled.getValue());
            return true;
        }
        
        // FPS
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            FPS.enabled.setValue(!FPS.enabled.getValue());
            return true;
        }
        
        // Mouse Move toggle
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.mouseMoveEnabled = !BATclient_Config.mouseMoveEnabled;
            return true;
        }
        
        // Potions HUD
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.hudBetterPotionsHudEnabled = !BATclient_Config.hudBetterPotionsHudEnabled;
            return true;
        }
        
        toggleY += 30;
        
        // Target HUD
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.targetHudEnabled = !BATclient_Config.targetHudEnabled;
            return true;
        }
        
        // Target Render
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.targetRenderEnabled = !BATclient_Config.targetRenderEnabled;
            return true;
        }
        
        // Hit Bubbles
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.hitBubblesEnabled = !BATclient_Config.hitBubblesEnabled;
            return true;
        }
        
        // Hit Sound
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.hitSoundEnabled = !BATclient_Config.hitSoundEnabled;
            return true;
        }
        
        // Swing Hand
        toggleY += 25;
        if (x >= 15 && x <= 15 + toggleWidth && y >= toggleY && y <= toggleY + 20) {
            BATclient_Config.swingHandEnabled = !BATclient_Config.swingHandEnabled;
            return true;
        }
        
        return false;
    }
    
    private boolean handleSettingsTab(int x, int y) {
        int btnY = 130;
        
        // Reset All
        if (x >= 15 && x <= 135 && y >= btnY && y <= btnY + 20) {
            UIElementManager.resetToDefaults();
            return true;
        }
        
        // Save Config
        if (x >= 140 && x <= 260 && y >= btnY && y <= btnY + 20) {
            UIElementManager.savePositions();
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingElement != null) {
            if (draggingElement.resizing) {
                draggingElement.updateResize((int)mouseX, (int)mouseY);
            } else if (draggingElement.dragging) {
                draggingElement.updateDrag((int)mouseX, (int)mouseY);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingElement != null) {
            draggingElement.stopDrag();
            draggingElement.stopResize();
            draggingElement = null;
            UIElementManager.savePositions();
            return true;
        }
        return false;
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
