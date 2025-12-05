package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String description, String defaultValue) {
        super(name, description, defaultValue);
    }
}

