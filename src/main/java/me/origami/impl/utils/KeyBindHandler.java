package me.origami.impl.utils;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyBindHandler {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();

    public static boolean isKeyPressed(int keyCode) {
        if (keyCode == -1) return false;

        long windowHandle = mc.getWindow().getHandle();
        boolean pressed = GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
        boolean wasPressed = keyStates.getOrDefault(keyCode, false);

        // Возвращаем true только при первом нажатии (не залипание)
        if (pressed && !wasPressed) {
            keyStates.put(keyCode, true);
            return true;
        } else if (!pressed) {
            keyStates.put(keyCode, false);
        }

        return false;
    }

    public static void updateKeyStates() {
        // Можно вызывать каждый тик для обновления состояний
        for (Integer key : keyStates.keySet()) {
            long windowHandle = mc.getWindow().getHandle();
            boolean pressed = GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS;
            keyStates.put(key, pressed);
        }
    }
}