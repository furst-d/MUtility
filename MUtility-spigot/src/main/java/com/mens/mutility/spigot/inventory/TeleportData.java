package com.mens.mutility.spigot.inventory;

public class TeleportData {
    private final int id;
    private final String inventory;
    private final String gamemode;

    public TeleportData(int id, String inventory, String gamemode) {
        this.id = id;
        this.inventory = inventory;
        this.gamemode = gamemode;
    }

    public int getId() {
        return id;
    }

    public String getInventory() {
        return inventory;
    }

    public String getGamemode() {
        return gamemode;
    }
}
