package me.origami.gui.widget;

import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MeteorSliderWidget extends ButtonWidget {
    private final Setting<?> setting;
    private boolean dragging;
    private double value;
    private static final int BACKGROUND_COLOR = 0xFF1E1E1E;
    private static final int FILL_COLOR = 0xFF3A5DDA;
    private static final int BORDER_COLOR = 0xFF404040;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public MeteorSliderWidget(int x, int y, int width, int height, Setting<?> setting) {
        super(x, y, width, height, Text.empty(), ButtonWidget::onPress, DEFAULT_NARRATION_SUPPLIER);
        this.setting = setting;
        this.dragging = false;
        updateValueFromSetting();
    }

    private void updateValueFromSetting() {
        Object currentValue = setting.getValue();
        if (currentValue instanceof Number) {
            this.value = ((Number) currentValue).doubleValue();
        }
    }

    @SuppressWarnings("unchecked")
    private void updateSettingFromValue() {
        Object currentValue = setting.getValue();
        if (currentValue instanceof Integer) {
            Setting<Integer> intSetting = (Setting<Integer>) setting;
            intSetting.setValue((int) Math.round(value));
        } else if (currentValue instanceof Double) {
            Setting<Double> doubleSetting = (Setting<Double>) setting;
            doubleSetting.setValue(value);
        }
    }

    private double getPercentage() {
        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        return (value - min) / (max - min);
    }

    private void setValueFromMouse(double mouseX) {
        double percentage = (mouseX - this.getX()) / (double) this.getWidth();
        percentage = Math.max(0.0, Math.min(1.0, percentage));

        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double increment = setting.getIncrement();

        double newValue = min + (max - min) * percentage;

        if (increment > 0) {
            newValue = Math.round(newValue / increment) * increment;
        }

        newValue = Math.max(min, Math.min(max, newValue));
        this.value = newValue;
        updateSettingFromValue();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValueFromMouse(mouseX);
        this.dragging = true;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.dragging) {
            setValueFromMouse(mouseX);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.dragging = false;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = this.isHovered() || this.dragging;

        // Фон слайдера
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), BACKGROUND_COLOR);

        // Заполненная часть
        int sliderWidth = (int) (this.getWidth() * getPercentage());
        int fillColor = hovered ? 0xFF4A6DF2 : FILL_COLOR;
        context.fill(this.getX(), this.getY(), this.getX() + sliderWidth, this.getY() + this.getHeight(), fillColor);

        // Рамка
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, BORDER_COLOR);
        context.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), BORDER_COLOR);
        context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), BORDER_COLOR);
        context.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), BORDER_COLOR);

        // Текст - используем textRenderer из MinecraftClient
        String displayText;
        Object currentValue = setting.getValue();
        if (currentValue instanceof Integer) {
            displayText = String.format("%s: %d", setting.getName(), (int) Math.round(value));
        } else {
            displayText = String.format("%s: %.1f", setting.getName(), value);
        }

        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal(displayText),
                this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, TEXT_COLOR);
    }
}