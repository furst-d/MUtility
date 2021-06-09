package com.mens.mutility.bungeecord.portal;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PortalRequest {
    private ProxiedPlayer player;
    private ServerInfo target;
    private String subChannel;
    private double x;
    private double y;
    private double z;

    public PortalRequest(ProxiedPlayer player, ServerInfo target, String subChannel, double x, double y, double z) {
        this.player = player;
        this.target = target;
        this.subChannel = subChannel;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public ServerInfo getTarget() {
        return target;
    }

    public String getSubChannel() {
        return subChannel;
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
}
