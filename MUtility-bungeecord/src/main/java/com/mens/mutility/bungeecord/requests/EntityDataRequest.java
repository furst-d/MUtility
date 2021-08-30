package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;

public class EntityDataRequest {
    private final String entityTypeName;
    private final String nbt;
    private final ServerInfo server;

    public EntityDataRequest(String entityTypeName, String nbt, ServerInfo server) {
        this.entityTypeName = entityTypeName;
        this.nbt = nbt;
        this.server = server;
    }

    public String getEntityTypeName() {
        return entityTypeName;
    }

    public String getNbt() {
        return nbt;
    }

    public ServerInfo getServer() {
        return server;
    }
}
