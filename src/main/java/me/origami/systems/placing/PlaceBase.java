package me.origami.systems.placing;

import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceBase implements SubModule {
    protected final String name;
    protected final Setting<Boolean> enabled;
    protected final Setting<Double> placeRange;
    protected final Setting<Boolean> instantPlace;
    protected final Setting<String> placeMode;
    protected final Setting<Boolean> raytrace;
    protected final Setting<Integer> placeDelay;

    private final List<Setting<?>> settings = new ArrayList<>();

    public PlaceBase(String name) {
        this.name = name;
        this.enabled = new Setting<>("Enabled", true, "Enable placing");
        this.placeRange = new Setting<>("Range", 5.0, "Place range", 1.0, 6.0, 0.1);
        this.instantPlace = new Setting<>("InstantPlace", false, "Instant place");
        this.placeMode = new Setting<>("Mode", "Normal", "Place mode", new String[]{"Normal", "Silent", "Packet"});
        this.raytrace = new Setting<>("Raytrace", true, "Raytrace check");
        this.placeDelay = new Setting<>("Delay", 2, "Place delay (ticks)", 0, 10, 1);

        settings.add(enabled);
        settings.add(placeRange);
        settings.add(instantPlace);
        settings.add(placeMode);
        settings.add(raytrace);
        settings.add(placeDelay);
    }

    @Override public String getName() { return name; }
    @Override public boolean isEnabled() { return enabled.getValue(); }
    @Override public void setEnabled(boolean enabled) { this.enabled.setValue(enabled); }
    @Override public List<Setting<?>> getSettings() { return settings; }
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}