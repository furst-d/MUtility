package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;

public class EntityPortalRequest extends EntityDataRequest {
    private final double x;
    private final double y;
    private final double z;
    private final String world;

    public EntityPortalRequest(String entityTypeName, String nbt, ServerInfo server, double x, double y, double z, String world) {
        super(entityTypeName, nbt, server);
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
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
}
