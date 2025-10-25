package me.origami.systems.rendering;

import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import java.util.ArrayList;
import java.util.List;

public abstract class RenderBase implements SubModule {
    protected final String name;
    protected final Setting<Boolean> enabled;
    protected final Setting<Boolean> renderBox;
    protected final Setting<Integer> boxColor;
    protected final Setting<Boolean> renderText;
    protected final Setting<Integer> textColor;

    private final List<Setting<?>> settings = new ArrayList<>();

    public RenderBase(String name) {
        this.name = name;
        this.enabled = new Setting<>("Enabled", true, "Enable rendering");
        this.renderBox = new Setting<>("RenderBox", true, "Render crystal box");
        this.boxColor = new Setting<>("BoxColor", 0xFFFF0000, "Box color", 0x00000000, 0xFFFFFFFF, 1);
        this.renderText = new Setting<>("RenderText", false, "Render info text");
        this.textColor = new Setting<>("TextColor", 0xFFFFFFFF, "Text color", 0x00000000, 0xFFFFFFFF, 1);

        settings.add(enabled);
        settings.add(renderBox);
        settings.add(boxColor);
        settings.add(renderText);
        settings.add(textColor);
    }

    @Override public String getName() { return name; }
    @Override public boolean isEnabled() { return enabled.getValue(); }
    @Override public void setEnabled(boolean enabled) { this.enabled.setValue(enabled); }
    @Override public List<Setting<?>> getSettings() { return settings; }
    @Override public void onEnable() {}
    @Override public void onDisable() {}
}