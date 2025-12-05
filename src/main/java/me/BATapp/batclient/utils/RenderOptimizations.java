package me.BATapp.batclient.utils;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

/**
 * Rendering optimizations for particles and effects
 * Inspired by Lithium rendering improvements
 */
public class RenderOptimizations {
    
    // Vertex buffer pooling to reduce GC pressure
    private static final int POOL_SIZE = 32;
    private static VertexBufferPool[] vertexBufferPools = new VertexBufferPool[POOL_SIZE];
    private static int poolIndex = 0;
    
    /**
     * Efficient quad rendering
     */
    public static void renderQuad(VertexConsumer consumer, Matrix4f matrix,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  float u1, float v1, float u2, float v2,
                                  int r, int g, int b, int a) {
        consumer.vertex(matrix, x1, y1, z1).texture(u1, v1).color(r, g, b, a);
        consumer.vertex(matrix, x2, y2, z2).texture(u2, v1).color(r, g, b, a);
        consumer.vertex(matrix, x3, y3, z3).texture(u2, v2).color(r, g, b, a);
        consumer.vertex(matrix, x4, y4, z4).texture(u1, v2).color(r, g, b, a);
    }
    
    /**
     * Check if object is in view frustum (simple AABB check)
     */
    public static boolean isInViewFrustum(float x, float y, float z, float radius) {
        // Simple distance check (can be optimized with proper frustum)
        float distSquared = x*x + y*y + z*z;
        float maxDist = 256.0f;
        return distSquared < maxDist * maxDist + radius * radius;
    }
    
    /**
     * Interpolate between two colors efficiently
     */
    public static int lerpColorFast(int color1, int color2, float t) {
        t = Math.max(0, Math.min(1, t));
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Vertex buffer pool for reducing allocations
     */
    public static class VertexBufferPool {
        private static final int VERTEX_COUNT = 1024;
        private float[] vertices;
        private boolean inUse;
        
        public VertexBufferPool() {
            this.vertices = new float[VERTEX_COUNT * 8]; // 8 floats per vertex
            this.inUse = false;
        }
        
        public float[] getVertices() {
            return vertices;
        }
        
        public void reset() {
            inUse = false;
        }
    }
    
    /**
     * Acquire vertex buffer from pool
     */
    public static float[] acquireVertexBuffer() {
        // Try to find unused buffer
        for (VertexBufferPool pool : vertexBufferPools) {
            if (pool != null && !pool.inUse) {
                pool.inUse = true;
                return pool.getVertices();
            }
        }
        
        // Create new buffer if needed
        VertexBufferPool newPool = new VertexBufferPool();
        newPool.inUse = true;
        vertexBufferPools[poolIndex] = newPool;
        poolIndex = (poolIndex + 1) % POOL_SIZE;
        
        return newPool.getVertices();
    }
    
    /**
     * Release vertex buffer back to pool
     */
    public static void releaseVertexBuffer(float[] buffer) {
        for (VertexBufferPool pool : vertexBufferPools) {
            if (pool != null && pool.vertices == buffer) {
                pool.reset();
                break;
            }
        }
    }
}
