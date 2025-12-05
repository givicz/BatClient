package me.BATapp.batclient.font;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class FontRenderers {
    public static FontRenderer modules;
    public static FontRenderer sf_bold;
    public static FontRenderer sf_bold_17;
    public static FontRenderer sf_bold_12;
    public static FontRenderer sf_bold_mini;
    public static FontRenderer sf_medium;
    public static FontRenderer minecraft;

    /**
     * ✅ HLAVNÍ INICIALIZAČNÍ METODA - volat v onInitializeClient()
     */
    public static void initialize(MinecraftClient client) {
        if (sf_bold != null) return; // Již inicializováno

        try {
            System.out.println("BATclient: Loading custom fonts...");
            sf_bold = create(20, "sf_bold");
            sf_bold_17 = create(17, "sf_bold");
            sf_bold_12 = create(12, "sf_bold");
            sf_bold_mini = create(8, "sf_bold");
            sf_medium = create(16, "sf_medium");
            modules = create(15, "sf_bold");
            minecraft = create(16, "sf_bold");
            System.out.println("BATclient: Fonts loaded successfully!");
        } catch (Exception e) {
            System.err.println("BATclient: Failed to load fonts!");
            e.printStackTrace();
            // Fonty zůstanou null - v rendererech se to ošetří
        }
    }

    public static @NotNull FontRenderer create(float size, String name) throws IOException, FontFormatException {
        // ✅ FIX: Použijeme FontRenderers.class místo BATclient_Main.class
        return new FontRenderer(
                Font.createFont(Font.TRUETYPE_FONT,
                        Objects.requireNonNull(
                                FontRenderers.class.getClassLoader().getResourceAsStream(
                                        "assets/batclient/fonts/" + name + ".ttf"
                                )
                        )
                ).deriveFont(Font.PLAIN, size / 2f),
                size / 2f
        );
    }
}