package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportDataRequest {
    private final ProxiedPlayer player;
    private final ServerInfo server;

    public TeleportDataRequest(ProxiedPlayer player, ServerInfo server) {
        this.player = player;
        this.server = server;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public ServerInfo getServer() {
        return server;
    }
}
