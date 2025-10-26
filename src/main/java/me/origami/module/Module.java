package me.origami.module;

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
    private boolean enabled = false;
    private int keyBind = -1;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T> Setting<T> register(Setting<T> setting) {
        settings.add(setting);
        return setting;
    }

    // Автоматически добавляет бинд в конец настроек
    protected void finishRegistration() {
        // Бинд добавляется автоматически как последняя настройка
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

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public List<Setting<?>> getSettings() {
        List<Setting<?>> allSettings = new ArrayList<>(settings);
        // Бинд всегда последний
        allSettings.add(new Setting<>("Bind", keyBind, "Module keybind", -1, 348, 1));
        return allSettings;
    }

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
    }
}