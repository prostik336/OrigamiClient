package me.origami.gui.clickgui;

import me.origami.impl.settings.ColorSetting;
import me.origami.impl.utils.InputUtil;
import net.minecraft.client.gui.DrawContext;

public class ColorPicker {
    private final ColorSetting setting;
    private boolean draggingSB = false; // Saturation/Brightness
    private boolean draggingHue = false;
    private boolean draggingAlpha = false;

    private static final int WIDTH = 98; // 60 (SB) + 10 (отступ) + 28 (Hue)
    private static final int HEIGHT = 68; // 60 (SB) + 8 (Alpha)

    public ColorPicker(ColorSetting setting) {
        this.setting = setting;
    }

    public void draw(DrawContext ctx, int x, int y) {
        System.out.println("ColorPicker draw: x=" + x + ", y=" + y + ", setting=" + setting.getName());

        // Saturation/Brightness picker (60x60)
        float s = setting.getSaturation();
        float b = setting.getBrightness();
        float h = setting.getHue();
        float a = setting.getAlpha();

        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 60; j++) {
                float sat = i / 60.0f;
                float bright = 1.0f - j / 60.0f;
                int color = setting.hsbToRgb(h, sat, bright);
                ctx.fill(x + i, y + j, x + i + 1, y + j + 1, color);
            }
        }

        // Draw crosshair for current S/B
        int crossX = (int) (x + s * 60);
        int crossY = (int) (y + (1.0f - b) * 60);
        ctx.fill(crossX - 2, crossY, crossX + 3, crossY + 1, 0xFFFFFFFF);
        ctx.fill(crossX, crossY - 2, crossX + 1, crossY + 3, 0xFFFFFFFF);

        // Draw Hue slider (vertical, 28x60, справа)
        for (int i = 0; i < 60; i++) {
            float hue = i / 60.0f;
            int color = setting.hsbToRgb(hue, 1.0f, 1.0f);
            ctx.fill(x + 70, y + i, x + 98, y + i + 1, color);
        }

        // Draw Hue marker
        int hueY = (int) (y + h * 60);
        ctx.fill(x + 70, hueY - 1, x + 98, hueY + 2, 0xFFFFFFFF);

        // Draw Alpha slider (horizontal, под SB)
        for (int i = 0; i < 60; i++) {
            float alpha = i / 60.0f;
            int color = (int) (alpha * 255) << 24 | (setting.getValue() & 0xFFFFFF);
            ctx.fill(x + i, y + 60, x + i + 1, y + 68, color);
        }

        // Draw Alpha marker
        int alphaX = (int) (x + a * 60);
        ctx.fill(alphaX - 1, y + 60, alphaX + 2, y + 68, 0xFFFFFFFF);
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        System.out.println("ColorPicker handleClick: mouseX=" + mouseX + ", mouseY=" + mouseY + ", button=" + button + ", x=" + setting.getX() + ", y=" + setting.getY() + ", setting=" + setting.getName());

        if (button != 0) return false;

        // Saturation/Brightness area (60x60)
        if (InputUtil.isMouseInBounds(mouseX, mouseY, setting.getX(), setting.getY(), 60, 60)) {
            draggingSB = true;
            updateSB(mouseX, mouseY);
            return true;
        }

        // Hue slider (28x60, справа)
        if (InputUtil.isMouseInBounds(mouseX, mouseY, setting.getX() + 70, setting.getY(), 28, 60)) {
            draggingHue = true;
            updateHue(mouseY);
            return true;
        }

        // Alpha slider (60x8, снизу)
        if (InputUtil.isMouseInBounds(mouseX, mouseY, setting.getX(), setting.getY() + 60, 60, 8)) {
            draggingAlpha = true;
            updateAlpha(mouseX);
            return true;
        }

        return false;
    }

    public boolean handleDrag(double mouseX, double mouseY) {
        System.out.println("ColorPicker handleDrag: mouseX=" + mouseX + ", mouseY=" + mouseY + ", setting=" + setting.getName());
        if (draggingSB) {
            updateSB(mouseX, mouseY);
            return true;
        }
        if (draggingHue) {
            updateHue(mouseY);
            return true;
        }
        if (draggingAlpha) {
            updateAlpha(mouseX);
            return true;
        }
        return false;
    }

    public void stopDrag() {
        draggingSB = false;
        draggingHue = false;
        draggingAlpha = false;
    }

    private void updateSB(double mouseX, double mouseY) {
        double relX = Math.max(0, Math.min(60, mouseX - setting.getX()));
        double relY = Math.max(0, Math.min(60, mouseY - setting.getY()));
        float s = (float) (relX / 60.0);
        float b = (float) (1.0 - relY / 60.0);
        setting.setHSBA(setting.getHue(), s, b, setting.getAlpha());
    }

    private void updateHue(double mouseY) {
        double relY = Math.max(0, Math.min(60, mouseY - setting.getY()));
        float h = (float) (relY / 60.0);
        setting.setHSBA(h, setting.getSaturation(), setting.getBrightness(), setting.getAlpha());
    }

    private void updateAlpha(double mouseX) {
        double relX = Math.max(0, Math.min(60, mouseX - setting.getX()));
        float a = (float) (relX / 60.0);
        setting.setHSBA(setting.getHue(), setting.getSaturation(), setting.getBrightness(), a);
    }

    public int getHeight() {
        return HEIGHT;
    }

    public ColorSetting getSetting() {
        return setting;
    }
}