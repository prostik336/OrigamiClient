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
            } catch (IOException e) {
                System.err.println("Failed to load GUI config: " + e.getMessage());
            }
        }
    }

    public static void saveTabPosition(ClickGuiTab t) {
        P.setProperty(t.getTitle() + ".x", String.valueOf(t.getX()));
        P.setProperty(t.getTitle() + ".y", String.valueOf(t.getY()));
        try (FileOutputStream fos = new FileOutputStream(FILE)) {
            P.store(fos, "Origami ClickGui positions");
        } catch (IOException e) {
            System.err.println("Failed to save GUI config: " + e.getMessage());
        }
    }

    public static void loadTabPosition(ClickGuiTab t) {
        try {
            t.setX(Integer.parseInt(P.getProperty(t.getTitle() + ".x", String.valueOf(t.getX()))));
            t.setY(Integer.parseInt(P.getProperty(t.getTitle() + ".y", String.valueOf(t.getY()))));
        } catch (NumberFormatException e) {
            System.err.println("Invalid position format for tab " + t.getTitle());
        }
    }

    public static void saveAllTabs(ClickGuiManager manager) {
        for (ClickGuiTab tab : manager.getTabs()) {
            saveTabPosition(tab);
        }
    }

    public static void loadAllTabs(ClickGuiManager manager) {
        for (ClickGuiTab tab : manager.getTabs()) {
            loadTabPosition(tab);
        }
    }
}