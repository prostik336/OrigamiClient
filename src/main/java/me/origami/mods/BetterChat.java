package me.origami.mods;

import me.origami.api.module.Module;

public class BetterChat extends Module {
    private boolean prependGreen = true;
    private boolean timestamps = true;

    public BetterChat() {
        super("BetterChat", Category.MISC);
    }

    public boolean isPrependGreen() {
        return prependGreen;
    }

    public void setPrependGreen(boolean prependGreen) {
        this.prependGreen = prependGreen;
    }

    public boolean hasTimestamps() {
        return timestamps;
    }

    public void setTimestamps(boolean timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public String getDescription() {
        return "Enhances the chat with additional features";
    }
}