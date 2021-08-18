package com.mens.mutility.spigot.utils;

public class ServerInfo {
    private final String name;
    private final boolean isThis;
    private BorderInfo border1;
    private BorderInfo border2;
    private AreaInfo randomTeleport;

    public ServerInfo(String name, boolean isThis) {
        this.name = name;
        this.isThis = isThis;
    }

    public String getName() {
        return name;
    }

    public boolean isThis() {
        return isThis;
    }

    public BorderInfo getBorder1() {
        return border1;
    }

    public BorderInfo getBorder2() {
        return border2;
    }

    public AreaInfo getRandomTeleport() {
        return randomTeleport;
    }

    public void setBorder1(BorderInfo border1) {
        this.border1 = border1;
    }

    public void setBorder2(BorderInfo border2) {
        this.border2 = border2;
    }

    public void setRandomTeleport(AreaInfo randomTeleport) {
        this.randomTeleport = randomTeleport;
    }
}
