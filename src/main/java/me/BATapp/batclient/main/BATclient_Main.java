package me.BATapp.batclient.main;

import me.BATapp.batclient.config.BATclient_Config;
import me.BATapp.batclient.utils.OptimizationManager;
import me.BATapp.batclient.utils.UIElementManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BATclient_Main implements ModInitializer {
    public static long initTime;
    public static String ac = "";

    public static final SimpleParticleType STAR = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        // Configuration now managed via BATSettingsScreen only
        registerParticles();
        
        // Initialize performance optimizations
        OptimizationManager.initialize();
        
        // Load UI element positions
        UIElementManager.loadPositions();
        
        // Register UI elements
        UIElementManager.registerElement("keystroke_display", 10, 10, 100, 100);
        UIElementManager.registerElement("fps_display", 10, 30, 60, 20);
    }

    private void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("batclient", "star"), STAR);
    }
}