package com.mens.mutility.bungeecord.database;

import com.mens.mutility.bungeecord.chat.Prefix;

public class DatabaseTables {
    private final Prefix prefix;

    public DatabaseTables() {
        prefix = new Prefix();
    }

    private String getTable(String tableName) {
        return prefix.getTablePrefix() + tableName;
    }

    public String getEventsTable() {
        return getTable("events");
    }

    public String getInventoryTable() {
        return getTable("inventory2");
    }

    public String getNavrhyTable() {
        return getTable("navrhy2");
    }

    public String getStavbaCompetitorsTable() {
        return getTable("stavba_competitors");
    }

    public String getStavbaKeysTable() {
        return getTable("stavba_keys2");
    }

    public String getStavbaSeasonsTable() {
        return getTable("stavba_seasons");
    }

    public String getStavbaVotesTable() {
        return getTable("stavba_votes");
    }

    public String getZalohyTable() {
        return getTable("zalohy");
    }

    public String getTeleportDataTable() {
        return getTable("teleport_data");
    }

    public String getMParticleTable() {
        return getTable("mparticle");
    }
}
