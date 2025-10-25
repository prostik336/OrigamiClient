package me.origami.systems.rotate;

public class RotateHandler extends RotateBase {
    public RotateHandler(String name) { super(name); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Rotation logic here
    }
}