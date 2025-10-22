package me.origami.gui;

import me.origami.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindScreen extends Screen {
    private final Screen parent;
    private final Module module;

    public KeybindScreen(Screen parent, Module module) {
        super(Text.literal("Set Keybind: " + module.getName()));
        this.parent = parent;
        this.module = module;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(20, 20, 60, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press a key to set keybind for " + module.getName(), this.width / 2, this.height / 2, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        module.setKeyBind(keyCode);
        if (this.client != null && this.client.player != null) {
            this.client.player.sendMessage(Text.literal("§6Keybind for " + module.getName() + " set to: " + GLFW.glfwGetKeyName(keyCode, scanCode)), false);
        }
        this.client.setScreen(this.parent);
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}