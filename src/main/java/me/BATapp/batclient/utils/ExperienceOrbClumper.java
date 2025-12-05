package me.BATapp.batclient.utils;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * Experience Orb Clustering - optimizes particle rendering
 * Inspired by Clumps mod
 * Groups nearby experience orbs together to reduce render calls
 */
public class ExperienceOrbClumper {
    private static final double CLUSTER_DISTANCE = 8.0;
    private static final int MAX_CLUSTERS = 256;
    private static List<OrbCluster> clusters = new ArrayList<>();
    
    public static class OrbCluster {
        public Vec3d position;
        public int count;
        public long lastUpdate;
        
        public OrbCluster(Vec3d pos, int count) {
            this.position = pos;
            this.count = count;
            this.lastUpdate = System.currentTimeMillis();
        }
    }
    
    /**
     * Find or create cluster for orb
     */
    public static OrbCluster getCluster(ExperienceOrbEntity orb) {
        Vec3d orbPos = orb.getPos();
        
        // Find nearby cluster
        for (OrbCluster cluster : clusters) {
            if (orbPos.squaredDistanceTo(cluster.position) < CLUSTER_DISTANCE * CLUSTER_DISTANCE) {
                cluster.count++;
                cluster.lastUpdate = System.currentTimeMillis();
                return cluster;
            }
        }
        
        // Create new cluster if space available
        if (clusters.size() < MAX_CLUSTERS) {
            OrbCluster newCluster = new OrbCluster(orbPos, 1);
            clusters.add(newCluster);
            return newCluster;
        }
        
        return null;
    }
    
    /**
     * Update cluster positions
     */
    public static void updateClusters() {
        long currentTime = System.currentTimeMillis();
        clusters.removeIf(cluster -> currentTime - cluster.lastUpdate > 30000); // Remove stale clusters
    }
    
    /**
     * Reset clusters each frame
     */
    public static void resetClusters() {
        for (OrbCluster cluster : clusters) {
            cluster.count = 0;
        }
    }
    
    /**
     * Get cluster statistics
     */
    public static int getClusterCount() {
        return clusters.size();
    }
    
    /**
     * Clear all clusters
     */
    public static void clearClusters() {
        clusters.clear();
    }
}
