package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;

public class IDDataRequest {
    private final int id;
    private final ServerInfo server;

    public IDDataRequest(int id, ServerInfo server) {
        this.id = id;
        this.server = server;
    }

    public int getId() {
        return id;
    }

    public ServerInfo getServer() {
        return server;
    }
}
