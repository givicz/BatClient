package me.BATapp.batclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

/**
 * Performance Optimization Manager
 * Integrates all optimization modules for coordinated performance improvements
 * Inspired by optimizations from: Lithium, FerriteCore, EntityCulling, Clumps
 */
public class OptimizationManager {
    private static boolean isOptimizationEnabled = true;
    private static long lastCleanup = 0;
    private static final long CLEANUP_INTERVAL = 30000; // 30 seconds
    
    /**
     * Initialize all optimization systems
     */
    public static void initialize() {
        AssetCache.clearCache();
        ExperienceOrbClumper.clearClusters();
        BlockStateCache.clear();
        ModelRenderCache.clear();
    }
    
    /**
     * Update optimization systems each tick
     */
    public static void onClientTick() {
        if (!isOptimizationEnabled) return;
        
        // Update clusters
        ExperienceOrbClumper.updateClusters();
        
        // Periodic cleanup
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanup > CLEANUP_INTERVAL) {
            AssetCache.cleanupExpired();
            lastCleanup = currentTime;
        }
    }
    
    /**
     * Update render systems each frame
     */
    public static void onRenderFrame(Camera camera) {
        if (!isOptimizationEnabled) return;
        
        // Prepare culling data
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.cameraEntity != null) {
            // Culling calculations can be done here
        }
    }
    
    /**
     * Check if entity should be rendered
     */
    public static boolean shouldRenderEntity(Object entity) {
        if (!isOptimizationEnabled) return true;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.cameraEntity != null && entity instanceof net.minecraft.entity.Entity e) {
            return EntityCullingHelper.shouldRenderEntity(e, mc.gameRenderer.getCamera());
        }
        
        return true;
    }
    
    /**
     * Get optimized vertex buffer
     */
    public static float[] getOptimizedVertexBuffer() {
        return RenderOptimizations.acquireVertexBuffer();
    }
    
    /**
     * Release optimized vertex buffer
     */
    public static void releaseOptimizedVertexBuffer(float[] buffer) {
        RenderOptimizations.releaseVertexBuffer(buffer);
    }
    
    /**
     * Get fast math utilities
     */
    public static float fastSin(float angle) {
        return PerformanceUtils.fastSin(angle);
    }
    
    /**
     * Get fast cosine
     */
    public static float fastCos(float angle) {
        return PerformanceUtils.fastCos(angle);
    }
    
    /**
     * Get distance squared (faster than regular distance)
     */
    public static float distanceSquared(float x1, float y1, float z1, 
                                       float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        return dx*dx + dy*dy + dz*dz;
    }
    
    /**
     * Enable/disable optimizations
     */
    public static void setOptimizationEnabled(boolean enabled) {
        isOptimizationEnabled = enabled;
    }
    
    /**
     * Check if optimizations are enabled
     */
    public static boolean isOptimizationEnabled() {
        return isOptimizationEnabled;
    }
    
    /**
     * Print performance statistics
     */
    public static void printPerformanceStats() {
        if (!isOptimizationEnabled) return;
        
        System.out.println("=== BAT Client Performance Stats ===");
        System.out.println("Asset Cache Size: " + AssetCache.getCacheSize());
        System.out.println("Experience Orb Clusters: " + ExperienceOrbClumper.getClusterCount());
        System.out.println("Cached Block States: " + BlockStateCache.getCachedSize());
        System.out.println("Cached Models: " + ModelRenderCache.getCachedModelCount());
        System.out.println("====================================");
    }
    
    /**
     * Shutdown and cleanup all optimization systems
     */
    public static void shutdown() {
        AssetCache.clearCache();
        ExperienceOrbClumper.clearClusters();
        BlockStateCache.clear();
        ModelRenderCache.clear();
    }
}
