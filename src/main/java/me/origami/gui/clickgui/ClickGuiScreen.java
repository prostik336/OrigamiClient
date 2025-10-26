package me.origami.gui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    private final ClickGuiManager manager;

    public ClickGuiScreen() {
        super(Text.literal("Origami ClickGui"));
        this.manager = ClickGuiManager.get();
        manager.loadModulesFromClient();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        manager.draw(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        manager.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        manager.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        manager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (manager.keyPressed(keyCode)) {
            return true;
        }
        if (manager.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (manager.handleCharTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        super.close();
        manager.onClose();
    }
}