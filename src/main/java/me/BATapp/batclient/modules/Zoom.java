package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.*;
import me.BATapp.batclient.utils.MathUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;

public class Zoom extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable zoom", false);
    public static final KeyBindingSetting zoomKey = new KeyBindingSetting("Zoom Key", "Key to hold for zoom", 90); // 90 = 'Z' key

    // OPRAVENO: Přidáno 'f' ke všem číselným hodnotám pro konverzi na float
    public static final SliderSetting zoomLevel = new SliderSetting("Zoom Level", "Zoom multiplier", 3.0f, 1.0f, 10.0f, 0.1f);
    // OPRAVENO: Přidáno 'f' ke všem číselným hodnotám pro konverzi na float
    public static final SliderSetting smoothness = new SliderSetting("Smoothness", "Zoom smoothness", 0.15f, 0.01f, 1.0f, 0.01f);
    public static final ColorSetting accentColor = new ColorSetting("Accent Color", "Zoom accent color", 0xFF00d4ff);

    private static double currentZoom = 1.0;
    private static double targetZoom = 1.0;

    public Zoom() {
        super("Zoom", Category.OTHER);
    }

    public static void onTick() {
        if (mc.player == null || mc.gameRenderer == null) return;

        if (!enabled.getValue()) {
            targetZoom = 1.0;
            currentZoom = MathHelper.lerp(smoothness.getValue(), currentZoom, targetZoom);
            return;
        }

        // Zoom je zapnutý - kontroluj klávesnici
        boolean isZooming = zoomKey.getKeyBinding() != null && zoomKey.isPressed();
        targetZoom = isZooming ? zoomLevel.getValue() : 1.0;

        // Smooth interpolation
        double smooth = smoothness.getValue();
        currentZoom = MathHelper.lerp(smooth, currentZoom, targetZoom);
    }

    public static double getZoomLevel() {
        return enabled.getValue() ? currentZoom : 1.0;
    }

    // Zoom is applied via projection matrix scaling in GameRendererMixin
}