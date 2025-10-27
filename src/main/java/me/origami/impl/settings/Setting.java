package me.origami.impl.settings;

import me.origami.module.Module;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Setting<T> {
    private final String name;
    private T value;
    private final boolean isNumeric;
    private final double minValue;
    private final double maxValue;
    private final double increment;
    private final List<T> modes;
    private Module parentModule;

    // Система под-настроек
    private final List<Setting<?>> subSettings = new ArrayList<>();
    private boolean expanded = false;
    private boolean modeExpanded = false;

    // Boolean, String
    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.isNumeric = false;
        this.minValue = 0;
        this.maxValue = 0;
        this.increment = 0;
        this.modes = null;
    }

    // Enum
    public <E extends Enum<E>> Setting(String name, E defaultValue, Class<E> enumClass) {
        this.name = name;
        this.value = (T) defaultValue;
        this.isNumeric = false;
        this.minValue = 0;
        this.maxValue = 0;
        this.increment = 0;
        this.modes = (List<T>) Arrays.asList(enumClass.getEnumConstants());
    }

    // Double
    public Setting(String name, Double defaultValue, double minValue, double maxValue, double increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    // Integer
    public Setting(String name, Integer defaultValue, int minValue, int maxValue, int increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    // Установка родительского модуля
    public void setParentModule(Module module) {
        this.parentModule = module;
        // Рекурсивно устанавливаем для всех под-настроек
        for (Setting<?> subSetting : subSettings) {
            subSetting.setParentModule(module);
        }
    }

    // Геттеры
    public String getName() { return name; }
    public T getValue() { return value; }
    public boolean isNumeric() { return isNumeric; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public double getIncrement() { return increment; }
    public List<T> getModes() { return modes; }
    public boolean hasModes() { return modes != null && !modes.isEmpty(); }

    // Под-настройки
    public void addSubSetting(Setting<?> subSetting) {
        subSettings.add(subSetting);
        if (parentModule != null) {
            subSetting.setParentModule(parentModule);
        }
    }

    public List<Setting<?>> getSubSettings() {
        return new ArrayList<>(subSettings);
    }

    public boolean hasSubSettings() {
        return !subSettings.isEmpty();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded) this.modeExpanded = false;
    }

    public void toggleExpanded() {
        setExpanded(!expanded);
    }

    public boolean isModeExpanded() {
        return modeExpanded;
    }

    public void setModeExpanded(boolean modeExpanded) {
        this.modeExpanded = modeExpanded;
        if (modeExpanded) this.expanded = false;
    }

    public void toggleModeExpanded() {
        setModeExpanded(!modeExpanded);
    }

    // Fluent API для создания под-настроек
    public Setting<T> withSubSettings(Setting<?>... subSettings) {
        for (Setting<?> subSetting : subSettings) {
            addSubSetting(subSetting);
        }
        return this;
    }

    public void setValue(Object value) {
        T oldValue = this.value;

        if (isNumeric && value instanceof Number) {
            double val = ((Number) value).doubleValue();
            val = Math.max(minValue, Math.min(maxValue, val));
            if (this.value instanceof Integer) {
                val = Math.round(val / increment) * increment;
                this.value = (T) Integer.valueOf((int) val);
            } else if (this.value instanceof Double) {
                val = Math.round(val / increment) * increment;
                this.value = (T) Double.valueOf(val);
            }
        } else if (hasModes() && modes.contains(value)) {
            this.value = (T) value;
        } else {
            try {
                this.value = (T) value;
            } catch (ClassCastException e) {
                System.err.println("Invalid value type for setting " + name + ": " + value);
                return;
            }
        }

        // Уведомляем родительский модуль об изменении
        if (parentModule != null && !this.value.equals(oldValue)) {
            parentModule.onSettingChanged(this);
        }
    }

    public void cycleMode() {
        if (hasModes()) {
            int currentIndex = modes.indexOf(value);
            int nextIndex = (currentIndex + 1) % modes.size();
            setValue(modes.get(nextIndex));
        }
    }
}