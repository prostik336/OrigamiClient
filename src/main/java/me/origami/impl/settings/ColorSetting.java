package me.origami.impl.settings;

import me.origami.impl.utils.MathUtil;

public class ColorSetting extends Setting<Integer> {
    private boolean pickerOpen = false;
    private float hue, saturation, brightness, alpha;
    private int x, y;

    public ColorSetting(String name, int defaultValue) {
        super(name, defaultValue, 0, 0xFFFFFFFF, 1);
        updateHSBFromRGB(defaultValue);
    }

    public boolean isPickerOpen() {
        return pickerOpen;
    }

    public void setPickerOpen(boolean pickerOpen) {
        this.pickerOpen = pickerOpen;
    }

    public void togglePicker() {
        this.pickerOpen = !this.pickerOpen;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer) {
            int newValue = (Integer) value;
            newValue = MathUtil.clamp(newValue, 0, 0xFFFFFFFF);
            super.setValue(newValue);
            updateHSBFromRGB(newValue);
        }
    }

    public void setHSBA(float h, float s, float b, float a) {
        hue = MathUtil.clamp(h, 0f, 1f);
        saturation = MathUtil.clamp(s, 0f, 1f);
        brightness = MathUtil.clamp(b, 0f, 1f);
        alpha = MathUtil.clamp(a, 0f, 1f);

        int rgb = hsbToRgb(hue, saturation, brightness);
        int newColor = ((int)(alpha * 255) << 24) | (rgb & 0xFFFFFF);
        super.setValue(newColor);
    }

    public float getHue() { return hue; }
    public float getSaturation() { return saturation; }
    public float getBrightness() { return brightness; }
    public float getAlpha() { return alpha; }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void updateHSBFromRGB(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        alpha = ((color >> 24) & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        // Hue
        if (delta == 0) {
            hue = 0;
        } else if (max == r) {
            hue = ((g - b) / delta) % 6;
        } else if (max == g) {
            hue = (b - r) / delta + 2;
        } else {
            hue = (r - g) / delta + 4;
        }
        hue = hue / 6.0f;
        if (hue < 0) hue += 1.0f;

        // Saturation
        saturation = max == 0 ? 0 : delta / max;

        // Brightness
        brightness = max;
    }

    public int hsbToRgb(float h, float s, float v) {
        h = h % 1.0f;
        if (h < 0) h += 1.0f;

        int hInt = (int) (h * 6);
        float f = h * 6 - hInt;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;
        switch (hInt) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            case 5: r = v; g = p; b = q; break;
            default: r = 0; g = 0; b = 0; break;
        }

        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }
}