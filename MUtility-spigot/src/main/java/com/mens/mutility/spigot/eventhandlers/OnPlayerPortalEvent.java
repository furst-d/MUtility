package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class OnPlayerPortalEvent implements Listener {
    public MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnPlayerPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel(plugin);
    }

    @EventHandler
    public void OnPlayerPortalEvent(PlayerPortalEvent event) {
        if(event.getFrom().getWorld().getName().equalsIgnoreCase("world")) {
            messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:send-to-nether", event.getFrom().getX() / 8, event.getFrom().getY(), event.getFrom().getZ() / 8);
        } else if(event.getFrom().getWorld().getName().equalsIgnoreCase("world_nether")) {
            System.out.println(event.getTo().getX() + ", " + event.getTo().getY() + ", " +  event.getTo().getZ());
            messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:send-to-overworld", event.getFrom().getX() * 8, event.getFrom().getY(), event.getFrom().getZ() * 8);
        }
        event.setCancelled(true);
    }
}
