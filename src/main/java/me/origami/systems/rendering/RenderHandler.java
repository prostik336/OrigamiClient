package me.origami.systems.rendering;

public class RenderHandler extends RenderBase {
    public RenderHandler(String name) { super(name); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Render logic here
    }
}