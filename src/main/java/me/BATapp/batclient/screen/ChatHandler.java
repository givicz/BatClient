package me.BATapp.batclient.screen;

import me.BATapp.batclient.utils.ClipboardUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

public class ChatHandler {
    
    public static void register() {
        // Chat handlers are registered via mixins now
        // This class is kept for utility functions
    }
    
    public static void copyMessage(String message) {
        if (message != null && !message.isEmpty()) {
            ClipboardUtils.copyToClipboard(message);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                    Text.literal("✓ Zpráva zkopírována!")
            );
        }
    }
}
