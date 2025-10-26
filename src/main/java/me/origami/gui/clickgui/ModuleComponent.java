package me.origami.gui.clickgui;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ModuleComponent {
    private final Module module;
    private int height = 14;
    private boolean settingsOpen = false;
    private int width;
    private Setting<?> selectedSetting = null;
    private boolean isDraggingSlider = false;

    public ModuleComponent(Module module) {
        this.module = module;
    }

    public void draw(DrawContext ctx, int x, int y, int width) {
        this.width = width;
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Module header
        int bg = module.isEnabled() ? 0xFF4B1A1A : 0xFF1F1F1F;
        ctx.fill(x + 1, y, x + width - 1, y + height, bg);
        ctx.drawText(tr, module.getName(), x + 6, y + 3, 0xFFFFFFFF, false);
        ctx.drawText(tr, getBindText(), x + width - tr.getWidth(getBindText()) - 4, y + 3, 0xFFCCCCCC, false);

        int curY = y + height;

        // Settings
        if (settingsOpen) {
            for (Setting<?> setting : module.getSettings()) {
                drawSetting(ctx, setting, x, curY, width);
                curY += 14;

                // Sub-settings
                if (setting.isExpanded()) {
                    for (Setting<?> subSetting : setting.getSubSettings()) {
                        drawSubSetting(ctx, subSetting, x, curY, width);
                        curY += 12;
                    }
                }

                // Mode options
                if (setting.isModeExpanded()) {
                    for (String mode : setting.getModes()) {
                        drawModeOption(ctx, setting, mode, x, curY, width);
                        curY += 12;
                    }
                }
            }
        }
    }

    private void drawSetting(DrawContext ctx, Setting<?> setting, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Background
        int bgColor = setting == selectedSetting ? 0xFF3A3A3A : 0xFF252525;
        ctx.fill(x + 1, y, x + width - 1, y + 14, bgColor);

        // Numeric slider background
        if (setting.isNumeric()) {
            drawSliderBackground(ctx, setting, x, y, width);
        }

        // Text and icons
        String displayText = setting.getName() + ": " + getValueDisplay(setting);
        ctx.drawText(tr, displayText, x + 8, y + 3, 0xFFFFFFFF, false);

        // Icons
        if (setting.hasSubSettings()) {
            String icon = setting.isExpanded() ? "−" : "+";
            int iconWidth = tr.getWidth(icon);
            ctx.drawText(tr, icon, x + width - iconWidth - 4, y + 3, 0xFFCCCCCC, false);
        } else if (setting.hasModes() && !setting.isModeExpanded()) {
            int textWidth = tr.getWidth(displayText);
            ctx.drawText(tr, "...", x + 8 + textWidth + 4, y + 3, 0xFFCCCCCC, false);
        }
    }

    private void drawSubSetting(DrawContext ctx, Setting<?> setting, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        ctx.fill(x + 5, y, x + width - 5, y + 12, 0xFF1A1A1A);

        if (setting.isNumeric()) {
            drawSubSliderBackground(ctx, setting, x, y, width);
        }

        String displayText = "  " + setting.getName() + ": " + getValueDisplay(setting);
        ctx.drawText(tr, displayText, x + 10, y + 2, 0xFFCCCCCC, false);
    }

    private void drawModeOption(DrawContext ctx, Setting<?> setting, String mode, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        ctx.fill(x + 5, y, x + width - 5, y + 12, 0xFF1A1A1A);

        boolean isSelected = mode.equals(setting.getValue());
        int color = isSelected ? 0xFF00FF00 : 0xFFCCCCCC;
        String prefix = isSelected ? "  ✓ " : "  ";

        ctx.drawText(tr, prefix + mode, x + 10, y + 2, color, false);
    }

    private void drawSliderBackground(DrawContext ctx, Setting<?> setting, int x, int y, int width) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double value = ((Number) setting.getValue()).doubleValue();
        double percentage = (value - min) / (max - min);
        int sliderWidth = (int) (percentage * (width - 2));

        if (sliderWidth > 0) {
            ctx.fill(x + 1, y, x + 1 + sliderWidth, y + 14, 0xFF4B1A1A);
        }
        if (sliderWidth < width - 2) {
            ctx.fill(x + 1 + sliderWidth, y, x + width - 1, y + 14, 0xFF1A1A1A);
        }

        ctx.fill(x, y, x + 1, y + 14, 0xFF8B2B2B);
        ctx.fill(x + width - 1, y, x + width, y + 14, 0xFF8B2B2B);
    }

    private void drawSubSliderBackground(DrawContext ctx, Setting<?> setting, int x, int y, int width) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double value = ((Number) setting.getValue()).doubleValue();
        double percentage = (value - min) / (max - min);
        int sliderWidth = (int) (percentage * (width - 15));

        if (sliderWidth > 0) {
            ctx.fill(x + 5, y, x + 5 + sliderWidth, y + 12, 0xFF3A1A1A);
        }
    }

    private String getValueDisplay(Setting<?> setting) {
        Object value = setting.getValue();

        if (value instanceof Double) {
            return String.format("%.2f", (Double) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value ? "ON" : "OFF";
        } else if (setting.hasModes() && value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    private String getBindText() {
        int bind = module.getKeyBind();
        if (bind == -1) return "[]";

        String keyName = switch (bind) {
            case 32 -> "SPACE";
            case 340 -> "SHIFT";
            case 341 -> "CTRL";
            case 342 -> "ALT";
            case 256 -> "ESC";
            case 257 -> "ENTER";
            case 258 -> "TAB";
            default -> {
                if (bind >= 65 && bind <= 90) yield String.valueOf((char) bind);
                if (bind >= 48 && bind <= 57) yield String.valueOf((char) bind);
                yield "K" + bind;
            }
        };

        return "[" + keyName + "]";
    }

    public int getHeight() {
        int total = height;

        if (settingsOpen) {
            for (Setting<?> setting : module.getSettings()) {
                total += 14;
                if (setting.isExpanded()) total += setting.getSubSettings().size() * 12;
                if (setting.isModeExpanded()) total += setting.getModes().size() * 12;
            }
        }

        return total;
    }

    public int getWidth() {
        return width;
    }

    // Click handling
    public boolean onLeftClick(double mouseX, double mouseY, int x, int y) {
        if (mouseY >= y && mouseY <= y + height) {
            module.toggle();
            return true;
        }
        return false;
    }

    public boolean onRightClick(double mouseX, double mouseY, int x, int y) {
        if (mouseY >= y && mouseY <= y + height) {
            settingsOpen = !settingsOpen;
            selectedSetting = null;
            return true;
        }
        return false;
    }

    public boolean handleSettingClick(double mouseX, double mouseY, int x, int y) {
        if (!settingsOpen) return false;

        int curY = y + height;

        for (Setting<?> setting : module.getSettings()) {
            // Main setting click
            if (isInBounds(mouseX, mouseY, x, curY, width, 14)) {
                handleMainSettingClick(setting, mouseX, x, false); // ЛКМ
                return true;
            }
            curY += 14;

            // Sub-settings clicks
            if (setting.isExpanded()) {
                for (Setting<?> subSetting : setting.getSubSettings()) {
                    if (isInBounds(mouseX, mouseY, x, curY, width, 12)) {
                        handleSubSettingClick(subSetting, mouseX, x);
                        return true;
                    }
                    curY += 12;
                }
            }

            // Mode options clicks
            if (setting.isModeExpanded()) {
                for (String mode : setting.getModes()) {
                    if (isInBounds(mouseX, mouseY, x, curY, width, 12)) {
                        setting.setValue(mode);
                        setting.setModeExpanded(false);
                        return true;
                    }
                    curY += 12;
                }
            }
        }

        return false;
    }

    public boolean handleSettingRightClick(double mouseX, double mouseY, int x, int y) {
        if (!settingsOpen) return false;

        int curY = y + height;

        for (Setting<?> setting : module.getSettings()) {
            if (isInBounds(mouseX, mouseY, x, curY, width, 14)) {
                handleMainSettingClick(setting, mouseX, x, true); // ПКМ
                return true;
            }
            curY += getSettingHeight(setting);
        }

        return false;
    }

    private void handleMainSettingClick(Setting<?> setting, double mouseX, int x, boolean isRightClick) {
        if (isRightClick) {
            // ПКМ - раскрыть/свернуть
            if (setting.hasSubSettings()) {
                setting.toggleExpanded();
            } else if (setting.hasModes()) {
                setting.toggleModeExpanded();
            }
        } else {
            // ЛКМ - изменить значение
            if (setting.isNumeric()) {
                isDraggingSlider = true;
                selectedSetting = setting;
                updateSliderValue(setting, mouseX, x);
            } else if (setting.hasModes() && !setting.isModeExpanded()) {
                setting.cycleMode();
            } else if (setting.getValue() instanceof Boolean) {
                setting.setValue(!((Boolean) setting.getValue()));
            } else if (setting.getValue() instanceof String && !setting.hasModes()) {
                selectedSetting = setting;
                ClickGuiManager.get().startStringEditing(setting, (String) setting.getValue());
            }
        }
    }

    private void handleSubSettingClick(Setting<?> setting, double mouseX, int x) {
        if (setting.isNumeric()) {
            isDraggingSlider = true;
            selectedSetting = setting;
            updateSubSliderValue(setting, mouseX, x);
        } else if (setting.hasModes()) {
            setting.cycleMode();
        } else if (setting.getValue() instanceof Boolean) {
            setting.setValue(!((Boolean) setting.getValue()));
        }
    }

    // Drag handling
    public boolean handleSettingDrag(double mouseX, double mouseY, int x, int y) {
        if (isDraggingSlider && selectedSetting != null && selectedSetting.isNumeric()) {
            if (isMainSetting(selectedSetting)) {
                updateSliderValue(selectedSetting, mouseX, x);
            } else {
                updateSubSliderValue(selectedSetting, mouseX, x);
            }
            return true;
        }
        return false;
    }

    public boolean handleMouseRelease() {
        if (isDraggingSlider) {
            isDraggingSlider = false;
            return true;
        }
        return false;
    }

    private void updateSliderValue(Setting<?> setting, double mouseX, int x) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double relativeX = Math.max(0, Math.min(width - 2, mouseX - (x + 1)));
        double percentage = relativeX / (width - 2);
        double newValue = min + (max - min) * percentage;

        if (setting.getValue() instanceof Integer) {
            setting.setValue((int) Math.round(newValue));
        } else if (setting.getValue() instanceof Double) {
            double rounded = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
            setting.setValue(rounded);
        }
    }

    private void updateSubSliderValue(Setting<?> setting, double mouseX, int x) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double relativeX = Math.max(0, Math.min(width - 15, mouseX - (x + 5)));
        double percentage = relativeX / (width - 15);
        double newValue = min + (max - min) * percentage;

        if (setting.getValue() instanceof Integer) {
            setting.setValue((int) Math.round(newValue));
        } else if (setting.getValue() instanceof Double) {
            double rounded = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
            setting.setValue(rounded);
        }
    }

    // Utility methods
    private boolean isInBounds(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private int getSettingHeight(Setting<?> setting) {
        int height = 14;
        if (setting.isExpanded()) height += setting.getSubSettings().size() * 12;
        if (setting.isModeExpanded()) height += setting.getModes().size() * 12;
        return height;
    }

    private boolean isMainSetting(Setting<?> setting) {
        return module.getSettings().contains(setting);
    }

    public Module getModule() {
        return module;
    }
}