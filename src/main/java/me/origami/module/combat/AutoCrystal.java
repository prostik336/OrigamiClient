package me.origami.module.combat;

import me.origami.module.Module;
import me.origami.impl.settings.ColorSetting;
import me.origami.impl.settings.Setting;

public class AutoCrystal extends Module {
    // Основные настройки с под-настройками
    public Setting<Boolean> place = register(new Setting<>("Place", true)
            .withSubSettings(
                    new Setting<>("PlaceRange", 4.5, 1.0, 6.0, 0.1),
                    new Setting<>("PlaceDelay", 2, 0, 10, 1),
                    new Setting<>("InstantPlace", false),
                    new Setting<>("PlaceMode", CrystalMode.NORMAL, CrystalMode.class)
            ));

    public Setting<Boolean> breakSetting = register(new Setting<>("Break", true)
            .withSubSettings(
                    new Setting<>("BreakRange", 4.5, 1.0, 6.0, 0.1),
                    new Setting<>("BreakDelay", 2, 0, 10, 1),
                    new Setting<>("MultiBreak", false)
            ));

    public Setting<Boolean> rotate = register(new Setting<>("Rotate", true)
            .withSubSettings(
                    new Setting<>("RotateMode", CrystalMode.NORMAL, CrystalMode.class),
                    new Setting<>("YawStep", 30.0, 1.0, 180.0, 1.0),
                    new Setting<>("StrictDirection", false)
            ));

    public Setting<Boolean> render = register(new Setting<>("Render", true)
            .withSubSettings(
                    new Setting<>("RenderBox", true),
                    new ColorSetting("BoxColor", 0xFFFF0000), // Используем ColorSetting
                    new Setting<>("RenderText", false),
                    new ColorSetting("TextColor", 0xFFFFFFFF) // Используем ColorSetting
            ));

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and breaks crystals", Category.COMBAT);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
    @Override public void onTick() {}

    enum CrystalMode {
        NORMAL,
        SILENT,
        PACKET
    }
}