package me.origami.impl.utils;

import org.lwjgl.glfw.GLFW;

public class KeyBindUtil {

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
            case 302: return "F13";
            case 303: return "F14";
            case 304: return "F15";
            case 305: return "F16";
            case 306: return "F17";
            case 307: return "F18";
            case 308: return "F19";
            case 309: return "F20";
            case 310: return "F21";
            case 311: return "F22";
            case 312: return "F23";
            case 313: return "F24";
            case 314: return "F25";
            case 320: return "KP_0";
            case 321: return "KP_1";
            case 322: return "KP_2";
            case 323: return "KP_3";
            case 324: return "KP_4";
            case 325: return "KP_5";
            case 326: return "KP_6";
            case 327: return "KP_7";
            case 328: return "KP_8";
            case 329: return "KP_9";
            case 330: return "KP_DECIMAL";
            case 331: return "KP_DIVIDE";
            case 332: return "KP_MULTIPLY";
            case 333: return "KP_SUBTRACT";
            case 334: return "KP_ADD";
            case 335: return "KP_ENTER";
            case 336: return "KP_EQUAL";
            default:
                if (keyCode >= 65 && keyCode <= 90) return String.valueOf((char) keyCode);
                if (keyCode >= 48 && keyCode <= 57) return String.valueOf((char) keyCode);
                return "KEY_" + keyCode;
        }
    }

    public static boolean isMouseKey(int keyCode) {
        return keyCode >= GLFW.GLFW_MOUSE_BUTTON_1 && keyCode <= GLFW.GLFW_MOUSE_BUTTON_8;
    }
}