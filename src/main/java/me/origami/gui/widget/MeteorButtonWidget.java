package me.origami.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MeteorButtonWidget extends ButtonWidget {
    private static final int BACKGROUND_COLOR = 0xFF1E1E1E;
    private static final int BORDER_COLOR = 0xFF404040;
    private static final int HOVER_COLOR = 0xFF2A2A2A;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_COLOR_DISABLED = 0xFFAAAAAA;

    public MeteorButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Фон кнопки
        int backgroundColor = this.active ?
                (this.isHovered() ? HOVER_COLOR : BACKGROUND_COLOR) :
                0xFF121212;

        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), backgroundColor);

        // Рамка
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, BORDER_COLOR);
        context.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), BORDER_COLOR);
        context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), BORDER_COLOR);
        context.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), BORDER_COLOR);

        // Текст - используем textRenderer из MinecraftClient
        int textColor = this.active ? TEXT_COLOR : TEXT_COLOR_DISABLED;
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(),
                this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, textColor);
    }
}