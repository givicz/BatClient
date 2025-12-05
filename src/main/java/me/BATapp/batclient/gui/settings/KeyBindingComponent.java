package me.BATapp.batclient.gui.settings;

import me.BATapp.batclient.settings.impl.KeyBindingSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class KeyBindingComponent extends SettingComponent<Integer> {
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 16;
    private boolean waiting = false;

    public KeyBindingComponent(KeyBindingSetting setting, int x, int y, int width, int height) {
        super(setting, x, y, width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        KeyBindingSetting keySetting = (KeyBindingSetting) setting;
        int keyCode = keySetting.getValue();
        String keyName = InputUtil.fromKeyCode(keyCode, 0).getLocalizedText().getString();

        // Render button background
        int bgColor = waiting ? 0xFF00d4ff : 0xFF2a2a3e;
        context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, bgColor);

        // Render border
        int borderColor = waiting ? 0xFF00d4ff : 0xFF444444;
        context.fill(x, y, x + BUTTON_WIDTH, y + 1, borderColor);
        context.fill(x, y + BUTTON_HEIGHT - 1, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, borderColor);
        context.fill(x, y, x + 1, y + BUTTON_HEIGHT, borderColor);
        context.fill(x + BUTTON_WIDTH - 1, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, borderColor);

        // Render text
        String displayText = waiting ? "Press key..." : keyName;
        MinecraftClient mc = MinecraftClient.getInstance();
        int textWidth = mc.textRenderer.getWidth(displayText);
        int textX = x + (BUTTON_WIDTH - textWidth) / 2;
        int textY = y + (BUTTON_HEIGHT - 8) / 2;
        context.drawText(textRenderer, displayText, textX, textY, 0xFFe0e0e0, false);

        // Highlight on hover
        if (!waiting && mouseX >= x && mouseX <= x + BUTTON_WIDTH && mouseY >= y && mouseY <= y + BUTTON_HEIGHT) {
            context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, 0x4400d4ff);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 1) {
                setting.reset();
            } else if (button == 0) {
                waiting = true;
                MinecraftClient.getInstance().setScreen(new KeyBindingListenerScreen(this, (KeyBindingSetting) setting));
            }
        }
    }

    public void setKeyCode(int keyCode) {
        ((KeyBindingSetting) setting).setValue(keyCode);
        waiting = false;
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    /**
     * Screen for listening to key input
     */
    public static class KeyBindingListenerScreen extends Screen {
        private final KeyBindingComponent component;
        private final KeyBindingSetting setting;

        public KeyBindingListenerScreen(KeyBindingComponent component, KeyBindingSetting setting) {
            super(Text.literal("Press any key..."));
            this.component = component;
            this.setting = setting;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 256) { // ESC key - cancel
                MinecraftClient.getInstance().setScreen(null);
                component.waiting = false;
                return true;
            }

            // Set the new key binding
            component.setKeyCode(keyCode);
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            // Semi-transparent dark background
            context.fill(0, 0, this.width, this.height, 0xC0101010);
            
            // Draw text
            int textY1 = this.height / 2 - 20;
            int textWidth = this.textRenderer.getWidth(this.title);
            context.drawText(this.textRenderer, this.title, (this.width - textWidth) / 2, textY1, 0xFFFFFF, false);
            
            String cancelText = "Press ESC to cancel";
            int cancelWidth = this.textRenderer.getWidth(cancelText);
            context.drawText(this.textRenderer, cancelText, (this.width - cancelWidth) / 2, this.height / 2, 0xAAAAAA, false);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}
