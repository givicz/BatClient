package me.BATapp.batclient.gui;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.config.ConfigManager;
import me.BATapp.batclient.config.ConfigSaver;
import me.BATapp.batclient.gui.settings.*;
import me.BATapp.batclient.modules.*;
import me.BATapp.batclient.settings.Setting;
import me.BATapp.batclient.settings.impl.*;
import net.minecraft.client.MinecraftClient;
import me.BATapp.batclient.gui.RGBColorPickerScreen;
import me.BATapp.batclient.config.ConfigSaver;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

public class BATSettingsScreen extends Screen {

    private final Map<SoupModule.Category, List<SoupModule>> modulesByCategory;
    private final Map<Setting<?>, SettingComponent<?>> settingComponents = new HashMap<>();

    private SoupModule.Category selectedCategory = SoupModule.Category.WORLD;
    private SoupModule selectedModule = null;
    private boolean themeMode = false;

    // Screen dimensions
    public int screenWidth = 500;
    public int screenHeight = 300;
    public int screenX;
    public int screenY;

    // Dragging
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private int customX = -1;
    private int customY = -1;

    // Animation
    private float scaleAnimation = 0.8f;
    private float alphaAnimation = 0.0f;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float ALPHA_SPEED = 0.12f;

    // Color settings
    public static int COLOR_PRIMARY = 0xFF222222;
    public static int COLOR_SECONDARY = 0xFF333333;
    public static int COLOR_ACCENT = 0xFF008FCC;
    public static int COLOR_HIGHLIGHT = 0xFF00BFFF;
    public static int COLOR_TEXT = 0xFFFFFFFF;
    public static int COLOR_TEXT_DARK = 0xFFCCCCCC;
    public static final int BORDER_RADIUS = 6;
    public static final int HEADER_HEIGHT = 27;

    private static final List<SoupModule> ALL_MODULES = new ArrayList<>();

    static {
        ALL_MODULES.add(new AmbientParticle());
        ALL_MODULES.add(new AspectRatio());
        ALL_MODULES.add(new BetterHudStyles());
        ALL_MODULES.add(new Capes());
        ALL_MODULES.add(new CustomFog());
        ALL_MODULES.add(new FullBright());
        ALL_MODULES.add(new JumpCircles());
        ALL_MODULES.add(new BreakingAnimation());
        ALL_MODULES.add(new Freecam());
        ALL_MODULES.add(new Freelook());
        ALL_MODULES.add(new PerformanceOptimizer());
        ALL_MODULES.add(new me.BATapp.batclient.modules.Saturation());
        ALL_MODULES.add(new Keystroke());
        ALL_MODULES.add(new Zoom());
        // Hotbar handled by BetterHudStyles
    }

    public BATSettingsScreen() {
        super(Text.literal("BAT CLIENT"));

        this.modulesByCategory = new EnumMap<>(SoupModule.Category.class);
        for (SoupModule.Category cat : SoupModule.Category.values()) {
            modulesByCategory.put(cat, new ArrayList<>());
        }
        for (SoupModule module : ALL_MODULES) {
            modulesByCategory.get(module.getCategory()).add(module);
        }

        ConfigManager.loadConfig();
    }

    @Override
    public void init() {
        super.init();
        // Centrujeme screen když se otevře
        if (customX == -1 && customY == -1) {
            customX = (this.width - screenWidth) / 2;
            customY = (this.height - screenHeight) / 2;
        }

        // Load screen position
        customX = (int) ConfigSaver.getInt("screen.x", -1);
        customY = (int) ConfigSaver.getInt("screen.y", -1);

        String savedCategory = ConfigManager.getMetadata("selectedCategory");
        String savedModule = ConfigManager.getMetadata("selectedModule");

        if (savedCategory != null) {
            try {
                selectedCategory = SoupModule.Category.valueOf(savedCategory);
            } catch (IllegalArgumentException ignored) {}
        }

        if (savedModule != null) {
            for (SoupModule mod : ALL_MODULES) {
                if (mod.getClass().getSimpleName().equals(savedModule)) {
                    selectedModule = mod;
                    break;
                }
            }
        }
    }

    private String getModuleName(SoupModule mod) {
        return switch (mod.getClass().getSimpleName()) {
            case "AmbientParticle" -> "Ambient Particles";
            case "AspectRatio" -> "Aspect Ratio";
            case "BetterHudStyles" -> "Better HUD";
            case "Capes" -> "Capes";
            case "CustomFog" -> "Custom Fog";
            case "FullBright" -> "Full Bright";
            case "JumpCircles" -> "Jump Circles";
            case "ChinaHat" -> "China Hat";
            case "Freecam" -> "Freecam";
            case "Freelook" -> "Freelook";
            case "PerformanceOptimizer" -> "Performance Optimizer";
            case "Zoom" -> "Zoom";
            case "Hotbar" -> "Hotbar";
            default -> mod.getDisplayName().getString();
        };
    }

    public static List<SoupModule> getAllModules() {
        return ALL_MODULES;
    }

    @Override
    public void tick() {
        super.tick();

        // Scale animation - start at 0.8 and grow to 1.0
        float scaleTarget = 1.0f;
        scaleAnimation += (scaleTarget - scaleAnimation) * ANIMATION_SPEED;

        // Alpha animation - fade in from 0 to 1
        float alphaTarget = 1.0f;
        alphaAnimation += (alphaTarget - alphaAnimation) * ALPHA_SPEED;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        // Calculate screen position
        if (customX == -1 && customY == -1) {
            screenX = (this.width - screenWidth) / 2;
            screenY = (this.height - screenHeight) / 2;
        } else {
            screenX = customX;
            screenY = customY;
        }

        // Draw with scale animation
        int centerX = screenX + screenWidth / 2;
        int centerY = screenY + screenHeight / 2;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(scaleAnimation, scaleAnimation, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        // Draw menu panel with alpha fade in
        int alphaColor = (int) (alphaAnimation * 255) << 24;
        int primaryWithAlpha = (COLOR_PRIMARY & 0xFFFFFF) | (alphaColor & 0xFF000000);
        fillRounded(context, screenX, screenY, screenX + screenWidth, screenY + screenHeight, BORDER_RADIUS, primaryWithAlpha);

        int accentWithAlpha = (COLOR_ACCENT & 0xFFFFFF) | (alphaColor & 0xFF000000);
        drawBorder(context, screenX, screenY, screenX + screenWidth, screenY + screenHeight, BORDER_RADIUS, accentWithAlpha, 2);

        // Draw title with fade in
        int highlightWithAlpha = (COLOR_HIGHLIGHT & 0xFFFFFF) | (alphaColor & 0xFF000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, "SETTINGS", screenX + 15, screenY + 10, highlightWithAlpha, false);

        int contentY = screenY + HEADER_HEIGHT + 5;
        int contentHeight = screenHeight - HEADER_HEIGHT - 10;

        // Left: Categories
        int catX = screenX + 10;
        int catWidth = 120;
        renderCategories(context, catX, contentY, catWidth, contentHeight, mouseX, mouseY);

        // Middle: Modules
        int modX = catX + catWidth + 8;
        int modWidth = 120;

        if (themeMode) {
            renderThemeSettings(context, modX, contentY, modWidth, contentHeight, mouseX, mouseY);
        } else {
            renderModules(context, modX, contentY, modWidth, contentHeight, mouseX, mouseY);

            // Right: Settings
            int setX = modX + modWidth + 8;
            int setWidth = screenX + screenWidth - setX - 10;

            if (selectedModule != null) {
                renderSettings(context, setX, contentY, setWidth, contentHeight, mouseX, mouseY);
            }
        }

        context.getMatrices().pop();

        // Drag indicator
        if (isDragging) {
            float centerLineX = screenX + screenWidth / 2;
            float centerLineY = screenY + screenHeight / 2;
            context.fill((int)centerLineX - 1, screenY, (int)centerLineX + 1, screenY + screenHeight, 0x88FFFFFF);
            context.fill(screenX, (int)centerLineY - 1, screenX + screenWidth, (int)centerLineY + 1, 0x88FFFFFF);
        }

        // **ODEBRÁN ŘÁDEK super.render(context, mouseX, mouseY, delta); PRO ODSTRANĚNÍ BLURU**
    }

    private void renderCategories(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        fillRounded(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_SECONDARY);
        drawBorder(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_ACCENT, 1);

        context.drawText(MinecraftClient.getInstance().textRenderer, "Categories", x + 8, y + 6, COLOR_TEXT, false);

        int itemY = y + 20;
        int itemHeight = 16;
        int itemPadding = 3;

        // Theme
        boolean themeSelected = themeMode;
        boolean themeHovered = mouseX >= x + itemPadding && mouseX <= x + width - itemPadding &&
                mouseY >= itemY && mouseY <= itemY + itemHeight;

        int themeBg = themeSelected ? COLOR_HIGHLIGHT : (themeHovered ? COLOR_ACCENT : 0xFF333333);
        int themeText = themeSelected ? COLOR_PRIMARY : COLOR_TEXT;

        fillRounded(context, x + itemPadding, itemY, x + width - itemPadding, itemY + itemHeight, 3, themeBg);
        // draw theme icon (scaled automatically)
        me.BATapp.batclient.render.Render2D.drawIcon(context.getMatrices(), me.BATapp.batclient.utils.TexturesManager.GUI_BUCKET, x + 6, itemY + 2, 12);
        context.drawText(MinecraftClient.getInstance().textRenderer, "Theme", x + 22, itemY + 4, themeText, false);
        itemY += itemHeight + 3;

        // Categories
        for (SoupModule.Category cat : SoupModule.Category.values()) {
            if (itemY >= y + height - 3) break;

            boolean selected = cat == selectedCategory;
            boolean hovered = mouseX >= x + itemPadding && mouseX <= x + width - itemPadding &&
                    mouseY >= itemY && mouseY <= itemY + itemHeight;

            int itemColor = selected ? COLOR_HIGHLIGHT : (hovered ? COLOR_ACCENT : 0xFF333333);
            int textColor = selected ? COLOR_PRIMARY : COLOR_TEXT;

            fillRounded(context, x + itemPadding, itemY, x + width - itemPadding, itemY + itemHeight, 3, itemColor);
            String catName = cat.name().charAt(0) + cat.name().substring(1).toLowerCase();
            // draw category icon next to text for some categories
            int iconX = x + 6;
            int iconY = itemY + 2;
            switch (cat) {
                case HUD -> me.BATapp.batclient.render.Render2D.drawIcon(context.getMatrices(), me.BATapp.batclient.utils.TexturesManager.GUI_ARMOR, iconX, iconY, 12);
                case WORLD -> me.BATapp.batclient.render.Render2D.drawIcon(context.getMatrices(), me.BATapp.batclient.utils.TexturesManager.GUI_GLOBAL, iconX, iconY, 12);
                case OTHER -> me.BATapp.batclient.render.Render2D.drawIcon(context.getMatrices(), me.BATapp.batclient.utils.TexturesManager.GUI_OTHER, iconX, iconY, 12);
                default -> { }
            }
            context.drawText(MinecraftClient.getInstance().textRenderer, catName, x + 22, itemY + 4, textColor, false);

            itemY += itemHeight + 3;
        }
    }

    private void renderModules(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        fillRounded(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_SECONDARY);
        drawBorder(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_ACCENT, 1);

        context.drawText(MinecraftClient.getInstance().textRenderer, "Modules", x + 8, y + 6, COLOR_TEXT, false);

        List<SoupModule> mods = modulesByCategory.get(selectedCategory);
        int itemY = y + 20;
        int itemHeight = 16;
        int itemPadding = 3;

        for (SoupModule mod : mods) {
            if (itemY >= y + height - 3) break;

            boolean selected = mod == selectedModule;
            boolean hovered = mouseX >= x + itemPadding && mouseX <= x + width - itemPadding &&
                    mouseY >= itemY && mouseY <= itemY + itemHeight;

            int itemColor = selected ? COLOR_HIGHLIGHT : (hovered ? COLOR_ACCENT : 0xFF333333);
            int textColor = selected ? COLOR_PRIMARY : COLOR_TEXT;

            fillRounded(context, x + itemPadding, itemY, x + width - itemPadding, itemY + itemHeight, 3, itemColor);

            String modName = getModuleName(mod);
            String shortName = modName.length() > 11 ? modName.substring(0, 9) + ".." : modName;
            context.drawText(MinecraftClient.getInstance().textRenderer, shortName, x + 7, itemY + 4, textColor, false);

            itemY += itemHeight + 3;
        }
    }

    private void renderSettings(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        fillRounded(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_SECONDARY);
        drawBorder(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_ACCENT, 1);

        context.drawText(MinecraftClient.getInstance().textRenderer, "Settings", x + 8, y + 6, COLOR_TEXT, false);

        if (selectedModule == null) return;
        List<Setting<?>> settings = selectedModule.getSettings();
        // If module has no settings, show its display name so user knows what module is selected
        if (settings.isEmpty()) {
            String disp = selectedModule.getDisplayName().getString();
            context.drawText(MinecraftClient.getInstance().textRenderer, disp, x + 8, y + 20, COLOR_TEXT_DARK, false);
            return;
        }
        int settingY = y + 20;

        for (Setting<?> setting : settings) {
            if (settingY >= y + height - 3) break;

            context.drawText(MinecraftClient.getInstance().textRenderer, setting.getName(), x + 8, settingY, COLOR_TEXT_DARK, false);

            // Ensure component exists
            SettingComponent<?> comp = settingComponents.get(setting);
            if (comp == null) {
                comp = createComponent(setting, x + 8, settingY + 14);
                if (comp != null) {
                    settingComponents.put(setting, comp);
                }
            }

            // Render component
            if (comp != null) {
                comp.x = x + 8;
                comp.y = settingY + 14;
                comp.render(context, mouseX, mouseY, 0.016f);
            }

            settingY += 28;
        }
    }

    private void renderThemeSettings(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        fillRounded(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_SECONDARY);
        drawBorder(context, x, y, x + width, y + height, BORDER_RADIUS, COLOR_ACCENT, 1);

        context.drawText(MinecraftClient.getInstance().textRenderer, "Theme", x + 8, y + 6, COLOR_TEXT, false);

        int itemY = y + 20;
        int itemHeight = 16;

        drawColorSetting(context, "Primary", COLOR_PRIMARY, x, itemY);
        itemY += itemHeight + 3;

        drawColorSetting(context, "Secondary", COLOR_SECONDARY, x, itemY);
        itemY += itemHeight + 3;

        drawColorSetting(context, "Accent", COLOR_ACCENT, x, itemY);
        itemY += itemHeight + 3;

        drawColorSetting(context, "Highlight", COLOR_HIGHLIGHT, x, itemY);
        itemY += itemHeight + 3;

        drawColorSetting(context, "Text", COLOR_TEXT, x, itemY);
        itemY += itemHeight + 3;

        drawColorSetting(context, "Text Dark", COLOR_TEXT_DARK, x, itemY);
    }

    private void drawColorSetting(DrawContext context, String label, int color, int x, int y) {
        context.drawText(MinecraftClient.getInstance().textRenderer, label, x + 8, y, COLOR_TEXT_DARK, false);
        context.fill(x + 60, y, x + 70, y + 12, color);
        String hex = String.format("%08X", color).substring(2);
        context.drawText(MinecraftClient.getInstance().textRenderer, hex, x + 75, y + 2, COLOR_TEXT_DARK, false);
    }

    private void fillRounded(DrawContext context, int x1, int y1, int x2, int y2, int radius, int color) {
        UIComponentRenderer.drawRoundedRect(context, x1, y1, x2 - x1, y2 - y1, radius, color);
    }

    private void drawBorder(DrawContext context, int x1, int y1, int x2, int y2, int radius, int color, int thickness) {
        UIComponentRenderer.drawRoundedBorder(context, x1, y1, x2 - x1, y2 - y1, radius, color, thickness);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int contentY = screenY + HEADER_HEIGHT + 5;
        int contentHeight = screenHeight - HEADER_HEIGHT - 10;

        int catX = screenX + 10;
        int catWidth = 120;
        int modX = catX + catWidth + 8;
        int modWidth = 120;
        int setX = modX + modWidth + 8;

        // Header dragging
        if (button == 0 && mouseY >= screenY && mouseY <= screenY + HEADER_HEIGHT &&
                mouseX >= screenX && mouseX <= screenX + screenWidth) {
            isDragging = true;
            dragOffsetX = (int)(screenX - mouseX);
            dragOffsetY = (int)(screenY - mouseY);
            return true;
        }

        // Theme button
        int itemY = contentY + 20;
        if (mouseX >= catX + 3 && mouseX <= catX + catWidth - 3 &&
                mouseY >= itemY && mouseY <= itemY + 16) {
            themeMode = true;
            selectedModule = null;
            settingComponents.clear();
            ConfigSaver.saveBoolean("theme.mode", true);
            return true;
        }
        itemY += 19;

        // Categories
        for (SoupModule.Category cat : SoupModule.Category.values()) {
            if (mouseX >= catX + 3 && mouseX <= catX + catWidth - 3 &&
                    mouseY >= itemY && mouseY <= itemY + 16) {
                themeMode = false;
                selectedCategory = cat;
                selectedModule = null;
                settingComponents.clear();
                ConfigSaver.saveBoolean("theme.mode", false);
                ConfigSaver.saveSetting("selected.category", cat.name());
                return true;
            }
            itemY += 19;
        }

        if (themeMode) {
            // Theme settings - color boxes are at x + 60, y + (height*rowNumber + 20)
            int colorBoxX = modX + 60;
            int colorBoxY = contentY + 20;
            int itemHeight = 16;
            
            // Primary
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_PRIMARY, (c) -> {
                    COLOR_PRIMARY = c;
                    ConfigSaver.saveInt("theme.primary", c);
                }));
                return true;
            }
            colorBoxY += itemHeight + 3;
            
            // Secondary
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_SECONDARY, (c) -> {
                    COLOR_SECONDARY = c;
                    ConfigSaver.saveInt("theme.secondary", c);
                }));
                return true;
            }
            colorBoxY += itemHeight + 3;
            
            // Accent
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_ACCENT, (c) -> {
                    COLOR_ACCENT = c;
                    ConfigSaver.saveInt("theme.accent", c);
                }));
                return true;
            }
            colorBoxY += itemHeight + 3;
            
            // Highlight
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_HIGHLIGHT, (c) -> {
                    COLOR_HIGHLIGHT = c;
                    ConfigSaver.saveInt("theme.highlight", c);
                }));
                return true;
            }
            colorBoxY += itemHeight + 3;
            
            // Text
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_TEXT, (c) -> {
                    COLOR_TEXT = c;
                    ConfigSaver.saveInt("theme.text", c);
                }));
                return true;
            }
            colorBoxY += itemHeight + 3;
            
            // Text Dark
            if (mouseX >= colorBoxX && mouseX <= colorBoxX + 10 && mouseY >= colorBoxY && mouseY <= colorBoxY + 12) {
                MinecraftClient.getInstance().setScreen(new RGBColorPickerScreen(this, COLOR_TEXT_DARK, (c) -> {
                    COLOR_TEXT_DARK = c;
                    ConfigSaver.saveInt("theme.text_dark", c);
                }));
                return true;
            }
            return true;
        }

        // Modules
        int modItemY = contentY + 20;
        List<SoupModule> mods = modulesByCategory.get(selectedCategory);
        for (SoupModule mod : mods) {
            if (mouseX >= modX + 3 && mouseX <= modX + modWidth - 3 &&
                    mouseY >= modItemY && mouseY <= modItemY + 16) {
                selectedModule = mod;
                settingComponents.clear();
                ConfigSaver.saveSetting("selected.module", mod.getClass().getSimpleName());
                return true;
            }
            modItemY += 19;
        }

        // Settings
        if (selectedModule != null) {
            int setWidth = screenX + screenWidth - setX - 10;
            int settingY = contentY + 20;

            for (Setting<?> setting : selectedModule.getSettings()) {
                if (settingY >= contentY + contentHeight - 3) break;

                // Get or create component
                SettingComponent<?> comp = settingComponents.get(setting);
                if (comp == null) {
                    comp = createComponent(setting, setX + 8, settingY + 14);
                    if (comp != null) {
                        settingComponents.put(setting, comp);
                    }
                }

                // Check if clicked
                if (comp != null) {
                    comp.x = setX + 8;
                    comp.y = settingY + 14;
                    if (mouseX >= comp.x && mouseX <= comp.x + comp.width &&
                        mouseY >= comp.y && mouseY <= comp.y + comp.height) {
                        comp.mouseClicked(mouseX, mouseY, button);
                        ConfigManager.saveConfig();
                        ConfigSaver.saveAll();
                        return true;
                    }
                }

                settingY += 28;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && button == 0) {
            customX = (int) Math.max(0, Math.min(mouseX + dragOffsetX, this.width - screenWidth));
            customY = (int) Math.max(0, Math.min(mouseY + dragOffsetY, this.height - screenHeight));

            // Save position
            ConfigSaver.saveInt("screen.x", customX);
            ConfigSaver.saveInt("screen.y", customY);

            return true;
        }

        // Settings dragging
        if (selectedModule != null) {
            int contentY = screenY + HEADER_HEIGHT + 5;
            int modX = screenX + 10 + 120 + 8;
            int modWidth = 120;
            int setX = modX + modWidth + 8;
            int settingY = contentY + 20;

            for (Setting<?> setting : selectedModule.getSettings()) {
                if (settingY >= contentY + (screenHeight - HEADER_HEIGHT - 10) - 3) break;

                SettingComponent<?> comp = settingComponents.get(setting);
                if (comp != null) {
                    comp.x = setX + 8;
                    comp.y = settingY + 12;
                    comp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
                }
                settingY += 19;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging && button == 0) {
            isDragging = false;
            return true;
        }

        // Settings release
        if (selectedModule != null) {
            int contentY = screenY + HEADER_HEIGHT + 5;
            int modX = screenX + 10 + 120 + 8;
            int modWidth = 120;
            int setX = modX + modWidth + 8;
            int settingY = contentY + 20;

            for (Setting<?> setting : selectedModule.getSettings()) {
                if (settingY >= contentY + (screenHeight - HEADER_HEIGHT - 10) - 3) break;

                SettingComponent<?> comp = settingComponents.get(setting);
                if (comp != null) {
                    comp.x = setX + 8;
                    comp.y = settingY + 12;
                    comp.mouseReleased(mouseX, mouseY, button);
                }
                settingY += 19;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private SettingComponent<?> createComponent(Setting<?> setting, int x, int y) {
        return switch (setting) {
            case BooleanSetting bs -> new BooleanComponent(bs, x, y, 80, 12);
            case SliderSetting ss -> new SliderComponent(ss, x, y, 80, 12);
            case EnumSetting<?> es -> new EnumComponent<>(es, x, y, 80, 12);
            case ButtonSetting bs -> new ButtonComponent(bs, x, y, 80, 12);
            case StringSetting ss -> new StringComponent(ss, x, y, 80, 12);
            case ColorSetting cs -> new ColorComponent(cs, x, y, 60, 16);
            case KeyBindingSetting kbs -> new KeyBindingComponent(kbs, x, y, 60, 16);
            default -> null;
        };
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (SettingComponent<?> comp : settingComponents.values()) {
            if (comp instanceof StringComponent sc) {
                if (sc.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (SettingComponent<?> comp : settingComponents.values()) {
            if (comp instanceof StringComponent sc) {
                if (sc.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}