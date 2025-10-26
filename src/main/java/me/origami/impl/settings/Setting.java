package me.origami.impl.settings;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private final String description;
    private final boolean isNumeric;
    private final double minValue;
    private final double maxValue;
    private final double increment;
    private final List<String> modes;

    // Sub-settings
    private final List<Setting<?>> subSettings = new ArrayList<>();
    private boolean expanded = false;
    private boolean modeExpanded = false;

    public Setting(String name, T defaultValue, String description) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.description = description;
        this.isNumeric = false;
        this.minValue = 0.0;
        this.maxValue = 0.0;
        this.increment = 0.0;
        this.modes = null;
    }

    public Setting(String name, String defaultValue, String description, String[] modes) {
        this.name = name;
        this.value = (T) defaultValue;
        this.defaultValue = (T) defaultValue;
        this.description = description;
        this.isNumeric = false;
        this.minValue = 0.0;
        this.maxValue = 0.0;
        this.increment = 0.0;
        this.modes = Arrays.asList(modes);
    }

    public Setting(String name, Double defaultValue, String description, double minValue, double maxValue, double increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.defaultValue = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    public Setting(String name, Integer defaultValue, String description, int minValue, int maxValue, int increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.defaultValue = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    // Basic getters
    public String getName() { return name; }
    public T getValue() { return value; }
    public T getDefaultValue() { return defaultValue; }
    public String getDescription() { return description; }
    public boolean isNumeric() { return isNumeric; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public double getIncrement() { return increment; }
    public List<String> getModes() { return modes; }
    public boolean hasModes() { return modes != null && !modes.isEmpty(); }

    // Value setting with proper type handling
    public void setValue(Object value) {
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
        } else {
            this.value = (T) value;
        }
    }

    // Sub-settings management
    public void addSubSetting(Setting<?> subSetting) {
        subSettings.add(subSetting);
    }

    public List<Setting<?>> getSubSettings() {
        return new ArrayList<>(subSettings);
    }

    public boolean hasSubSettings() {
        return !subSettings.isEmpty();
    }

    // Expansion states
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

    // Mode cycling
    public void cycleMode() {
        if (hasModes() && value instanceof String) {
            int currentIndex = modes.indexOf((String) value);
            int nextIndex = (currentIndex + 1) % modes.size();
            setValue(modes.get(nextIndex));
        }
    }
}