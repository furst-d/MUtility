package com.mens.mutility.bungeecord.utils.randomteleport;

import net.md_5.bungee.api.config.ServerInfo;

public class ServerData {
    private final ServerInfo server;
    private final String name;
    private final int playerCount;
    private final double centerX;
    private final double centerZ;
    private final int radius;
    private final boolean loadTeleportData;

    public ServerData(ServerInfo server, String name, int playerCount, double centerX, double centerZ, int radius, boolean loadTeleportData) {
        this.server = server;
        this.name = name;
        this.playerCount = playerCount;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.loadTeleportData = loadTeleportData;
    }

    public ServerInfo getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isLoadTeleportData() {
        return loadTeleportData;
    }
}
