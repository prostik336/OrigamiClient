package me.origami.gui.clickgui;

import me.origami.OrigamiClient;
import me.origami.module.Module;
import me.origami.impl.managers.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiManager {
    private static ClickGuiManager INSTANCE;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<ClickGuiTab> tabs = new ArrayList<>();
    private ClickGuiTab draggedTab = null;
    private int dragOffsetX, dragOffsetY;

    public static ClickGuiManager get() {
        if (INSTANCE == null) INSTANCE = new ClickGuiManager();
        return INSTANCE;
    }

    public ClickGuiManager() {
        // Создаем табы для всех категорий
        int startX = 8, startY = 8, gap = 6;
        int i = 0;

        for (Module.Category category : Module.Category.values()) {
            ClickGuiTab tab = new ClickGuiTab(category.getName(), startX + i * (100 + gap), startY);
            tabs.add(tab);
            i++;
        }
        loadModulesFromClient();
    }

    public void loadModulesFromClient() {
        ModuleManager mm = OrigamiClient.MODULE_MANAGER;
        if (mm == null) return;

        for (ClickGuiTab tab : tabs) {
            tab.clearModules();

            for (Module.Category category : Module.Category.values()) {
                if (tab.getTitle().equals(category.getName())) {
                    for (Module module : mm.getModulesByCategory(category)) {
                        tab.addModule(new ModuleComponent(module));
                    }
                }
            }
        }
    }

    public void draw(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (ClickGuiTab tab : tabs) {
            tab.draw(ctx, delta);
        }

        // Обновляем позицию перетаскиваемого таба
        if (draggedTab != null) {
            draggedTab.setX(mouseX - dragOffsetX);
            draggedTab.setY(mouseY - dragOffsetY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Перетаскивание табов
        for (ClickGuiTab tab : tabs) {
            if (mouseInBounds(mouseX, mouseY, tab.getX(), tab.getY(), tab.getWidth(), 15)) {
                if (button == 0) {
                    draggedTab = tab;
                    dragOffsetX = (int) (mouseX - tab.getX());
                    dragOffsetY = (int) (mouseY - tab.getY());
                    return true;
                }
            }
        }

        // Обработка кликов в табах
        for (ClickGuiTab tab : tabs) {
            if (tab.handleMouseClick(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggedTab = null;
            for (ClickGuiTab tab : tabs) {
                tab.handleMouseRelease();
            }
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggedTab != null) return;

        if (button == 0) {
            for (ClickGuiTab tab : tabs) {
                if (tab.handleMouseDrag(mouseX, mouseY)) {
                    return;
                }
            }
        }
    }

    private boolean mouseInBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public List<ClickGuiTab> getTabs() {
        return tabs;
    }

    public boolean keyPressed(int keyCode) {
        for (ClickGuiTab tab : tabs) {
            if (tab.keyPressed(keyCode, 0, 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (ClickGuiTab tab : tabs) {
            if (tab.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleCharTyped(char chr, int modifiers) {
        for (ClickGuiTab tab : tabs) {
            if (tab.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }
}