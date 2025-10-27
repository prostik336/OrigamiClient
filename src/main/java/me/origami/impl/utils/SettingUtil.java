package me.origami.impl.utils;

import me.origami.impl.settings.Setting;

public class SettingUtil {

    public static String getValueDisplay(Setting<?> setting) {
        Object value = setting.getValue();
        if (value instanceof Double) return String.format("%.1f", (Double) value);
        if (value instanceof Boolean) return (Boolean) value ? "ON" : "OFF";
        if (value instanceof Enum<?>) return ((Enum<?>) value).name();
        return value.toString();
    }
}