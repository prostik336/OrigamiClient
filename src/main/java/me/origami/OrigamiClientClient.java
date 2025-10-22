package me.origami;

import me.origami.gui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class OrigamiClientClient implements ClientModInitializer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        // Регистрируем обработчик тиков для клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null && client.player != null) {
                // Проверка нажатия правого Shift
                if (client.options.sneakKey.isPressed()) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });

        System.out.println("Origami Client (client-side) initialized!");
    }
}