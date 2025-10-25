package me.origami.module;

import me.origami.impl.managers.ModuleManager;
import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false; // FIXED: Initialize as false
    private int keyBind = -1;
    private List<Setting<?>> settings;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.settings = new ArrayList<>();
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int keyBind) { this.keyBind = keyBind; }
    public List<Setting<?>> getSettings() { return settings; }
    public void setSettings(List<Setting<?>> settings) { this.settings = settings != null ? settings : new ArrayList<>(); }

    public Text getDisplayName() {
        return Text.literal(name);
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        MISC("Misc"),
        PLAYER("Player"),
        CLIENT("Client"),
        HUD("HUD");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() { return name; }

        // в OrigamiClient.java



    }

}