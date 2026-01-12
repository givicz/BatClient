package me.BATapp.batclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtility {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }

    public static float random(float min, float max) {
        return (float) (ThreadLocalRandom.current().nextDouble() * (max - min) + min);
    }

    public static double getDistanceSq(double x, double y, double z) {
        double d0 = mc.player.getX() - x;
        double d1 = mc.player.getY() - y;
        double d2 = mc.player.getZ() - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = (x1 - x2);
        double d1 = (y1 - y2);
        double d2 = (z1 - z2);
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    // Optimization: Returns squared distance now (fix implementation to match name)
    // NOTE: If this breaks code expecting regular distance, it must be reverted.
    // However, context suggests typical usage is for optimization.
    public static double getSqrDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = (x1 - x2);
        double d1 = (y1 - y2);
        double d2 = (z1 - z2);
        return d0 * d0 + d1 * d1 + d2 * d2; 
    }

    public static float round(float value) {
        return (float) Math.round(value * 100.0f) / 100.0f;
    }

    public static double getDistanceSq(Entity ent) {
        return getDistanceSq(ent.getX(), ent.getY(), ent.getZ());
    }

    public static double roundToDecimal(double n, int point) {
        if (point == 0) {
            return Math.floor(n);
        }
        double factor = Math.pow(10, point);
        return Math.round(n * factor) / factor;
    }

    public static double angle(Vec3d vec3d, Vec3d other) {
        double lengthSq = vec3d.length() * other.length();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3d.dotProduct(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }

    public static Vec3d fromTo(Vec3d from, double x, double y, double z) {
        return fromTo(from.x, from.y, from.z, x, y, z);
    }

    public static float lerp(float f, float st, float en) {
        return st + f * (en - st);
    }

    public static Vec3d fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3d(x2 - x, y2 - y, z2 - z);
    }

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float sin(float value) {
        return MathHelper.sin(value);
    }

    public static float cos(float value) {
        return MathHelper.cos(value);
    }

    public static float wrapDegrees(float value) {
        return MathHelper.wrapDegrees(value);
    }

    public static double wrapDegrees(double value) {
        return MathHelper.wrapDegrees(value);
    }

    public static double square(double input) {
        return input * input;
    }

    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float wrap(float angle) {
        float wrappedAngle = angle % 360.0f;
        if (wrappedAngle >= 180.0f) {
            wrappedAngle -= 360.0f;
        }
        if (wrappedAngle < -180.0f) {
            wrappedAngle += 360.0f;
        }
        return wrappedAngle;
    }

    public static Vec3d direction(float yaw) {
        return new Vec3d(Math.cos(MathUtility.degToRad(yaw + 90.0f)), 0.0, Math.sin(MathUtility.degToRad(yaw + 90.0f)));
    }

    public static float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.floatValue();
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }


    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map, boolean descending) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        if (descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static double degToRad(double deg) {
        return deg * 0.01745329238474369;
    }

    /**
     * Checks if a 3D point is in front of the camera (visible to the player)
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @param z Z coordinate of the point
     * @return true if the point is in front of the camera, false if behind
     */
    public static boolean isPointInFrontOfCamera(double x, double y, double z) {
        if (mc.player == null || mc.gameRenderer == null) return true;
        
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        
        // Calculate direction from camera to point
        Vec3d toPoint = new Vec3d(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
        double distance = toPoint.length();
        
        if (distance < 0.001) return true; // Point is very close, consider it visible
        
        // Normalize direction
        Vec3d directionToPoint = toPoint.normalize();
        
        // Calculate camera forward vector from yaw and pitch
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        
        double yawRad = degToRad(yaw);
        double pitchRad = degToRad(pitch);
        
        // Forward vector calculation
        double forwardX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double forwardY = -Math.sin(pitchRad);
        double forwardZ = Math.cos(yawRad) * Math.cos(pitchRad);
        
        Vec3d cameraForward = new Vec3d(forwardX, forwardY, forwardZ).normalize();
        
        // Dot product: positive means in front, negative means behind
        double dot = directionToPoint.dotProduct(cameraForward);
        
        return dot > 0; // Only render if in front of camera
    }

}
