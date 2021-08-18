package com.mens.mutility.spigot.utils;

public class BorderInfo extends AreaInfo {
    private final String direction;

    public BorderInfo(int fromX, int toX, int fromY, int toY, int fromZ, int toZ, String direction) {
        super(fromX, toX, fromY, toY, fromZ, toZ);
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
