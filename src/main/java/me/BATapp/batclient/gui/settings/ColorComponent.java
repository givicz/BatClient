package me.BATapp.batclient.gui.settings;

import me.BATapp.batclient.settings.impl.ColorSetting;
import me.BATapp.batclient.gui.RGBColorPickerScreen;
import me.BATapp.batclient.config.ConfigManager;
import me.BATapp.batclient.config.ConfigSaver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ColorComponent extends SettingComponent<Integer> {
    private static final int BOX_WIDTH = 60;
    private static final int BOX_HEIGHT = 16;
    private static final int SWATCH_SIZE = 14;

    public ColorComponent(ColorSetting setting, int x, int y, int width, int height) {
        super(setting, x, y, width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ColorSetting colorSetting = (ColorSetting) setting;
        int color = colorSetting.getValue();

        // Render background box
        context.fill(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, 0xFF2a2a3e);

        // Render color swatch
        context.fill(x + 3, y + 1, x + 3 + SWATCH_SIZE, y + 1 + SWATCH_SIZE, color);

        // Render border around swatch
        context.fill(x + 2, y, x + 3 + SWATCH_SIZE, y + 1, 0xFF444444);
        context.fill(x + 2, y + 1 + SWATCH_SIZE, x + 3 + SWATCH_SIZE, y + 2 + SWATCH_SIZE, 0xFF444444);
        context.fill(x + 2, y, x + 3, y + 2 + SWATCH_SIZE, 0xFF444444);
        context.fill(x + 2 + SWATCH_SIZE, y, x + 3 + SWATCH_SIZE, y + 2 + SWATCH_SIZE, 0xFF444444);

        // Render hex value
        String hex = String.format("%08X", color).substring(2);
        context.drawText(textRenderer, hex, x + 20, y + 4, 0xFFe0e0e0, false);

        // Highlight on hover
        if (mouseX >= x && mouseX <= x + BOX_WIDTH && mouseY >= y && mouseY <= y + BOX_HEIGHT) {
            context.fill(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, 0x4400d4ff);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 1) {
                setting.reset();
            } else if (button == 0) {
                // Open the RGB color picker screen and apply the result
                ColorSetting cs = (ColorSetting) setting;
                int initial = cs.getValue();
                MinecraftClient client = MinecraftClient.getInstance();
                client.setScreen(new RGBColorPickerScreen(client.currentScreen, initial, (newColor) -> {
                    cs.setColor(newColor);
                    try {
                        ConfigManager.saveConfig();
                        ConfigSaver.saveAll();
                    } catch (Throwable ignored) {}
                }));
            }
        }
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
}
