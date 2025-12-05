package me.BATapp.batclient.utils;

import me.BATapp.batclient.config.BATclient_Config;
import me.BATapp.batclient.config.BATclient_ConfigEnums;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.render.RenderWithAnimatedColor;

import java.awt.*;
import java.util.Random;

import static me.BATapp.batclient.config.ConfigurableModule.CONFIG;

public class Palette {
    private static final Random random = new Random();

    public static int getRawColor1() {
        return BATclient_Config.c1;
    }

    public static int getRawColor2() {
        return BATclient_Config.c2;
    }

    public static int getRawColor3() {
        return BATclient_Config.c3;
    }

    public static int getRawColor4() {
        return BATclient_Config.c4;
    }

    public static int getColorsCount() {
        return switch (getStyle()) {
            case SOLO -> 1;
            case DUO -> 2;
            case TRIO -> 3;
            case QUARTET -> 4;
        };
    }

    public static Color getInterpolatedPaletteColor(float progress) {
        progress = progress % 1f;
        if (progress < 0f) progress += 1f;

        BATclient_ConfigEnums.PaletteStyle style = BATclient_Config.paletteStyle;
        int c1 = BATclient_Config.c1;
        int c2 = BATclient_Config.c2;
        int c3 = BATclient_Config.c3;
        int c4 = BATclient_Config.c4;

        switch (style) {
            case SOLO:
                return new Color(c1);
            case DUO:
                return OklabUtils.interpolate(new Color(c1), new Color(c2), progress);
            case TRIO:
                if (progress < 0.5f) {
                    return OklabUtils.interpolate(new Color(c1), new Color(c2), progress * 2f);
                } else {
                    return OklabUtils.interpolate(new Color(c2), new Color(c3), (progress - 0.5f) * 2f);
                }
            case QUARTET:
                if (progress < 1f / 3f) {
                    return OklabUtils.interpolate(new Color(c1), new Color(c2), progress * 3f);
                } else if (progress < 2f / 3f) {
                    return OklabUtils.interpolate(new Color(c2), new Color(c3), (progress - 1f / 3f) * 3f);
                } else {
                    return OklabUtils.interpolate(new Color(c3), new Color(c4), (progress - 2f / 3f) * 3f);
                }
            default:
                return new Color(c1); // fallback
        }
    }

    public static Color getBackColor() {
        int alpha = (int) (255 * (CONFIG.backAlpha / 100f));
        return Render2D.injectAlpha(new Color(CONFIG.backColor), alpha);
    }

    public static ColorsStyle getStyle() {
        // Convert enum to ColorsStyle
        return ColorsStyle.valueOf(BATclient_Config.paletteStyle.name());
    }

    public static Color getColor(float position) {
        ColorsStyle style = getStyle();

        return switch (style) {
            case SOLO -> new Color(getRawColor1());
            case DUO -> OklabUtils.interpolate(new Color(getRawColor1()), new Color(getRawColor2()), position);
            case TRIO -> interpolate3(position, new Color(getRawColor1()), new Color(getRawColor2()), new Color(getRawColor3()));
            case QUARTET -> interpolate4(position, new Color(getRawColor1()), new Color(getRawColor2()), new Color(getRawColor3()), new Color(getRawColor4()));
        };
    }

    private static Color interpolate3(float t, Color c1, Color c2, Color c3) {
        if (t < 0.5f) {
            return OklabUtils.interpolate(c1, c2, t * 2f);
        } else {
            return OklabUtils.interpolate(c2, c3, (t - 0.5f) * 2f);
        }
    }

    private static Color interpolate4(float t, Color c1, Color c2, Color c3, Color c4) {
        if (t < 1f / 3f) {
            return OklabUtils.interpolate(c1, c2, t * 3f);
        } else if (t < 2f / 3f) {
            return OklabUtils.interpolate(c2, c3, (t - 1f / 3f) * 3f);
        } else {
            return OklabUtils.interpolate(c3, c4, (t - 2f / 3f) * 3f);
        }
    }

    public static Color getRandomColor() {
        int steps = 20;
        int index = random.nextInt(steps);
        float position = index / (float) (steps - 1); // от 0 до 1 включительно

        return getColor(position);
    }

    public static int getTextColor() {
        return CONFIG.textColor + 0xFF000000;
    }

    public enum ColorsStyle {
        SOLO, DUO, TRIO, QUARTET
    }
}


