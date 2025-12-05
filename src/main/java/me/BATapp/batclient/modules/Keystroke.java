package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ColorSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import me.BATapp.batclient.utils.UIElementManager;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class Keystroke extends SoupModule {

    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "description", false);
    public static final BooleanSetting showMouse = new BooleanSetting("Show Mouse", "description", false);
    public static final ColorSetting colorNormal = new ColorSetting("Color Normal", "description", 0x80000000);
    public static final ColorSetting colorPressed = new ColorSetting("Color Pressed", "description", 0xFFFFFFFF);
    public static final ColorSetting textColor = new ColorSetting("Text Color", "description", 0xFFFFFFFF);
    public static final SliderSetting scale = new SliderSetting("Scale", "description", 100, 50, 200, 1);

    private static final int KEY_W = GLFW.GLFW_KEY_W;
    private static final int KEY_A = GLFW.GLFW_KEY_A;
    private static final int KEY_S = GLFW.GLFW_KEY_S;
    private static final int KEY_D = GLFW.GLFW_KEY_D;
    private static final int KEY_SPACE = GLFW.GLFW_KEY_SPACE;
    private static final int KEY_SHIFT = GLFW.GLFW_KEY_LEFT_SHIFT;
    private static final int KEY_LMOUSE = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    private static final int KEY_RMOUSE = GLFW.GLFW_MOUSE_BUTTON_RIGHT;

    public Keystroke() {
        super("Keystroke", Category.HUD);
    }

    public static void render(DrawContext context) {
        if (!enabled.getValue() || mc.player == null) return;

        float scaleF = scale.getValue() / 100f;
        int colorNorm = colorNormal.getValue();
        int colorPress = colorPressed.getValue();
        int textCol = textColor.getValue();

        // Get position from UIElementManager
        UIElementManager.UIElement keystrokeElement = UIElementManager.getElement("keystroke_display");
        int baseX = keystrokeElement != null ? (int)keystrokeElement.x : 10;
        int baseY = keystrokeElement != null ? (int)keystrokeElement.y : 10;
        float elemScale = keystrokeElement != null ? (keystrokeElement.width / 100f) : scaleF;

        int boxWidth = (int) (18 * elemScale);
        int boxHeight = (int) (18 * elemScale);
        int spacing = (int) (2 * elemScale);

        // Check key states
        boolean wPressed = isKeyPressed(KEY_W);
        boolean aPressed = isKeyPressed(KEY_A);
        boolean sPressed = isKeyPressed(KEY_S);
        boolean dPressed = isKeyPressed(KEY_D);
        boolean spacePressed = isKeyPressed(KEY_SPACE);
        boolean shiftPressed = isKeyPressed(KEY_SHIFT);
        boolean lmbPressed = showMouse.getValue() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), KEY_LMOUSE) == GLFW.GLFW_PRESS;
        boolean rmbPressed = showMouse.getValue() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), KEY_RMOUSE) == GLFW.GLFW_PRESS;

        // WASD Layout (3x3 grid)
        // _W_
        // ASD
        // ___
        
        int x = baseX;
        int y = baseY;
        
        // W - top center
        drawKeyBox(context, x + boxWidth + spacing, y, boxWidth, boxHeight, "W", wPressed ? colorPress : colorNorm, textCol);
        
        // A - middle left
        drawKeyBox(context, x, y + boxHeight + spacing, boxWidth, boxHeight, "A", aPressed ? colorPress : colorNorm, textCol);
        
        // S - middle center
        drawKeyBox(context, x + boxWidth + spacing, y + boxHeight + spacing, boxWidth, boxHeight, "S", sPressed ? colorPress : colorNorm, textCol);
        
        // D - middle right
        drawKeyBox(context, x + (boxWidth + spacing) * 2, y + boxHeight + spacing, boxWidth, boxHeight, "D", dPressed ? colorPress : colorNorm, textCol);

        // Space - below (full width)
        int spaceWidth = (boxWidth + spacing) * 3 - spacing;
        drawKeyBox(context, x, y + (boxHeight + spacing) * 2, spaceWidth, boxHeight, "SPACE", spacePressed ? colorPress : colorNorm, textCol);

        // Shift - optional row
        drawKeyBox(context, x, y + (boxHeight + spacing) * 3, (int) (spaceWidth * 0.5f), boxHeight, "SHIFT", shiftPressed ? colorPress : colorNorm, textCol);

        // Mouse buttons if enabled
        if (showMouse.getValue()) {
            int mouseY = y;
            drawKeyBox(context, x + spaceWidth + spacing + 5, mouseY, boxWidth, boxHeight, "LMB", lmbPressed ? colorPress : colorNorm, textCol);
            drawKeyBox(context, x + spaceWidth + spacing + boxWidth + spacing + 10, mouseY, boxWidth, boxHeight, "RMB", rmbPressed ? colorPress : colorNorm, textCol);
        }
    }

    private static void drawKeyBox(DrawContext context, int x, int y, int width, int height, String text, int bgColor, int textColor) {
        // Draw background
        context.fill(x, y, x + width, y + height, bgColor);
        
        // Draw border
        context.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        context.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        context.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
        context.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF);

        // Draw text centered
        int textWidth = mc.textRenderer.getWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;
        context.drawText(mc.textRenderer, text, textX, textY, textColor, false);
    }

    private static boolean isKeyPressed(int keyCode) {
        try {
            long window = mc.getWindow().getHandle();
            return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
        } catch (Exception e) {
            return false;
        }
    }
}
