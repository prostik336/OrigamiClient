package me.origami.systems.swapping;

import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import java.util.ArrayList;
import java.util.List;

public abstract class SwapBase implements SubModule {
    protected final String name;
    protected final Setting<Boolean> enabled;
    protected final Setting<String> mode;
    protected final Setting<Integer> swapDelay;
    protected final Setting<Boolean> silentSwap;

    private final List<Setting<?>> settings = new ArrayList<>();

    public SwapBase(String name) {
        this.name = name;
        this.enabled = new Setting<>("Enabled", true, "Enable swapping");
        this.mode = new Setting<>("Mode", "Normal", "Swap mode", new String[]{"Normal", "Silent", "Packet"});
        this.swapDelay = new Setting<>("Delay", 1, "Swap delay (ticks)", 0, 5, 1);
        this.silentSwap = new Setting<>("Silent", true, "Silent swap");

        settings.add(enabled);
        settings.add(mode);
        settings.add(swapDelay);
        settings.add(silentSwap);
    }

    @Override public String getName() { return name; }
    @Override public boolean isEnabled() { return enabled.getValue(); }
    @Override public void setEnabled(boolean enabled) { this.enabled.setValue(enabled); }
    @Override public List<Setting<?>> getSettings() { return settings; }
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}