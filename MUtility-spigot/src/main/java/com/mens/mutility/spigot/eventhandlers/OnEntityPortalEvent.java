package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public class OnEntityPortalEvent implements Listener {
    public MUtilitySpigot plugin;

    public OnEntityPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent event) {

    }
}
