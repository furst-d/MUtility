package com.mens.mutility.spigot.gui;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.chat.PluginColors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIManager implements Listener {
    private Inventory inventory;
    private String inventoryName;
    private boolean movable;

    private final MUtilitySpigot plugin;
    private final PluginColors colors;

    public GUIManager(MUtilitySpigot plugin, int size, String inventoryName) {
        this.plugin = plugin;
        plugin.getPm().registerEvents(this, plugin);
        colors = new PluginColors();
        inventory = Bukkit.createInventory(null, size, colors.getPrimaryColor() + inventoryName);
        this.inventoryName = inventoryName;
        movable = false;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void addItem(ItemStack item, int slot) {
        inventory.setItem(slot, item);
    }

    public void openGUI(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().equals(inventory)) {
            if(!movable) {
                event.setCancelled(true);
            }
        }
    }
}