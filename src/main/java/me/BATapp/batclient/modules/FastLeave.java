package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.utils.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;

public class FastLeave extends SoupModule {
    
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable fast leave", false);
    
    public FastLeave() {
        super("Fast Leave", Category.OTHER);
    }
    
    public static void onTick() {
        if (!enabled.getValue()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        
        // Check if keybind is pressed
        if (KeyBindingRegistry.isKeyPressed(KeyBindingRegistry.FAST_LEAVE_KEY)) {
            disconnect();
        }
    }
    
    private static void disconnect() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().onDisconnect(null);
            mc.disconnect();
        }
    }
}
