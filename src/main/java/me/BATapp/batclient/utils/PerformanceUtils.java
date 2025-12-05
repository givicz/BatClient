package me.BATapp.batclient.utils;

/**
 * Performance optimization utilities
 * Inspired by Lithium, FerriteCore, and other optimization mods
 */
public class PerformanceUtils {
    
    // Fast math utilities to avoid expensive calculations
    public static final float[] SIN_TABLE = new float[0x10000];
    public static final float[] COS_TABLE = new float[0x10000];
    
    static {
        // Pre-compute sin/cos tables for faster lookups
        for (int i = 0; i < 0x10000; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 0x10000);
            COS_TABLE[i] = (float) Math.cos((double) i * Math.PI * 2.0D / 0x10000);
        }
    }
    
    /**
     * Fast sin lookup - avoids expensive Math.sin() calls
     */
    public static float fastSin(float angle) {
        return SIN_TABLE[(int) (angle * 0x10000 / (Math.PI * 2)) & 0xFFFF];
    }
    
    /**
     * Fast cos lookup - avoids expensive Math.cos() calls
     */
    public static float fastCos(float angle) {
        return COS_TABLE[(int) (angle * 0x10000 / (Math.PI * 2)) & 0xFFFF];
    }
    
    /**
     * Fast Math.abs() for integers
     */
    public static int fastAbs(int value) {
        int mask = value >> 31;
        return (value ^ mask) - mask;
    }
    
    /**
     * Object pooling helper for frequently created objects
     */
    public static class ObjectPool<T> {
        private final java.util.Queue<T> pool;
        private final java.util.function.Supplier<T> factory;
        private final int maxSize;
        
        public ObjectPool(java.util.function.Supplier<T> factory, int maxSize) {
            this.factory = factory;
            this.maxSize = maxSize;
            this.pool = new java.util.LinkedList<>();
        }
        
        public T acquire() {
            T obj = pool.poll();
            return obj != null ? obj : factory.get();
        }
        
        public void release(T obj) {
            if (pool.size() < maxSize) {
                pool.offer(obj);
            }
        }
    }
    
    /**
     * Calculate distance squared (avoids expensive sqrt)
     */
    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Clamp value between min and max
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }
    
    /**
     * Clamp value between min and max (int version)
     */
    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }
    
    /**
     * Linear interpolation
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp(t, 0, 1);
    }
    
    /**
     * Smooth step interpolation
     */
    public static float smoothstep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0, 1);
        return t * t * (3 - 2 * t);
    }
}
