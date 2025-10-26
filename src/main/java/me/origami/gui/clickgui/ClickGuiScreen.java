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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        manager.draw(drawContext, mouseX, mouseY, delta);
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
        manager.updateSettingDrag(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        manager.mouseScrolled(dy);
        return super.mouseScrolled(mouseX, mouseY, dx, dy);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (manager.hasSelectedStringSetting()) {
            manager.handleCharTyped(chr);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (manager.handleKeybindListening(keyCode)) {
            return true;
        }

        if (manager.hasSelectedStringSetting()) {
            if (keyCode == 259) { // Backspace
                manager.handleBackspace();
                return true;
            }
            if (keyCode == 257 || keyCode == 256) { // Enter or Escape
                manager.finishStringEditing();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        super.close();
        manager.onClose();
    }
}