package me.origami.systems;

import me.origami.impl.settings.Setting;
import java.util.List;

public interface SubModule {
    String getName();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    void onEnable();
    void onDisable();
    void onTick();
    List<Setting<?>> getSettings();
}