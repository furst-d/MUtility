package com.mens.mutility.spigot.commands.commands.mparticle.enums;

public enum Colors {
    BLACK("black",0,0,0),
    RED("red",255,0,0),
    GREEN("green",0,255,0),
    BLUE("blue",0,0,255),
    AQUA("aqua",0,255,255),
    MAGENTA("magenta",255,0,255),
    YELLOW("yellow",255,255,0),
    WHITE("white",255,255,255),
    RAINBOW("rainbow",-1,-1,-1);

    private final String name;
    private final int red;
    private final int green;
    private final int blue;

    Colors(String name, int red, int green, int blue) {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String getName() {
        return this.name;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
