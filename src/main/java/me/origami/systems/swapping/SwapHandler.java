package me.origami.systems.swapping;

public class SwapHandler extends SwapBase {
    public SwapHandler(String name) { super(name); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Swap logic here
    }
}