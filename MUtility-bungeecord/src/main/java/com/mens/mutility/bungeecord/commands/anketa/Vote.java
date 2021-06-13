package com.mens.mutility.bungeecord.commands.anketa;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Vote {
    private int id;
    private ProxiedPlayer player;

    public Vote(int id, ProxiedPlayer player) {
        this.id = id;
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ProxiedPlayer player) {
        this.player = player;
    }
}
