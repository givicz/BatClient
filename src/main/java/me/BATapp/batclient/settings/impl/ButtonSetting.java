package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;

public class ButtonSetting extends Setting<Void> {

    private final Runnable onClick;

    public ButtonSetting(String name, String description, Runnable onClick) {
        super(name, description, null);
        this.onClick = onClick;
    }

    public void click() {
        if (onClick != null) {
            onClick.run();
        }
    }
}

