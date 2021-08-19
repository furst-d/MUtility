package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RandomTeleportRequest extends TeleportDataRequest {
    private final double centerX;
    private final double centerZ;
    private final int radius;
    private final boolean loadTeleportData;

    public RandomTeleportRequest(ProxiedPlayer player, ServerInfo server, double centerX, double centerZ, int radius, boolean loadTeleportData) {
        super(player, server);
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.loadTeleportData = loadTeleportData;
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
