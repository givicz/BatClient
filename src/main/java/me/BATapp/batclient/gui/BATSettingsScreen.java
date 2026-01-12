package me.BATapp.batclient.gui;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.config.ConfigManager;
import me.BATapp.batclient.config.ConfigSaver;
import me.BATapp.batclient.gui.settings.*;
import me.BATapp.batclient.modules.*;
import me.BATapp.batclient.settings.Setting;
import me.BATapp.batclient.settings.impl.*;
import me.BATapp.batclient.utils.SmoothGraphics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public class BATSettingsScreen extends Screen {

    private final Map<SoupModule.Category, List<SoupModule>> modulesByCategory;
    private final Map<Setting<?>, SettingComponent<?>> settingComponents = new HashMap<>();

    private SoupModule.Category selectedCategory = null; // null = ALL
    private SoupModule selectedModule = null; // For settings overlay
    private String searchQuery = "";
    private boolean isTypingSearch = false;

    // Screen dimensions & Position (Centered default)
    public int screenWidth = 600;
    public int screenHeight = 350;
    public int screenX;
    public int screenY;

    // Animation
    private float scaleAnimation = 0.8f;
    private float alphaAnimation = 0.0f;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float ALPHA_SPEED = 0.12f;

    // Scroll
    private float scrollY = 0;
    private float targetScrollY = 0;
    private float maxScroll = 0;

    // Colors
    private static final int COLOR_BG = 0xFF141414;
    private static final int COLOR_CARD_BG = 0xFF1E1E1E;
    private static final int COLOR_CARD_HOVER = 0xFF252525;
    private static final int COLOR_PRIMARY_RED = 0xFFFF4D4D; // Red for "MOD MENU"
    private static final int COLOR_ACCENT_GREEN = 0xFF2ECC71;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFFAAAAAA;

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
        ALL_MODULES.add(new TargetRender());
        ALL_MODULES.add(new TargetHud());
        ALL_MODULES.add(new HitParticles());
        ALL_MODULES.add(new HitColor());
        ALL_MODULES.add(new HitBubbles());
        ALL_MODULES.add(new HitSound());
        ALL_MODULES.add(new Halo());
        ALL_MODULES.add(new ChinaHat());
        ALL_MODULES.add(new Trails());
        ALL_MODULES.add(new Trajectories());
        // Add other modules as needed
    }

    public BATSettingsScreen() {
        super(Text.literal("BAT CLIENT"));
        this.modulesByCategory = new EnumMap<>(SoupModule.Category.class);
        for (SoupModule.Category cat : SoupModule.Category.values()) {
            modulesByCategory.put(cat, new ArrayList<>());
        }
        for (SoupModule module : ALL_MODULES) {
            if (module.getCategory() == null) continue;
            modulesByCategory.get(module.getCategory()).add(module);
        }
        ConfigManager.loadConfig();
    }

    @Override
    public void init() {
        super.init();
        screenX = (this.width - screenWidth) / 2;
        screenY = (this.height - screenHeight) / 2;
        
        // Ensure screen fits nicely
        if (this.width < screenWidth) {
            screenWidth = this.width - 20;
            screenX = 10;
        }
        if (this.height < screenHeight) {
            screenHeight = this.height - 20;
            screenY = 10;
        }
    }

    @Override
    public void tick() {
        super.tick();
        float scaleTarget = 1.0f;
        scaleAnimation += (scaleTarget - scaleAnimation) * ANIMATION_SPEED;
        float alphaTarget = 1.0f;
        alphaAnimation += (alphaTarget - alphaAnimation) * ALPHA_SPEED;
        
        scrollY = MathHelper.lerp(0.2f, scrollY, targetScrollY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta); // Default dark BG

        int alpha = (int) (alphaAnimation * 255);
        int alphaColor = alpha << 24;

        context.getMatrices().push();
        // Animation scale from center
        float centerX = screenX + screenWidth / 2.0f;
        float centerY = screenY + screenHeight / 2.0f;
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(scaleAnimation, scaleAnimation, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        // Main Background
        // Shadow effect
        SmoothGraphics.drawRoundedRect(context, screenX - 5, screenY - 5, screenWidth + 10, screenHeight + 10, 15, 0x55000000);
        SmoothGraphics.drawRoundedRect(context, screenX, screenY, screenWidth, screenHeight, 10, COLOR_BG | alphaColor);

        // Top Bar
        renderTopBar(context, mouseX, mouseY, alphaColor);

        // Filters Bar
        renderFilters(context, mouseX, mouseY, alphaColor);

        // Grid Content
        if (selectedModule == null) {
            renderModuleGrid(context, mouseX, mouseY, delta, alphaColor);
        } else {
            // Render settings overlay for selected module
            renderModuleGrid(context, mouseX, mouseY, delta, alphaColor); // Draw grid behind dimmed
            context.fill(screenX, screenY, screenX + screenWidth, screenY + screenHeight, 0xCC000000); // Dim overlay
            renderSettingsOverlay(context, mouseX, mouseY, alphaColor);
        }

        context.getMatrices().pop();
    }

    private void renderTopBar(DrawContext context, int mouseX, int mouseY, int alphaColor) {
        int x = screenX + 20;
        int y = screenY + 20;

        // "MOD MENU" Red Button
        SmoothGraphics.drawRoundedRect(context, x, y, 100, 30, 6, COLOR_PRIMARY_RED | alphaColor);
        context.drawCenteredTextWithShadow(textRenderer, "MOD MENU", x + 50, y + 11, COLOR_TEXT_WHITE | alphaColor);

        // Search Bar (Right side)
        int searchW = 180;
        int searchX = screenX + screenWidth - 20 - searchW;
        int searchH = 30;
        int searchBg = isTypingSearch ? 0xFF353535 : 0xFF2A2A2A;
        SmoothGraphics.drawRoundedRect(context, searchX, y, searchW, searchH, 6, searchBg | alphaColor);
        
        String searchText = searchQuery.isEmpty() && !isTypingSearch ? "Search..." : searchQuery + (isTypingSearch && (System.currentTimeMillis() / 500 % 2 == 0) ? "_" : "");
        int textColor = searchQuery.isEmpty() && !isTypingSearch ? COLOR_TEXT_GRAY : COLOR_TEXT_WHITE;
        context.drawText(textRenderer, searchText, searchX + 10, y + 11, textColor | alphaColor, false);
    }

    private void renderFilters(DrawContext context, int mouseX, int mouseY, int alphaColor) {
        int startX = screenX + 130;
        int y = screenY + 20;
        int btnH = 30;
        int gap = 10;
        
        List<String> filters = new ArrayList<>();
        filters.add("All");
        for(SoupModule.Category c : SoupModule.Category.values()) {
            filters.add(c.name().charAt(0) + c.name().substring(1).toLowerCase());
        }

        int currentX = startX;
        for (String filter : filters) {
            int width = textRenderer.getWidth(filter) + 20;
            // Check if selected
            boolean selected = false;
            if (filter.equals("All")) {
                selected = selectedCategory == null;
            } else {
                selected = selectedCategory != null && filter.equalsIgnoreCase(selectedCategory.name());
            }

            boolean hovered = mouseX >= currentX && mouseX <= currentX + width && mouseY >= y && mouseY <= y + btnH;

            int color = selected ? 0xFF444444 : (hovered ? 0xFF353535 : 0xFF2A2A2A);
            SmoothGraphics.drawRoundedRect(context, currentX, y, width, btnH, 6, color | alphaColor);
            context.drawCenteredTextWithShadow(textRenderer, filter, currentX + width / 2, y + 11, (selected ? COLOR_TEXT_WHITE : COLOR_TEXT_GRAY) | alphaColor);
            
            currentX += width + gap;
        }
    }

    private void renderModuleGrid(DrawContext context, int mouseX, int mouseY, float delta, int alphaColor) {
        int startX = screenX + 20;
        int startY = screenY + 70;
        int endY = screenY + screenHeight - 20;
        int viewWidth = screenWidth - 40;
        int viewHeight = endY - startY;

        context.enableScissor(screenX, startY, screenX + screenWidth, endY);
        
        // Push scroll transform
        context.getMatrices().push();
        context.getMatrices().translate(0, -scrollY, 0);

        List<SoupModule> filteredModules = getFilteredModules();

        int cardWidth = 135;
        int cardHeight = 90;
        int gap = 15;
        int cols = Math.max(1, viewWidth / (cardWidth + gap));
        
        int totalRows = (int) Math.ceil((double) filteredModules.size() / cols);
        int contentHeight = totalRows * (cardHeight + gap) - gap;
        
        maxScroll = Math.max(0, contentHeight - viewHeight);
        
        // Render relative to startY (which is 0 in local space + startY offset)
        // But since we translated by -scrollY, we render at absolute Y.
        
        // Wait, context.translate affects drawing.
        // We want to draw at startY + row*... - scrollY.
        // If we translate by -scrollY, we draw at startY + row*...
        
        for (int i = 0; i < filteredModules.size(); i++) {
            SoupModule mod = filteredModules.get(i);
            int col = i % cols;
            int row = i / cols;
            
            int x = startX + col * (cardWidth + gap);
            int y = startY + row * (cardHeight + gap);

            // Culling (in absolute coordinates)
            float absY = y - scrollY;
            if (absY + cardHeight < startY || absY > endY) continue;

            renderModuleCard(context, mod, x, y, cardWidth, cardHeight, mouseX, mouseY + (int)scrollY, alphaColor);
        }

        context.getMatrices().pop();
        context.disableScissor();
        
        // Scrollbar
        if (maxScroll > 0) {
            int barHeight = (int) ((viewHeight / (float) (contentHeight + viewHeight)) * viewHeight); // Fix calculation
            barHeight = Math.max(30, barHeight);
            int barArea = viewHeight - barHeight;
            int barY = startY + (int) ((scrollY / maxScroll) * barArea);
            int barX = screenX + screenWidth - 6;
            SmoothGraphics.drawRoundedRect(context, barX, barY, 4, barHeight, 2, 0xFF555555 | alphaColor);
        }
    }

    private void renderModuleCard(DrawContext context, SoupModule mod, int x, int y, int w, int h, int mouseX, int mouseY, int alphaColor) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        boolean enabled = isModuleEnabled(mod);
        
        int bg = hovered ? COLOR_CARD_HOVER : COLOR_CARD_BG;
        
        // Card BG
        SmoothGraphics.drawRoundedRect(context, x, y, w, h, 8, bg | alphaColor);
        // Accent border if enabled?
        /* if (enabled) {
             // draw thin border?
        } */

        // Icon placeholder (Circle)
        int iconSize = 24;
        SmoothGraphics.drawRoundedRect(context, x + 10, y + 12, iconSize, iconSize, 6, 0xFF353535 | alphaColor);
        
        // Title
        String name = getModuleName(mod);
        if (textRenderer.getWidth(name) > w - 20) {
            name = textRenderer.trimToWidth(name, w - 20);
        }
        context.drawText(textRenderer, name, x + 10, y + 45, COLOR_TEXT_WHITE | alphaColor, false);

        int toggleW = 40;
        int toggleH = 20;
        int toggleX = x + w - toggleW - 10;
        int toggleY = y + 10;
        
        // Toggle Switch
        int switchBg = enabled ? COLOR_ACCENT_GREEN : 0xFF333333;
        SmoothGraphics.drawRoundedRect(context, toggleX, toggleY, toggleW, toggleH, 10, switchBg | alphaColor);
        int knobX = enabled ? toggleX + toggleW - 14 : toggleX + 4;
        SmoothGraphics.drawRoundedRect(context, knobX, toggleY + 4, 12, 12, 6, 0xFFFFFFFF | alphaColor);

        // Gear Icon (Settings)
        int gearX = x + 10;
        int gearY = y + h - 20;
        context.drawText(textRenderer, "⚙", gearX, gearY, 0xFFAAAAAA | alphaColor, false);
        
        // Status Text
        String statusText = enabled ? "Enabled" : "Disabled";
        int statusColor = enabled ? COLOR_ACCENT_GREEN : 0xFF555555;
        context.drawText(textRenderer, statusText, x + w - textRenderer.getWidth(statusText) - 10, y + h - 20, statusColor | alphaColor, false);
    }
    
    private void renderSettingsOverlay(DrawContext context, int mouseX, int mouseY, int alphaColor) {
        if (selectedModule == null) return;
        
        int w = 400;
        int h = 300;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        
        // Background
        SmoothGraphics.drawRoundedRect(context, x, y, w, h, 12, 0xFF202020 | alphaColor);
        
        // Header
        context.drawText(textRenderer, selectedModule.getDisplayName().getString(), x + 20, y + 20, COLOR_TEXT_WHITE | alphaColor, false);
        
        // Close Button (X)
        int closeX = x + w - 30;
        int closeY = y + 15;
        context.drawText(textRenderer, "X", closeX, closeY + 5, COLOR_TEXT_GRAY | alphaColor, false);
        
        // Separator
        context.fill(x + 20, y + 45, x + w - 20, y + 46, 0xFF333333);
        
        // Render Settings List
        int contentY = y + 60;
        List<Setting<?>> settings = selectedModule.getSettings();
        
        if (settings.isEmpty()) {
             context.drawCenteredTextWithShadow(textRenderer, "No settings available", x + w/2, y + h/2, COLOR_TEXT_GRAY | alphaColor);
        }
        
        for (Setting<?> setting : settings) {
            if (contentY > y + h - 30) break; 
            
            context.drawText(textRenderer, setting.getName(), x + 30, contentY + 2, COLOR_TEXT_GRAY | alphaColor, false);
            
            // Render component
            SettingComponent<?> comp = settingComponents.get(setting);
            if (comp == null) {
                comp = createComponent(setting, x + 200, contentY); // Align components
                if (comp != null) settingComponents.put(setting, comp);
            }
            
            if (comp != null) {
                comp.x = x + 200;
                comp.y = contentY;
                comp.render(context, mouseX, mouseY, 0); 
            }
            
            contentY += 30;
        }
    }

    private List<SoupModule> getFilteredModules() {
        return ALL_MODULES.stream()
                .filter(m -> selectedCategory == null || m.getCategory() == selectedCategory)
                .filter(m -> searchQuery.isEmpty() || getModuleName(m).toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
    }

    private boolean isModuleEnabled(SoupModule mod) {
        try {
           Optional<Setting<?>> enabledSet = mod.getSettings().stream().filter(s -> s.getName().equalsIgnoreCase("Enabled")).findFirst();
           if (enabledSet.isPresent() && enabledSet.get() instanceof BooleanSetting bs) {
               return bs.getValue();
           }
        } catch (Exception e) {}
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Overlay interaction
        if (selectedModule != null) {
            int w = 400; int h = 300;
            int x = (this.width - w) / 2; int y = (this.height - h) / 2;
            
            // Close button
            if (mouseX >= x + w - 40 && mouseX <= x + w - 10 && mouseY >= y + 10 && mouseY <= y + 40) {
                selectedModule = null;
                settingComponents.clear(); 
                return true;
            }
            
            // Handle Setting interactions
             for (SettingComponent<?> comp : settingComponents.values()) {
                if (mouseX >= comp.x && mouseX <= comp.x + comp.width &&
                    mouseY >= comp.y && mouseY <= comp.y + comp.height) {
                    comp.mouseClicked(mouseX, mouseY, button);
                    ConfigManager.saveConfig();
                    ConfigSaver.saveAll();
                    return true;
                }
            }
            
            // Click outside to close
            if (mouseX < x || mouseX > x + w || mouseY < y || mouseY > y + h) {
                selectedModule = null;
                return true;
            }
            return true; // Consume click
        }

        // Search Bar click
        int searchW = 180;
        int searchX = screenX + screenWidth - 20 - searchW;
        int searchY = screenY + 20;
        if (mouseX >= searchX && mouseX <= searchX + searchW && mouseY >= searchY && mouseY <= searchY + 30) {
            isTypingSearch = !isTypingSearch;
            return true;
        } else if (isTypingSearch) {
             isTypingSearch = false; // Click away
        }

        // Filter clicks
        int startX = screenX + 130;
        int filterY = screenY + 20;
        int btnH = 30;
        int gap = 10;
        
        List<String> filters = new ArrayList<>();
        filters.add("All");
        for(SoupModule.Category c : SoupModule.Category.values()) {
            filters.add(c.name().charAt(0) + c.name().substring(1).toLowerCase());
        }
        
        int currentX = startX;
        for (int i = 0; i < filters.size(); i++) {
            String filter = filters.get(i);
            int width = textRenderer.getWidth(filter) + 20;
            
            if (mouseX >= currentX && mouseX <= currentX + width && mouseY >= filterY && mouseY <= filterY + btnH) {
                if (i == 0) selectedCategory = null;
                else selectedCategory = SoupModule.Category.values()[i-1];
                targetScrollY = 0; // Reset scroll
                return true;
            }
            currentX += width + gap;
        }

        // Grid clicks
        List<SoupModule> filtered = getFilteredModules();
        int gridStartX = screenX + 20;
        int gridStartY = screenY + 70;
        
        // Mouse Y relative to grid for scrolling
        // But we check absolute mouse position against absolute card position.
        // Card Y = startY + row*... - scrollY
        // So we need to compute card position.
        
        int cardWidth = 135; int cardHeight = 90; int cardGap = 15;
        int viewWidth = screenWidth - 40;
        int cols = Math.max(1, viewWidth / (cardWidth + cardGap));

        for (int i = 0; i < filtered.size(); i++) {
            SoupModule mod = filtered.get(i);
            int col = i % cols;
            int row = i / cols;
            int mx = gridStartX + col * (cardWidth + cardGap);
            int my = (int) (gridStartY + row * (cardHeight + cardGap) - scrollY);

            // Culling / Bounds Check
             if (my + cardHeight < gridStartY || my > screenY + screenHeight - 20) continue;
             // Click inside grid area only
             if (mouseY < gridStartY || mouseY > screenY + screenHeight - 20) continue;

            if (mouseX >= mx && mouseX <= mx + cardWidth && mouseY >= my && mouseY <= my + cardHeight) {
                // Determine if clicked toggle or card (settings)
                int toggleW = 40; int toggleH = 20;
                int toggleX = mx + cardWidth - toggleW - 10;
                int toggleY = my + 10;

                if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
                    // Toggle
                     try {
                       Optional<Setting<?>> enabledSet = mod.getSettings().stream().filter(s -> s.getName().equalsIgnoreCase("Enabled")).findFirst();
                       if (enabledSet.isPresent() && enabledSet.get() instanceof BooleanSetting bs) {
                           bs.setValue(!bs.getValue());
                           ConfigManager.saveConfig();
                       }
                    } catch (Exception e) {}
                } else {
                    // Open Settings
                    selectedModule = mod;
                    settingComponents.clear();
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedModule != null) return false;
        
        targetScrollY -= verticalAmount * 40; 
        targetScrollY = MathHelper.clamp(targetScrollY, 0, maxScroll);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (selectedModule != null) {
            for (SettingComponent<?> comp : settingComponents.values()) {
                 if (comp instanceof StringComponent sc) sc.charTyped(chr, modifiers);
            }
            return true;
        }

        if (isTypingSearch) {
            searchQuery += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedModule != null) {
            for (SettingComponent<?> comp : settingComponents.values()) {
                if (comp instanceof StringComponent sc) sc.keyPressed(keyCode, scanCode, modifiers);
                if (comp instanceof KeyBindingComponent kbc) kbc.keyPressed(keyCode, scanCode, modifiers);
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedModule = null;
                return true;
            }
            return true;
        }

        if (isTypingSearch) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                isTypingSearch = false;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
         if (selectedModule != null) {
            for (SettingComponent<?> comp : settingComponents.values()) {
                 comp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
         }
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
         if (selectedModule != null) {
            for (SettingComponent<?> comp : settingComponents.values()) {
                 comp.mouseReleased(mouseX, mouseY, button);
            }
         }
         return super.mouseReleased(mouseX, mouseY, button);
    }
    
    private String getModuleName(SoupModule mod) {
        return switch (mod.getClass().getSimpleName()) {
            case "AmbientParticle" -> "Ambient Particles";
            case "AspectRatio" -> "Aspect Ratio";
            case "BetterHudStyles" -> "Better HUD";
            default -> mod.getDisplayName().getString();
        };
    }
    
    private SettingComponent<?> createComponent(Setting<?> setting, int x, int y) {
        return switch (setting) {
            case BooleanSetting bs -> new BooleanComponent(bs, x, y, 80, 12);
            case SliderSetting ss -> new SliderComponent(ss, x, y, 140, 12);
            case EnumSetting<?> es -> new EnumComponent<>(es, x, y, 140, 12);
            case ButtonSetting bs -> new ButtonComponent(bs, x, y, 140, 12);
            case StringSetting ss -> new StringComponent(ss, x, y, 140, 12);
            case ColorSetting cs -> new ColorComponent(cs, x, y, 60, 16);
            case KeyBindingSetting kbs -> new KeyBindingComponent(kbs, x, y, 60, 16);
            default -> null;
        };
    }
}
