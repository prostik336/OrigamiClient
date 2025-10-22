package me.origami.module.misc;

import me.origami.impl.settings.Setting;
import me.origami.module.Module;

import java.util.ArrayList;
import java.util.List;

public class BetterChat extends Module {

    public BetterChat() {
        super("BetterChat", "Improves chat functionality", Category.MISC);
        initializeSettings();
    }

    private void initializeSettings() {
        Setting<Boolean> arrowPrefix = new Setting<>("ArrowPrefix", true, "Add '>' prefix to chat messages");
        List<Setting<?>> settings = new ArrayList<>();
        settings.add(arrowPrefix);
        setSettings(settings);
    }

    @Override
    public void onEnable() {
        // Логика при включении модуля
    }

    @Override
    public void onDisable() {
        // Логика при выключении модуля
    }
}