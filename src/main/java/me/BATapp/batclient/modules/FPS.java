package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ColorSetting;
import me.BATapp.batclient.utils.UIElementManager;
import net.minecraft.client.gui.DrawContext;

public class FPS extends SoupModule {

    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Display FPS counter", false);
    public static final ColorSetting colorText = new ColorSetting("Text Color", "FPS text color", 0xFFFFFFFF);
    public static final ColorSetting colorBackground = new ColorSetting("Background Color", "FPS background color", 0x80000000);

    private static long lastTime = System.currentTimeMillis();
    private static int frameCount = 0;
    private static int fps = 0;

    public FPS() {
        super("FPS", Category.HUD);
    }

    public static void render(DrawContext context) {
        if (!enabled.getValue() || mc.player == null) return;

        // Calculate FPS
        long currentTime = System.currentTimeMillis();
        frameCount++;
        if (currentTime - lastTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = currentTime;
        }

        String fpsText = fps + " FPS";
        UIElementManager.UIElement fpsElement = UIElementManager.getElement("fps_display");

        int x = fpsElement != null ? (int)fpsElement.x : 10;
        int y = fpsElement != null ? (int)fpsElement.y : 30;
        int width = fpsElement != null ? fpsElement.width : 60;
        int height = fpsElement != null ? fpsElement.height : 20;

        // Draw background
        context.fill(x, y, x + width, y + height, colorBackground.getValue());

        // Draw FPS text
        int textX = x + (width - mc.textRenderer.getWidth(fpsText)) / 2;
        int textY = y + (height - mc.textRenderer.fontHeight) / 2;
        context.drawText(mc.textRenderer, fpsText, textX, textY, colorText.getValue(), false);

        // Draw resize handle
        context.fill(x + width - 10, y + height - 10, x + width, y + height, 0xFF00d4ff);
    }
}
