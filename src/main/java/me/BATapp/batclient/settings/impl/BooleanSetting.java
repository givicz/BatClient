package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }
}

