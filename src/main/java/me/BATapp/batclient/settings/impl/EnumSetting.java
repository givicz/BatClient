package me.BATapp.batclient.settings.impl;

import me.BATapp.batclient.settings.Setting;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final Class<E> enumClass;

    public EnumSetting(String name, String description, E defaultValue, Class<E> enumClass) {
        super(name, description, defaultValue);
        this.enumClass = enumClass;
    }

    public E[] getValues() {
        return enumClass.getEnumConstants();
    }
}

