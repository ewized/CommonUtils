package com.gmail.favorlock.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum FontFormat {

    BLACK("0"),
    DARK_BLUE("1"),
    DARK_GREEN("2"),
    DARK_AQUA("3"),
    DARK_RED("4"),
    PURPLE("5"),
    ORANGE("6"),
    GREY("7"),
    DARK_GREY("8"),
    BLUE("9"),
    GREEN("a"),
    AQUA("b"),
    RED("c"),
    PINK("d"),
    YELLOW("e"),
    WHITE("f"),
    RANDOM("k"),
    BOLD("l"),
    STRIKE("m"),
    UNDERLINED("n"),
    ITALICS("o"),
    RESET("r");

    private final String value;
    private static final Map<String, String> translate;
    private static final String COLOR_PREFIX_CHARACTER = "\u00a7";
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf("&") + "[0-9A-FK-OR]");

    private FontFormat(String value) {
        this.value = COLOR_PREFIX_CHARACTER + value;
    }

    public String toString() {
        return this.value;
    }

    static {
        translate = new HashMap<String, String>();
        createMap();
    }

    private static void createMap() {
        translate.put("&0", COLOR_PREFIX_CHARACTER + "0");
        translate.put("&1", COLOR_PREFIX_CHARACTER + "1");
        translate.put("&2", COLOR_PREFIX_CHARACTER + "2");
        translate.put("&3", COLOR_PREFIX_CHARACTER + "3");
        translate.put("&4", COLOR_PREFIX_CHARACTER + "4");
        translate.put("&5", COLOR_PREFIX_CHARACTER + "5");
        translate.put("&6", COLOR_PREFIX_CHARACTER + "6");
        translate.put("&7", COLOR_PREFIX_CHARACTER + "7");
        translate.put("&8", COLOR_PREFIX_CHARACTER + "8");
        translate.put("&9", COLOR_PREFIX_CHARACTER + "9");
        translate.put("&a", COLOR_PREFIX_CHARACTER + "a");
        translate.put("&b", COLOR_PREFIX_CHARACTER + "b");
        translate.put("&c", COLOR_PREFIX_CHARACTER + "c");
        translate.put("&d", COLOR_PREFIX_CHARACTER + "d");
        translate.put("&e", COLOR_PREFIX_CHARACTER + "e");
        translate.put("&f", COLOR_PREFIX_CHARACTER + "f");
        translate.put("&k", COLOR_PREFIX_CHARACTER + "k");
        translate.put("&l", COLOR_PREFIX_CHARACTER + "l");
        translate.put("&m", COLOR_PREFIX_CHARACTER + "m");
        translate.put("&n", COLOR_PREFIX_CHARACTER + "n");
        translate.put("&o", COLOR_PREFIX_CHARACTER + "o");
        translate.put("&r", COLOR_PREFIX_CHARACTER + "r");
    }

    public static String translateString(String value) {
        for (String code : translate.keySet()) {
            value = value.replace(code, translate.get(code));
        }

        return value;
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
