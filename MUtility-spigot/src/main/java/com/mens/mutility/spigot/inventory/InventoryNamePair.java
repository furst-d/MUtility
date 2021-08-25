package com.mens.mutility.spigot.inventory;

public class InventoryNamePair {
    private final String name;
    private final String inventory;

    public InventoryNamePair(String name, String inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    public String getName() {
        return name;
    }

    public String getInventory() {
        return inventory;
    }
}
