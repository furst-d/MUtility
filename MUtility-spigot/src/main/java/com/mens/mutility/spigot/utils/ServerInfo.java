package com.mens.mutility.spigot.utils;

public class ServerInfo {
    private final String name;
    private final boolean isThis;

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
}
