// BrowserModule.java
package me.origami.module.client;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;

public class BrowserModule extends Module {
    public Setting<Boolean> useMonochrome = register(new Setting<>("UseMonochrome", false));
    public Setting<Integer> monochromeColor = register(new Setting<>("MonochromeColor", 0xFFFFFF, 0x000000, 0xFFFFFF, 1));
    public Setting<Double> browserWidth = register(new Setting<>("BrowserWidth", 360.0, 200.0, 600.0, 10.0));
    public Setting<Double> browserHeight = register(new Setting<>("BrowserHeight", 300.0, 200.0, 600.0, 10.0));

    public BrowserModule() {
        super("Browser", "Manages browser settings", Category.CLIENT);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
}