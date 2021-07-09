package com.mens.mutility.spigot.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryPair {
    private final List<ItemStack> items;
    private final List<ItemStack> armor;

    public InventoryPair(List<ItemStack> items, List<ItemStack> armor) {
        this.items = items;
        this.armor = armor;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<ItemStack> getArmor() {
        return armor;
    }
}
