package me.origami.gui;

import me.origami.gui.widget.MeteorButtonWidget;
import me.origami.gui.widget.MeteorSliderWidget;
import me.origami.impl.settings.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class BrowserSettingsScreen extends Screen {
    private final Screen parent;
    private final List<Setting<?>> browserSettings;

    public BrowserSettingsScreen(Screen parent, List<Setting<?>> browserSettings) {
        super(Text.literal("Browser Settings"));
        this.parent = parent;
        this.browserSettings = browserSettings;
    }

    @Override
    protected void init() {
        int y = 30;
        int buttonWidth = 200;
        int buttonHeight = 20;

        // Добавляем кнопки для каждой настройки браузера
        for (Setting<?> setting : browserSettings) {
            if (setting.getName().equals("UseMonochrome") || setting.getName().equals("UseGradientForButtons")) {
                // Булевые настройки
                @SuppressWarnings("unchecked")
                Setting<Boolean> booleanSetting = (Setting<Boolean>) setting;
                final int finalY = y;

                this.addDrawableChild(new MeteorButtonWidget(
                        this.width / 2 - buttonWidth / 2, finalY, buttonWidth, buttonHeight,
                        Text.literal(setting.getName() + ": " + (booleanSetting.getValue() != null && booleanSetting.getValue() ? "ON" : "OFF")),
                        button -> {
                            boolean currentValue = booleanSetting.getValue() != null ? booleanSetting.getValue() : false;
                            booleanSetting.setValue(!currentValue);
                            button.setMessage(Text.literal(setting.getName() + ": " + (booleanSetting.getValue() ? "ON" : "OFF")));
                        }));
                y += 25;
            } else if (setting.isNumeric()) {
                // Числовые настройки - ползунки
                final int finalY = y;
                this.addDrawableChild(new MeteorSliderWidget(this.width / 2 - buttonWidth / 2, finalY, buttonWidth, buttonHeight, setting));
                y += 25;
            } else {
                // Остальные настройки
                final int finalY = y;
                this.addDrawableChild(new MeteorButtonWidget(
                        this.width / 2 - buttonWidth / 2, finalY, buttonWidth, buttonHeight,
                        Text.literal(setting.getName() + ": " + setting.getValue()),
                        button -> {
                            // Обработка других типов настроек
                        }));
                y += 25;
            }
        }

        // Кнопка "Back"
        this.addDrawableChild(new MeteorButtonWidget(
                this.width / 2 - buttonWidth / 2, y, buttonWidth, buttonHeight,
                Text.literal("Back"),
                button -> this.client.setScreen(this.parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Темный фон как в Meteor
        this.renderBackground(context, mouseX, mouseY, delta);

        // Заголовок в стиле Meteor
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Browser Settings"), this.width / 2, 10, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Customize browser appearance"), this.width / 2, 22, 0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);

        // Отображение tooltips при наведении
        for (var element : this.children()) {
            if (element instanceof MeteorButtonWidget button && button.isHovered()) {
                Setting<?> correspondingSetting = findSettingForButton(button);
                if (correspondingSetting != null) {
                    context.drawTooltip(this.textRenderer, Text.literal(correspondingSetting.getDescription()), mouseX, mouseY);
                }
            }
        }
    }

    private Setting<?> findSettingForButton(MeteorButtonWidget button) {
        String buttonText = button.getMessage().getString();
        for (Setting<?> setting : browserSettings) {
            if (buttonText.contains(setting.getName())) {
                return setting;
            }
        }
        return null;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}