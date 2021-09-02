package com.mens.mutility.spigot.commands.commands.mparticle.enums;

import com.mens.mutility.spigot.commands.commands.mparticle.RGB;

public enum Colors {
    BLACK("black", new RGB(0, 0, 0)),
    RED("red", new RGB(255,0,0)),
    GREEN("green", new RGB(0,255,0)),
    BLUE("blue", new RGB(0,0,255)),
    AQUA("aqua", new RGB(0,255,255)),
    MAGENTA("magenta", new RGB(255,0,255)),
    YELLOW("yellow", new RGB(255,255,0)),
    WHITE("white", new RGB(255,255,255)),
    RAINBOW("rainbow", new RGB(-1,-1,-1));

    private final String name;
    private final RGB rgb;

    Colors(String name, RGB rgb) {
        this.name = name;
        this.rgb = rgb;
    }

    public String getName() {
        return this.name;
    }

    public RGB getRgb() {
        return rgb;
    }

    public static RGB getRGBByName(String name) {
        return Colors.valueOf(name.toUpperCase()).getRgb();
    }
}
