package me.origami.gui.clickgui;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import me.origami.systems.SubModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.lang.reflect.Method;
import java.util.List;

public class ModuleComponent {
    private final Module module;
    private int height = 14;
    private boolean settingsOpen = false;
    private boolean subModulesOpen = false;
    private int width;
    private Setting<?> selectedSetting = null;
    private boolean isDraggingSlider = false;

    public ModuleComponent(Module module) {
        this.module = module;
    }

    public void draw(DrawContext ctx, int x, int y, int width) {
        this.width = width;
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int bg = module.isEnabled() ? 0xFF4B1A1A : 0xFF1F1F1F;
        ctx.fill(x + 1, y, x + width - 1, y + height, bg);
        ctx.drawText(tr, module.getName(), x + 6, y + 3, 0xFFFFFFFF, false);

        String bindText = getBindText();
        ctx.drawText(tr, bindText, x + width - tr.getWidth(bindText) - 4, y + 3, 0xFFCCCCCC, false);

        int curY = y + height;

        // Draw submodules
        if (subModulesOpen && hasSubModules()) {
            List<SubModule> subModules = getSubModules();
            for (SubModule sub : subModules) {
                drawSubModule(ctx, sub, x, curY, width);
                curY += 14;

                if (sub.isEnabled()) {
                    for (Setting<?> setting : sub.getSettings()) {
                        drawSetting(ctx, setting, x, curY, width, false);
                        curY += 12;
                    }
                }
            }
        }

        // Draw main module settings
        if (settingsOpen) {
            List<Setting<?>> settings = module.getSettings();
            for (Setting<?> setting : settings) {
                drawSetting(ctx, setting, x, curY, width, setting == selectedSetting);
                curY += 14;
            }
        }
    }

    private void drawSetting(DrawContext ctx, Setting<?> setting, int x, int y, int width, boolean selected) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        // Background
        int bgColor = selected ? 0xFF3A3A3A : 0xFF252525;
        ctx.fill(x + 1, y, x + width - 1, y + 14, bgColor);

        // Draw slider background for numeric settings (на всю ширину)
        if (setting.isNumeric()) {
            drawFullWidthSlider(ctx, setting, x, y, width);
        }

        // Setting name and value
        String displayText = setting.getName() + ": " + getSettingValueDisplay(setting);
        ctx.drawText(tr, displayText, x + 8, y + 3, 0xFFFFFFFF, false);
    }

    private void drawFullWidthSlider(DrawContext ctx, Setting<?> setting, int x, int y, int width) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double value = ((Number) setting.getValue()).doubleValue();
        double percentage = (value - min) / (max - min);

        // Calculate slider width based on current value
        int sliderWidth = (int) (percentage * (width - 2));

        // Draw slider fill (закрашенная часть)
        if (sliderWidth > 0) {
            ctx.fill(x + 1, y, x + 1 + sliderWidth, y + 14, 0xFF4B1A1A);
        }

        // Draw slider track (незакрашенная часть)
        if (sliderWidth < width - 2) {
            ctx.fill(x + 1 + sliderWidth, y, x + width - 1, y + 14, 0xFF1A1A1A);
        }

        // Draw slider border
        ctx.fill(x, y, x + 1, y + 14, 0xFF8B2B2B);
        ctx.fill(x + width - 1, y, x + width, y + 14, 0xFF8B2B2B);
    }

    private String getSettingValueDisplay(Setting<?> setting) {
        if (setting.isNumeric()) {
            Number value = (Number) setting.getValue();
            if (value instanceof Double) {
                return String.format("%.2f", value.doubleValue());
            } else if (value instanceof Integer) {
                return String.valueOf(value.intValue());
            }
        } else if (setting.hasModes() && setting.getValue() instanceof String) {
            return (String) setting.getValue();
        } else if (setting.getValue() instanceof Boolean) {
            return (Boolean) setting.getValue() ? "ON" : "OFF";
        } else if (setting.getValue() instanceof String) {
            String text = (String) setting.getValue();
            return text.length() > 10 ? text.substring(0, 10) + "..." : text;
        }
        return setting.getValue().toString();
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
                total += 14;
                if (sub.isEnabled()) {
                    total += sub.getSettings().size() * 12;
                }
            }
        }

        if (settingsOpen) {
            total += module.getSettings().size() * 14;
        }

        return total;
    }

    public int getWidth() {
        return width;
    }

    // ИЗМЕНЕНО: Теперь onLeftClick проверяет, открыты ли настройки
    public boolean onLeftClick(double mouseX, double mouseY, int x, int y) {
        // Если клик по заголовку модуля (верхняя часть)
        if (mouseY >= y && mouseY <= y + height) {
            module.toggle();
            return true;
        }
        return false;
    }

    // ИЗМЕНЕНО: onRightClick теперь принимает координаты
    public boolean onRightClick(double mouseX, double mouseY, int x, int y) {
        // Если клик по заголовку модуля (верхняя часть)
        if (mouseY >= y && mouseY <= y + height) {
            // Переключение между настройками и подмодулями
            if (!settingsOpen && !subModulesOpen) {
                settingsOpen = true;
                selectedSetting = null;
            } else if (settingsOpen) {
                settingsOpen = false;
                subModulesOpen = true;
                selectedSetting = null;
            } else {
                subModulesOpen = false;
                selectedSetting = null;
            }
            return true;
        }
        return false;
    }

    // Обработка кликов по настройкам и подмодулям
    public boolean handleSubModuleClick(double mouseX, double mouseY, int x, int y) {
        if (settingsOpen) {
            return handleSettingClick(mouseX, mouseY, x, y);
        } else if (subModulesOpen && hasSubModules()) {
            return handleSubModuleToggleClick(mouseX, mouseY, x, y);
        }
        return false;
    }

    private boolean handleSettingClick(double mouseX, double mouseY, int x, int y) {
        int curY = y + height;
        List<Setting<?>> settings = module.getSettings();

        for (Setting<?> setting : settings) {
            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= curY && mouseY <= curY + 14) {

                // ИЗМЕНЕНО: Сразу изменяем значение, а не выбираем настройку
                handleSettingInteraction(setting, mouseX, x);
                return true;
            }
            curY += 14;
        }
        return false;
    }

    private void handleSettingInteraction(Setting<?> setting, double mouseX, int x) {
        if (setting.isNumeric()) {
            // Для числовых настроек - сразу начинаем drag и устанавливаем значение
            isDraggingSlider = true;
            selectedSetting = setting;
            updateSliderValue(setting, mouseX, x);
        } else if (setting.hasModes()) {
            // Для настроек с режимами - сразу переключаем режим
            setting.cycleMode();
        } else if (setting.getValue() instanceof Boolean) {
            // Для Boolean - сразу переключаем
            Boolean currentValue = (Boolean) setting.getValue();
            setSettingValue(setting, !currentValue);
        } else if (setting.getValue() instanceof String && !setting.hasModes()) {
            // Для обычных строковых настроек (без режимов) - переключаем на текстовый ввод
            // Пока просто выделяем для будущего редактирования
            selectedSetting = setting;
            // Можно добавить логику для текстового ввода здесь
            System.out.println("String setting selected: " + setting.getName());
        }
    }

    // Вспомогательный метод для установки значения через reflection
    private void setSettingValue(Setting<?> setting, Object value) {
        try {
            Method setValueMethod = setting.getClass().getMethod("setValue", Object.class);
            setValueMethod.invoke(setting, value);
        } catch (Exception e) {
            System.err.println("Ошибка при установке значения настройки: " + e.getMessage());
        }
    }

    private boolean handleSubModuleToggleClick(double mouseX, double mouseY, int x, int y) {
        int curY = y + height;
        List<SubModule> subModules = getSubModules();

        for (SubModule sub : subModules) {
            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= curY && mouseY <= curY + 14) {
                sub.setEnabled(!sub.isEnabled());
                return true;
            }
            curY += 14;

            if (sub.isEnabled()) {
                curY += sub.getSettings().size() * 12;
            }
        }
        return false;
    }

    // Новый метод для обработки drag слайдера
    public boolean handleSettingDrag(double mouseX, double mouseY, int x, int y) {
        if (isDraggingSlider && selectedSetting != null && selectedSetting.isNumeric()) {
            updateSliderValue(selectedSetting, mouseX, x);
            return true;
        }
        return false;
    }

    // ИЗМЕНЕНО: Обработка отпускания кнопки мыши
    public boolean handleMouseRelease() {
        if (isDraggingSlider) {
            isDraggingSlider = false;
            return true;
        }
        return false;
    }

    private int findSettingY(Setting<?> target, int x, int y) {
        int curY = y + height;
        for (Setting<?> setting : module.getSettings()) {
            if (setting == target) {
                return curY;
            }
            curY += 14;
        }
        return -1;
    }

    private void updateSliderValue(Setting<?> setting, double mouseX, int x) {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double sliderWidth = width - 2;
        double relativeX = Math.max(0, Math.min(sliderWidth, mouseX - (x + 1)));
        double percentage = relativeX / sliderWidth;
        double newValue = min + (max - min) * percentage;

        if (setting.getValue() instanceof Integer) {
            int intValue = (int) Math.round(newValue);
            setSettingValue(setting, intValue);
        } else if (setting.getValue() instanceof Double) {
            double roundedValue = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
            setSettingValue(setting, roundedValue);
        }
    }

    public Module getModule() { return module; }
}