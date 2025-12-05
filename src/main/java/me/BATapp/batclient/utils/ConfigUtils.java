package me.BATapp.batclient.utils;

public class ConfigUtils {
    public static int floatPercentToHexInt(float percent) {
        return Math.round(percent * 255);
    }

    public static int intPercentToHexInt(int percent) {
        return Math.round(percent / 100f * 255);
    }
}
