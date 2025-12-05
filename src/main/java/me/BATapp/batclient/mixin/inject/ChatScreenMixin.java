package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.utils.ClipboardUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    
    @Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
    public void onChatMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        // Right click (button 1) to copy chat messages
        if (button == 1) {
            try {
                ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
                
                // Calculate which line was clicked based on mouse Y position
                int lineHeight = 9;
                int yStart = MinecraftClient.getInstance().getWindow().getHeight() - 40;
                int clickedLine = (int) ((yStart - mouseY) / lineHeight);
                
                if (clickedLine >= 0 && clickedLine < 100) {
                    // Use reflection to access messages from ChatHud since getMessage is not public in 1.21.4
                    java.lang.reflect.Field messagesField = ChatHud.class.getDeclaredField("messages");
                    messagesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.List<Text> messages = (java.util.List<Text>) messagesField.get(chatHud);
                    
                    if (messages != null && clickedLine < messages.size()) {
                        Text messageText = messages.get(clickedLine);
                        if (messageText != null) {
                            String message = messageText.getString();
                            if (!message.isEmpty()) {
                                ClipboardUtils.copyToClipboard(message);
                                cir.setReturnValue(true);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

