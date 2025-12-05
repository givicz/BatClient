package me.BATapp.batclient.utils;

import net.minecraft.util.Identifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe asset cache for textures and models
 * Prevents redundant asset loading
 */
public class AssetCache {
    private static final Map<String, Object> assetCache = new ConcurrentHashMap<>();
    private static final Map<String, Long> accessTime = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 512;
    private static final long CACHE_EXPIRY = 600000; // 10 minutes
    
    /**
     * Get asset from cache
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAsset(String key, Class<T> type) {
        Object asset = assetCache.get(key);
        
        if (asset != null) {
            // Update access time
            accessTime.put(key, System.currentTimeMillis());
            return (T) asset;
        }
        
        return null;
    }
    
    /**
     * Cache asset
     */
    public static <T> void cacheAsset(String key, T asset) {
        if (assetCache.size() >= MAX_CACHE_SIZE) {
            evictOldest();
        }
        
        assetCache.put(key, asset);
        accessTime.put(key, System.currentTimeMillis());
    }
    
    /**
     * Remove oldest entry
     */
    private static void evictOldest() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, Long> entry : accessTime.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            assetCache.remove(oldestKey);
            accessTime.remove(oldestKey);
        }
    }
    
    /**
     * Clear expired entries
     */
    public static void cleanupExpired() {
        long currentTime = System.currentTimeMillis();
        accessTime.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > CACHE_EXPIRY);
        
        accessTime.keySet().stream()
                .filter(key -> !accessTime.containsKey(key))
                .forEach(assetCache::remove);
    }
    
    /**
     * Clear entire cache
     */
    public static void clearCache() {
        assetCache.clear();
        accessTime.clear();
    }
    
    /**
     * Get cache statistics
     */
    public static int getCacheSize() {
        return assetCache.size();
    }
    
    /**
     * Check if asset is cached
     */
    public static boolean isCached(String key) {
        return assetCache.containsKey(key);
    }
}
