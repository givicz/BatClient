package me.BATapp.batclient.utils;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBindingRegistry {
    
    // Freecam & Freelook
    public static final int FREECAM_KEY = GLFW.GLFW_KEY_F;
    public static final int FREELOOK_KEY = GLFW.GLFW_KEY_V;
    
    // Fast Leave & Fast Quit
    public static final int FAST_LEAVE_KEY = GLFW.GLFW_KEY_X;
    public static final int FAST_QUIT_KEY = GLFW.GLFW_KEY_Q;
    
    // Other
    public static final int ZOOM_KEY = GLFW.GLFW_KEY_Z;
    
    public static boolean isKeyPressed(int keyCode) {
        long window = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
        if (window == 0) return false;
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }
}
