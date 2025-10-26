package me.origami.gui.clickgui;

import me.origami.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiTab {
    private final String title;
    private int x, y;
    private final int width = 98;
    private final int headerHeight = 15;
    private boolean collapsed = false;
    private final List<ModuleComponent> modules = new ArrayList<>();

    public ClickGuiTab(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;
    }

    public void draw(DrawContext ctx, float delta) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        ctx.fill(x, y, x + width, y + headerHeight, 0xFF2B2B2B);
        ctx.drawText(tr, title, x + 6, y + 3, 0xFFFFFFFF, false);

        if (!collapsed) {
            int curY = y + headerHeight;
            for (ModuleComponent module : modules) {
                module.draw(ctx, x, curY, width);
                curY += module.getHeight();
            }
        }
    }

    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (mouseInBounds(mouseX, mouseY, x, y, width, headerHeight)) {
            if (button == 1) collapsed = !collapsed;
            return true;
        }
        if (collapsed) return false;

        int curY = y + headerHeight;
        for (ModuleComponent module : modules) {
            int moduleHeight = module.getHeight();
            if (mouseInBounds(mouseX, mouseY, x, curY, width, moduleHeight)) {
                if (button == 0 && mouseInBounds(mouseX, mouseY, x, curY, width, 14)) {
                    module.getModule().toggle();
                    return true;
                }
                if (button == 1 && mouseInBounds(mouseX, mouseY, x, curY, width, 14)) {
                    module.toggleSettings();
                    return true;
                }
                return module.handleClick(mouseX, mouseY, x, curY, button == 1);
            }
            curY += moduleHeight;
        }
        return false;
    }

    public boolean handleMouseDrag(double mouseX, double mouseY) {
        if (collapsed) return false;

        int curY = y + headerHeight;
        for (ModuleComponent module : modules) {
            if (mouseInBounds(mouseX, mouseY, x, curY, width, module.getHeight())) {
                module.handleDrag(mouseX, mouseY, x, curY);
                return true;
            }
            curY += module.getHeight();
        }
        return false;
    }

    public void handleMouseRelease() {
        for (ModuleComponent module : modules) {
            module.stopDrag();
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (collapsed) return false;

        for (ModuleComponent module : modules) {
            if (module.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (collapsed) return false;

        for (ModuleComponent module : modules) {
            if (module.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    private boolean mouseInBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public void addModule(ModuleComponent module) {
        modules.add(module);
    }

    public void clearModules() {
        modules.clear();
    }

    public String getTitle() { return title; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public List<ModuleComponent> getModules() { return modules; }

    public Module getModuleAt(double mouseX, double mouseY) {
        if (collapsed) return null;

        int curY = y + headerHeight;
        for (ModuleComponent module : modules) {
            if (mouseInBounds(mouseX, mouseY, x, curY, width, 14)) {
                return module.getModule();
            }
            curY += module.getHeight();
        }
        return null;
    }

    public boolean toggleSettings() {
        return !collapsed;
    }
}