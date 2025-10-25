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
    private ClickGuiTab grabbedTab = null;
    private int grabOffsetX, grabOffsetY;

    // Bind system fix
    private Module bindListeningModule = null;
    private boolean isListeningForBind = false;

    private ClickGuiManager() {
        int startX = 8;
        int startY = 8;
        int gap = 6;
        String[] cats = {"Combat","Movement","Render","Misc","Client"};
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
        // Draw bind listening overlay
        if (isListeningForBind && bindListeningModule != null) {
            ctx.fill(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0x80000000);
            String text = "Press any key for " + bindListeningModule.getName() + " (ESC to clear)";
            int textWidth = mc.textRenderer.getWidth(text);
            ctx.drawText(mc.textRenderer, text,
                    mc.getWindow().getWidth() / 2 - textWidth / 2,
                    mc.getWindow().getHeight() / 2 - 5,
                    0xFFFFFFFF, true);
        }

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
        if (isListeningForBind) return;

        if (button == 0) {
            // Check for tab dragging first
            for (ClickGuiTab t : tabs) {
                if (t.isMouseOnTitle(mouseX, mouseY)) {
                    grabbedTab = t;
                    grabOffsetX = (int)(mouseX - t.getX());
                    grabOffsetY = (int)(mouseY - t.getY());
                    return;
                }
            }

            // Check for module clicks
            for (ClickGuiTab t : tabs) {
                if (t.isMouseInside(mouseX, mouseY)) {
                    if (t.onLeftClick(mouseX, mouseY)) return;
                }
            }

            // Check for submodule clicks
            for (ClickGuiTab t : tabs) {
                if (t.isMouseInside(mouseX, mouseY)) {
                    if (t.handleSubModuleClick(mouseX, mouseY)) return;
                }
            }
        } else if (button == 1) {
            // Right click for settings/submodules
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
        } else if (button == 2) { // Middle click for bind
            for (ClickGuiTab t : tabs) {
                if (t.isMouseInside(mouseX, mouseY)) {
                    Module module = t.getModuleAt(mouseX, mouseY);
                    if (module != null) {
                        startBindListening(module);
                        return;
                    }
                }
            }
        }
    }

    // NEW: Start bind listening with middle click
    public void startBindListening(Module module) {
        bindListeningModule = module;
        isListeningForBind = true;
    }

    // NEW: Handle key input for bind
    public boolean handleKeybindListening(int keyCode) {
        if (isListeningForBind && bindListeningModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                bindListeningModule.setKeyBind(-1); // Clear bind
            } else {
                bindListeningModule.setKeyBind(keyCode); // Set bind
            }
            stopBindListening();
            return true;
        }
        return false;
    }

    // NEW: Stop bind listening
    public void stopBindListening() {
        isListeningForBind = false;
        bindListeningModule = null;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            grabbedTab = null;
        }
    }

    public void mouseScrolled(double verticalAmount) {
        if (isListeningForBind) return;
        for (ClickGuiTab t : tabs) t.setY((int)(t.getY() + verticalAmount * 30));
    }

    public void onClose() {
        for (ClickGuiTab t : tabs) {
            GuiConfig.saveTabPosition(t);
        }
        stopBindListening();
    }
}