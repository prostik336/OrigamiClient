package me.origami.systems.placing;

public class PlaceHandler extends PlaceBase {
    public PlaceHandler(String name) { super(name); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Place logic here
    }
}