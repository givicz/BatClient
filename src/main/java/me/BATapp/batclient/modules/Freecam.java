package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import me.BATapp.batclient.utils.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Freecam extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable freecam", false);
    public static final SliderSetting speed = new SliderSetting("Speed", "Freecam movement speed", 1.0f, 0.1f, 10.0f, 0.1f);

    private static Vec3d cameraPos = Vec3d.ZERO;
    private static float cameraYaw = 0.0f;
    private static float cameraPitch = 0.0f;
    private static boolean active = false;
    private static Vec3d originalPos = Vec3d.ZERO;
    private static float originalYaw = 0.0f;
    private static float originalPitch = 0.0f;
    private static boolean wasPressed = false;

    public Freecam() {
        super("Freecam", Category.OTHER);
    }

    public static void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (!enabled.getValue()) {
            if (active) {
                disableFreecam();
            }
            return;
        }

        boolean isPressed = KeyBindingRegistry.isKeyPressed(KeyBindingRegistry.FREECAM_KEY);

        if (isPressed && !wasPressed) {
            if (active) {
                disableFreecam();
            } else {
                enableFreecam();
            }
        }

        wasPressed = isPressed;

        if (active) {
            updateFreecam();
        }
    }

    private static void enableFreecam() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        originalPos = mc.player.getPos();
        originalYaw = mc.player.getYaw();
        originalPitch = mc.player.getPitch();

        cameraPos = originalPos;
        cameraYaw = originalYaw;
        cameraPitch = originalPitch;

        active = true;
        mc.player.noClip = true;
    }

    private static void disableFreecam() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Restore player position and rotation
        mc.player.setPosition(originalPos);
        mc.player.setYaw(originalYaw);
        mc.player.setPitch(originalPitch);
        
        mc.player.noClip = false;
        active = false;
    }

    private static void updateFreecam() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options == null) return;

        GameOptions options = mc.options;
        float moveSpeed = (float) speed.getValue();

        Vec3d movement = Vec3d.ZERO;

        if (options.forwardKey.isPressed()) {
            movement = movement.add(getForwardVector().multiply(moveSpeed));
        }
        if (options.backKey.isPressed()) {
            movement = movement.add(getForwardVector().multiply(-moveSpeed));
        }
        if (options.leftKey.isPressed()) {
            movement = movement.add(getRightVector().multiply(-moveSpeed));
        }
        if (options.rightKey.isPressed()) {
            movement = movement.add(getRightVector().multiply(moveSpeed));
        }
        if (options.jumpKey.isPressed()) {
            movement = movement.add(0, moveSpeed, 0);
        }
        if (options.sneakKey.isPressed()) {
            movement = movement.add(0, -moveSpeed, 0);
        }

        cameraPos = cameraPos.add(movement);
        
        // Suppress player movement - zero out velocity
        mc.player.setVelocity(0, 0, 0);
        
        // Update player rotation to match camera (so we see correct view)
        mc.player.setYaw(cameraYaw);
        mc.player.setPitch(cameraPitch);
    }

    private static Vec3d getForwardVector() {
        float yawRad = (float) Math.toRadians(cameraYaw);
        float pitchRad = (float) Math.toRadians(cameraPitch);
        return new Vec3d(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );
    }

    private static Vec3d getRightVector() {
        float yawRad = (float) Math.toRadians(cameraYaw);
        return new Vec3d(Math.cos(yawRad), 0, Math.sin(yawRad));
    }

    public static Vec3d getCameraPos() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return active ? cameraPos : (mc.player != null ? mc.player.getPos() : Vec3d.ZERO);
    }

    public static float getCameraYaw() {
        return active ? cameraYaw : (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getYaw() : 0.0f);
    }

    public static float getCameraPitch() {
        return active ? cameraPitch : (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getPitch() : 0.0f);
    }

    public static void handleMouseMovement(double deltaX, double deltaY) {
        if (!active || !enabled.getValue()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;
        float sens = (float) (options.getMouseSensitivity().getValue() * 0.6f + 0.2f);

        cameraYaw += (float) (deltaX * sens);
        cameraPitch = MathHelper.clamp(cameraPitch - (float) (deltaY * sens), -90.0f, 90.0f);
    }

    public static boolean isActive() {
        return enabled.getValue() && active;
    }
}