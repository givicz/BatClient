package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.KeyBindingSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import me.BATapp.batclient.utils.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;

public class Freelook extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable freelook", false);
    public static final KeyBindingSetting freelookKey = new KeyBindingSetting("Freelook Key", "Key to hold for freelook", 91); // 91 = Left bracket
    public static final SliderSetting sensitivity = new SliderSetting("Sensitivity", "Freelook sensitivity", 1.0f, 0.1f, 5.0f, 0.1f);

    private static float cameraYaw = 0.0f;
    private static float cameraPitch = 0.0f;
    private static boolean wasActive = false;

    public Freelook() {
        super("Freelook", Category.OTHER);
    }

    public static void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options == null) return;

        if (!enabled.getValue()) {
            wasActive = false;
            return;
        }

        boolean isActive = KeyBindingRegistry.isKeyPressed(KeyBindingRegistry.FREELOOK_KEY);

        if (isActive && !wasActive) {
            // Initialize camera angles when starting freelook
            cameraYaw = mc.player.getYaw();
            cameraPitch = mc.player.getPitch();
        }

        wasActive = isActive;
    }

    public static void handleMouseMovement(double deltaX, double deltaY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!enabled.getValue() || mc.player == null) return;

        boolean isActive = KeyBindingRegistry.isKeyPressed(KeyBindingRegistry.FREELOOK_KEY);
        if (!isActive) return;

        GameOptions options = mc.options;
        float sens = (float) (sensitivity.getValue() * 0.6f + 0.2f);

        cameraYaw += (float) (deltaX * sens);
        cameraPitch = MathHelper.clamp(cameraPitch - (float) (deltaY * sens), -90.0f, 90.0f);
    }

    public static float getCameraYaw() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return enabled.getValue() && wasActive ? cameraYaw : mc.player != null ? mc.player.getYaw() : 0.0f;
    }

    public static float getCameraPitch() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return enabled.getValue() && wasActive ? cameraPitch : mc.player != null ? mc.player.getPitch() : 0.0f;
    }

    public static boolean isActive() {
        return enabled.getValue() && wasActive;
    }
}