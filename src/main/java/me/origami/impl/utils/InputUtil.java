package me.origami.impl.utils;

public class InputUtil {

    public static boolean isMouseInBounds(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static String getKeyName(int keyCode) {
        if (keyCode == -1) return "NONE";

        switch (keyCode) {
            case 32: return "SPACE";
            case 340: return "SHIFT";
            case 341: return "CTRL";
            case 342: return "ALT";
            case 344: return "R_SHIFT";
            case 345: return "R_CTRL";
            case 346: return "R_ALT";
            case 256: return "ESC";
            case 257: return "ENTER";
            case 258: return "TAB";
            case 259: return "BACKSPACE";
            case 260: return "INSERT";
            case 261: return "DELETE";
            case 262: return "RIGHT";
            case 263: return "LEFT";
            case 264: return "DOWN";
            case 265: return "UP";
            case 266: return "PAGE_UP";
            case 267: return "PAGE_DOWN";
            case 268: return "HOME";
            case 269: return "END";
            case 280: return "CAPS_LOCK";
            case 281: return "SCROLL_LOCK";
            case 282: return "NUM_LOCK";
            case 283: return "PRINT_SCREEN";
            case 284: return "PAUSE";
            case 290: return "F1";
            case 291: return "F2";
            case 292: return "F3";
            case 293: return "F4";
            case 294: return "F5";
            case 295: return "F6";
            case 296: return "F7";
            case 297: return "F8";
            case 298: return "F9";
            case 299: return "F10";
            case 300: return "F11";
            case 301: return "F12";
            default:
                if (keyCode >= 65 && keyCode <= 90) return String.valueOf((char) keyCode);
                if (keyCode >= 48 && keyCode <= 57) return String.valueOf((char) keyCode);
                return "KEY_" + keyCode;
        }
    }
}