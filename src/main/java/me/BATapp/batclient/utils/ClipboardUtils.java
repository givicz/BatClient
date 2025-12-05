package me.BATapp.batclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClipboardUtils {
    
    public static void copyToClipboard(String text) {
        MinecraftClient.getInstance().keyboard.setClipboard(text);
    }
    
    public static String getFromClipboard() {
        return MinecraftClient.getInstance().keyboard.getClipboard();
    }
}
