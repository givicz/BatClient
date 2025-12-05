package me.BATapp.batclient.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.gui.BATSettingsScreen;
import me.BATapp.batclient.settings.Setting;
import me.BATapp.batclient.settings.impl.*;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config", "batclient_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, Map<String, Object>> config = new HashMap<>();
    private static Map<String, String> metadata = new HashMap<>();

    public static void saveConfig() {
        Map<String, Object> fullData = new HashMap<>();

        // Собираем настройки модулей
        Map<String, Map<String, Object>> modulesConfig = new HashMap<>();
        for (SoupModule module : BATSettingsScreen.getAllModules()) {
            modulesConfig.put(module.getClass().getSimpleName(), getStringObjectMap(module));
        }

        fullData.put("modules", modulesConfig);
        fullData.put("meta", metadata);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.write(CONFIG_PATH, GSON.toJson(fullData).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static @NotNull Map<String, Object> getStringObjectMap(SoupModule module) {
        Map<String, Object> moduleSettings = new HashMap<>();
        for (Setting<?> setting : module.getSettings()) {
            Object value = setting.getValue();
            if (setting instanceof EnumSetting<?> enumSetting) {
                value = value.toString();
            } else if (setting instanceof MinMaxSliderSetting minMaxSetting) {
                value = new float[]{minMaxSetting.getMinValue(), minMaxSetting.getMaxValue()};
            }
            moduleSettings.put(setting.getName(), value);
        }
        return moduleSettings;
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) return;

        try {
            String json = new String(Files.readAllBytes(CONFIG_PATH));
            if (json.trim().isEmpty()) return;

            JsonObject fullData = JsonParser.parseString(json).getAsJsonObject();

            JsonObject modulesJson = fullData.getAsJsonObject("modules");
            JsonObject metaJson = fullData.getAsJsonObject("meta");

            Type modulesType = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> modulesConfig = GSON.fromJson(modulesJson, modulesType);
            config = Objects.requireNonNullElseGet(modulesConfig, HashMap::new);

            if (metaJson != null) {
                Type metaType = new TypeToken<Map<String, String>>(){}.getType();
                metadata = GSON.fromJson(metaJson, metaType);
            }

            for (SoupModule module : BATSettingsScreen.getAllModules()) {
                Map<String, Object> moduleSettings = config.get(module.getClass().getSimpleName());
                if (moduleSettings == null) continue;

                for (Setting<?> setting : module.getSettings()) {
                    Object value = moduleSettings.get(setting.getName());
                    if (value != null) {
                        try {
                            switch (setting) {
                                case BooleanSetting booleanSetting -> booleanSetting.setValue((Boolean) value);
                                case SliderSetting sliderSetting ->
                                        sliderSetting.setValue(((Double) value).floatValue());
                                case EnumSetting<?> enumSetting -> {
                                    Enum<?>[] values = enumSetting.getValues();
                                    for (Enum<?> enumValue : values) {
                                        if (enumValue.toString().equals(value)) {
                                            ((EnumSetting) enumSetting).setValue(enumValue);
                                            break;
                                        }
                                    }
                                }
                                case MinMaxSliderSetting minMaxSetting -> {
                                    double[] array = (double[]) value;
                                    minMaxSetting.setMinValue((float) array[0]);
                                    minMaxSetting.setMaxValue((float) array[1]);
                                }
                                case ColorSetting colorSetting -> {
                                    if (value instanceof Double) {
                                        colorSetting.setValue(((Double) value).intValue());
                                    } else if (value instanceof Integer) {
                                        colorSetting.setValue((Integer) value);
                                    }
                                }
                                case KeyBindingSetting keyBindingSetting -> {
                                    if (value instanceof Double) {
                                        keyBindingSetting.setValue(((Double) value).intValue());
                                    } else if (value instanceof Integer) {
                                        keyBindingSetting.setValue((Integer) value);
                                    }
                                }
                                case StringSetting stringSetting -> stringSetting.setValue((String) value);
                                default -> {
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
    }

    public static void setMetadata(String key, String value) {
        if (value == null) metadata.remove(key);
        else metadata.put(key, value);
    }

    public static String getMetadata(String key) {
        return metadata.get(key);
    }
}
