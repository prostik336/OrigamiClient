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
        // Существующие настройки
        Setting<Integer> gradientStartColor = new Setting<>("GradientStartColor", 0xFF0000, "Starting color of gradient (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Integer> gradientEndColor = new Setting<>("GradientEndColor", 0x0000FF, "Ending color of gradient (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Boolean> useMonochrome = new Setting<>("UseMonochrome", false, "Use monochrome color instead of gradient");
        Setting<Integer> monochromeColor = new Setting<>("MonochromeColor", 0xFFFFFF, "Monochrome color (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Double> browserWidth = new Setting<>("BrowserWidth", 360.0, "Width of browser window", 200.0, 600.0, 10.0);
        Setting<Double> browserHeight = new Setting<>("BrowserHeight", 300.0, "Height of browser window", 200.0, 600.0, 10.0);
        Setting<Double> buttonWidth = new Setting<>("ButtonWidth", 50.0, "Width of buttons", 30.0, 100.0, 5.0);
        Setting<Double> buttonHeight = new Setting<>("ButtonHeight", 20.0, "Height of buttons", 15.0, 40.0, 5.0);
        Setting<Integer> textColor = new Setting<>("TextColor", 0xFFFFFF, "Color of text (RGB hex)", 0x000000, 0xFFFFFF, 1);

        // Новые настройки для кнопок
        Setting<Integer> buttonOutlineColor = new Setting<>("ButtonOutlineColor", 0xFFFFFF, "Color of button outlines (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Boolean> useGradientForButtons = new Setting<>("UseGradientForButtons", true, "Use gradient for buttons instead of solid color");
        Setting<Integer> buttonStartColor = new Setting<>("ButtonStartColor", 0x1E3A8A, "Start color for button gradient (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Integer> buttonEndColor = new Setting<>("ButtonEndColor", 0x7E22CE, "End color for button gradient (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Integer> buttonSolidColor = new Setting<>("ButtonSolidColor", 0x3A5DDA, "Solid color for buttons (RGB hex)", 0x000000, 0xFFFFFF, 1);
        Setting<Integer> buttonTransparency = new Setting<>("ButtonTransparency", 255, "Button transparency (0-255)", 0, 255, 1);

        List<Setting<?>> settings = new ArrayList<>();
        settings.add(gradientStartColor);
        settings.add(gradientEndColor);
        settings.add(useMonochrome);
        settings.add(monochromeColor);
        settings.add(browserWidth);
        settings.add(browserHeight);
        settings.add(buttonWidth);
        settings.add(buttonHeight);
        settings.add(textColor);

        // Добавляем новые настройки
        settings.add(buttonOutlineColor);
        settings.add(useGradientForButtons);
        settings.add(buttonStartColor);
        settings.add(buttonEndColor);
        settings.add(buttonSolidColor);
        settings.add(buttonTransparency);

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