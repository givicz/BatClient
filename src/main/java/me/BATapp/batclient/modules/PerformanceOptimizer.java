package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;

public class PerformanceOptimizer extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable performance optimizations", true);
    public static final BooleanSetting afkDetection = new BooleanSetting("AFK Detection", "Reduce FPS when AFK", true);
    public static final SliderSetting afkTime = new SliderSetting("AFK Time", "Seconds before considered AFK", 5.0f, 1.0f, 30.0f, 1.0f);
    public static final SliderSetting afkFpsLimit = new SliderSetting("AFK FPS Limit", "FPS limit when AFK", 10.0f, 5.0f, 60.0f, 5.0f);

    public static final BooleanSetting optimizeBlockRendering = new BooleanSetting("Optimize Block Rendering", "Don't render hidden blocks", true);
    public static final BooleanSetting optimizeEntityRendering = new BooleanSetting("Optimize Entity Rendering", "Don't render hidden entity parts", true);
    public static final BooleanSetting minimizeF3Debug = new BooleanSetting("Minimize F3 Debug", "Show only FPS, Coords, Direction", true);

    private static Vec3d lastPlayerPos = Vec3d.ZERO;
    private static float lastPlayerYaw = 0f;
    private static float lastPlayerPitch = 0f;
    private static long lastMovementTime = System.currentTimeMillis();
    private static boolean isAFK = false;
    private static int originalMaxFps = 0;
    private static long lastPacketTime = System.currentTimeMillis();

    public PerformanceOptimizer() {
        super("Performance Optimizer", Category.OTHER);
    }

    public static void onTick() {
        if (!enabled.getValue() || mc.player == null) {
            if (isAFK) restoreFps();
            return;
        }

        if (afkDetection.getValue()) {
            handleAFKDetection();
        }
    }

    private static void handleAFKDetection() {
        if (mc.player == null) return;

        PlayerEntity player = mc.player;
        Vec3d currentPos = player.getPos();
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();
        double posDistance = currentPos.distanceTo(lastPlayerPos);
        double yawDiff = Math.abs(currentYaw - lastPlayerYaw);
        double pitchDiff = Math.abs(currentPitch - lastPlayerPitch);
        long timeSinceLastPacket = System.currentTimeMillis() - lastPacketTime;
        long afkThresholdMs = (long) (afkTime.getValue() * 1000.0f);

        // Check for actual activity: position change OR camera rotation (yaw/pitch)
        boolean hasMovement = posDistance > 0.01 || yawDiff > 0.5 || pitchDiff > 0.5;

        if (hasMovement) {
            // Player moved or looked around
            lastMovementTime = System.currentTimeMillis();
            lastPlayerPos = currentPos;
            lastPlayerYaw = currentYaw;
            lastPlayerPitch = currentPitch;
            if (isAFK) restoreFps();
            isAFK = false;
        } else if (timeSinceLastPacket < 500) {
            // Player is sending packets (arm swinging, etc.) but not moving/looking
            // This counts as activity - don't reduce FPS
            lastMovementTime = System.currentTimeMillis();
            if (isAFK) restoreFps();
            isAFK = false;
        } else {
            // No movement, no rotation, no packets - check if AFK
            long timeSinceMovement = System.currentTimeMillis() - lastMovementTime;

            if (timeSinceMovement > afkThresholdMs && !isAFK) {
                reduceFps();
                isAFK = true;
            }
        }
    }

    /**
     * Call this from ClientPlayNetworkHandler to track incoming packets
     */
    public static void onPacketReceived() {
        lastPacketTime = System.currentTimeMillis();
    }

    private static void reduceFps() {
        if (mc.options == null) return;

        GameOptions options = mc.options;
        originalMaxFps = options.getMaxFps().getValue();

        // Správný a bezpečný převod float → int (zaokrouhlení + clamp)
        int targetFps = Math.round(afkFpsLimit.getValue());
        targetFps = MathHelper.clamp(targetFps, 5, 60); // Zajistí, že hodnota zůstane v rozmezí slideru

        options.getMaxFps().setValue(targetFps);
    }

    private static void restoreFps() {
        if (mc.options == null || originalMaxFps == 0) return;

        mc.options.getMaxFps().setValue(originalMaxFps);
        originalMaxFps = 0;
        isAFK = false;
    }

    public static boolean shouldOptimizeBlockRendering() {
        return enabled.getValue() && optimizeBlockRendering.getValue();
    }

    public static boolean shouldOptimizeEntityRendering() {
        return enabled.getValue() && optimizeEntityRendering.getValue();
    }

    public static boolean shouldMinimizeF3Debug() {
        return enabled.getValue() && minimizeF3Debug.getValue();
    }
}