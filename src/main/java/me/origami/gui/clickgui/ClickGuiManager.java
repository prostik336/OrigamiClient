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
    public static ClickGuiManager get() {
        if (INSTANCE == null) INSTANCE = new ClickGuiManager();
        return INSTANCE;
    }

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<ClickGuiTab> tabs = new ArrayList<>();
    private ClickGuiTab grabbedTab = null;
    private int grabOffsetX, grabOffsetY;

    // for keybind assignment
    private ModuleComponent waitingBindComponent = null;

    private ClickGuiManager() {

        int startX = 8;
        int startY = 8;
        int gap = 6;
        String[] cats = {"Combat","Exploits","Miscellaneous","Movement","Render","World","Other","Debug"};
        for (int i = 0; i < cats.length; i++) {
            ClickGuiTab t = new ClickGuiTab(cats[i], startX + i * (100 + gap), startY);

            GuiConfig.loadTabPosition(t);
            tabs.add(t);
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

            for (Module m : mm.getModulesByCategory(cat)) {
                tab.addModule(new ModuleComponent(m));
            }
        }
    }
    private ClickGuiTab findTabByTitle(String title) {
        for (ClickGuiTab t : tabs) if (t.getTitle().equalsIgnoreCase(title)) return t;
        return null;
    }

    public void draw(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {

        for (ClickGuiTab t : tabs) {
            t.update(mouseX, mouseY);
            t.draw(ctx, partialTicks);
        }

        if (grabbedTab != null) {
            grabbedTab.setX(mouseX - grabOffsetX);
            grabbedTab.setY(mouseY - grabOffsetY);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {

            for (ClickGuiTab t : tabs) {
                if (t.isMouseOnTitle(mouseX, mouseY)) {
                    grabbedTab = t;
                    grabOffsetX = (int)(mouseX - t.getX());
                    grabOffsetY = (int)(mouseY - t.getY());
                    return;
                }
            }

            for (ClickGuiTab t : tabs) {
                if (t.isMouseInside(mouseX, mouseY)) {
                    if (t.onLeftClick(mouseX, mouseY)) return;
                }
            }
        } else if (button == 1) {

            for (ClickGuiTab t : tabs) {
                if (t.isMouseOnTitle(mouseX, mouseY)) {
                    t.toggleCollapsed();
                    return;
                }
            }
            for (ClickGuiTab t : tabs) {
                if (t.isMouseInside(mouseX, mouseY)) {
                    if (t.onRightClick(mouseX, mouseY)) return;
                }
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            grabbedTab = null;
        }

    }

    public void mouseScrolled(double verticalAmount) {
        for (ClickGuiTab t : tabs) t.setY((int)(t.getY() + verticalAmount * 30));
    }

    public void onClose() {
        // save positions
        for (ClickGuiTab t : tabs) {
            GuiConfig.saveTabPosition(t);
        }
    }


    public void startListeningBind(ModuleComponent comp) {
        waitingBindComponent = comp;
    }

    public boolean handleKeybindListening(int keyCode) {
        if (waitingBindComponent != null) {
            // assign or clear
            if (keyCode == 256) {
                waitingBindComponent.getModule().setKeyBind(-1);
            } else {
                waitingBindComponent.getModule().setKeyBind(keyCode);
            }
            waitingBindComponent = null;
            return true;
        }
        return false;
    }
}
