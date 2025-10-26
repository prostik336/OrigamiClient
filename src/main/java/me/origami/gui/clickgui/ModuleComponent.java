package me.origami.gui.clickgui;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleComponent {
    private final Module module;
    private int height = 14;
    private boolean settingsOpen = false;
    private int width;
    private Setting<?> draggedSetting = null;
    private boolean isDragging = false;
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
        ctx.drawText(tr, getBindText(), x + width - tr.getWidth(getBindText()) - 4, y + 3, 0xFFCCCCCC, false);

        // Draw settings if open
        if (settingsOpen) {
            int curY = y + height;
            for (Setting<?> setting : module.getSettings()) {
                curY = drawSettingRecursive(ctx, setting, x, curY, width, 0);
            }
        }
    }

    private int drawSettingRecursive(DrawContext ctx, Setting<?> setting, int x, int y, int width, int indent) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int currentY = y;

        // Draw the setting itself
        drawSingleSetting(ctx, setting, x, currentY, width, 14, indent);
        currentY += 14;

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

    private void drawSingleSetting(DrawContext ctx, Setting<?> setting, int x, int y, int width, int height, int indent) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Background
        int bgColor = indent > 0 ? 0xFF1A1A1A : 0xFF252525;
        if (setting == editingSetting) {
            bgColor = 0x80333399;
        }

        // Особый цвет для настройки бинда
        if (isBindSetting(setting)) {
            bgColor = 0x80228B22; // Зеленый для бинда
        }

        ctx.fill(x + indent, y, x + width - indent, y + height, bgColor);

        // Slider for numeric settings
        if (setting.isNumeric() && setting != editingSetting && !isBindSetting(setting)) {
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
            // Editing mode with blinking cursor
            cursorBlink = (cursorBlink + 1) % 40;
            String cursor = cursorBlink < 20 ? "|" : "";
            String textBeforeCursor = editingText.substring(0, cursorPosition);
            String textAfterCursor = editingText.substring(cursorPosition);
            displayText = (indent > 0 ? "  " : "") + setting.getName() + ": " + textBeforeCursor + cursor + textAfterCursor;
        } else {
            if (isBindSetting(setting)) {
                // Для бинда показываем специальный текст
                displayText = (indent > 0 ? "  " : "") + "Bind: " + getKeyName((Integer) setting.getValue());
            } else {
                displayText = (indent > 0 ? "  " : "") + setting.getName() + ": " + getValueDisplay(setting);
            }
        }

        ctx.drawText(tr, displayText, x + indent + 8, y + (height - 8) / 2, indent > 0 ? 0xFFCCCCCC : 0xFFFFFFFF, false);

        // Icons (не для бинда)
        if (setting != editingSetting && !isBindSetting(setting)) {
            if (setting.hasSubSettings()) {
                String icon = setting.isExpanded() ? "−" : "+";
                ctx.drawText(tr, icon, x + width - tr.getWidth(icon) - 4, y + (height - 8) / 2, 0xFFCCCCCC, false);
            } else if (setting.hasModes()) {
                String icon = setting.isModeExpanded() ? "−" : "+";
                ctx.drawText(tr, icon, x + width - tr.getWidth(icon) - 4, y + (height - 8) / 2, 0xFFCCCCCC, false);
            }
        }
    }

    private void drawModeOption(DrawContext ctx, Setting<?> setting, String mode, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        ctx.fill(x + 5, y, x + width - 5, y + 12, 0xFF1A1A1A);

        boolean isSelected = mode.equals(setting.getValue().toString());
        int color = isSelected ? 0xFF00FF00 : 0xFFCCCCCC;
        String text = "  " + mode;
        ctx.drawText(tr, text, x + 10, y + 2, color, false);
    }

    private String getValueDisplay(Setting<?> setting) {
        Object value = setting.getValue();
        if (value instanceof Double) return String.format("%.1f", (Double) value);
        if (value instanceof Boolean) return (Boolean) value ? "ON" : "OFF";
        if (value instanceof Enum<?>) return ((Enum<?>) value).name();
        return value.toString();
    }

    private String getBindText() {
        int bind = module.getKeyBind();
        return bind == -1 ? "[]" : "[" + getKeyName(bind) + "]";
    }

    private String getKeyName(int keyCode) {
        if (keyCode == -1) return "NONE";

        switch (keyCode) {
            case 32: return "SPACE";
            case 340: return "SHIFT";
            case 341: return "CTRL";
            case 342: return "ALT";
            case 344: return "R_SHIFT";
            case 345: return "R_CTRL";
            case 346: return "R_ALT";
            case 256: return "ESC";
            case 257: return "ENTER";
            case 258: return "TAB";
            case 259: return "BACKSPACE";
            case 260: return "INSERT";
            case 261: return "DELETE";
            case 262: return "RIGHT";
            case 263: return "LEFT";
            case 264: return "DOWN";
            case 265: return "UP";
            case 266: return "PAGE_UP";
            case 267: return "PAGE_DOWN";
            case 268: return "HOME";
            case 269: return "END";
            case 280: return "CAPS_LOCK";
            case 281: return "SCROLL_LOCK";
            case 282: return "NUM_LOCK";
            case 283: return "PRINT_SCREEN";
            case 284: return "PAUSE";
            case 290: return "F1";
            case 291: return "F2";
            case 292: return "F3";
            case 293: return "F4";
            case 294: return "F5";
            case 295: return "F6";
            case 296: return "F7";
            case 297: return "F8";
            case 298: return "F9";
            case 299: return "F10";
            case 300: return "F11";
            case 301: return "F12";
            case 302: return "F13";
            case 303: return "F14";
            case 304: return "F15";
            case 305: return "F16";
            case 306: return "F17";
            case 307: return "F18";
            case 308: return "F19";
            case 309: return "F20";
            case 310: return "F21";
            case 311: return "F22";
            case 312: return "F23";
            case 313: return "F24";
            case 314: return "F25";
            case 320: return "KP_0";
            case 321: return "KP_1";
            case 322: return "KP_2";
            case 323: return "KP_3";
            case 324: return "KP_4";
            case 325: return "KP_5";
            case 326: return "KP_6";
            case 327: return "KP_7";
            case 328: return "KP_8";
            case 329: return "KP_9";
            case 330: return "KP_DECIMAL";
            case 331: return "KP_DIVIDE";
            case 332: return "KP_MULTIPLY";
            case 333: return "KP_SUBTRACT";
            case 334: return "KP_ADD";
            case 335: return "KP_ENTER";
            case 336: return "KP_EQUAL";
            default:
                if (keyCode >= 65 && keyCode <= 90) return String.valueOf((char) keyCode);
                if (keyCode >= 48 && keyCode <= 57) return String.valueOf((char) keyCode);
                return "KEY_" + keyCode;
        }
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
        int total = 14; // The setting itself

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

    public boolean handleClick(double mouseX, double mouseY, int x, int y, boolean isRightClick) {
        if (!settingsOpen) return false;

        // If editing, stop editing on any click
        if (editingSetting != null) {
            stopEditing(true);
            return true;
        }

        // Check clicks recursively
        int curY = y + height;
        for (Setting<?> setting : module.getSettings()) {
            ClickResult result = handleSettingClickRecursive(setting, mouseX, mouseY, x, curY, width, 0, isRightClick);
            if (result.handled) return true;
            curY += result.height;
        }

        return false;
    }

    private ClickResult handleSettingClickRecursive(Setting<?> setting, double mouseX, double mouseY, int x, int y, int width, int indent, boolean isRightClick) {
        int totalHeight = 14;
        int currentY = y;

        // Check click on the setting itself
        if (mouseInBounds(mouseX, mouseY, x + indent, currentY, width - indent * 2, 14)) {
            handleSingleSettingClick(setting, mouseX, x, isRightClick, currentY, indent);
            return new ClickResult(true, totalHeight);
        }
        currentY += 14;

        // Check clicks on expanded mode options
        if (setting.isModeExpanded() && setting.hasModes()) {
            for (Object mode : setting.getModes()) {
                if (mouseInBounds(mouseX, mouseY, x, currentY, width, 12)) {
                    if (!isRightClick) {
                        setting.setValue(mode);
                        setting.setModeExpanded(false);
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
                ClickResult result = handleSettingClickRecursive(subSetting, mouseX, mouseY, x, currentY, width, indent + 5, isRightClick);
                if (result.handled) return new ClickResult(true, totalHeight + result.height);
                currentY += result.height;
                totalHeight += result.height;
            }
        }

        return new ClickResult(false, totalHeight);
    }

    private void handleSingleSettingClick(Setting<?> setting, double mouseX, int x, boolean isRightClick, int y, int indent) {
        if (isRightClick) {
            // Right click - toggle expansion (не для бинда)
            if (!isBindSetting(setting)) {
                if (setting.hasSubSettings()) {
                    setting.toggleExpanded();
                } else if (setting.hasModes()) {
                    setting.toggleModeExpanded();
                }
            }
        } else {
            // Left click
            if (isBindSetting(setting)) {
                // Особый обработчик для бинда
                ClickGuiManager.get().startBindListening(module);
            } else if (setting.isNumeric()) {
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

    // Проверка, является ли настройка биндом
    private boolean isBindSetting(Setting<?> setting) {
        return "Bind".equals(setting.getName());
    }

    private void startEditing(Setting<?> setting) {
        editingSetting = setting;
        editingText = setting.getValue().toString();
        cursorPosition = editingText.length();
        cursorBlink = 0;
    }

    private void stopEditing(boolean save) {
        if (editingSetting != null) {
            if (save) {
                String newValue = editingText.trim();
                if (!newValue.isEmpty() && newValue.matches("[A-Za-z0-9_]{1,16}")) {
                    editingSetting.setValue(newValue);
                }
            }
            editingSetting = null;
            editingText = "";
            cursorPosition = 0;
        }
    }

    public boolean handleDrag(double mouseX, double mouseY, int x, int y) {
        if (editingSetting != null) return false;

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
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (editingSetting != null) {
            if (editingText.length() < 16 && (Character.isLetterOrDigit(chr) || chr == '_')) {
                editingText = editingText.substring(0, cursorPosition) + chr + editingText.substring(cursorPosition);
                cursorPosition++;
            }
            return true;
        }
        return false;
    }

    private boolean mouseInBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public boolean toggleSettings() {
        if (editingSetting != null) {
            stopEditing(true);
        }
        settingsOpen = !settingsOpen;
        return settingsOpen;
    }

    public Module getModule() {
        return module;
    }

    // Helper class for recursive click handling
    private static class ClickResult {
        final boolean handled;
        final int height;

        ClickResult(boolean handled, int height) {
            this.handled = handled;
            this.height = height;
        }
    }
}