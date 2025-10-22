package me.origami.impl.settings;

public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private final String description;
    private final boolean isNumeric;
    private final double minValue;
    private final double maxValue;
    private final double increment;

    // Constructor for non-numeric settings
    public Setting(String name, T defaultValue, String description) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.description = description;
        this.isNumeric = false;
        this.minValue = 0.0;
        this.maxValue = 0.0;
        this.increment = 0.0;
    }

    // Constructor for numeric settings with Double
    public Setting(String name, Double defaultValue, String description, double minValue, double maxValue, double increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.defaultValue = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
    }

    // Constructor for numeric settings with Integer
    public Setting(String name, Integer defaultValue, String description, int minValue, int maxValue, int increment) {
        this.name = name;
        this.value = (T) defaultValue;
        this.defaultValue = (T) defaultValue;
        this.description = description;
        this.isNumeric = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (isNumeric && value instanceof Number) {
            double val = ((Number) value).doubleValue();
            if (value instanceof Integer) {
                val = Math.round(val / increment) * increment;
                val = Math.max(minValue, Math.min(maxValue, val));
                this.value = (T) Integer.valueOf((int) val);
            } else if (value instanceof Double) {
                val = Math.round(val / increment) * increment;
                val = Math.max(minValue, Math.min(maxValue, val));
                this.value = (T) Double.valueOf(val);
            }
        } else {
            this.value = value;
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getIncrement() {
        return increment;
    }
}