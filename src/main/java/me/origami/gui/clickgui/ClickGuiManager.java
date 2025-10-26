package me.origami.gui.clickgui;

import me.origami.OrigamiClient;
import me.origami.module.Module;
import me.origami.impl.managers.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiManager {
    private static ClickGuiManager INSTANCE;

    public static ClickGuiManager get() {
        if (INSTANCE == null) INSTANCE = new ClickGuiManager();
        return INSTANCE;
    }

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<ClickGuiTab> tabs = new ArrayList<>();
    private ClickGuiTab draggedTab = null;
    private int dragOffsetX, dragOffsetY;
    private Module bindListeningModule = null;

    public ClickGuiManager() {
        String[] categories = {"Combat", "Movement", "Render", "Misc", "Client"};
        int startX = 8, startY = 8, gap = 6;

        for (int i = 0; i < categories.length; i++) {
            ClickGuiTab tab = new ClickGuiTab(categories[i], startX + i * (100 + gap), startY);
            tabs.add(tab);
        }
    }

    public void loadModulesFromClient() {
        ModuleManager mm = OrigamiClient.MODULE_MANAGER;
        if (mm == null) return;

        for (Module.Category cat : Module.Category.values()) {
            String name = cat.getName();
            ClickGuiTab tab = findTabByTitle(name);

            if (tab == null) {
                tab = new ClickGuiTab(name, 50, 50);
                tabs.add(tab);
            } else {
                tab.clearModules();
            }

            for (Module module : mm.getModulesByCategory(cat)) {
                tab.addModule(new ModuleComponent(module));
            }
        }
    }

    private ClickGuiTab findTabByTitle(String title) {
        for (ClickGuiTab tab : tabs) {
            if (tab.getTitle().equalsIgnoreCase(title)) {
                return tab;
            }
        }
        return null;
    }

    public void draw(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Отрисовка затемнения при установке бинда
        if (bindListeningModule != null) {
            ctx.fill(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0x80000000);
            String text = "Press any key for " + bindListeningModule.getName() + " (ESC to clear)";
            int textWidth = mc.textRenderer.getWidth(text);
            ctx.drawText(mc.textRenderer, text,
                    mc.getWindow().getWidth() / 2 - textWidth / 2,
                    mc.getWindow().getHeight() / 2 - 5,
                    0xFFFFFFFF, true);
        }

        // Отрисовка табов
        for (ClickGuiTab tab : tabs) {
            tab.draw(ctx, delta);
        }

        if (draggedTab != null) {
            draggedTab.setX(mouseX - dragOffsetX);
            draggedTab.setY(mouseY - dragOffsetY);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (bindListeningModule != null) return;

        // Перетаскивание табов
        for (ClickGuiTab tab : tabs) {
            if (mouseInBounds(mouseX, mouseY, tab.getX(), tab.getY(), tab.getWidth(), 15)) {
                if (button == 0) {
                    draggedTab = tab;
                    dragOffsetX = (int) (mouseX - tab.getX());
                    dragOffsetY = (int) (mouseY - tab.getY());
                    return;
                }
            }
        }

        // Обработка кликов в табах
        for (ClickGuiTab tab : tabs) {
            if (tab.handleMouseClick(mouseX, mouseY, button)) {
                return;
            }
        }
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

    public boolean keyPressed(int keyCode) {
        if (bindListeningModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                bindListeningModule.setKeyBind(-1);
            } else {
                bindListeningModule.setKeyBind(keyCode);
            }
            bindListeningModule = null;
            return true;
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

    // Новый метод для начала прослушивания бинда
    public void startBindListening(Module module) {
        bindListeningModule = module;
    }

    // Проверка, идет ли установка бинда
    public boolean isBinding() {
        return bindListeningModule != null;
    }

    private boolean mouseInBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public void onClose() {
        bindListeningModule = null;
    }

    public List<ClickGuiTab> getTabs() {
        return tabs;
    }
}