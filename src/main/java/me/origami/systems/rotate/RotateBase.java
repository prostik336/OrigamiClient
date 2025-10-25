package me.origami.systems.rotate;

import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import java.util.ArrayList;
import java.util.List;

public abstract class RotateBase implements SubModule {
    protected final String name;
    protected final Setting<Boolean> enabled;
    protected final Setting<String> mode;
    protected final Setting<Double> yawStep;
    protected final Setting<Boolean> strictDirection;

    private final List<Setting<?>> settings = new ArrayList<>();

    public RotateBase(String name) {
        this.name = name;
        this.enabled = new Setting<>("Enabled", true, "Enable rotation");
        this.mode = new Setting<>("Mode", "Normal", "Rotation mode", new String[]{"Normal", "Silent", "Packet"});
        this.yawStep = new Setting<>("YawStep", 30.0, "Yaw step", 1.0, 180.0, 1.0);
        this.strictDirection = new Setting<>("StrictDirection", false, "Strict direction");

        settings.add(enabled);
        settings.add(mode);
        settings.add(yawStep);
        settings.add(strictDirection);
    }

    @Override public String getName() { return name; }
    @Override public boolean isEnabled() { return enabled.getValue(); }
    @Override public void setEnabled(boolean enabled) { this.enabled.setValue(enabled); }
    @Override public List<Setting<?>> getSettings() { return settings; }
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}