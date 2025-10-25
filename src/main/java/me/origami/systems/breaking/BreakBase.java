package me.origami.systems.breaking;

import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import java.util.ArrayList;
import java.util.List;

public abstract class BreakBase implements SubModule {
    protected final String name;
    protected final Setting<Boolean> enabled;
    protected final Setting<Double> breakRange;
    protected final Setting<Integer> breakDelay;
    protected final Setting<Boolean> multiBreak;

    private final List<Setting<?>> settings = new ArrayList<>();

    public BreakBase(String name) {
        this.name = name;
        this.enabled = new Setting<>("Enabled", true, "Enable breaking");
        this.breakRange = new Setting<>("Range", 4.5, "Break range", 1.0, 6.0, 0.1);
        this.breakDelay = new Setting<>("Delay", 2, "Break delay (ticks)", 0, 10, 1);
        this.multiBreak = new Setting<>("MultiBreak", false, "Multi break");

        settings.add(enabled);
        settings.add(breakRange);
        settings.add(breakDelay);
        settings.add(multiBreak);
    }

    @Override public String getName() { return name; }
    @Override public boolean isEnabled() { return enabled.getValue(); }
    @Override public void setEnabled(boolean enabled) { this.enabled.setValue(enabled); }
    @Override public List<Setting<?>> getSettings() { return settings; }
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}