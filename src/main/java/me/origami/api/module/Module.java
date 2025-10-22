package me.origami.api.module;

import me.origami.api.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private String name;
    private String description;
    private String category;  // Используем String, ссылаясь на Category
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public boolean isOn() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public <T> Setting<T> register(Setting<T> setting) {
        settings.add(setting);
        return setting;
    }
}