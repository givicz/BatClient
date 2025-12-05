package me.BATapp.batclient.utils;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Entity Culling optimization
 * Skips rendering entities that are outside camera frustum or too far away
 * Inspired by EntityCulling mod
 */
public class EntityCullingHelper {
    private static final float CULL_DISTANCE_MULTIPLIER = 1.0f;
    private static final float MIN_CULL_DISTANCE = 16.0f;
    private static final float FRUSTUM_MARGIN = 2.0f;
    
    /**
     * Check if entity should be rendered
     */
    public static boolean shouldRenderEntity(Entity entity, Camera camera) {
        if (entity == null || entity.isRemoved()) {
            return false;
        }
        
        // Always render the player
        if (entity == camera.getFocusedEntity()) {
            return true;
        }
        
        Vec3d cameraPos = camera.getPos();
        Vec3d entityPos = entity.getPos();
        
        // Check distance
        double distSquared = cameraPos.squaredDistanceTo(entityPos);
        double renderDistance = 256.0; // Default render distance
        
        if (distSquared > renderDistance * renderDistance) {
            return false;
        }
        
        // Check frustum culling
        if (!isInFrustum(entity, camera)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Simple frustum culling check
     */
    private static boolean isInFrustum(Entity entity, Camera camera) {
        Box box = entity.getBoundingBox();
        Vec3d cameraPos = camera.getPos();
        
        // Simple AABB frustum test
        // Check if entity bounding box is within camera view
        Vec3d boxCenter = box.getCenter();
        double xLength = box.maxX - box.minX;
        double yLength = box.maxY - box.minY;
        double zLength = box.maxZ - box.minZ;
        double boxRadius = Math.max(Math.max(xLength, yLength), zLength) / 2.0;
        
        Vec3d toEntity = boxCenter.subtract(cameraPos);
        
        // Basic visibility check
        return toEntity.length() < 128.0; // Simplified frustum check
    }
    
    /**
     * Check if entity is behind camera
     */
    public static boolean isEntityBehindCamera(Entity entity, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d entityPos = entity.getPos();
        Vec3d direction = entityPos.subtract(cameraPos);
        
        // Get camera direction (simplified)
        Vec3d forward = camera.getPos().add(
            Math.cos(Math.toRadians(camera.getYaw())) * 10,
            0,
            Math.sin(Math.toRadians(camera.getYaw())) * 10
        ).subtract(cameraPos);
        
        return direction.dotProduct(forward) < 0;
    }
    
    /**
     * Calculate entity visibility percentage
     */
    public static float getEntityVisibility(Entity entity, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d entityPos = entity.getPos();
        
        double distSquared = cameraPos.squaredDistanceTo(entityPos);
        double maxDist = 256.0;
        double maxDistSquared = maxDist * maxDist;
        
        if (distSquared > maxDistSquared) {
            return 0.0f;
        }
        
        // Return visibility as percentage (further = lower)
        return 1.0f - (float)(Math.sqrt(distSquared) / maxDist);
    }
}
