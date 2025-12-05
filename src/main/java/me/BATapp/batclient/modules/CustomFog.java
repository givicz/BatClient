package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.EnumSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import me.BATapp.batclient.utils.Palette;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;

import java.awt.*;

public class CustomFog extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable custom fog", false);
    public static final BooleanSetting thick = new BooleanSetting("Thick", "Thick fog mode", false);
    public static final EnumSetting<CustomFog.CustomFogShape> shape = new EnumSetting<>("Shape", "Fog shape", CustomFogShape.SPHERE, CustomFogShape.class);
    public static final SliderSetting density = new SliderSetting("Density", "Fog density (0-100)", 80, 1, 100, 1);
    public static final SliderSetting start = new SliderSetting("Start", "Fog start distance", 5, 1, 100, 1);
    public static final SliderSetting end = new SliderSetting("End", "Fog end distance", 20, 1, 100, 1);
    public static final me.BATapp.batclient.settings.impl.ColorSetting color = new me.BATapp.batclient.settings.impl.ColorSetting("Color", "Fog color", 0xFF7F7F7F);

    public CustomFog() {
        super("Custom Fog", Category.WORLD);
    }

    public static Fog getCustomFog() {
        int col = color.getValue();
        float r = ((col >> 16) & 0xFF) / 255f;
        float g = ((col >> 8) & 0xFF) / 255f;
        float b = (col & 0xFF) / 255f;
        float a = density.getValue() / 100f;

        return (new Fog(start.getValue(), end.getValue(), getCustomFogShape(), r, g, b, a));
    }

    private static FogShape getCustomFogShape() {
        return switch (shape.getValue()) {
            case SPHERE -> FogShape.SPHERE;
            case CYLINDER -> FogShape.CYLINDER;
        };
    }

    public enum CustomFogShape {
        SPHERE, CYLINDER
    }
}
