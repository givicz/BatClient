package me.BATapp.batclient;

import me.BATapp.batclient.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SoupModule {

    protected final String translationKey;
    protected final Category category;

    public static MinecraftClient mc = MinecraftClient.getInstance();

    public SoupModule(String name, Category category) {
        this.translationKey = "batclient.module." + name.toLowerCase(Locale.ROOT).replace(" ", "_");
        this.category = category;
    }

    public List<Setting<?>> getSettings() {
        List<Setting<?>> result = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!Setting.class.isAssignableFrom(field.getType())) continue;

            field.setAccessible(true);
            try {
                Setting<?> setting = (Setting<?>) field.get(this);
                if (setting != null) {
                    result.add(setting);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Text getDisplayName() {
        return Text.translatable(translationKey);
    }

    public Category getCategory() {
        return category;
    }

    public enum Category {
        HUD("batclient.category.hud"),
        PARTICLES("batclient.category.particles"),
        WORLD("batclient.category.world"),
        OTHER("batclient.category.other"),
        CONFIG("batclient.category.config");

        private final String translationKey;

        Category(String key) {
            this.translationKey = key;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}