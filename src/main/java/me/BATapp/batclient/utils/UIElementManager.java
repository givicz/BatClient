package me.BATapp.batclient.utils;

import net.minecraft.util.math.Vec2f;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manager pro ukládání a načítání pozic UI prvků
 * Zajišťuje persistenci nastavení elementů
 */
public class UIElementManager {
    private static final Path CONFIG_DIR = Paths.get("config/batclient");
    private static final Path UI_POSITIONS_FILE = CONFIG_DIR.resolve("ui_positions.json");
    private static final Map<String, UIElement> elements = new HashMap<>();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static class UIElement {
        public String id;
        public float x;
        public float y;
        public int width;
        public int height;
        public boolean dragging = false;
        public boolean resizing = false;
        public int dragOffsetX = 0;
        public int dragOffsetY = 0;
        public int resizeOffsetX = 0;
        public int resizeOffsetY = 0;
        public int backgroundColor = 0xCC222222; // Moderní tmavě šedá
        // Moderní paleta barev
        public int accentColor = 0xFF00BFFF; // Jasně modrá
        public int textColor = 0xFFFFFFFF;   // Bílá
        public int backgroundColorAlphaOverride = 0xCC222222; // Moderní tmavě šedá s alpha
        public float alpha = 0.85f; // Mírně průhledné
        public boolean gradientEnabled = true;
        private static final int RESIZE_HANDLE_SIZE = 10;
        
        public UIElement(String id, float x, float y, int width, int height) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public UIElement() {}
        
        /**
         * Kontrola, zda je myš nad elementem
         */
        public boolean isMouseOver(int mx, int my) {
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
        
        /**
         * Kontrola, zda je myš nad resize handlem (dolní pravý roh)
         */
        public boolean isResizeHandleOver(int mx, int my) {
            int resizeX = (int)(x + width - RESIZE_HANDLE_SIZE);
            int resizeY = (int)(y + height - RESIZE_HANDLE_SIZE);
            return mx >= resizeX && mx <= x + width && my >= resizeY && my <= y + height;
        }
        
        /**
         * Zahájit tažení elementu
         */
        public void startDrag(int mx, int my) {
            dragging = true;
            dragOffsetX = (int)(mx - x);
            dragOffsetY = (int)(my - y);
        }
        
        /**
         * Zastavit tažení elementu
         */
        public void stopDrag() {
            dragging = false;
        }
        
        /**
         * Aktualizovat pozici během tažení
         */
        public void updateDrag(int mx, int my) {
            if (dragging) {
                x = mx - dragOffsetX;
                y = my - dragOffsetY;
            }
        }
        
        /**
         * Zahájit změnu velikosti
         */
        public void startResize(int mx, int my) {
            resizing = true;
            resizeOffsetX = (int)(mx - (x + width));
            resizeOffsetY = (int)(my - (y + height));
        }
        
        /**
         * Zastavit změnu velikosti
         */
        public void stopResize() {
            resizing = false;
        }
        
        /**
         * Aktualizovat velikost během změny
         */
        public void updateResize(int mx, int my) {
            if (resizing) {
                int newWidth = (int)(mx - x - resizeOffsetX);
                int newHeight = (int)(my - y - resizeOffsetY);
                // Minimum velikost
                if (newWidth > 30) this.width = newWidth;
                if (newHeight > 20) this.height = newHeight;
            }
        }
        
        /**
         * Vrátit, zda je element právě se měnící
         */
        public boolean isMoving() {
            return dragging || resizing;
        }
    }
    
    /**
     * Inicializovat UI element s defaultní pozicí
     */
    public static UIElement registerElement(String id, float defaultX, float defaultY, 
                                           int width, int height) {
        UIElement element = new UIElement(id, defaultX, defaultY, width, height);
        elements.put(id, element);
        return element;
    }
    
    /**
     * Získat UI element
     */
    public static UIElement getElement(String id) {
        return elements.getOrDefault(id, null);
    }
    
    /**
     * Nastavit barvu prvku
     */
    public static void setElementColor(String id, int bgColor, int accentColor, int textColor) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.backgroundColor = bgColor;
            elem.accentColor = accentColor;
            elem.textColor = textColor;
        }
    }
    
    /**
     * Nastavit Alpha prvku
     */
    public static void setElementAlpha(String id, float alpha) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.alpha = Math.max(0.1f, Math.min(1.0f, alpha));
        }
    }
    
    /**
     * Povolit/zakázat gradient
     */
    public static void setGradientEnabled(String id, boolean enabled) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.gradientEnabled = enabled;
        }
    }
    
    /**
     * Posunout element
     */
    public static void moveElement(String id, float x, float y) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.x = x;
            elem.y = y;
        }
    }
    
    /**
     * Začít tažení elementu
     */
    public static void startDragging(String id) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.dragging = true;
        }
    }
    
    /**
     * Skončit tažení elementu
     */
    public static void stopDragging(String id) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            elem.dragging = false;
            savePositions(); // Uložit při uvolnění
        }
    }
    
    /**
     * Uložit pozice do JSON souboru
     */
    public static void savePositions() {
        try {
            Files.createDirectories(CONFIG_DIR);
            
            JsonObject root = new JsonObject();
            for (UIElement elem : elements.values()) {
                JsonObject elemJson = new JsonObject();
                elemJson.addProperty("x", elem.x);
                elemJson.addProperty("y", elem.y);
                elemJson.addProperty("width", elem.width);
                elemJson.addProperty("height", elem.height);
                elemJson.addProperty("backgroundColor", elem.backgroundColor);
                elemJson.addProperty("accentColor", elem.accentColor);
                elemJson.addProperty("textColor", elem.textColor);
                elemJson.addProperty("alpha", elem.alpha);
                elemJson.addProperty("gradientEnabled", elem.gradientEnabled);
                root.add(elem.id, elemJson);
            }
            
            try (FileWriter writer = new FileWriter(UI_POSITIONS_FILE.toFile())) {
                gson.toJson(root, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Načíst pozice ze souboru
     */
    public static void loadPositions() {
        try {
            if (Files.exists(UI_POSITIONS_FILE)) {
                String content = new String(Files.readAllBytes(UI_POSITIONS_FILE));
                JsonObject root = JsonParser.parseString(content).getAsJsonObject();
                
                for (UIElement elem : elements.values()) {
                    if (root.has(elem.id)) {
                        JsonObject elemJson = root.getAsJsonObject(elem.id);
                        elem.x = elemJson.get("x").getAsFloat();
                        elem.y = elemJson.get("y").getAsFloat();
                        elem.width = elemJson.get("width").getAsInt();
                        elem.height = elemJson.get("height").getAsInt();
                        
                        if (elemJson.has("backgroundColor")) {
                            elem.backgroundColor = elemJson.get("backgroundColor").getAsInt();
                        }
                        if (elemJson.has("accentColor")) {
                            elem.accentColor = elemJson.get("accentColor").getAsInt();
                        }
                        if (elemJson.has("textColor")) {
                            elem.textColor = elemJson.get("textColor").getAsInt();
                        }
                        if (elemJson.has("alpha")) {
                            elem.alpha = elemJson.get("alpha").getAsFloat();
                        }
                        if (elemJson.has("gradientEnabled")) {
                            elem.gradientEnabled = elemJson.get("gradientEnabled").getAsBoolean();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reset na defaultní pozice
     */
    public static void resetToDefaults() {
        for (UIElement elem : elements.values()) {
            elem.backgroundColor = 0xCC222222;
            elem.accentColor = 0xFF00BFFF;
            elem.textColor = 0xFFFFFFFF;
            elem.alpha = 0.85f;
            elem.gradientEnabled = true;
        }
        savePositions();
    }
    
    /**
     * Aplikovat alpha na barvu
     */
    public static int applyAlpha(int color, float alpha) {
        int a = (int)((color >> 24 & 0xFF) * alpha);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Získat všechny prvky
     */
    public static Collection<UIElement> getAllElements() {
        return elements.values();
    }
    
    /**
     * Počet registrovaných prvků
     */
    public static int getElementCount() {
        return elements.size();
    }
    
    /**
     * Zvětšit element
     */
    public static void resizeElement(String id, int newWidth, int newHeight) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            if (newWidth > 30) elem.width = newWidth;
            if (newHeight > 20) elem.height = newHeight;
        }
    }
    
    /**
     * Zvětšit element o procento
     */
    public static void scaleElement(String id, float scale) {
        UIElement elem = elements.get(id);
        if (elem != null) {
            int newWidth = (int)(elem.width * scale);
            int newHeight = (int)(elem.height * scale);
            if (newWidth > 30) elem.width = newWidth;
            if (newHeight > 20) elem.height = newHeight;
        }
    }
}
