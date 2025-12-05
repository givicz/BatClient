package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.utils.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;

public class FastQuit extends SoupModule {
    
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable fast quit", false);
    
    public FastQuit() {
        super("Fast Quit", Category.OTHER);
    }
    
    public static void onTick() {
        if (!enabled.getValue()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) return;
        
        // Check if keybind is pressed
        if (KeyBindingRegistry.isKeyPressed(KeyBindingRegistry.FAST_QUIT_KEY)) {
            quit();
        }
    }
    
    private static void quit() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.scheduleStop();
    }
}
