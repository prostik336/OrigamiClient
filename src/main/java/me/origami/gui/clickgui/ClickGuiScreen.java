package me.origami.gui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {
    private final ClickGuiManager manager;

    public ClickGuiScreen() {
        super(Text.literal("Origami ClickGui"));
        this.manager = ClickGuiManager.get();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        System.out.println("ClickGuiScreen render: mouseX=" + mouseX + ", mouseY=" + mouseY);
        manager.draw(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("ClickGuiScreen mouseClicked: mouseX=" + mouseX + ", mouseY=" + mouseY + ", button=" + button);
        boolean handled = manager.mouseClicked(mouseX, mouseY, button);
        System.out.println("ClickGuiScreen mouseClicked handled: " + handled);
        return handled || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        System.out.println("ClickGuiScreen mouseReleased: mouseX=" + mouseX + ", mouseY=" + mouseY + ", button=" + button);
        manager.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        System.out.println("ClickGuiScreen mouseDragged: mouseX=" + mouseX + ", mouseY=" + mouseY + ", button=" + button);
        manager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        System.out.println("ClickGuiScreen keyPressed: keyCode=" + keyCode);
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
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
        System.out.println("ClickGuiScreen charTyped: chr=" + chr);
        if (manager.handleCharTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        super.close();
    }
}