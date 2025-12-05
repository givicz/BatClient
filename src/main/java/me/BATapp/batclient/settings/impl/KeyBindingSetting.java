package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBindingSetting extends Setting<Integer> {
    private KeyBinding keyBinding;

    public KeyBindingSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
        this.keyBinding = new KeyBinding(
                getTranslationKey(),
                InputUtil.Type.KEYSYM,
                defaultKey,
                "batclient.category.other"
        );
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value);
        if (keyBinding != null) {
            keyBinding.setBoundKey(InputUtil.fromKeyCode(value, 0));
        }
    }

    public boolean isPressed() {
        return keyBinding != null && keyBinding.isPressed();
    }

    public boolean wasPressed() {
        if (keyBinding == null) return false;
        boolean pressed = keyBinding.wasPressed();
        if (pressed) {
            keyBinding.setPressed(false);
        }
        return pressed;
    }
}

