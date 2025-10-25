package me.origami.gui.clickgui;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ModuleComponent {
    private final Module module;
    private int height = 14;

    // settings display state
    private boolean settingsOpen = false;
    private int settingsOffsetY = 0;

    public ModuleComponent(Module module) {
        this.module = module;
    }

    public void draw(DrawContext ctx, int x, int y, int width) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int bg = module.isEnabled() ? 0xFF4B1A1A : 0xFF1F1F1F;
        ctx.fill(x + 1, y, x + width - 1, y + height, bg);
        ctx.drawText(tr, module.getName(), x + 6, y + 3, 0xFFFFFFFF, false);

        // draw bind hint
        String bindText = module.getKeyBind() > 0 ? "[" + module.getKeyBind() + "]" : "[]";
        ctx.drawText(tr, bindText, x + width - 28, y + 3, 0xFFCCCCCC, false);

        // если настройки открыты — рисуем снизу их элементы
        if (settingsOpen) {
            int curY = y + height;
            List<Setting<?>> settings = module.getSettings();
            for (Setting<?> s : settings) {
                int itemH = 14;
                ctx.fill(x + 1, curY, x + width - 1, curY + itemH, 0xFF252525);
                ctx.drawText(tr, s.getName() + ": " + s.getValue(), x + 8, curY + 3, 0xFFFFFFFF, false);
                curY += itemH;
            }
        }
    }

    public int getHeight() {
        int total = height;
        if (settingsOpen) {
            total += module.getSettings().size() * 14;
        }
        return total;
    }

    public void onLeftClick() {
        module.toggle();
    }

    public void onRightClick() {
        // open settings or start bind assignment
        // toggle settings on right click + start bind mode when pressing small area?
        // For simplicity: open settings; if settings already open - start listening for bind
        if (!settingsOpen) {
            settingsOpen = true;
        } else {
            // start listening for next key to set bind
            ClickGuiManager.get().startListeningBind(this);
            // feedback: you can print to console, or later add overlay text
            System.out.println("Press key to bind for module " + module.getName() + " (Esc to clear)");
        }
    }

    public Module getModule() { return module; }
}
