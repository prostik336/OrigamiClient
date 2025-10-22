package me.origami.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {
    public ClickGuiScreen() {
        super(Text.literal("Origami ClickGUI"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("🌐 Open Browser"), button -> {
            this.client.setScreen(new BrowserScreen(this));
        }).dimensions(this.width / 2 - 100, this.height / 2 - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Modules"), button -> {
            // TODO: Open modules list
        }).dimensions(this.width / 2 - 100, this.height / 2, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Settings"), button -> {
            // TODO: Open settings
        }).dimensions(this.width / 2 - 100, this.height / 2 + 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(this.width / 2 - 100, this.height / 2 + 60, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "Origami Client", this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press RShift to open/close", this.width / 2, this.height / 2 - 45, 0xAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}