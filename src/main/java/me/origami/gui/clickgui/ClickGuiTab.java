package me.origami.gui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiTab {
    private final String title;
    private int x, y;
    private final int width = 98;
    private final int headerHeight = 15;
    private boolean collapsed = false;
    private final List<ModuleComponent> children = new ArrayList<>();

    public ClickGuiTab(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;
    }

    public void update(double mouseX, double mouseY) {
        // можно анимации добавить позже
    }

    public void draw(DrawContext ctx, float partialTicks) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        // header background
        ctx.fill(x, y - 1, x + width, y + headerHeight, 0xFF2E2E2E);
        // title
        ctx.drawText(tr, title, x + 6, y + 3, 0xFFFFFFFF, false);
        // border
        ctx.fill(x, y - 1, x + 1, y + headerHeight, 0xFF8B2B2B);
        ctx.fill(x + width - 1, y - 1, x + width, y + headerHeight, 0xFF8B2B2B);

        if (!collapsed) {
            int curY = y + headerHeight;
            for (ModuleComponent child : children) {
                child.draw(ctx, x, curY, width);
                curY += child.getHeight();
            }
        }
    }

    public boolean isMouseOnTitle(double mx, double my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + headerHeight;
    }

    public boolean isMouseInside(double mx, double my) {
        int bottom = y + headerHeight;
        if (!collapsed) {
            for (ModuleComponent c : children) bottom += c.getHeight();
        }
        return mx >= x && mx <= x + width && my >= y && my <= bottom;
    }

    public boolean onLeftClick(double mouseX, double mouseY) {
        if (collapsed) return false;
        int curY = y + headerHeight;
        for (ModuleComponent c : children) {
            if (mouseY >= curY && mouseY <= curY + c.getHeight()) {
                c.onLeftClick();
                return true;
            }
            curY += c.getHeight();
        }
        return false;
    }

    public boolean onRightClick(double mouseX, double mouseY) {
        if (collapsed) return false;
        int curY = y + headerHeight;
        for (ModuleComponent c : children) {
            if (mouseY >= curY && mouseY <= curY + c.getHeight()) {
                c.onRightClick();
                return true;
            }
            curY += c.getHeight();
        }
        return false;
    }

    public void toggleCollapsed() { collapsed = !collapsed; }

    public void addModule(ModuleComponent comp) { children.add(comp); }

    public void clearModules() { children.clear(); }

    public String getTitle() { return title; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
}
