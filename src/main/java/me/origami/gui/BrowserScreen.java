package me.origami.gui;

import me.origami.gui.widget.MeteorButtonWidget;
import me.origami.impl.managers.ModuleManager;
import me.origami.impl.settings.Setting;
import me.origami.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BrowserScreen extends Screen {
    private TextFieldWidget searchField;
    private final Screen parent;
    private final ModuleManager moduleManager;
    private List<Module> filteredModules;
    private final List<Setting<?>> browserSettings;

    public BrowserScreen(Screen parent) {
        super(Text.literal("Origami Browser"));
        this.parent = parent;
        this.moduleManager = new ModuleManager();
        this.filteredModules = new ArrayList<>(moduleManager.getModules());
        this.browserSettings = createBrowserSettings();
    }

    // Создаем настройки браузера напрямую, без зависимости от BrowserModule
    private List<Setting<?>> createBrowserSettings() {
        List<Setting<?>> settings = new ArrayList<>();

        // Основные настройки браузера
        settings.add(new Setting<>("GradientStartColor", 0xFF1E1E1E, "Starting color of gradient (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("GradientEndColor", 0xFF2D2D2D, "Ending color of gradient (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("UseMonochrome", false, "Use monochrome color instead of gradient"));
        settings.add(new Setting<>("MonochromeColor", 0xFF1E1E1E, "Monochrome color (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("BrowserWidth", 360.0, "Width of browser window", 200.0, 600.0, 10.0));
        settings.add(new Setting<>("BrowserHeight", 300.0, "Height of browser window", 200.0, 600.0, 10.0));
        settings.add(new Setting<>("ButtonWidth", 50.0, "Width of buttons", 30.0, 100.0, 5.0));
        settings.add(new Setting<>("ButtonHeight", 20.0, "Height of buttons", 15.0, 40.0, 5.0));
        settings.add(new Setting<>("TextColor", 0xFFFFFFFF, "Color of text (RGB hex)", 0x000000, 0xFFFFFF, 1));

        // Настройки кнопок
        settings.add(new Setting<>("ButtonOutlineColor", 0xFF404040, "Color of button outlines (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("UseGradientForButtons", false, "Use gradient for buttons instead of solid color"));
        settings.add(new Setting<>("ButtonStartColor", 0xFF1E1E1E, "Start color for button gradient (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("ButtonEndColor", 0xFF2D2D2D, "End color for button gradient (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("ButtonSolidColor", 0xFF1E1E1E, "Solid color for buttons (RGB hex)", 0x000000, 0xFFFFFF, 1));
        settings.add(new Setting<>("ButtonTransparency", 255, "Button transparency (0-255)", 0, 255, 1));

        return settings;
    }

    @Override
    protected void init() {
        Setting<?> browserWidthSetting = getSettingByName("BrowserWidth");
        double browserWidth = browserWidthSetting != null && browserWidthSetting.getValue() instanceof Double
                ? (Double) browserWidthSetting.getValue()
                : 360.0;

        this.searchField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - (int)(browserWidth / 2), 60, (int)browserWidth, 20,
                Text.literal("Search modules...")
        );
        this.searchField.setChangedListener(this::onSearchTextChanged);
        this.searchField.setMaxLength(100);
        this.addSelectableChild(this.searchField);

        Setting<?> buttonWidthSetting = getSettingByName("ButtonWidth");
        double buttonWidth = buttonWidthSetting != null && buttonWidthSetting.getValue() instanceof Double
                ? (Double) buttonWidthSetting.getValue()
                : 50.0;
        Setting<?> buttonHeightSetting = getSettingByName("ButtonHeight");
        double buttonHeight = buttonHeightSetting != null && buttonHeightSetting.getValue() instanceof Double
                ? (Double) buttonHeightSetting.getValue()
                : 20.0;

        // Кнопка "Back" справа сверху
        this.addDrawableChild(new MeteorButtonWidget(
                this.width - (int)buttonWidth - 20, 20, (int)buttonWidth, (int)buttonHeight,
                Text.literal("Back"), button -> {
            this.client.setScreen(this.parent);
        }));

        // Кнопка настроек (шестеренка) слева сверху
        this.addDrawableChild(new MeteorSettingsButtonWidget(20, 20, (int)buttonHeight, (int)buttonHeight,
                Text.literal("⚙"), button -> {
            this.client.setScreen(new BrowserSettingsScreen(this, browserSettings));
        }));

        setInitialFocus(this.searchField);
    }

    private Setting<?> getSettingByName(String name) {
        for (Setting<?> setting : browserSettings) {
            if (setting.getName().equals(name)) {
                return setting;
            }
        }
        return null;
    }

    private void onSearchTextChanged(String text) {
        filterModules(text);
    }

    private void filterModules(String query) {
        filteredModules.clear();

        if (query.isEmpty()) {
            filteredModules.addAll(moduleManager.getModules());
            return;
        }

        String lowerQuery = query.toLowerCase();
        for (Module module : moduleManager.getModules()) {
            if (module.getName().toLowerCase().contains(lowerQuery) ||
                    module.getDescription().toLowerCase().contains(lowerQuery) ||
                    module.getCategory().getName().toLowerCase().contains(lowerQuery)) {
                filteredModules.add(module);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Отрисовка фона браузера в стиле Meteor
        Setting<?> browserWidthSetting = getSettingByName("BrowserWidth");
        double browserWidth = browserWidthSetting != null && browserWidthSetting.getValue() instanceof Double
                ? (Double) browserWidthSetting.getValue()
                : 360.0;
        Setting<?> browserHeightSetting = getSettingByName("BrowserHeight");
        double browserHeight = browserHeightSetting != null && browserHeightSetting.getValue() instanceof Double
                ? (Double) browserHeightSetting.getValue()
                : 300.0;

        int startX = this.width / 2 - (int)(browserWidth / 2);
        int endX = this.width / 2 + (int)(browserWidth / 2);
        int startY = 0;
        int endY = (int)browserHeight;

        Setting<?> useMonochromeSetting = getSettingByName("UseMonochrome");
        Setting<?> monochromeColorSetting = getSettingByName("MonochromeColor");
        Setting<?> gradientStartColorSetting = getSettingByName("GradientStartColor");
        Setting<?> gradientEndColorSetting = getSettingByName("GradientEndColor");

        // Темный фон в стиле Meteor
        if (useMonochromeSetting != null && monochromeColorSetting != null && useMonochromeSetting.getValue() instanceof Boolean && (Boolean)useMonochromeSetting.getValue()) {
            context.fill(startX, startY, endX, endY, (Integer)monochromeColorSetting.getValue());
        } else if (gradientStartColorSetting != null && gradientEndColorSetting != null && gradientStartColorSetting.getValue() instanceof Integer && gradientEndColorSetting.getValue() instanceof Integer) {
            context.fillGradient(startX, startY, endX, endY, (Integer)gradientStartColorSetting.getValue(), (Integer)gradientEndColorSetting.getValue(), 0);
        } else {
            // Фон по умолчанию в стиле Meteor
            context.fill(startX, startY, endX, endY, 0xFF1E1E1E);
        }

        // Рамка браузера
        context.fill(startX, startY, endX, startY + 1, 0xFF404040); // Верх
        context.fill(startX, endY - 1, endX, endY, 0xFF404040); // Низ
        context.fill(startX, startY, startX + 1, endY, 0xFF404040); // Лево
        context.fill(endX - 1, startY, endX, endY, 0xFF404040); // Право

        Setting<?> textColorSetting = getSettingByName("TextColor");
        int textColor = textColorSetting != null && textColorSetting.getValue() instanceof Integer
                ? (Integer) textColorSetting.getValue()
                : 0xFFFFFFFF;

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Origami Browser"), this.width / 2, 20, textColor);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(moduleManager.getModules().size() + " modules available"), this.width / 2, 35, 0xAAAAAA);

        this.searchField.render(context, mouseX, mouseY, delta);

        // Отрисовка модулей с кастомными кнопками в стиле Meteor
        Setting<?> buttonHeightSetting = getSettingByName("ButtonHeight");
        double buttonHeight = buttonHeightSetting != null && buttonHeightSetting.getValue() instanceof Double
                ? (Double) buttonHeightSetting.getValue()
                : 20.0;
        Setting<?> buttonWidthSetting = getSettingByName("ButtonWidth");
        double buttonWidth = buttonWidthSetting != null && buttonWidthSetting.getValue() instanceof Double
                ? (Double) buttonWidthSetting.getValue()
                : 50.0;

        int startYModules = 100;
        int moduleHeight = (int)buttonHeight + 10;

        for (int i = 0; i < Math.min(filteredModules.size(), 10); i++) {
            Module module = filteredModules.get(i);
            int yPos = startYModules + i * moduleHeight;

            // Фон модуля в стиле Meteor
            int bgColor = module.isEnabled() ? 0x4444FF44 : 0x44333333;
            context.fill(startX, yPos, endX, yPos + moduleHeight - 2, bgColor);

            // Рамка модуля
            context.fill(startX, yPos, endX, yPos + 1, 0xFF404040);
            context.fill(startX, yPos + moduleHeight - 2, endX, yPos + moduleHeight - 1, 0xFF404040);

            // Текст модуля
            String status = module.isEnabled() ? "ON" : "OFF";
            int statusColor = module.isEnabled() ? 0xFF00FF00 : 0xFFFF0000;

            context.drawTextWithShadow(this.textRenderer, Text.literal(module.getName()), startX + 10, yPos + 5, textColor);
            context.drawTextWithShadow(this.textRenderer, Text.literal(status), startX + 10, yPos + 15, statusColor);
            context.drawTextWithShadow(this.textRenderer, Text.literal(module.getDescription()), startX + 10, yPos + 25, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, Text.literal(module.getCategory().getName()), startX + 10, yPos + 35, 0x666666);

            // Кнопка включения/выключения в стиле Meteor
            int buttonX = endX - (int)buttonWidth - 10;
            renderMeteorStyleButton(context, buttonX, yPos + 5, (int)buttonWidth, (int)buttonHeight,
                    module.isEnabled() ? "Disable" : "Enable",
                    mouseX, mouseY, module.isEnabled());
        }

        context.drawTextWithShadow(this.textRenderer, Text.literal("Showing " + filteredModules.size() + " modules"), 20, endY - 30, 0x666666);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderMeteorStyleButton(DrawContext context, int x, int y, int width, int height, String text, int mouseX, int mouseY, boolean isEnabled) {
        // Стиль Meteor: темный фон с серой рамкой
        int backgroundColor = 0xFF1E1E1E;
        int borderColor = 0xFF404040;
        int hoverColor = 0xFF2A2A2A;
        int textColor = 0xFFFFFFFF;

        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        // Фон кнопки
        context.fill(x, y, x + width, y + height, hovered ? hoverColor : backgroundColor);

        // Рамка
        context.fill(x, y, x + width, y + 1, borderColor);
        context.fill(x, y + height - 1, x + width, y + height, borderColor);
        context.fill(x, y, x + 1, y + height, borderColor);
        context.fill(x + width - 1, y, x + width, y + height, borderColor);

        // Текст
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(text),
                x + width / 2, y + (height - 8) / 2, textColor);
    }

    // Кастомная кнопка настроек в стиле Meteor
    private class MeteorSettingsButtonWidget extends ButtonWidget {
        private static final int BACKGROUND_COLOR = 0xFF1E1E1E;
        private static final int BORDER_COLOR = 0xFF404040;
        private static final int HOVER_COLOR = 0xFF2A2A2A;
        private static final int TEXT_COLOR = 0xFFFFFFFF;

        public MeteorSettingsButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            // Круглая кнопка для настроек в стиле Meteor
            int centerX = this.getX() + this.getWidth() / 2;
            int centerY = this.getY() + this.getHeight() / 2;
            int radius = this.getWidth() / 2 - 1;

            // Фон кнопки
            int backgroundColor = this.active ?
                    (this.isHovered() ? HOVER_COLOR : BACKGROUND_COLOR) :
                    0xFF121212;

            drawCircle(context, centerX, centerY, radius, backgroundColor);
            drawCircleOutline(context, centerX, centerY, radius, BORDER_COLOR);

            // Текст (шестеренка)
            int textColor = this.active ? TEXT_COLOR : 0xFFAAAAAA;
            context.drawCenteredTextWithShadow(BrowserScreen.this.client.textRenderer, this.getMessage(),
                    centerX, centerY - 4, textColor);
        }

        private void drawCircle(DrawContext context, int centerX, int centerY, int radius, int color) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    if (x * x + y * y <= radius * radius) {
                        context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                    }
                }
            }
        }

        private void drawCircleOutline(DrawContext context, int centerX, int centerY, int radius, int color) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    double distance = Math.sqrt(x * x + y * y);
                    if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                        context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Setting<?> browserWidthSetting = getSettingByName("BrowserWidth");
            double browserWidth = browserWidthSetting != null && browserWidthSetting.getValue() instanceof Double
                    ? (Double) browserWidthSetting.getValue()
                    : 360.0;
            int startX = this.width / 2 - (int)(browserWidth / 2);
            int endX = this.width / 2 + (int)(browserWidth / 2);
            int startY = 100;

            Setting<?> buttonHeightSetting = getSettingByName("ButtonHeight");
            double buttonHeight = buttonHeightSetting != null && buttonHeightSetting.getValue() instanceof Double
                    ? (Double) buttonHeightSetting.getValue()
                    : 20.0;
            Setting<?> buttonWidthSetting = getSettingByName("ButtonWidth");
            double buttonWidth = buttonWidthSetting != null && buttonWidthSetting.getValue() instanceof Double
                    ? (Double) buttonWidthSetting.getValue()
                    : 50.0;

            int moduleHeight = (int)buttonHeight + 10;

            for (int i = 0; i < Math.min(filteredModules.size(), 10); i++) {
                Module module = filteredModules.get(i);
                int yPos = startY + i * moduleHeight;

                int buttonX = endX - (int)buttonWidth - 10;
                if (mouseX >= buttonX && mouseX <= buttonX + (int)buttonWidth &&
                        mouseY >= yPos + 5 && mouseY <= yPos + (int)buttonHeight) {
                    module.toggle();
                    return true;
                }

                if (mouseX >= startX && mouseX <= endX &&
                        mouseY >= yPos && mouseY <= yPos + moduleHeight - 2) {
                    this.client.setScreen(new ModuleSettingsScreen(this, module));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }

        return this.searchField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}