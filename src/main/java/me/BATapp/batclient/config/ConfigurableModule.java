package me.BATapp.batclient.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public abstract class ConfigurableModule {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final BATclient_Config CONFIG = new BATclient_Config();

    public static void saveConfig() {
        // Configuration saved automatically via ConfigManager/ConfigSaver
        ConfigManager.saveConfig();
    }

    public static void saveAll(Screen screen) {
        // Configuration saved automatically via ConfigManager/ConfigSaver
        ConfigManager.saveConfig();
    }
}

