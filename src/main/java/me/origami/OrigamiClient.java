package me.origami;

import me.origami.impl.managers.ModuleManager;
import me.origami.gui.clickgui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OrigamiClient implements ClientModInitializer {
    public static final net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
    public static ModuleManager MODULE_MANAGER = new ModuleManager();

    private KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        this.openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.origami.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.origami.gui"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MODULE_MANAGER.onTick();

            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });

        System.out.println("Origami Client initialized!");
    }
}