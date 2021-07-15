package com.mens.mutility.spigot.eventhandlers;

import com.mens.mutility.spigot.MUtilitySpigot;
import com.mens.mutility.spigot.messages.MessageChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

public class OnPlayerPortalEvent implements Listener {
    public MUtilitySpigot plugin;
    MessageChannel messageChannel;

    public OnPlayerPortalEvent(MUtilitySpigot plugin) {
        this.plugin = plugin;
        messageChannel = new MessageChannel(plugin);
    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        event.setCancelled(true);
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if(Objects.requireNonNull(event.getFrom().getWorld()).getName().equalsIgnoreCase("world")) {
                messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:portalRequest", "nether", event.getFrom().getX() / 8, event.getFrom().getY(), event.getFrom().getZ() / 8);
            } else if(event.getFrom().getWorld().getName().equalsIgnoreCase("world_nether")) {
                messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:portalRequest", "overworld", event.getFrom().getX() * 8, event.getFrom().getY(), event.getFrom().getZ() * 8);
            }
        } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if(Objects.requireNonNull(event.getFrom().getWorld()).getName().equalsIgnoreCase("world")) {
                messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:portalRequest", "end", 0, 0, 0);
            } else if(event.getFrom().getWorld().getName().equalsIgnoreCase("world_the_end")) {
                //messageChannel.sendPortalInfoToBungeeCord(event.getPlayer(), "mens:portalRequest", "overworld", event.getFrom().getX() * 8, event.getFrom().getY(), event.getFrom().getZ() * 8);
            }
        }
    }
}
