package me.origami.gui.clickgui;

import me.origami.impl.utils.SettingUtil;
import me.origami.impl.utils.InputUtil;
import me.origami.module.Module;
import me.origami.impl.settings.ColorSetting;
import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class ModuleComponent {
    private final Module module;
    private int height = 14;
    private boolean settingsOpen = false;
    private int width;
    private Setting<?> draggedSetting = null;
    private boolean isDragging = false;
    private ColorPicker activeColorPicker = null;

    // Система редактирования текста
    private Setting<?> editingSetting = null;
    private String editingText = "";
    private int cursorPosition = 0;
    private int cursorBlink = 0;

    public ModuleComponent(Module module) {
        this.module = module;
    }

    public void draw(DrawContext ctx, int x, int y, int width) {
        this.width = width;
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Draw module header
        int bg = module.isEnabled() ? 0xFF4B1A1A : 0xFF1F1F1F;
        ctx.fill(x + 1, y, x + width - 1, y + height, bg);
        ctx.drawText(tr, module.getName(), x + 6, y + 3, 0xFFFFFFFF, false);

        // Draw settings if open
        if (settingsOpen) {
            int curY = y + height;
            for (Setting<?> setting : module.getSettings()) {
                curY = drawSettingRecursive(ctx, setting, x, curY, width, 0);
            }
        }
    }

    private int drawSettingRecursive(DrawContext ctx, Setting<?> setting, int x, int y, int width, int indent) {
        int currentY = y;

        // Устанавливаем координаты для ColorSetting
        if (setting instanceof ColorSetting) {
            ((ColorSetting) setting).setPosition(x + indent, currentY);
        }

        // Draw the setting itself
        currentY = drawSingleSetting(ctx, setting, x, currentY, width, 14, indent);

        // Draw color picker if open
        if (setting instanceof ColorSetting && ((ColorSetting) setting).isPickerOpen()) {
            if (activeColorPicker == null || activeColorPicker.getSetting() != setting) {
                activeColorPicker = new ColorPicker((ColorSetting) setting);
            }
            System.out.println("Drawing ColorPicker for " + setting.getName() + " at x=" + (x + indent) + ", y=" + currentY);
            activeColorPicker.draw(ctx, x + indent, currentY);
            currentY += activeColorPicker.getHeight();
        }

        // Draw sub-settings if expanded
        if (setting.isExpanded()) {
            for (Setting<?> subSetting : setting.getSubSettings()) {
                currentY = drawSettingRecursive(ctx, subSetting, x, currentY, width, indent + 5);
            }
        }

        // Draw mode options if expanded
        if (setting.isModeExpanded() && setting.hasModes()) {
            for (Object mode : setting.getModes()) {
                drawModeOption(ctx, setting, mode.toString(), x, currentY, width);
                currentY += 12;
            }
        }

        return currentY;
    }

    private int drawSingleSetting(DrawContext ctx, Setting<?> setting, int x, int y, int width, int height, int indent) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Background
        int bgColor = indent > 0 ? 0xFF1A1A1A : 0xFF252525;
        if (setting == editingSetting) {
            bgColor = 0x80333399;
        }
        ctx.fill(x + indent, y, x + width - indent, y + height, bgColor);

        // Draw color preview square for ColorSetting
        if (setting instanceof ColorSetting) {
            int color = ((ColorSetting) setting).getValue();
            ctx.fill(x + width - 20 - indent, y + 2, x + width - 8 - indent, y + height - 2, color);
        }

        // Slider for numeric settings
        if (setting.isNumeric() && setting != editingSetting && !(setting instanceof ColorSetting)) {
            double min = setting.getMinValue(), max = setting.getMaxValue();
            double value = ((Number) setting.getValue()).doubleValue();
            double percentage = (value - min) / (max - min);
            int sliderWidth = (int) (percentage * (width - 2 - indent * 2));
            if (sliderWidth > 0) {
                ctx.fill(x + indent, y, x + indent + sliderWidth, y + height, indent > 0 ? 0xFF3A1A1A : 0xFF4B1A1A);
            }
        }

        // Text
        String displayText;
        if (setting == editingSetting) {
            cursorBlink = (cursorBlink + 1) % 40;
            String cursor = cursorBlink < 20 ? "|" : "";
            String textBeforeCursor = editingText.substring(0, cursorPosition);
            String textAfterCursor = editingText.substring(cursorPosition);
            displayText = (indent > 0 ? "  " : "") + setting.getName() + ": " + textBeforeCursor + cursor + textAfterCursor;
        } else {
            displayText = (indent > 0 ? "  " : "") + setting.getName() + ": " +
                    (setting instanceof ColorSetting ? "Color" : SettingUtil.getValueDisplay(setting));
        }

        ctx.drawText(tr, displayText, x + indent + 8, y + (height - 8) / 2, indent > 0 ? 0xFFCCCCCC : 0xFFFFFFFF, false);

        // Icons for other settings
        if (setting != editingSetting) {
            if (setting.hasSubSettings()) {
                String icon = setting.isExpanded() ? "−" : "+";
                ctx.drawText(tr, icon, x + width - tr.getWidth(icon) - 4, y + (height - 8) / 2, 0xFFCCCCCC, false);
            } else if (setting.hasModes()) {
                String icon = setting.isModeExpanded() ? "•" : "...";
                ctx.drawText(tr, icon, x + width - tr.getWidth(icon) - 4, y + (height - 8) / 2, 0xFFCCCCCC, false);
            }
        }

        return y + height;
    }

    private void drawModeOption(DrawContext ctx, Setting<?> setting, String mode, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        ctx.fill(x + 5, y, x + width - 5, y + 12, 0xFF1A1A1A);

        boolean isSelected = mode.equals(setting.getValue().toString());
        int color = isSelected ? 0xFF00FF00 : 0xFFCCCCCC;
        String text = "  " + mode;
        ctx.drawText(tr, text, x + 10, y + 2, color, false);
    }

    public int getHeight() {
        if (!settingsOpen) return height;

        int total = height;
        for (Setting<?> setting : module.getSettings()) {
            total += getSettingHeight(setting);
        }
        return total;
    }

    private int getSettingHeight(Setting<?> setting) {
        int total = 14;

        if (setting instanceof ColorSetting && ((ColorSetting) setting).isPickerOpen()) {
            total += new ColorPicker((ColorSetting) setting).getHeight();
        }

        if (setting.isExpanded()) {
            for (Setting<?> subSetting : setting.getSubSettings()) {
                total += getSettingHeight(subSetting);
            }
        }

        if (setting.isModeExpanded() && setting.hasModes()) {
            total += setting.getModes().size() * 12;
        }

        return total;
    }

    public boolean handleClick(double mouseX, double mouseY, int x, int y, int button) {
        System.out.println("ModuleComponent handleClick: mouseX=" + mouseX + ", mouseY=" + mouseY + ", button=" + button + ", x=" + x + ", y=" + y + ", module=" + module.getName());

        // Module toggle (left click) or settings toggle (right click)
        if (InputUtil.isMouseInBounds(mouseX, mouseY, x, y, width, height)) {
            if (button == 0) {
                module.toggle();
                System.out.println("Toggled module: " + module.getName());
                return true;
            } else if (button == 1) {
                if (editingSetting != null) {
                    stopEditing(true);
                }
                if (activeColorPicker != null) {
                    activeColorPicker.getSetting().setPickerOpen(false);
                    activeColorPicker = null;
                    System.out.println("Closed ColorPicker due to module header click");
                }
                closeAllExpansions();
                settingsOpen = !settingsOpen;
                System.out.println("Toggled settingsOpen to: " + settingsOpen);
                return true;
            }
        }

        // Handle ColorPicker clicks
        if (activeColorPicker != null) {
            if (activeColorPicker.handleClick(mouseX, mouseY, button)) {
                System.out.println("Clicked inside ColorPicker for " + activeColorPicker.getSetting().getName());
                return true;
            } else if (button == 1) {
                // Right click outside ColorPicker closes it
                activeColorPicker.getSetting().setPickerOpen(false);
                activeColorPicker = null;
                System.out.println("Closed ColorPicker due to right click outside");
                return true;
            }
        }

        // Handle editing
        if (editingSetting != null) {
            stopEditing(true);
            return true;
        }

        // Settings clicks
        if (settingsOpen) {
            int curY = y + height;
            for (Setting<?> setting : module.getSettings()) {
                ClickResult result = handleSettingClickRecursive(setting, mouseX, mouseY, x, curY, width, 0, button);
                if (result.handled) {
                    System.out.println("Setting click handled: " + setting.getName());
                    return true;
                }
                curY += result.height;
            }
        }

        return false;
    }

    private ClickResult handleSettingClickRecursive(Setting<?> setting, double mouseX, double mouseY, int x, int y, int width, int indent, int button) {
        int totalHeight = 14;
        int currentY = y;

        // Check click on the setting itself
        if (InputUtil.isMouseInBounds(mouseX, mouseY, x + indent, currentY, width - indent * 2, 14)) {
            System.out.println("Clicked setting: " + setting.getName() + ", button=" + button);
            handleSingleSettingClick(setting, mouseX, mouseY, x, button, currentY, indent);
            return new ClickResult(true, totalHeight);
        }
        currentY += 14;

        // Check clicks on color picker
        if (setting instanceof ColorSetting && ((ColorSetting) setting).isPickerOpen()) {
            if (activeColorPicker == null || activeColorPicker.getSetting() != setting) {
                activeColorPicker = new ColorPicker((ColorSetting) setting);
            }
            if (activeColorPicker.handleClick(mouseX, mouseY, button)) {
                System.out.println("Clicked inside ColorPicker for " + setting.getName());
                return new ClickResult(true, totalHeight + activeColorPicker.getHeight());
            }
            totalHeight += activeColorPicker.getHeight();
            currentY += activeColorPicker.getHeight();
        }

        // Check clicks on expanded mode options
        if (setting.isModeExpanded() && setting.hasModes()) {
            for (Object mode : setting.getModes()) {
                if (InputUtil.isMouseInBounds(mouseX, mouseY, x, currentY, width, 12)) {
                    if (button == 0) {
                        setting.setValue(mode);
                    }
                    return new ClickResult(true, totalHeight);
                }
                currentY += 12;
                totalHeight += 12;
            }
        }

        // Check clicks on sub-settings
        if (setting.isExpanded()) {
            for (Setting<?> subSetting : setting.getSubSettings()) {
                ClickResult result = handleSettingClickRecursive(subSetting, mouseX, mouseY, x, currentY, width, indent + 5, button);
                if (result.handled) return new ClickResult(true, totalHeight + result.height);
                currentY += result.height;
                totalHeight += result.height;
            }
        }

        return new ClickResult(false, totalHeight);
    }

    private void handleSingleSettingClick(Setting<?> setting, double mouseX, double mouseY, int x, int button, int y, int indent) {
        if (button == 1) {
            // Right click
            if (setting instanceof ColorSetting) {
                closeOtherExpansions(setting);
                ColorSetting colorSetting = (ColorSetting) setting;
                colorSetting.togglePicker();
                System.out.println("Toggled ColorPicker for " + setting.getName() + ": " + colorSetting.isPickerOpen());
                if (colorSetting.isPickerOpen()) {
                    activeColorPicker = new ColorPicker(colorSetting);
                } else {
                    activeColorPicker = null;
                }
            } else if (setting.hasSubSettings()) {
                closeOtherExpansions(setting);
                setting.toggleExpanded();
            } else if (setting.hasModes()) {
                closeOtherExpansions(setting);
                setting.toggleModeExpanded();
            }
        } else {
            // Left click
            if (setting.isNumeric() && !(setting instanceof ColorSetting)) {
                isDragging = true;
                draggedSetting = setting;
                updateSlider(setting, mouseX, x, indent);
            } else if (setting.hasModes()) {
                if (!setting.isModeExpanded()) {
                    setting.cycleMode();
                }
            } else if (setting.getValue() instanceof Boolean) {
                setting.setValue(!((Boolean) setting.getValue()));
            } else if (setting.getValue() instanceof String && !setting.hasModes()) {
                startEditing(setting);
            }
        }
    }

    private void startEditing(Setting<?> setting) {
        closeAllExpansions();
        if (activeColorPicker != null) {
            activeColorPicker.getSetting().setPickerOpen(false);
            activeColorPicker = null;
        }
        editingSetting = setting;
        editingText = setting.getValue().toString();
        cursorPosition = editingText.length();
        cursorBlink = 0;
    }

    private void stopEditing(boolean save) {
        if (editingSetting != null) {
            if (save) {
                String newValue = editingText.trim();
                if (!newValue.isEmpty()) {
                    editingSetting.setValue(newValue);
                }
            }
            editingSetting = null;
            editingText = "";
            cursorPosition = 0;
        }
    }

    private void closeAllExpansions() {
        for (Setting<?> setting : module.getSettings()) {
            closeExpansionsRecursive(setting);
        }
    }

    private void closeOtherExpansions(Setting<?> currentSetting) {
        for (Setting<?> setting : module.getSettings()) {
            if (setting != currentSetting) {
                closeExpansionsRecursive(setting);
            }
        }
    }

    private void closeExpansionsRecursive(Setting<?> setting) {
        setting.setExpanded(false);
        setting.setModeExpanded(false);
        if (setting instanceof ColorSetting) {
            ((ColorSetting) setting).setPickerOpen(false);
        }
        for (Setting<?> subSetting : setting.getSubSettings()) {
            closeExpansionsRecursive(subSetting);
        }
    }

    public boolean handleDrag(double mouseX, double mouseY, int x, int y) {
        if (editingSetting != null) return false;

        // Handle color picker drag
        if (activeColorPicker != null && activeColorPicker.handleDrag(mouseX, mouseY)) {
            System.out.println("Dragging ColorPicker for " + activeColorPicker.getSetting().getName());
            return true;
        }

        // Handle numeric sliders
        if (isDragging && draggedSetting != null && draggedSetting.isNumeric()) {
            int indent = module.getSettings().contains(draggedSetting) ? 0 : 5;
            updateSlider(draggedSetting, mouseX, x, indent);
            return true;
        }
        return false;
    }

    public void stopDrag() {
        isDragging = false;
        draggedSetting = null;
        if (activeColorPicker != null) {
            activeColorPicker.stopDrag();
        }
    }

    private void updateSlider(Setting<?> setting, double mouseX, int x, int indent) {
        double min = setting.getMinValue(), max = setting.getMaxValue();
        double relativeX = Math.max(0, Math.min(width - 2 - indent * 2, mouseX - (x + indent)));
        double percentage = relativeX / (width - 2 - indent * 2);
        double newValue = min + (max - min) * percentage;

        if (setting.getValue() instanceof Integer) {
            setting.setValue((int) Math.round(newValue));
        } else if (setting.getValue() instanceof Double) {
            setting.setValue(Math.round(newValue * 10.0) / 10.0);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        System.out.println("ModuleComponent keyPressed: keyCode=" + keyCode);
        if (editingSetting != null) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                stopEditing(true);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                stopEditing(false);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (cursorPosition > 0) {
                    editingText = editingText.substring(0, cursorPosition - 1) + editingText.substring(cursorPosition);
                    cursorPosition--;
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                if (cursorPosition < editingText.length()) {
                    editingText = editingText.substring(0, cursorPosition) + editingText.substring(cursorPosition + 1);
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_LEFT) {
                if (cursorPosition > 0) {
                    cursorPosition--;
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                if (cursorPosition < editingText.length()) {
                    cursorPosition++;
                }
                return true;
            }
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE && activeColorPicker != null) {
            activeColorPicker.getSetting().setPickerOpen(false);
            activeColorPicker = null;
            System.out.println("Closed ColorPicker via ESC");
            return true;
        }

        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        System.out.println("ModuleComponent charTyped: chr=" + chr);
        if (editingSetting != null) {
            if (Character.isLetterOrDigit(chr) || chr == ' ' || chr == '_' || chr == '-') {
                editingText = editingText.substring(0, cursorPosition) + chr + editingText.substring(cursorPosition);
                cursorPosition++;
            }
            return true;
        }
        return false;
    }

    public boolean toggleSettings() {
        System.out.println("ModuleComponent toggleSettings called, settingsOpen: " + settingsOpen);
        if (editingSetting != null) {
            stopEditing(true);
        }
        if (activeColorPicker != null) {
            activeColorPicker.getSetting().setPickerOpen(false);
            activeColorPicker = null;
        }
        closeAllExpansions();
        settingsOpen = !settingsOpen;
        System.out.println("Toggled settingsOpen to: " + settingsOpen);
        return settingsOpen;
    }

    public Module getModule() {
        return module;
    }

    private static class ClickResult {
        final boolean handled;
        final int height;

        ClickResult(boolean handled, int height) {
            this.handled = handled;
            this.height = height;
        }
    }
}