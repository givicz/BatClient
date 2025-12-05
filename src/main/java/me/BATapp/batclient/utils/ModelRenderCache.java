package me.BATapp.batclient.utils;

import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Model rendering optimization cache
 * Inspired by Entity_Model_Features caching
 * Reduces redundant model lookups and rendering
 */
public class ModelRenderCache {
    private static final int MAX_MODELS = 64;
    private static ModelCacheEntry[] modelCache = new ModelCacheEntry[MAX_MODELS];
    private static int cacheIndex = 0;
    
    private static class ModelCacheEntry {
        EntityType<?> entityType;
        Model model;
        long lastAccessed;
        int accessCount;
        
        ModelCacheEntry(EntityType<?> type, Model model) {
            this.entityType = type;
            this.model = model;
            this.lastAccessed = System.nanoTime();
            this.accessCount = 1;
        }
    }
    
    /**
     * Get cached model or null if not found
     */
    public static Model getCachedModel(EntityType<?> entityType) {
        // Check cache
        for (ModelCacheEntry entry : modelCache) {
            if (entry != null && entry.entityType == entityType) {
                entry.lastAccessed = System.nanoTime();
                entry.accessCount++;
                return entry.model;
            }
        }
        return null;
    }
    
    /**
     * Cache a model
     */
    public static void cacheModel(EntityType<?> entityType, Model model) {
        // Check if already cached
        for (ModelCacheEntry entry : modelCache) {
            if (entry != null && entry.entityType == entityType) {
                entry.model = model;
                entry.lastAccessed = System.nanoTime();
                entry.accessCount++;
                return;
            }
        }
        
        // Add to cache
        modelCache[cacheIndex] = new ModelCacheEntry(entityType, model);
        cacheIndex = (cacheIndex + 1) % MAX_MODELS;
    }
    
    /**
     * Check if model is cached
     */
    public static boolean isCached(EntityType<?> entityType) {
        for (ModelCacheEntry entry : modelCache) {
            if (entry != null && entry.entityType == entityType) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Clear cache
     */
    public static void clear() {
        for (int i = 0; i < modelCache.length; i++) {
            modelCache[i] = null;
        }
        cacheIndex = 0;
    }
    
    /**
     * Get cache statistics
     */
    public static int getCachedModelCount() {
        int count = 0;
        for (ModelCacheEntry entry : modelCache) {
            if (entry != null) count++;
        }
        return count;
    }
    
    /**
     * Get most accessed models
     */
    public static void printCacheStats() {
        int total = 0;
        for (ModelCacheEntry entry : modelCache) {
            if (entry != null) {
                total += entry.accessCount;
            }
        }
    }
}
