package me.BATapp.batclient.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages fade in/out animations for screens
 */
public class ScreenAnimationManager {
    
    private static final Map<Class<?>, Float> screenAlpha = new HashMap<>();
    private static final float FADE_SPEED = 0.08f;
    
    /**
     * Get alpha for a screen (0.0 to 1.0)
     */
    public static float getAlpha(Class<?> screenClass) {
        return screenAlpha.getOrDefault(screenClass, 1.0f);
    }
    
    /**
     * Set initial alpha when screen opens
     */
    public static void onScreenOpen(Class<?> screenClass) {
        screenAlpha.put(screenClass, 0.0f);
    }
    
    /**
     * Update animation each tick
     */
    public static void updateAnimation(Class<?> screenClass) {
        float current = screenAlpha.getOrDefault(screenClass, 1.0f);
        float target = 1.0f;
        float updated = current + (target - current) * FADE_SPEED;
        screenAlpha.put(screenClass, updated);
    }
    
    /**
     * Reset when screen closes
     */
    public static void onScreenClose(Class<?> screenClass) {
        screenAlpha.remove(screenClass);
    }
    
    /**
     * Apply alpha to color (ARGB format)
     */
    public static int applyAlpha(int color, float alpha) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
