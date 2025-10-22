package me.origami.api.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final Category category;
    private boolean enabled;
    private int keyBind = -1;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
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
    public String getDescription() {
        return getClass().getSimpleName() + " module for " + category.getName().toLowerCase() + " features";
    }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int keyBind) { this.keyBind = keyBind; }

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