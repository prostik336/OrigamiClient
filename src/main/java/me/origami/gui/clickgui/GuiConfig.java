package me.origami.gui.clickgui;

import java.io.*;
import java.util.Properties;

public class GuiConfig {
    private static final File FILE = new File(System.getProperty("user.dir"), ".origami_clickgui.properties");
    private static final Properties P = new Properties();

    static {
        if (FILE.exists()) {
            try (FileInputStream fis = new FileInputStream(FILE)) {
                P.load(fis);
            } catch (Exception ignored) {}
        }
    }

    public static void saveTabPosition(ClickGuiTab t) {
        P.setProperty(t.getTitle() + ".x", Integer.toString(t.getX()));
        P.setProperty(t.getTitle() + ".y", Integer.toString(t.getY()));
        try (FileOutputStream fos = new FileOutputStream(FILE)) {
            P.store(fos, "Origami ClickGui positions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTabPosition(ClickGuiTab t) {
        String sx = P.getProperty(t.getTitle() + ".x");
        String sy = P.getProperty(t.getTitle() + ".y");
        if (sx != null) {
            try { t.setX(Integer.parseInt(sx)); } catch (Exception ignored) {}
        }
        if (sy != null) {
            try { t.setY(Integer.parseInt(sy)); } catch (Exception ignored) {}
        }
    }
}
