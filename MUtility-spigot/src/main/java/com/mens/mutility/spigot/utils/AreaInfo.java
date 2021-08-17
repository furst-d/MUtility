package com.mens.mutility.spigot.utils;

public class AreaInfo {
    private final int fromX;
    private final int toX;
    private final int fromY;
    private final int toY;
    private final int fromZ;
    private final int toZ;

    public AreaInfo(int fromX, int toX, int fromY, int toY, int fromZ, int toZ) {
        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
    }

    public int getFromX() {
        return fromX;
    }

    public int getToX() {
        return toX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToY() {
        return toY;
    }

    public int getFromZ() {
        return fromZ;
    }

    public int getToZ() {
        return toZ;
    }
}
