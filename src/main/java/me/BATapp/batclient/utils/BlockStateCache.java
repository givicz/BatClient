package me.BATapp.batclient.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block state cache for efficient lookups
 * Reduces block state retrieval overhead
 * Inspired by FerriteCore block state caching
 */
public class BlockStateCache {
    private static final int CACHE_SIZE = 256;
    private static BlockStateEntry[] cache = new BlockStateEntry[CACHE_SIZE];
    private static int cacheIndex = 0;
    
    private static class BlockStateEntry {
        long posLong; // Packed position
        BlockState state;
        long timestamp;
        
        BlockStateEntry(long pos, BlockState state) {
            this.posLong = pos;
            this.state = state;
            this.timestamp = System.nanoTime();
        }
    }
    
    /**
     * Get block state with caching
     */
    public static BlockState getBlockState(World world, BlockPos pos) {
        long posLong = pos.asLong();
        
        // Check cache first
        for (BlockStateEntry entry : cache) {
            if (entry != null && entry.posLong == posLong) {
                // Cache hit
                return entry.state;
            }
        }
        
        // Cache miss - get from world
        BlockState state = world.getBlockState(pos);
        
        // Store in cache (round-robin)
        cache[cacheIndex] = new BlockStateEntry(posLong, state);
        cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
        
        return state;
    }
    
    /**
     * Invalidate specific cache entry
     */
    public static void invalidate(BlockPos pos) {
        long posLong = pos.asLong();
        
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] != null && cache[i].posLong == posLong) {
                cache[i] = null;
            }
        }
    }
    
    /**
     * Clear entire cache
     */
    public static void clear() {
        for (int i = 0; i < cache.length; i++) {
            cache[i] = null;
        }
        cacheIndex = 0;
    }
    
    /**
     * Get cache hit rate for debugging
     */
    public static int getCachedSize() {
        int count = 0;
        for (BlockStateEntry entry : cache) {
            if (entry != null) count++;
        }
        return count;
    }
}
