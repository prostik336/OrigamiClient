package me.origami;

import me.origami.api.managers.ModuleManager;
import me.origami.gui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OrigamiClient implements ClientModInitializer {
    public static final net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();

    // Объявляем KeyBinding для открытия GUI
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        // Регистрируем клавишу Right Shift для открытия GUI
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.origami.open_gui", // ID для keybind
                InputUtil.Type.KEYSYM, // Тип ввода (клавиатура)
                GLFW.GLFW_KEY_RIGHT_SHIFT, // Код клавиши Right Shift
                "category.origami.gui" // Категория в настройках управления
        ));

        // Регистрируем обработчик тиков для проверки нажатия клавиши
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Проверяем, была ли нажата наша клавиша
            while (openGuiKey.wasPressed()) {
                // Открываем GUI только если нет другого открытого экрана
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });

        System.out.println("Origami Client initialized!");
    }
}