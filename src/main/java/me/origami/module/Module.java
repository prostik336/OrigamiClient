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
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T> Setting<T> register(Setting<T> setting) {
        settings.add(setting);
        setting.setParentModule(this); // УСТАНАВЛИВАЕМ РОДИТЕЛЬСКИЙ МОДУЛЬ
        return setting;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    // НОВЫЙ МЕТОД: вызывается когда настройка изменяется
    public void onSettingChanged(Setting<?> setting) {
        // Переопределяется в конкретных модулях
        System.out.println("Setting changed in " + getName() + ": " + setting.getName() + " = " + setting.getValue());
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<Setting<?>> getSettings() {
        return new ArrayList<>(settings);
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