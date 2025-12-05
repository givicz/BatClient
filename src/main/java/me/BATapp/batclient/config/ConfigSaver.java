package me.BATapp.batclient.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigSaver {
    
    private static final Path CONFIG_DIR = Paths.get("BatClient");
    private static final Path SETTINGS_FILE = CONFIG_DIR.resolve("settings.properties");
    private static final Path POSITIONS_FILE = CONFIG_DIR.resolve("positions.properties");
    
    private static final Properties settingsProps = new Properties();
    private static final Properties positionsProps = new Properties();
    
    static {
        loadConfigs();
    }
    
    private static void loadConfigs() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Load settings
        if (Files.exists(SETTINGS_FILE)) {
            try (InputStream in = Files.newInputStream(SETTINGS_FILE)) {
                settingsProps.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Load positions
        if (Files.exists(POSITIONS_FILE)) {
            try (InputStream in = Files.newInputStream(POSITIONS_FILE)) {
                positionsProps.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void saveSetting(String key, String value) {
        settingsProps.setProperty(key, value);
        saveSettings();
    }
    
    public static String getSetting(String key, String defaultValue) {
        return settingsProps.getProperty(key, defaultValue);
    }
    
    public static void savePosition(String key, float x, float y) {
        positionsProps.setProperty(key + ".x", String.valueOf(x));
        positionsProps.setProperty(key + ".y", String.valueOf(y));
        savePositions();
    }
    
    public static float getPositionX(String key, float defaultX) {
        try {
            return Float.parseFloat(positionsProps.getProperty(key + ".x", String.valueOf(defaultX)));
        } catch (NumberFormatException e) {
            return defaultX;
        }
    }
    
    public static float getPositionY(String key, float defaultY) {
        try {
            return Float.parseFloat(positionsProps.getProperty(key + ".y", String.valueOf(defaultY)));
        } catch (NumberFormatException e) {
            return defaultY;
        }
    }
    
    public static void saveColor(String key, int color) {
        saveSetting("color." + key, String.format("%08X", color));
    }
    
    public static int getColor(String key, int defaultColor) {
        try {
            String hex = getSetting("color." + key, String.format("%08X", defaultColor));
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }
    
    public static void saveBoolean(String key, boolean value) {
        saveSetting(key, String.valueOf(value));
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getSetting(key, String.valueOf(defaultValue)));
    }
    
    public static void saveInt(String key, int value) {
        saveSetting(key, String.valueOf(value));
    }
    
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getSetting(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private static void saveSettings() {
        try (OutputStream out = Files.newOutputStream(SETTINGS_FILE)) {
            settingsProps.store(out, "BAT Client Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void savePositions() {
        try (OutputStream out = Files.newOutputStream(POSITIONS_FILE)) {
            positionsProps.store(out, "BAT Client Positions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveAll() {
        saveSettings();
        savePositions();
    }
}
