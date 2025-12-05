package me.BATapp.batclient.gui.settings;

import me.BATapp.batclient.settings.impl.ButtonSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ButtonComponent extends SettingComponent<Void> {
    private boolean isHovering = false;

    public ButtonComponent(ButtonSetting setting, int x, int y, int width, int height) {
        super(setting, x, y, width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Determine if button is hovered
        isHovering = isHovered(mouseX, mouseY);
        
        // Draw button with hover effect
        int bgColor = isHovering ? 0xFF6666FF : 0xFF5555FF;
        context.fill(x, y, x + width, y + height, bgColor);
        
        // Draw border
        context.fill(x, y, x + width, y + 1, 0xFF888888);
        context.fill(x + width - 1, y, x + width, y + height, 0xFF888888);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF888888);
        context.fill(x, y, x + 1, y + height, 0xFF888888);
        
        // Draw button text (just a simple label)
        String buttonText = "Press";
        int textX = x + (width - textRenderer.getWidth(buttonText)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawText(textRenderer, Text.literal(buttonText), textX, textY, 0xFFFFFFFF, false);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            ((ButtonSetting) setting).click();
        }
        // Also call parent for reset on right-click
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {}

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
