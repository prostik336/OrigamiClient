package me.origami.gui;

import me.origami.api.managers.ModuleManager;
import me.origami.api.module.Module;
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

    public BrowserScreen(Screen parent) {
        super(Text.literal("Origami Browser"));
        this.parent = parent;
        this.moduleManager = new ModuleManager();
        this.filteredModules = new ArrayList<>(moduleManager.getModules());
    }

    @Override
    protected void init() {
        super.init();

        this.searchField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 150, 60, 300, 20,
                Text.literal("Search modules...")
        );
        this.searchField.setChangedListener(this::onSearchTextChanged);
        this.searchField.setMaxLength(100);
        this.addSelectableChild(this.searchField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(20, 20, 60, 20).build());

        setInitialFocus(this.searchField);
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
        this.renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, "§b§lOrigami Browser", this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "§7" + moduleManager.getModules().size() + " modules available", this.width / 2, 35, 0xAAAAAA);

        this.searchField.render(context, mouseX, mouseY, delta);

        int startY = 100;
        int moduleHeight = 30;

        for (int i = 0; i < Math.min(filteredModules.size(), 10); i++) {
            Module module = filteredModules.get(i);
            int yPos = startY + i * moduleHeight;

            int bgColor = module.isEnabled() ? 0x4444FF44 : 0x44FFFFFF;
            context.fill(this.width / 2 - 180, yPos, this.width / 2 + 180, yPos + moduleHeight - 2, bgColor);

            String status = module.isEnabled() ? "§aON" : "§7OFF";
            context.drawTextWithShadow(this.textRenderer, "§e" + module.getName() + " " + status, this.width / 2 - 170, yPos + 5, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7" + module.getDescription(), this.width / 2 - 170, yPos + 15, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, "§8" + module.getCategory().getName(), this.width / 2 - 170, yPos + 25, 0x666666);

            int buttonX = this.width / 2 + 120;
            String buttonText = module.isEnabled() ? "[OFF]" : "[ON]";
            if (mouseX >= buttonX && mouseX <= buttonX + 50 &&
                    mouseY >= yPos + 5 && mouseY <= yPos + 20) {
                context.fill(buttonX, yPos + 5, buttonX + 50, yPos + 20, 0x44FFAA00);
            }
            context.drawTextWithShadow(this.textRenderer, "§6" + buttonText, buttonX + 10, yPos + 8, 0xFFFFFF);
        }

        context.drawTextWithShadow(this.textRenderer, "§7Showing " + filteredModules.size() + " modules", 20, this.height - 30, 0x666666);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int startY = 100;
            int moduleHeight = 30;

            for (int i = 0; i < Math.min(filteredModules.size(), 10); i++) {
                Module module = filteredModules.get(i);
                int yPos = startY + i * moduleHeight;

                int buttonX = this.width / 2 + 120;
                if (mouseX >= buttonX && mouseX <= buttonX + 50 &&
                        mouseY >= yPos + 5 && mouseY <= yPos + 20) {
                    module.toggle();
                    return true;
                }

                if (mouseX >= this.width / 2 - 180 && mouseX <= this.width / 2 + 180 &&
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