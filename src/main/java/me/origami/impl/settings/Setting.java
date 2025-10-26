package me.origami.impl.settings;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Setting<T> {
    private final String name;
    private T value;
    private final String description;
    private final boolean isNumeric;
    private final double minValue;
    private final double maxValue;
    private final double increment;
    private final List<T> modes; // Changed to List<T> for enums
    private final List<Setting<?>> subSettings = new ArrayList<>();
    private boolean expanded = false;
    private boolean modeExpanded = false;

    // Constructor for non-numeric, non-mode settings (e.g., Boolean, String)
    public Setting(String name, T defaultValue, String description) {
        this.name = name;
        this.value = defaultValue;
        this.description = description;
        this.isNumeric = false;
        this.minValue = 0;
        this.maxValue = 0;
        this.increment = 0;
        this.modes = null;
    }

    // Constructor for enum modes
    public <E extends Enum<E>> Setting(String name, E defaultValue, String description, Class<E> enumClass) {
        this.name = name;
        this.value = (T) defaultValue;
        this.description = description;
        this.isNumeric = false;
        this.minValue = 0;
        this.maxValue = 0;
        this.increment = 0;
        this.modes = (List<T>) Arrays.asList(enumClass.getEnumConstants());
    }

    // Constructor for numeric double settings
    public Setting(String name, Double defaultValue, String description, double minValue, double maxValue, double increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    // Constructor for numeric integer settings
    public Setting(String name, Integer defaultValue, String description, int minValue, int maxValue, int increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.modes = null;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public String getDescription() { return description; }
    public boolean isNumeric() { return isNumeric; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public double getIncrement() { return increment; }
    public List<T> getModes() { return modes; }
    public boolean hasModes() { return modes != null && !modes.isEmpty(); }

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
        } else if (hasModes() && modes.contains(value)) {
            this.value = (T) value; // Ensure value is a valid enum
        } else {
            this.value = (T) value;
        }
    }

    public void addSubSetting(Setting<?> subSetting) {
        subSettings.add(subSetting);
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

    public void cycleMode() {
        if (hasModes()) {
            int currentIndex = modes.indexOf(value);
            int nextIndex = (currentIndex + 1) % modes.size();
            setValue(modes.get(nextIndex));
        }
    }
}