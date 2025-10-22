package me.origami.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    public ClickGuiScreen() {
        super(Text.literal("Origami ClickGUI"));
    }

    @Override
    protected void init() {
        super.init();
        // Инициализация компонентов GUI
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);  // Рисуем фон
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "ClickGUI (RShift)", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;  // Не паузим игру при открытии
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Закрытие GUI по нажатию ESC
        if (keyCode == 256) { // ESC
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}