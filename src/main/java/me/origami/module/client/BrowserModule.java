package me.origami.module.client;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class BrowserModule extends Module {
    // Settings like in Skeet
    public Setting<Boolean> useMonochrome = this.register(new Setting<>("UseMonochrome", false, "Use monochrome color instead of gradient"));
    public Setting<Integer> monochromeColor = this.register(new Setting<>("MonochromeColor", 0xFFFFFF, "Monochrome color (RGB hex)", 0x000000, 0xFFFFFF, 1));
    public Setting<Double> browserWidth = this.register(new Setting<>("BrowserWidth", 360.0, "Width of browser window", 200.0, 600.0, 10.0));
    public Setting<Double> browserHeight = this.register(new Setting<>("BrowserHeight", 300.0, "Height of browser window", 200.0, 600.0, 10.0));

    public BrowserModule() {
        super("Browser", "Manages browser settings", Category.CLIENT);

        // Всегда в конце - добавляем бинд
        finishRegistration();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}