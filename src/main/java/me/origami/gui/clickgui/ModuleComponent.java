package me.origami.gui.clickgui;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ModuleComponent {
    private final Module module;
    private int height = 14;
    private boolean settingsOpen = false;
    private boolean subModulesOpen = false;
    private int width; // храним ширину

    public ModuleComponent(Module module) {
        this.module = module;
    }

    public void draw(DrawContext ctx, int x, int y, int width) {
        this.width = width; // сохраняем ширину
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int bg = module.isEnabled() ? 0xFF4B1A1A : 0xFF1F1F1F;
        ctx.fill(x + 1, y, x + width - 1, y + height, bg);
        ctx.drawText(tr, module.getName(), x + 6, y + 3, 0xFFFFFFFF, false);

        // Draw bind text
        String bindText = getBindText();
        ctx.drawText(tr, bindText, x + width - tr.getWidth(bindText) - 4, y + 3, 0xFFCCCCCC, false);

        int curY = y + height;

        // Draw submodules if module supports them
        if (subModulesOpen && hasSubModules()) {
            List<SubModule> subModules = getSubModules();
            for (SubModule sub : subModules) {
                drawSubModule(ctx, sub, x, curY, width);
                curY += 14;

                // Draw submodule settings if enabled
                if (sub.isEnabled()) {
                    for (Setting<?> setting : sub.getSettings()) {
                        ctx.fill(x + 5, curY, x + width - 5, curY + 12, 0xFF333333);
                        ctx.drawText(tr, setting.getName() + ": " + setting.getValue(), x + 10, curY + 2, 0xFFFFFFFF, false);
                        curY += 12;
                    }
                }
            }
        }

        // Draw main module settings if open
        if (settingsOpen) {
            List<Setting<?>> settings = module.getSettings();
            for (Setting<?> s : settings) {
                ctx.fill(x + 1, curY, x + width - 1, curY + 14, 0xFF252525);
                String settingText = s.getName() + ": " + s.getValue();
                ctx.drawText(tr, settingText, x + 8, curY + 3, 0xFFFFFFFF, false);
                curY += 14;
            }
        }
    }

    private void drawSubModule(DrawContext ctx, SubModule sub, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int bg = sub.isEnabled() ? 0xFF3A2A2A : 0xFF2A2A2A;
        ctx.fill(x + 2, y, x + width - 2, y + 12, bg);
        String status = sub.isEnabled() ? "ON" : "OFF";
        ctx.drawText(tr, "> " + sub.getName() + ": " + status, x + 8, y + 2, 0xFFFFFFFF, false);
    }

    private boolean hasSubModules() {
        try {
            var method = module.getClass().getMethod("getSubModules");
            return method.invoke(module) instanceof List;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private List<SubModule> getSubModules() {
        try {
            var method = module.getClass().getMethod("getSubModules");
            Object result = method.invoke(module);
            if (result instanceof List) {
                return (List<SubModule>) result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private String getBindText() {
        int bind = module.getKeyBind();
        if (bind == -1) return "[]";

        String keyName = getKeyName(bind);
        return "[" + keyName + "]";
    }

    private String getKeyName(int keyCode) {
        switch (keyCode) {
            case 32: return "SPACE";
            case 340: return "SHIFT";
            case 341: return "CTRL";
            case 342: return "ALT";
            case 256: return "ESC";
            case 257: return "ENTER";
            case 258: return "TAB";
            default:
                if (keyCode >= 65 && keyCode <= 90) return String.valueOf((char) keyCode);
                if (keyCode >= 48 && keyCode <= 57) return String.valueOf((char) keyCode);
                return "K" + keyCode;
        }
    }

    public int getHeight() {
        int total = height;

        if (subModulesOpen && hasSubModules()) {
            List<SubModule> subModules = getSubModules();
            for (SubModule sub : subModules) {
                total += 14; // submodule header
                if (sub.isEnabled()) {
                    total += sub.getSettings().size() * 12; // submodule settings
                }
            }
        }

        if (settingsOpen) {
            total += module.getSettings().size() * 14;
        }

        return total;
    }

    // NEW: Add getWidth method
    public int getWidth() {
        return width;
    }

    public void onLeftClick() {
        module.toggle();
    }

    public void onRightClick() {
        // Toggle between settings and submodules
        if (!settingsOpen && !subModulesOpen) {
            settingsOpen = true;
        } else if (settingsOpen) {
            settingsOpen = false;
            subModulesOpen = true;
        } else {
            subModulesOpen = false;
        }
    }

    // Handle clicks on submodules
    public boolean handleSubModuleClick(double mouseX, double mouseY, int x, int y) {
        if (!subModulesOpen || !hasSubModules()) return false;

        int curY = y + height;
        List<SubModule> subModules = getSubModules();

        for (SubModule sub : subModules) {
            if (mouseX >= x && mouseX <= x + getWidth() &&
                    mouseY >= curY && mouseY <= curY + 14) {
                sub.setEnabled(!sub.isEnabled());
                return true;
            }
            curY += 14;

            // Skip settings area if submodule is enabled
            if (sub.isEnabled()) {
                curY += sub.getSettings().size() * 12;
            }
        }

        return false;
    }

    public Module getModule() { return module; }
}