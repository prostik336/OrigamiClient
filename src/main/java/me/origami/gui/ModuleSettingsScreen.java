package me.origami.gui;

import me.origami.impl.settings.Setting;
import me.origami.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;
    private List<Setting<?>> settings;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SLIDER_WIDTH = 200;
    private static final int SLIDER_HEIGHT = 20;

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Text.literal(module.getName() + " Settings"));
        this.parent = parent;
        this.module = module;
        this.settings = module.getSettings();
    }

    @Override
    protected void init() {
        int y = 20;
        for (Setting<?> setting : settings) {
            if (setting.getName().equals("UseMonochrome")) {
                @SuppressWarnings("unchecked")
                Setting<Boolean> booleanSetting = (Setting<Boolean>) setting;
                final int finalY = y;
                addDrawableChild(new CustomButtonWidget(this.width / 2 - BUTTON_WIDTH / 2, finalY, BUTTON_WIDTH, BUTTON_HEIGHT,
                        Text.literal("Toggle Monochrome: " + (booleanSetting.getValue() != null && booleanSetting.getValue() ? "On" : "Off")),
                        button -> {
                            boolean currentValue = booleanSetting.getValue() != null ? booleanSetting.getValue() : false;
                            booleanSetting.setValue(!currentValue);
                            button.setMessage(Text.literal("Toggle Monochrome: " + (booleanSetting.getValue() ? "On" : "Off")));
                        }));
                y += 25;
            } else if (setting.isNumeric()) {
                final int finalY = y;
                // Создаем ползунок для числовых настроек
                addDrawableChild(new SliderWidget(this.width / 2 - SLIDER_WIDTH / 2, finalY, SLIDER_WIDTH, SLIDER_HEIGHT, setting));
                y += 25;
            } else {
                // Для нечисловых настроек используем обычную кнопку
                final int finalY = y;
                addDrawableChild(new CustomButtonWidget(this.width / 2 - BUTTON_WIDTH / 2, finalY, BUTTON_WIDTH, BUTTON_HEIGHT,
                        Text.literal(setting.getName() + ": " + setting.getValue()),
                        button -> {
                            // Обработка клика по нечисловой настройке
                        }));
                y += 25;
            }
        }

        addDrawableChild(new CustomButtonWidget(this.width / 2 - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Back"), button -> this.client.setScreen(parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Кастомная кнопка
    private class CustomButtonWidget extends ButtonWidget {
        public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int startColor = 0x1E3A8A;
            int endColor = 0x7E22CE;
            boolean hovered = this.isSelected();

            // Градиентный фон
            context.fillGradient(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(),
                    hovered ? 0xFF2A4DF2 : startColor, hovered ? 0xFF9D44D7 : endColor, 0);

            // Рамка
            context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, 0xFFFFFFFF);
            context.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFFFFFFFF);
            context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), 0xFFFFFFFF);
            context.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFFFFFFFF);

            // Текст
            int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
            context.drawCenteredTextWithShadow(ModuleSettingsScreen.this.client.textRenderer, this.getMessage(),
                    this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, textColor);

            // Эффект при наведении
            if (this.isHovered()) {
                context.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1,
                        this.getY() + this.getHeight() - 1, 0x22000000);
            }
        }
    }

    // Виджет ползунка для числовых настроек
    private class SliderWidget extends ButtonWidget {
        private final Setting<?> setting;
        private boolean dragging;
        private double value;

        public SliderWidget(int x, int y, int width, int height, Setting<?> setting) {
            super(x, y, width, height, Text.empty(), button -> {}, DEFAULT_NARRATION_SUPPLIER);
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
                // Для Integer - округляем и устанавливаем
                int intValue = (int) Math.round(value);
                Setting<Integer> intSetting = (Setting<Integer>) setting;
                intSetting.setValue(intValue);
            } else if (currentValue instanceof Double) {
                // Для Double - устанавливаем как есть
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

            // Округление до ближайшего шага
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
            int startColor = 0x1E3A8A;
            int endColor = 0x7E22CE;
            boolean hovered = this.isSelected() || this.dragging;

            // Фон ползунка
            context.fillGradient(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(),
                    hovered ? 0xFF2A4DF2 : startColor, hovered ? 0xFF9D44D7 : endColor, 0);

            // Заполненная часть ползунка
            int sliderWidth = (int) (this.getWidth() * getPercentage());
            context.fillGradient(this.getX(), this.getY(), this.getX() + sliderWidth, this.getY() + this.getHeight(),
                    hovered ? 0xFF4A6DF2 : 0xFF3A5DDA, hovered ? 0xFFAD64E7 : 0xFF9D54D7, 0);

            // Рамка
            context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, 0xFFFFFFFF);
            context.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFFFFFFFF);
            context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), 0xFFFFFFFF);
            context.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFFFFFFFF);

            // Текст с названием и значением
            String displayText;
            Object currentValue = setting.getValue();
            if (currentValue instanceof Integer) {
                displayText = String.format("%s: %d", setting.getName(), (int) Math.round(value));
            } else {
                displayText = String.format("%s: %.1f", setting.getName(), value);
            }

            int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
            context.drawCenteredTextWithShadow(ModuleSettingsScreen.this.client.textRenderer, Text.literal(displayText),
                    this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, textColor);

            // Эффект при наведении
            if (this.isHovered() || this.dragging) {
                context.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1,
                        this.getY() + this.getHeight() - 1, 0x22000000);
            }
        }
    }
}