// FakePlayer.java
package me.origami.module.render;

import me.origami.module.Module;
import me.origami.impl.settings.Setting;

public class FakePlayer extends Module {
    public Setting<String> name = register(new Setting<>("Name", "FakePlayer"));
    public Setting<Double> health = register(new Setting<>("Health", 20.0, 1.0, 20.0, 1.0));

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake player entity", Category.RENDER);
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}
}