package me.origami.impl.utils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class RenderUtil {

    public static void drawRect(DrawContext ctx, int x, int y, int width, int height, int color) {
        ctx.fill(x, y, x + width, y + height, color);
    }

    public static void drawBorder(DrawContext ctx, int x, int y, int width, int height, int color) {
        ctx.fill(x, y, x + width, y + 1, color); // Top
        ctx.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        ctx.fill(x, y, x + 1, y + height, color); // Left
        ctx.fill(x + width - 1, y, x + width, y + height, color); // Right
    }

    public static void drawCenteredText(DrawContext ctx, TextRenderer tr, String text, int x, int y, int width, int color) {
        int textWidth = tr.getWidth(text);
        int centeredX = x + (width - textWidth) / 2;
        ctx.drawText(tr, text, centeredX, y, color, false);
    }
}