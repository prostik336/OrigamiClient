package me.origami.gui.clickgui;

import me.origami.OrigamiClient;
import me.origami.module.Module;
import me.origami.impl.managers.ModuleManager;
import me.origami.impl.settings.Setting;
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

    // Systems
    private Module bindListeningModule = null;
    private boolean isListeningForBind = false;
    private boolean isDraggingSetting = false;
    private Setting<?> selectedStringSetting = null;
    private StringBuilder stringBuilder = new StringBuilder();

    private ClickGuiManager() {
        // Create tabs
        String[] categories = {"Combat", "Movement", "Render", "Misc", "Client"};
        int startX = 8, startY = 8, gap = 6;

        for (int i = 0; i < categories.length; i++) {
            ClickGuiTab tab = new ClickGuiTab(categories[i], startX + i * (100 + gap), startY);
            GuiConfig.loadTabPosition(tab);
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

    public void draw(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
        // Overlays
        if (isListeningForBind && bindListeningModule != null) {
            drawBindOverlay(ctx);
        }

        if (hasSelectedStringSetting()) {
            drawStringEditOverlay(ctx);
        }

        // Tabs
        for (ClickGuiTab tab : tabs) {
            tab.draw(ctx, partialTicks);
        }

        // Tab dragging
        if (grabbedTab != null) {
            grabbedTab.setX(mouseX - grabOffsetX);
            grabbedTab.setY(mouseY - grabOffsetY);
        }
    }

    private void drawBindOverlay(DrawContext ctx) {
        ctx.fill(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0x80000000);
        String text = "Press any key for " + bindListeningModule.getName() + " (ESC to clear)";
        int textWidth = mc.textRenderer.getWidth(text);
        ctx.drawText(mc.textRenderer, text,
                mc.getWindow().getWidth() / 2 - textWidth / 2,
                mc.getWindow().getHeight() / 2 - 5,
                0xFFFFFFFF, true);
    }

    private void drawStringEditOverlay(DrawContext ctx) {
        ctx.fill(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0x80000000);
        String text = "Editing " + selectedStringSetting.getName() + ": " + stringBuilder.toString() + "_";
        String hint = "Press ENTER to confirm, ESC to cancel";

        int textWidth = mc.textRenderer.getWidth(text);
        int hintWidth = mc.textRenderer.getWidth(hint);

        ctx.drawText(mc.textRenderer, text,
                mc.getWindow().getWidth() / 2 - textWidth / 2,
                mc.getWindow().getHeight() / 2 - 15,
                0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, hint,
                mc.getWindow().getWidth() / 2 - hintWidth / 2,
                mc.getWindow().getHeight() / 2 + 5,
                0xFFCCCCCC, true);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isListeningForBind || hasSelectedStringSetting()) return;

        switch (button) {
            case 0 -> handleLeftClick(mouseX, mouseY);
            case 1 -> handleRightClick(mouseX, mouseY);
            case 2 -> handleMiddleClick(mouseX, mouseY);
        }
    }

    private void handleLeftClick(double mouseX, double mouseY) {
        // Tab dragging
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseOnTitle(mouseX, mouseY)) {
                grabbedTab = tab;
                grabOffsetX = (int) (mouseX - tab.getX());
                grabOffsetY = (int) (mouseY - tab.getY());
                return;
            }
        }

        // Module and setting clicks
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseInside(mouseX, mouseY)) {
                if (tab.onLeftClick(mouseX, mouseY)) return;
            }
        }

        // Setting drag start
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseInside(mouseX, mouseY)) {
                isDraggingSetting = true;
                return;
            }
        }
    }

    private void handleRightClick(double mouseX, double mouseY) {
        // Tab collapsing
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseOnTitle(mouseX, mouseY)) {
                tab.toggleCollapsed();
                return;
            }
        }

        // Module and setting right clicks
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseInside(mouseX, mouseY)) {
                if (tab.onRightClick(mouseX, mouseY)) return;
            }
        }
    }

    private void handleMiddleClick(double mouseX, double mouseY) {
        // Bind listening
        for (ClickGuiTab tab : tabs) {
            if (tab.isMouseInside(mouseX, mouseY)) {
                Module module = tab.getModuleAt(mouseX, mouseY);
                if (module != null) {
                    startBindListening(module);
                    return;
                }
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            grabbedTab = null;
            isDraggingSetting = false;

            for (ClickGuiTab tab : tabs) {
                tab.handleMouseRelease();
            }
        }
    }

    public void updateSettingDrag(double mouseX, double mouseY) {
        if (isDraggingSetting) {
            for (ClickGuiTab tab : tabs) {
                if (tab.isMouseInside(mouseX, mouseY)) {
                    tab.handleSettingDrag(mouseX, mouseY);
                    return;
                }
            }
        }
    }

    // Bind system
    public void startBindListening(Module module) {
        bindListeningModule = module;
        isListeningForBind = true;
    }

    public boolean handleKeybindListening(int keyCode) {
        if (isListeningForBind && bindListeningModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                bindListeningModule.setKeyBind(-1);
            } else {
                bindListeningModule.setKeyBind(keyCode);
            }
            stopBindListening();
            return true;
        }
        return false;
    }

    public void stopBindListening() {
        isListeningForBind = false;
        bindListeningModule = null;
    }

    // String editing
    public boolean hasSelectedStringSetting() {
        return selectedStringSetting != null;
    }

    public void startStringEditing(Setting<?> setting, String currentValue) {
        selectedStringSetting = setting;
        stringBuilder.setLength(0);
        stringBuilder.append(currentValue);
    }

    public void handleCharTyped(char chr) {
        if (selectedStringSetting != null) {
            stringBuilder.append(chr);
            selectedStringSetting.setValue(stringBuilder.toString());
        }
    }

    public void handleBackspace() {
        if (selectedStringSetting != null && stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            selectedStringSetting.setValue(stringBuilder.toString());
        }
    }

    public void finishStringEditing() {
        selectedStringSetting = null;
        stringBuilder.setLength(0);
    }

    // Scrolling
    public void mouseScrolled(double verticalAmount) {
        if (isListeningForBind || hasSelectedStringSetting()) return;

        for (ClickGuiTab tab : tabs) {
            tab.setY((int) (tab.getY() + verticalAmount * 30));
        }
    }

    public void onClose() {
        for (ClickGuiTab tab : tabs) {
            GuiConfig.saveTabPosition(tab);
        }
        stopBindListening();
        finishStringEditing();
    }
}