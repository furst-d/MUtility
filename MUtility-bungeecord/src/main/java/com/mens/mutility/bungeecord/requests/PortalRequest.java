package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PortalRequest extends TeleportDataRequest{
    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private final boolean loadTeleportData;

    public PortalRequest(ProxiedPlayer player, ServerInfo server, double x, double y, double z, String world, boolean loadTeleportData) {
        super(player, server);
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.loadTeleportData = loadTeleportData;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public boolean isLoadTeleportData() {
        return loadTeleportData;
    }
}
