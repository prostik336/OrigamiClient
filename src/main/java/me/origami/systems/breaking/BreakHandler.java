package me.origami.systems.breaking;

public class BreakHandler extends BreakBase {
    public BreakHandler(String name) { super(name); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Break logic here
    }
}