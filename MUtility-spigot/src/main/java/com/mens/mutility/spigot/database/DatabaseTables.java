package com.mens.mutility.spigot.database;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.Prefix;

public class DatabaseTables {
    private final MUtilitySpigot plugin;
    private final Prefix prefix;

    public DatabaseTables(MUtilitySpigot plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
    }

    private String getTable(String tableName) {
        return prefix.getTablePrefix(plugin) + tableName;
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

    public String getInventoryTeleportTable() {
        return getTable("inventory_teleport");
    }
}
