package com.mens.mutility.bungeecord.requests;

import net.md_5.bungee.api.config.ServerInfo;

public class ParticleUpdateRequest extends  IDDataRequest {
    private final boolean start;
    private boolean runClass;

    public ParticleUpdateRequest(int id, ServerInfo server, boolean start) {
        super(id, server);
        this.start = start;
        runClass = false;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isRunClass() {
        return runClass;
    }

    public void setRunClass(boolean runClass) {
        this.runClass = runClass;
    }
}
