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
        
        // Always render the player or ridden entity
        if (entity == camera.getFocusedEntity() || entity.hasPassenger(camera.getFocusedEntity())) {
            return true;
        }
        
        Vec3d cameraPos = camera.getPos();
        Vec3d entityPos = entity.getPos();
        
        // Check distance squared
        double distSquared = cameraPos.squaredDistanceTo(entityPos);
        double renderDistance = 256.0; // Default render distance (16 chunks)
        // Optimization: Use separate squared distance check for culling
        if (distSquared > renderDistance * renderDistance) {
            return false;
        }
        
        // Check if entity is behind camera (Back-face culling for entities)
        // Only cull if significantly behind to support high FOV
        if (isEntityBehindCamera(entity, camera)) {
             return false;
        }

        return true;
    }
    
    // Deprecated: Incorrect implementation
    // private static boolean isInFrustum(Entity entity, Camera camera) { ... }
    
    /**
     * Check if entity is behind camera
     */
    public static boolean isEntityBehindCamera(Entity entity, Camera camera) {
        // Calculate relative position
        double dx = entity.getX() - camera.getPos().x;
        double dy = entity.getY() - camera.getPos().y;
        double dz = entity.getZ() - camera.getPos().z;
        
        // Calculate camera forward vector from yaw/pitch
        // Optimization: Inlined vector calculation to avoid allocation
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        
        float f = (float)Math.PI / 180F;
        float g = -net.minecraft.util.math.MathHelper.sin(yaw * f) * net.minecraft.util.math.MathHelper.cos(pitch * f);
        float h = -net.minecraft.util.math.MathHelper.sin(pitch * f);
        float k = net.minecraft.util.math.MathHelper.cos(yaw * f) * net.minecraft.util.math.MathHelper.cos(pitch * f);

        // Dot product
        double dot = dx * g + dy * h + dz * k;
        
        // Cull only if dot product is significantly negative (behind)
        // -2.0 tolerance allows entities slightly behind the strict plane (e.g. large hitboxes intersecting camera)
        return dot < -5.0; 
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
