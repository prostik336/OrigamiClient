package me.origami.module.combat;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;

public class AutoCrystal extends Module {
    // Основные настройки
    public Setting<Boolean> place = this.register(new Setting<>("Place", true, "Enable crystal placing"));
    public Setting<Boolean> breakSetting = this.register(new Setting<>("Break", true, "Enable crystal breaking"));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true, "Enable rotation"));
    public Setting<Boolean> render = this.register(new Setting<>("Render", true, "Enable rendering"));

    // Поднастройки для Place
    public Setting<Double> placeRange = this.register(new Setting<>("PlaceRange", 4.5, "Place range", 1.0, 6.0, 0.1));
    public Setting<Integer> placeDelay = this.register(new Setting<>("PlaceDelay", 2, "Place delay (ticks)", 0, 10, 1));
    public Setting<Boolean> instantPlace = this.register(new Setting<>("InstantPlace", false, "Instant place"));
    public Setting<CrystalMode> placeMode = this.register(new Setting<>("PlaceMode", CrystalMode.NORMAL, "Place mode", CrystalMode.class));

    // Поднастройки для Break
    public Setting<Double> breakRange = this.register(new Setting<>("BreakRange", 4.5, "Break range", 1.0, 6.0, 0.1));
    public Setting<Integer> breakDelay = this.register(new Setting<>("BreakDelay", 2, "Break delay (ticks)", 0, 10, 1));
    public Setting<Boolean> multiBreak = this.register(new Setting<>("MultiBreak", false, "Multi break"));

    // Поднастройки для Rotate
    public Setting<CrystalMode> rotateMode = this.register(new Setting<>("RotateMode", CrystalMode.NORMAL, "Rotation mode", CrystalMode.class));
    public Setting<Double> yawStep = this.register(new Setting<>("YawStep", 30.0, "Yaw step", 1.0, 180.0, 1.0));
    public Setting<Boolean> strictDirection = this.register(new Setting<>("StrictDirection", false, "Strict direction"));

    // Поднастройки для Render
    public Setting<Boolean> renderBox = this.register(new Setting<>("RenderBox", true, "Render crystal box"));
    public Setting<Integer> boxColor = this.register(new Setting<>("BoxColor", 0xFFFF0000, "Box color", 0x00000000, 0xFFFFFFFF, 1));
    public Setting<Boolean> renderText = this.register(new Setting<>("RenderText", false, "Render info text"));
    public Setting<Integer> textColor = this.register(new Setting<>("TextColor", 0xFFFFFFFF, "Text color", 0x00000000, 0xFFFFFFFF, 1));

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and breaks crystals", Category.COMBAT);

        // Добавляем поднастройки к основным настройкам
        place.addSubSetting(placeRange);
        place.addSubSetting(placeDelay);
        place.addSubSetting(instantPlace);
        place.addSubSetting(placeMode);

        breakSetting.addSubSetting(breakRange);
        breakSetting.addSubSetting(breakDelay);
        breakSetting.addSubSetting(multiBreak);

        rotate.addSubSetting(rotateMode);
        rotate.addSubSetting(yawStep);
        rotate.addSubSetting(strictDirection);

        render.addSubSetting(renderBox);
        render.addSubSetting(boxColor);
        render.addSubSetting(renderText);
        render.addSubSetting(textColor);

        // Бинд добавится автоматически в конце
        finishRegistration();
    }

    @Override
    public void onEnable() {
        // Enable logic
    }

    @Override
    public void onDisable() {
        // Disable logic
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        // Tick logic
    }

    enum CrystalMode {
        NORMAL,
        SILENT,
        PACKET
    }
}