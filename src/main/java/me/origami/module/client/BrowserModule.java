package me.origami.module.client;

import me.origami.impl.settings.Setting;
import me.origami.module.Module;

import java.util.ArrayList;
import java.util.List;

public class BrowserModule extends Module {
    public BrowserModule() {
        super("Browser", "Manages browser settings", Category.CLIENT);
        initializeSettings();
    }

    private void initializeSettings() {
        List<Setting<?>> settings = new ArrayList<>();

        // Только основные настройки
        Setting<Boolean> useMonochrome = new Setting<>("UseMonochrome", false, "Use monochrome color instead of gradient");
        Setting<Integer> monochromeColor = new Setting<>("MonochromeColor", 0xFFFFFF, "Monochrome color (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Double> browserWidth = new Setting<>("BrowserWidth", 360.0, "Width of browser window", 200.0, 600.0, 10.0);
        Setting<Double> browserHeight = new Setting<>("BrowserHeight", 300.0, "Height of browser window", 200.0, 600.0, 10.0);

        settings.add(useMonochrome);
        settings.add(monochromeColor);
        settings.add(browserWidth);
        settings.add(browserHeight);

        setSettings(settings);
    }

    @Override
    public void onEnable() {
        // Optional: Add logic when enabled
    }

    @Override
    public void onDisable() {
        // Optional: Add logic when disabled
    }
}