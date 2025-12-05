package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, String description, int defaultColor) {
        super(name, description, defaultColor);
    }

    /**
     * Get color as ARGB integer
     */
    public int getColor() {
        return getValue();
    }

    /**
     * Set color as ARGB integer
     */
    public void setColor(int color) {
        setValue(color);
    }

    /**
     * Extract alpha component (0-255)
     */
    public int getAlpha() {
        return (getValue() >> 24) & 0xFF;
    }

    /**
     * Extract red component (0-255)
     */
    public int getRed() {
        return (getValue() >> 16) & 0xFF;
    }

    /**
     * Extract green component (0-255)
     */
    public int getGreen() {
        return (getValue() >> 8) & 0xFF;
    }

    /**
     * Extract blue component (0-255)
     */
    public int getBlue() {
        return getValue() & 0xFF;
    }

    /**
     * Create color from ARGB components
     */
    public static int fromARGB(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
