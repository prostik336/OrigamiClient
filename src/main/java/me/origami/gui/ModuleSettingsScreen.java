package me.origami.gui;

import me.origami.api.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Text.literal("Settings: " + module.getName()));
        this.parent = parent;
        this.module = module;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(20, 20, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(module.isEnabled() ? "Disable" : "Enable"),
                button -> {
                    module.toggle();
                    button.setMessage(Text.literal(module.isEnabled() ? "Disable" : "Enable"));
                }
        ).dimensions(this.width / 2 - 100, 60, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Info"), button -> {
            showModuleInfo();
        }).dimensions(this.width / 2 - 100, 90, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Keybind"), button -> {
            setKeybind();
        }).dimensions(this.width / 2 + 5, 90, 95, 20).build());
    }

    private void showModuleInfo() {
        if (this.client != null && this.client.player != null) {
            this.client.player.sendMessage(Text.literal("§6Module: §e" + module.getName()), false);
            this.client.player.sendMessage(Text.literal("§6Description: §7" + module.getDescription()), false);
            this.client.player.sendMessage(Text.literal("§6Category: §b" + module.getCategory().getName()), false);
            this.client.player.sendMessage(Text.literal("§6Status: " + (module.isEnabled() ? "§aENABLED" : "§cDISABLED")), false);
        }
    }

    private void setKeybind() {
        if (this.client != null && this.client.player != null) {
            this.client.player.sendMessage(Text.literal("§6Press any key to set as keybind for " + module.getName()), false);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int categoryColor = getCategoryColor(module.getCategory());
        String colorCode = String.format("§#%06x", categoryColor & 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer, colorCode + "§l" + module.getName() + " Settings", this.width / 2, 30, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "§7" + module.getDescription(), this.width / 2, 45, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, "§8Category: " + module.getCategory().getName(), this.width / 2, 55, 0x666666);

        super.render(context, mouseX, mouseY, delta);
    }

    private int getCategoryColor(Module.Category category) {
        switch (category) {
            case COMBAT: return 0xFF5555;
            case MOVEMENT: return 0x55FF55;
            case RENDER: return 0x5555FF;
            case MISC: return 0xFFFF55;
            case PLAYER: return 0xFF55FF;
            case CLIENT: return 0x00FFFF;
            case HUD: return 0xFFAA00;
            default: return 0xFFFFFF;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}