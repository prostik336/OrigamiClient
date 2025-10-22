package me.origami.mods;

import me.origami.api.module.Module;
import me.origami.api.module.Category;
import me.origami.api.settings.Setting;
import me.origami.api.managers.ModuleManager;

public class BetterChat extends Module {
    public static BetterChat INSTANCE;

    public Setting<Boolean> prependGreen = register(new Setting<>("Prepend >", true, "Adds > for green text"));

    public BetterChat() {
        super("BetterChat", "Green chat prefix", Category.MISC);
        INSTANCE = this;
        ModuleManager.getModules().add(this);
    }
}