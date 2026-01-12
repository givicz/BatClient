package me.BATapp.batclient.screen;

import me.BATapp.batclient.config.ConfigurableModule;
import me.BATapp.batclient.modules.SwingHand;
import me.BATapp.batclient.utils.SmoothGraphics;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * SwingHand configuration screen - allows adjustment of hand animation settings
 */
public class SwingHandScreen extends Screen {
    private final Screen parent;

    public SwingHandScreen() {
        this(null);
    }

    public SwingHandScreen(Screen parent) {
        super(Text.of("Swing Hand Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelHeight = 180;
        int panelY = centerY - panelHeight / 2;

        // Save button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Save & Close"), (button) -> {
            this.save();
            this.close();
        }).dimensions(centerX - 75, panelY + panelHeight - 25, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelWidth = 200;
        int panelHeight = 180;
        int panelX = centerX - panelWidth / 2;
        int panelY = centerY - panelHeight / 2;

        SmoothGraphics.drawRoundedRect(context, panelX, panelY, panelWidth, panelHeight, 8, 0xCC222222);
        
        super.render(context, mouseX, mouseY, delta);

        int y = panelY + 8;

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Swing Hand Animation Settings"), centerX, y, 0xFFFFFF);
        y += 25;
        context.drawTextWithShadow(this.textRenderer, Text.literal("X Pos: " + String.format("%.2f", SwingHand.xPos)), panelX + 15, y, 0xFFe0e0e0);
        y += 15;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Y Pos: " + String.format("%.2f", SwingHand.yPos)), panelX + 15, y, 0xFFe0e0e0);
        y += 15;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Z Pos: " + String.format("%.2f", SwingHand.zPos)), panelX + 15, y, 0xFFe0e0e0);
        y += 15;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Scale: " + String.format("%.2f", SwingHand.scale)), panelX + 15, y, 0xFFe0e0e0);
        y += 15;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Speed: " + SwingHand.speed), panelX + 15, y, 0xFFe0e0e0);
    }

    private void save() {
        ConfigurableModule.CONFIG.swingHand_xPos = SwingHand.xPos;
        ConfigurableModule.CONFIG.swingHand_yPos = SwingHand.yPos;
        ConfigurableModule.CONFIG.swingHand_zPos = SwingHand.zPos;
        ConfigurableModule.CONFIG.swingHand_scale = SwingHand.scale;
        ConfigurableModule.CONFIG.swingHand_rotX = SwingHand.rotX;
        ConfigurableModule.CONFIG.swingHand_rotY = SwingHand.rotY;
        ConfigurableModule.CONFIG.swingHand_rotZ = SwingHand.rotZ;
        ConfigurableModule.CONFIG.swingHand_xSwingRot = SwingHand.xSwingRot;
        ConfigurableModule.CONFIG.swingHand_ySwingRot = SwingHand.ySwingRot;
        ConfigurableModule.CONFIG.swingHand_zSwingRot = SwingHand.zSwingRot;
        ConfigurableModule.CONFIG.swingHand_speed = SwingHand.speed;
        ConfigurableModule.saveConfig();
    }

    @Override
    public void close() {
        if (this.parent != null) {
            this.client.setScreen(this.parent);
        } else {
            super.close();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}

