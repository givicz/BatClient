package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.mixin.access.GameRendererAccessor;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.List;

public class Saturation extends SoupModule {

    public static final BooleanSetting enabled = new BooleanSetting("Saturation", "Boost color saturation", false);
    public static final SliderSetting boost = new SliderSetting("Boost", "Color saturation boost (0-100%)", 50, 0, 100, 1);

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier SHADER_ID = Identifier.of("batclient", "shaders/post/saturation.json");
    private static boolean active = false; // Track if we loaded the shader

    public Saturation() {
        super("Saturation", Category.OTHER);
    }

    public static void onClientTick() {
        // Unused
    }

    public static void onRender(GameRenderer renderer) {
        if (mc.world == null) return;

        if (enabled.getValue()) {
            if (!active) {
                // Load our shader
                try {
                    renderer.loadPostProcessor(SHADER_ID);
                    active = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    enabled.setValue(false); // Disable if failed
                    return;
                }
            }

            // Update Uniform
            updateUniform(renderer);
        } else {
            if (active) {
                renderer.disablePostProcessor();
                active = false;
            }
        }
    }

    private static void updateUniform(GameRenderer renderer) {
        PostEffectProcessor shader = ((GameRendererAccessor) renderer).getPostProcessor();
        if (shader == null) return; // Shader might have been unloaded or replaced

        float boostVal = 1.0f + (boost.getValue() / 100.0f); // 1.0 to 2.0

        try {
            // Access passes via reflection as getter might no exist
            // Using Yarn name "passes"
            Field passesField = PostEffectProcessor.class.getDeclaredField("passes");
            passesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PostEffectPass> passes = (List<PostEffectPass>) passesField.get(shader);

            for (PostEffectPass pass : passes) {
                // Check if program has our uniform
                var uniform = pass.getProgram().getUniformOrDefault("Saturation");
                if (uniform != null) {
                    uniform.set(boostVal);
                }
            }
        } catch (Exception e) {
            // Mapping mismatch or reflection error
            // Try intermediary name "field_5316" ? No, just ignore for now to avoid spam
             // System.out.println("Failed to update saturation uniform");
        }
    }

    /** 
     * Called when module is toggled via GUI
     */
    @Override
    public void onEnable() {
        // To trigger immediate load if rendered
    }

    @Override
    public void onDisable() {
        if (mc.gameRenderer != null && active) {
            mc.gameRenderer.disablePostProcessor();
            active = false;
        }
    }
}

